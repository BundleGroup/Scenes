package gg.bundlegroup.bundlescenes.api;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SceneManagerProvider {
    private static SceneManager manager;

    private SceneManagerProvider() {
    }

    public static void set(SceneManager manager) {
        SceneManagerProvider.manager = manager;
    }

    public static SceneManager get() {
        return manager;
    }
}
