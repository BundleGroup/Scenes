package gg.bundlegroup.scenes.api;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.Internal
public final class ScenesProvider {
    public ScenesProvider() {
    }

    static @Nullable Scenes instance;

    public static void setInstance(@Nullable Scenes instance) {
        ScenesProvider.instance = instance;
    }
}
