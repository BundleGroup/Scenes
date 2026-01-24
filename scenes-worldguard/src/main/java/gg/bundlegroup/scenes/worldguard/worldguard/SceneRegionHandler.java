package gg.bundlegroup.scenes.worldguard.worldguard;

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
import gg.bundlegroup.scenes.api.Controller;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class SceneRegionHandler extends FlagValueChangeHandler<Set<String>> {
    private final Controller controller;

    public SceneRegionHandler(Session session, Controller controller, Flag<Set<String>> flag) {
        super(session, flag);
        this.controller = controller;
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, @Nullable Set<String> value) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        if (value != null) {
            for (String name : value) {
                controller.showTag(bukkitPlayer, name);
            }
        }
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<String> currentValue, @Nullable Set<String> lastValue, MoveType moveType) {
        if (lastValue == null) {
            return true;
        }
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        for (String name : Sets.difference(lastValue, currentValue)) {
            controller.hideTag(bukkitPlayer, name);
        }
        for (String name : Sets.difference(currentValue, lastValue)) {
            controller.showTag(bukkitPlayer, name);
        }
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<String> lastValue, MoveType moveType) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        for (String name : lastValue) {
            controller.hideTag(bukkitPlayer, name);
        }
        return true;
    }

    public static class Factory extends Handler.Factory<SceneRegionHandler> {
        private final Controller controller;
        private final SetFlag<String> flag;

        public Factory(Controller controller, SetFlag<String> flag) {
            this.controller = controller;
            this.flag = flag;
        }

        @Override
        public SceneRegionHandler create(Session session) {
            return new SceneRegionHandler(session, controller, flag);
        }
    }
}
