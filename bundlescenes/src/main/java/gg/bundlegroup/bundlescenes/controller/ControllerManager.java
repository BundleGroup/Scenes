package gg.bundlegroup.bundlescenes.controller;

import gg.bundlegroup.bundlescenes.api.controller.Controller;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ControllerManager {
    private final Map<Plugin, Map<Key, PluginController>> pluginControllers = new HashMap<>();

    public void registerController(PluginController controller) {
        Controller existing = pluginControllers.computeIfAbsent(controller.getPlugin(), p -> new HashMap<>()).putIfAbsent(controller.key(), controller);
        if (existing != null) {
            throw new IllegalStateException("Plugin %s already has a controller with key %s"
                    .formatted(controller.getPlugin().getName(), controller.key()));
        }
    }

    public void unregisterController(PluginController controller) {
        pluginControllers.compute(controller.getPlugin(), (plugin, controllers) -> {
            if (controllers != null) {
                if (controllers.remove(controller.key(), controller)) {
                    if (!controllers.isEmpty()) {
                        return controllers;
                    }
                }
            }
            return null;
        });
    }

    public Stream<PluginController> controllers() {
        return pluginControllers.values().stream()
                .flatMap(controllers -> controllers.values().stream());
    }

    public void clear() {
        controllers().forEach(PluginController::close);
        pluginControllers.clear();
    }

    public void removePlayer(Player player) {
        for (Map<Key, PluginController> controllers : pluginControllers.values()) {
            for (PluginController controller : controllers.values()) {
                controller.removePlayer(player);
            }
        }
    }
}
