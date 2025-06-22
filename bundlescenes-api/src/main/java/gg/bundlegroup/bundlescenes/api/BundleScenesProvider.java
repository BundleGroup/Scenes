package gg.bundlegroup.bundlescenes.api;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class BundleScenesProvider {
    public BundleScenesProvider() {
    }

    static @Nullable BundleScenes instance;

    public static void setInstance(BundleScenes instance) {
        BundleScenesProvider.instance = instance;
    }
}
