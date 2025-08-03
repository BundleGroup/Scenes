package gg.bundlegroup.bundlescenes.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerSceneTrackerListener implements Listener {
    private final PlayerSceneTrackerManager manager;

    public PlayerSceneTrackerListener(PlayerSceneTrackerManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        manager.removePlayer(event.getPlayer());
    }
}
