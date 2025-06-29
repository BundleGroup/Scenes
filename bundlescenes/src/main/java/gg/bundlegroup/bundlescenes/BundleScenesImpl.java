package gg.bundlegroup.bundlescenes;

import gg.bundlegroup.bundlescenes.api.BundleScenes;
import gg.bundlegroup.bundlescenes.api.controller.Controller;
import gg.bundlegroup.bundlescenes.api.scene.ChunkSceneProvider;
import gg.bundlegroup.bundlescenes.api.scene.Scene;
import gg.bundlegroup.bundlescenes.chunk.TrackedChunkSceneProvider;
import gg.bundlegroup.bundlescenes.controller.ControllerManager;
import gg.bundlegroup.bundlescenes.controller.PlayerSceneTrackerManager;
import gg.bundlegroup.bundlescenes.controller.PluginController;
import gg.bundlegroup.bundlescenes.scene.SceneManager;
import net.kyori.adventure.key.Key;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.io.Closeable;
import java.util.Collection;

@NullMarked
public class BundleScenesImpl implements BundleScenes, Closeable {
    private final SceneManager sceneManager = new SceneManager();
    private final ControllerManager controllerManager = new ControllerManager();
    private final PlayerSceneTrackerManager playerSceneTrackerManager = new PlayerSceneTrackerManager();

    @Override
    public Controller createController(Plugin plugin, Key key) {
        return new PluginController(controllerManager, playerSceneTrackerManager, plugin, key);
    }

    @Override
    public Scene scene(Key key) {
        return sceneManager.getScene(key);
    }

    @Override
    public Collection<Scene> scenes() {
        return sceneManager.getScenes();
    }

    @Override
    public ChunkSceneProvider chunk(World world, int x, int z) {
        return new TrackedChunkSceneProvider(sceneManager, world, x, z);
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public ControllerManager getControllerManager() {
        return controllerManager;
    }

    public PlayerSceneTrackerManager getPlayerSceneTrackerManager() {
        return playerSceneTrackerManager;
    }

    @Override
    public void close() {
        controllerManager.clear();
        playerSceneTrackerManager.clear();
        sceneManager.clear();
    }
}
