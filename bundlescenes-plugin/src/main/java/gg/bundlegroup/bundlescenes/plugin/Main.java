package gg.bundlegroup.bundlescenes.plugin;

import gg.bundlegroup.bundlescenes.Addon;
import gg.bundlegroup.bundlescenes.BundleScenesImpl;
import gg.bundlegroup.bundlescenes.BundleScenesListener;
import gg.bundlegroup.bundlescenes.api.BundleScenesProvider;
import gg.bundlegroup.bundlescenes.api.Controller;
import gg.bundlegroup.bundlescenes.entity.EntityListener;
import gg.bundlegroup.bundlescenes.worldedit.WorldEditAddon;
import gg.bundlegroup.bundlescenes.worldguard.worldguard.WorldGuardAddon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends JavaPlugin {
    private final List<Addon> addons = new ArrayList<>();
    private @Nullable BundleScenesImpl scenes;
    private @Nullable Controller manualController;

    @Override
    public void onLoad() {
        scenes = new BundleScenesImpl(this);
        BundleScenesProvider.setInstance(scenes);
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
            addons.add(new WorldEditAddon(this, scenes));
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
        pluginManager.registerEvents(new BundleScenesListener(scenes), this);
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
        BundleScenesProvider.setInstance(null);
    }

    public BundleScenesImpl getScenes() {
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
}
