package gg.bundlegroup.bundlescenes.chunk;

import gg.bundlegroup.bundlescenes.api.scene.ChunkSceneProvider;
import gg.bundlegroup.bundlescenes.api.scene.Scene;
import gg.bundlegroup.bundlescenes.scene.SceneManager;
import net.kyori.adventure.key.Key;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TrackedChunkSceneProvider implements ChunkSceneProvider {
    private static final String VIEW_RANGE = "view-range";

    private final SceneManager sceneManager;
    private final World world;
    private final int x;
    private final int z;

    public TrackedChunkSceneProvider(SceneManager sceneManager, World world, int x, int z) {
        this.sceneManager = sceneManager;
        this.world = world;
        this.x = x;
        this.z = z;
    }

    @Override
    public Scene viewRangeScene() {
        return scene(VIEW_RANGE);
    }

    @SuppressWarnings("PatternValidation")
    public Scene scene(String name) {
        String value = "chunks/%s/%s/%s/%s".formatted(world.getName(), x, z, name);
        return sceneManager.getScene(Key.key("bundlescenes", value));
    }
}
