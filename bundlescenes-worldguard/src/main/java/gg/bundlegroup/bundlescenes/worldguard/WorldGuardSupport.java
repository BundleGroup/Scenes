package gg.bundlegroup.bundlescenes.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.session.SessionManager;
import gg.bundlegroup.bundlescenes.api.BundleScenes;
import gg.bundlegroup.bundlescenes.api.controller.Controller;
import net.kyori.adventure.key.Key;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.paper.util.sender.Source;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class WorldGuardSupport {
    private final Plugin plugin;
    private final BundleScenes scenes;
    private @Nullable SetFlag<String> flag;
    private @Nullable Controller controller;

    public WorldGuardSupport(Plugin plugin, BundleScenes scenes) {
        this.plugin = plugin;
        this.scenes = scenes;
    }

    public void load() {
        flag = new UnionSetFlag<>("scenes", new StringFlag(null));
        WorldGuard.getInstance().getFlagRegistry().register(flag);
    }

    public void enable() {
        Objects.requireNonNull(flag);
        controller = scenes.createController(plugin, Key.key("bundlescenes", "worldguard"));
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        sessionManager.registerHandler(new SceneRegionHandler.Factory(scenes, controller, flag), null);
    }

    public void registerCommands(CommandManager<Source> commandManager, Command.Builder<Source> root) {
    }

    public void disable() {
        if (controller != null) {
            controller.close();
            controller = null;
        }
    }
}
