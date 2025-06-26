package gg.bundlegroup.bundlescenes.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.session.SessionManager;
import gg.bundlegroup.bundlescenes.worldguard.command.AssignmentCommands;
import gg.bundlegroup.bundlescenes.worldguard.command.preprocessor.RegionSceneManagerPreprocessor;
import gg.bundlegroup.bundlescenes.worldguard.flag.UnionSetFlag;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.paper.util.sender.Source;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class WorldGuardSupport {
    private final Plugin plugin;
    private @Nullable RegionSceneManager sceneManager;
    private @Nullable SetFlag<String> flag;

    public WorldGuardSupport(Plugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        sceneManager = new RegionSceneManager();
        flag = new UnionSetFlag<>("scenes", new StringFlag(null));
        WorldGuard.getInstance().getFlagRegistry().register(flag);
    }

    public void enable(CommandManager<Source> commandManager) {
        Objects.requireNonNull(sceneManager);
        Objects.requireNonNull(flag);
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        sessionManager.registerHandler(new SceneRegionHandler.Factory(sceneManager, flag), null);
        plugin.getServer().getScheduler().runTaskTimer(plugin, sceneManager::update, 0, 1);
        plugin.getServer().getPluginManager().registerEvents(new RegionSceneListener(sceneManager), plugin);
        commandManager.registerCommandPreProcessor(new RegionSceneManagerPreprocessor<>(sceneManager));
        commandManager.command(new AssignmentCommands());
    }

    public void disable() {
        if (sceneManager != null) {
            sceneManager.close();
            sceneManager = null;
        }
    }

    public RegionSceneManager getSceneManager() {
        return Objects.requireNonNull(sceneManager);
    }
}
