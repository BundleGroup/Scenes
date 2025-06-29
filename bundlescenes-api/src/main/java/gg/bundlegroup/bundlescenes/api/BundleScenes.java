package gg.bundlegroup.bundlescenes.api;

import gg.bundlegroup.bundlescenes.api.controller.Controller;
import gg.bundlegroup.bundlescenes.api.scene.ChunkSceneProvider;
import gg.bundlegroup.bundlescenes.api.scene.Scene;
import net.kyori.adventure.key.Key;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Objects;

@NullMarked
public interface BundleScenes {
    Controller createController(Plugin plugin, Key key);

    Scene scene(Key key);

    Collection<Scene> scenes();

    default ChunkSceneProvider chunk(Location location) {
        return chunk(location.getChunk());
    }

    default ChunkSceneProvider chunk(Chunk chunk) {
        return chunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    ChunkSceneProvider chunk(World world, int x, int z);

    static BundleScenes get() {
        return Objects.requireNonNull(BundleScenesProvider.instance);
    }
}
