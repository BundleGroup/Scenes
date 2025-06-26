package gg.bundlegroup.bundlescenes.worldguard;

import gg.bundlegroup.bundleentities.api.tracker.PlayerListEntityTracker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RegionSceneListener implements Listener {
    private final RegionSceneManager manager;

    public RegionSceneListener(RegionSceneManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (PlayerListEntityTracker tracker : manager.getTrackers().values()) {
            tracker.removeViewer(player);
        }
    }
}
