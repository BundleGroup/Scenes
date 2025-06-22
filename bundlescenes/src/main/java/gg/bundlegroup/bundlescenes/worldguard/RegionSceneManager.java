package gg.bundlegroup.bundlescenes.worldguard;

import gg.bundlegroup.bundleentities.api.BundleEntities;
import gg.bundlegroup.bundleentities.api.tracker.PlayerListEntityTracker;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RegionSceneManager {
    private final Map<String, PlayerListEntityTracker> trackers = new HashMap<>();

    public PlayerListEntityTracker getTracker(String name) {
        return trackers.computeIfAbsent(name, s -> BundleEntities.get().entityTrackerFactory().createPlayerListTracker());
    }

    public Collection<PlayerListEntityTracker> getTrackers() {
        return Collections.unmodifiableCollection(trackers.values());
    }

    public void update() {
        for (PlayerListEntityTracker tracker : trackers.values()) {
            tracker.update();
        }
    }

    public void close() {
        for (PlayerListEntityTracker tracker : trackers.values()) {
            tracker.close();
        }
        trackers.clear();
    }
}
