package gg.bundlegroup.bundlescenes.controller;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class PlayerSceneTrackerManager {
    private final Map<Player, PlayerSceneTracker> playerTrackers = new HashMap<>();

    public PlayerSceneTracker getOrCreatePlayer(Player player) {
        if (!player.isOnline()) {
            throw new IllegalArgumentException("Player is offline");
        }
        return playerTrackers.computeIfAbsent(player, PlayerSceneTracker::new);
    }

    public @Nullable PlayerSceneTracker getPlayer(Player player) {
        return playerTrackers.get(player);
    }

    public void removePlayer(Player player) {
        playerTrackers.remove(player);
    }

    public void clear() {
        playerTrackers.clear();
    }
}
