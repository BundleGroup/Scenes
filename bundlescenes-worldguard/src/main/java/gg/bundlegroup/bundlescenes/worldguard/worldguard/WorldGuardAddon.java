package gg.bundlegroup.bundlescenes.worldguard.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.session.SessionManager;
import gg.bundlegroup.bundlescenes.Addon;
import gg.bundlegroup.bundlescenes.api.BundleScenes;
import gg.bundlegroup.bundlescenes.api.Controller;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class WorldGuardAddon implements Addon {
    private final Plugin plugin;
    private final BundleScenes scenes;
    private @Nullable SetFlag<String> flag;
    private @Nullable Controller controller;

    public WorldGuardAddon(Plugin plugin, BundleScenes scenes) {
        this.plugin = plugin;
        this.scenes = scenes;
    }

    @Override
    public void load() {
        flag = new UnionSetFlag<>("scenes", new StringFlag(null));
        WorldGuard.getInstance().getFlagRegistry().register(flag);
    }

    @Override
    public void enable() {
        Objects.requireNonNull(flag);
        controller = scenes.createController(plugin);
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        sessionManager.registerHandler(new SceneRegionHandler.Factory(controller, flag), null);
    }

    @Override
    public void disable() {
        if (controller != null) {
            controller.close();
            controller = null;
        }
    }
}
