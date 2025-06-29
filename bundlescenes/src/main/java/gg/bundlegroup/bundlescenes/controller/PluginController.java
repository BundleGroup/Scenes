package gg.bundlegroup.bundlescenes.controller;

import gg.bundlegroup.bundlescenes.api.controller.Controller;
import gg.bundlegroup.bundlescenes.api.scene.Scene;
import gg.bundlegroup.bundlescenes.scene.ViewableScene;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NullMarked
public class PluginController implements Controller {
    private final ControllerManager controllerManager;
    private final PlayerSceneTrackerManager playerSceneTrackerManager;
    private final Map<Player, Set<Scene>> visibleScenes = new HashMap<>();
    private final Plugin plugin;
    private final Key key;

    public PluginController(ControllerManager controllerManager, PlayerSceneTrackerManager playerSceneTrackerManager, Plugin plugin, Key key) {
        this.controllerManager = controllerManager;
        this.playerSceneTrackerManager = playerSceneTrackerManager;
        this.plugin = plugin;
        this.key = key;
        controllerManager.registerController(this);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public Key key() {
        return key;
    }

    @Override
    public void showScene(Player player, Scene scene) {
        playerSceneTrackerManager.getOrCreatePlayer(player).showScene(this, (ViewableScene) scene);
        visibleScenes.computeIfAbsent(player, p -> new HashSet<>()).add(scene);
    }

    @Override
    public void hideScene(Player player, Scene scene) {
        visibleScenes.compute(player, (p, scenes) -> {
            if (scenes == null) {
                return null;
            }
            if (scenes.remove(scene)) {
                if (scenes.isEmpty()) {
                    return null;
                }
            }
            return scenes;
        });

        PlayerSceneTracker tracker = playerSceneTrackerManager.getPlayer(player);
        if (tracker != null) {
            tracker.hideScene(this, (ViewableScene) scene);
        }
    }

    @Override
    public boolean isShowingScene(Player player, Scene scene) {
        Set<Scene> playerScenes = visibleScenes.get(player);
        return playerScenes != null && playerScenes.contains(scene);
    }

    public void removePlayer(Player player) {
        Set<Scene> scenes = visibleScenes.remove(player);
        PlayerSceneTracker tracker = playerSceneTrackerManager.getPlayer(player);
        if (scenes != null && tracker != null) {
            for (Scene scene : scenes) {
                tracker.hideScene(this, (ViewableScene) scene);
            }
        }
    }

    @Override
    public void close() {
        controllerManager.unregisterController(this);
    }
}
