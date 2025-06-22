package gg.bundlegroup.bundlescenes.worldguard;

import com.google.common.collect.Sets;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

@NullMarked
public class SceneRegionHandler extends FlagValueChangeHandler<Set<String>> {
    private final RegionSceneManager sceneManager;

    public SceneRegionHandler(Session session, RegionSceneManager sceneManager, Flag<Set<String>> flag) {
        super(session, flag);
        this.sceneManager = sceneManager;
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, Set<String> value) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        for (String name : value) {
            sceneManager.getTracker(name).addViewer(bukkitPlayer);
        }
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<String> currentValue, Set<String> lastValue, MoveType moveType) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        for (String name : Sets.difference(lastValue, currentValue)) {
            sceneManager.getTracker(name).removeViewer(bukkitPlayer);
        }
        for (String name : Sets.difference(currentValue, lastValue)) {
            sceneManager.getTracker(name).addViewer(bukkitPlayer);
        }
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<String> lastValue, MoveType moveType) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        for (String name : lastValue) {
            sceneManager.getTracker(name).removeViewer(bukkitPlayer);
        }
        return true;
    }

    @NullMarked
    public static class Factory extends Handler.Factory<SceneRegionHandler> {
        private final RegionSceneManager sceneManager;
        private final SetFlag<String> flag;

        public Factory(RegionSceneManager sceneManager, SetFlag<String> flag) {
            this.sceneManager = sceneManager;
            this.flag = flag;
        }

        @Override
        public SceneRegionHandler create(Session session) {
            return new SceneRegionHandler(session, sceneManager, flag);
        }
    }
}
