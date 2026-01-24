package gg.bundlegroup.scenes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class ScenesListener implements Listener {
    private final ScenesImpl scenes;

    public ScenesListener(ScenesImpl scenes) {
        this.scenes = scenes;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        scenes.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        scenes.removePlugin(event.getPlugin());
    }
}
