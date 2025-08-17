package gg.bundlegroup.bundlescenes.plugin.worldguard;

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
import gg.bundlegroup.bundlescenes.api.BundleScenes;
import gg.bundlegroup.bundlescenes.api.controller.Controller;
import gg.bundlegroup.bundlescenes.api.scene.Scene;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@NullMarked
@SuppressWarnings("PatternValidation")
public class SceneRegionHandler extends FlagValueChangeHandler<Set<String>> {
    private final BundleScenes scenes;
    private final Controller controller;

    public SceneRegionHandler(Session session, BundleScenes scenes, Controller controller, Flag<Set<String>> flag) {
        super(session, flag);
        this.scenes = scenes;
        this.controller = controller;
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, @Nullable Set<String> value) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        if (value != null) {
            for (String name : value) {
                controller.showScene(bukkitPlayer, getScene(name));
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
            controller.hideScene(bukkitPlayer, getScene(name));
        }
        for (String name : Sets.difference(currentValue, lastValue)) {
            controller.showScene(bukkitPlayer, getScene(name));
        }
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<String> lastValue, MoveType moveType) {
        Player bukkitPlayer = BukkitAdapter.adapt(player);
        for (String name : lastValue) {
            controller.hideScene(bukkitPlayer, getScene(name));
        }
        return true;
    }

    private Scene getScene(String name) {
        return scenes.scene(Key.key("bundlescenes", "worldguard/" + name));
    }

    @NullMarked
    public static class Factory extends Handler.Factory<SceneRegionHandler> {
        private final BundleScenes scenes;
        private final Controller controller;
        private final SetFlag<String> flag;

        public Factory(BundleScenes scenes, Controller controller, SetFlag<String> flag) {
            this.scenes = scenes;
            this.controller = controller;
            this.flag = flag;
        }

        @Override
        public SceneRegionHandler create(Session session) {
            return new SceneRegionHandler(session, scenes, controller, flag);
        }
    }
}
