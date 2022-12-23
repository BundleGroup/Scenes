package gg.bundlegroup.scenes.api;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface SceneManager {
    static SceneManager get() {
        return SceneManagerProvider.get();
    }

    @NotNull SceneController createController(@NotNull Plugin plugin, @Nullable String name);

    boolean isVisible(@NotNull Player player, @NotNull String scene);
}
