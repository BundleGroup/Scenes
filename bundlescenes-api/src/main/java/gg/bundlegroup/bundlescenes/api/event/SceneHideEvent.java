package gg.bundlegroup.bundlescenes.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class SceneHideEvent extends PlayerEvent {
    private static final HandlerList handlerList = new HandlerList();

    private final @NotNull String scene;

    public SceneHideEvent(@NotNull Player who, @NotNull String scene) {
        super(who);
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
