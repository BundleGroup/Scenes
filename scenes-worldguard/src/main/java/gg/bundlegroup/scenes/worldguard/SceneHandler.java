package gg.bundlegroup.scenes.worldguard;

import com.google.common.collect.Sets;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import gg.bundlegroup.scenes.api.SceneController;
import org.bukkit.entity.Player;

import java.util.Set;

public class SceneHandler extends FlagValueChangeHandler<Set<String>> {
    private final SceneController controller;

    protected SceneHandler(Session session, Flag<Set<String>> flag, SceneController controller) {
        super(session, flag);
        this.controller = controller;
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, Set<String> value) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        controller.hideAll(bukkitPlayer);
        for (String scene : value) {
            controller.show(bukkitPlayer, scene);
        }
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<String> currentValue, Set<String> lastValue, MoveType moveType) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        for (String scene : Sets.difference(lastValue, currentValue)) {
            controller.hide(bukkitPlayer, scene);
        }
        for (String scene : Sets.difference(currentValue, lastValue)) {
            controller.show(bukkitPlayer, scene);
        }
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<String> lastValue, MoveType moveType) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        controller.hideAll(bukkitPlayer);
        return true;
    }

    public static class SceneHandlerFactory extends Factory<SceneHandler> {
        private final Flag<Set<String>> flag;
        private final SceneController controller;

        public SceneHandlerFactory(Flag<Set<String>> flag, SceneController controller) {
            this.flag = flag;
            this.controller = controller;
        }

        @Override
        public SceneHandler create(Session session) {
            return new SceneHandler(session, flag, controller);
        }
    }
}
