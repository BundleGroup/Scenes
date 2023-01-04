package gg.bundlegroup.bundlescenes.plugin;

import gg.bundlegroup.bundlescenes.api.SceneManager;
import gg.bundlegroup.bundlescenes.api.event.SceneHideEvent;
import gg.bundlegroup.bundlescenes.api.event.SceneShowEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SceneManagerImpl implements SceneManager, Listener {
    private final Map<Plugin, Set<SceneControllerImpl>> sceneControllers = new HashMap<>();
    private final Map<Player, Map<String, Set<SceneControllerImpl>>> playerScenes = new HashMap<>();

    @Override
    public @NotNull SceneControllerImpl createController(@NotNull Plugin plugin, @Nullable String name) {
        SceneControllerImpl controller = new SceneControllerImpl(this, plugin, name);
        sceneControllers.computeIfAbsent(plugin, p -> new HashSet<>()).add(controller);
        return controller;
    }

    public void unregisterController(@NotNull SceneControllerImpl controller) {
        Plugin plugin = controller.plugin();
        Set<SceneControllerImpl> controllers = sceneControllers.get(plugin);
        if (controllers != null) {
            if (controllers.remove(controller)) {
                if (controllers.isEmpty()) {
                    sceneControllers.remove(plugin);
                }
            }
        }
    }

    @Override
    public boolean isVisible(@NotNull Player player, @NotNull String scene) {
        Map<String, Set<SceneControllerImpl>> scenes = playerScenes.get(player);
        if (scenes == null) {
            return false;
        }
        return scenes.containsKey(scene);
    }

    public Set<SceneControllerImpl> getVisibilityReason(@NotNull Player player, @NotNull String scene) {
        Map<String, Set<SceneControllerImpl>> scenes = playerScenes.get(player);
        if (scenes == null) {
            return Set.of();
        }
        Set<SceneControllerImpl> controllers = scenes.get(scene);
        if (controllers == null) {
            return Set.of();
        }
        return Set.copyOf(controllers);
    }

    public void show(SceneControllerImpl controller, Player player, String scene) {
        Map<String, Set<SceneControllerImpl>> sceneControllers = playerScenes.computeIfAbsent(player, p -> new HashMap<>());
        Set<SceneControllerImpl> controllers = sceneControllers.computeIfAbsent(scene, s -> new HashSet<>());
        boolean wasEmpty = controllers.isEmpty();
        controllers.add(controller);
        if (wasEmpty) {
            Bukkit.getPluginManager().callEvent(new SceneShowEvent(player, scene));
        }
    }

    public void hide(SceneControllerImpl controller, Player player, String scene) {
        Map<String, Set<SceneControllerImpl>> sceneControllers = playerScenes.get(player);
        if (sceneControllers == null) {
            return;
        }
        Set<SceneControllerImpl> controllers = sceneControllers.get(scene);
        if (controllers == null) {
            return;
        }
        controllers.remove(controller);
        if (controllers.isEmpty()) {
            sceneControllers.remove(scene, controllers);
            if (sceneControllers.isEmpty()) {
                playerScenes.remove(player, sceneControllers);
            }
            Bukkit.getPluginManager().callEvent(new SceneHideEvent(player, scene));
        }
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event) {
        Set<SceneControllerImpl> controllers = sceneControllers.remove(event.getPlugin());
        if (controllers != null) {
            for (SceneControllerImpl controller : controllers) {
                controller.remove();
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Map<String, Set<SceneControllerImpl>> sceneControllers = playerScenes.remove(player);
        if (sceneControllers != null) {
            for (Map.Entry<String, Set<SceneControllerImpl>> entry : sceneControllers.entrySet()) {
                String scene = entry.getKey();
                for (SceneControllerImpl controller : entry.getValue()) {
                    controller.hide(player, scene);
                }
                Bukkit.getPluginManager().callEvent(new SceneHideEvent(player, scene));
            }
        }
    }
}
