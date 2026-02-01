package gg.bundlegroup.scenes.plugin;

import gg.bundlegroup.scenes.Addon;
import gg.bundlegroup.scenes.ScenesImpl;
import gg.bundlegroup.scenes.ScenesListener;
import gg.bundlegroup.scenes.api.Controller;
import gg.bundlegroup.scenes.api.ScenesProvider;
import gg.bundlegroup.scenes.entity.EntityListener;
import gg.bundlegroup.scenes.traincarts.TrainCartsAddon;
import gg.bundlegroup.scenes.worldedit.WorldEditAddonImpl;
import gg.bundlegroup.scenes.worldguard.worldguard.WorldGuardAddon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends JavaPlugin {
    private final List<Addon> addons = new ArrayList<>();
    private @Nullable ScenesImpl scenes;
    private @Nullable Controller manualController;

    @Override
    public void onLoad() {
        scenes = new ScenesImpl(this);
        ScenesProvider.setInstance(scenes);
        if (Bukkit.getPluginManager().getPlugin("Train_Carts") != null) {
            addons.add(new TrainCartsAddon(this, scenes));
        }
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
            addons.add(new WorldEditAddonImpl(this, scenes));
        }
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            addons.add(new WorldGuardAddon(this, scenes));
        }
        loadAddons();
    }

    @Override
    public void onEnable() {
        if (scenes == null) {
            return;
        }

        manualController = scenes.createController(this);

        enableAddons();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ScenesListener(scenes), this);
        pluginManager.registerEvents(new EntityListener(scenes.getEntityManager()), this);

        scenes.getEntityManager().refreshAllEntities();
    }

    @Override
    public void onDisable() {
        if (scenes == null) {
            return;
        }

        if (manualController != null) {
            manualController.close();
            manualController = null;
        }

        disableAddons();
        addons.clear();

        scenes.close();
        scenes = null;
        ScenesProvider.setInstance(null);
    }

    public ScenesImpl getScenes() {
        return Objects.requireNonNull(scenes, "scenes");
    }

    public Controller getManualController() {
        return Objects.requireNonNull(manualController, "manualController");
    }

    private void loadAddons() {
        for (Addon addon : addons) {
            addon.load();
        }
    }

    private void enableAddons() {
        for (Addon addon : addons) {
            addon.enable();
        }
    }

    private void disableAddons() {
        for (Addon addon : addons) {
            addon.disable();
        }
    }

    public <T extends Addon> @Nullable T findAddon(Class<T> type) {
        for (Addon addon : addons) {
            if (type.isInstance(addon)) {
                return type.cast(addon);
            }
        }
        return null;
    }
}
