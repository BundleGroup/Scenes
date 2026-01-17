package gg.bundlegroup.bundlescenes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class BundleScenesListener implements Listener {
    private final BundleScenesImpl scenes;

    public BundleScenesListener(BundleScenesImpl scenes) {
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
