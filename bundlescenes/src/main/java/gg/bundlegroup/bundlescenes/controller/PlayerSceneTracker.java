package gg.bundlegroup.bundlescenes.controller;

import gg.bundlegroup.bundlescenes.api.scene.Scene;
import gg.bundlegroup.bundlescenes.scene.ViewableScene;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NullMarked
public class PlayerSceneTracker {
    private final Player player;
    private final Map<ViewableScene, Set<PluginController>> visibleScenes = new HashMap<>();

    public PlayerSceneTracker(Player player) {
        this.player = player;
    }

    public Set<PluginController> getControllersShowingScene(Scene scene) {
        if (scene instanceof ViewableScene viewableScene) {
            Set<PluginController> controllers = visibleScenes.get(viewableScene);
            if (controllers != null) {
                return Collections.unmodifiableSet(controllers);
            }
        }
        return Set.of();
    }

    public void showScene(PluginController controller, ViewableScene scene) {
        visibleScenes.computeIfAbsent(scene, s -> {
            s.addViewer(player);
            return new HashSet<>();
        }).add(controller);
    }

    public void hideScene(PluginController controller, ViewableScene scene) {
        visibleScenes.compute(scene, (s, controllers) -> {
            if (controllers == null) {
                // was not visible
                return null;
            }

            if (!controllers.remove(controller)) {
                // doesn't have this controller
                return controllers;
            }

            if (!controllers.isEmpty()) {
                // still visible due to other controllers
                return controllers;
            }

            // this was the last controller, no longer visible
            s.removeViewer(player);
            return null;
        });
    }
}
