package gg.bundlegroup.bundlescenes.api.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class SceneCompleteEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Set<String> scenes;

    public SceneCompleteEvent(Set<String> scenes) {
        super(!Bukkit.isPrimaryThread());
        this.scenes = scenes;
    }

    public void addScene(String scene) {
        this.scenes.add(scene);
    }

    public void addScenes(Collection<? extends String> scenes) {
        this.scenes.addAll(scenes);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }
}
