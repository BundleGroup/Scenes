package gg.bundlegroup.scenes.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;
import gg.bundlegroup.scenes.api.SceneController;
import gg.bundlegroup.scenes.api.SceneManager;
import gg.bundlegroup.scenes.plugin.Integration;
import gg.bundlegroup.scenes.worldguard.SceneHandler.SceneHandlerFactory;
import org.bukkit.plugin.Plugin;

public class WorldGuardIntegration implements Integration {
    private final Plugin plugin;
    private SetFlag<String> sceneFlag;
    private SceneController controller;
    private SceneHandlerFactory handlerFactory;

    public WorldGuardIntegration(Plugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    private SetFlag<String> findFlag() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            SetFlag<String> flag = new UnionSetFlag<>("scenes", new StringFlag(null));
            registry.register(flag);
            return flag;
        } catch (FlagConflictException e) {
            // some other plugin registered a flag by the same name already.
            // you can use the existing flag, but this may cause conflicts - be sure to check type
            Flag<?> existing = registry.get("scenes");
            if (existing instanceof SetFlag<?> setFlag) {
                if (setFlag.getType() instanceof StringFlag) {
                    return (SetFlag<String>) setFlag;
                }
            }
        }
        return null;
    }

    @Override
    public void load() {
        sceneFlag = findFlag();
        if (sceneFlag == null) {
            throw new IllegalStateException("Conflicting flag registered");
        }
    }

    @Override
    public void enable() {
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        controller = SceneManager.get().createController(plugin, "WorldGuard");
        handlerFactory = new SceneHandlerFactory(sceneFlag, controller);
        sessionManager.registerHandler(handlerFactory, null);
    }

    @Override
    public void disable() {
        if (handlerFactory != null) {
            SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
            sessionManager.unregisterHandler(handlerFactory);
            handlerFactory = null;
        }
        if (controller != null) {
            controller.remove();
            controller = null;
        }
    }
}
