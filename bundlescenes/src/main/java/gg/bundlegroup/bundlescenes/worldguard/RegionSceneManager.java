package gg.bundlegroup.bundlescenes.worldguard;

import gg.bundlegroup.bundleentities.api.BundleEntities;
import gg.bundlegroup.bundleentities.api.tracker.PlayerListEntityTracker;
import org.incendo.cloud.key.CloudKey;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.incendo.cloud.key.CloudKey.cloudKey;

public class RegionSceneManager {
    public static final CloudKey<RegionSceneManager> KEY = cloudKey("region_scene_manager", RegionSceneManager.class);

    private final Map<String, PlayerListEntityTracker> trackers = new HashMap<>();

    public PlayerListEntityTracker getTracker(String name) {
        return trackers.computeIfAbsent(name, s -> BundleEntities.get().entityTrackerFactory().createPlayerListTracker());
    }

    public Map<String, PlayerListEntityTracker> getTrackers() {
        return Collections.unmodifiableMap(trackers);
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
