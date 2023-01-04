package gg.bundlegroup.bundlescenes.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SceneClearEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private final @NotNull String scene;

    public SceneClearEvent(@NotNull String scene) {
        this.scene = scene;
    }

    public @NotNull String getScene() {
        return scene;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }
}
