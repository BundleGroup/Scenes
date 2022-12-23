package gg.bundlegroup.scenes.plugin;

import org.bukkit.event.Listener;

public interface Integration extends Listener {
    void load();

    void enable();

    void disable();
}
