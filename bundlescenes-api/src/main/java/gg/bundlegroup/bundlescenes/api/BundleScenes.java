package gg.bundlegroup.bundlescenes.api;

import gg.bundlegroup.bundleentities.api.tracker.EntityTracker;
import org.bukkit.Chunk;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public interface BundleScenes {
    EntityTracker getChunkEntityTracker(Chunk chunk);

    EntityTracker getSceneEntityTracker(String name);

    static BundleScenes get() {
        return Objects.requireNonNull(BundleScenesProvider.instance);
    }
}
