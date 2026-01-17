package gg.bundlegroup.bundlescenes.api;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.Internal
public final class BundleScenesProvider {
    public BundleScenesProvider() {
    }

    static @Nullable BundleScenes instance;

    public static void setInstance(@Nullable BundleScenes instance) {
        BundleScenesProvider.instance = instance;
    }
}
