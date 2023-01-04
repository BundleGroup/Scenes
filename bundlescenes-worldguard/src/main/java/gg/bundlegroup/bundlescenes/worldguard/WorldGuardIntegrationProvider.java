package gg.bundlegroup.bundlescenes.worldguard;

import gg.bundlegroup.bundlescenes.plugin.Integration;
import gg.bundlegroup.bundlescenes.plugin.IntegrationProvider;
import gg.bundlegroup.bundlescenes.plugin.ScenesPlugin;
import org.bukkit.Bukkit;

public class WorldGuardIntegrationProvider implements IntegrationProvider {
    @Override
    public String name() {
        return "WorldGuard";
    }

    @Override
    public boolean available() {
        return Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    }

    @Override
    public Integration create(ScenesPlugin plugin) {
        return new WorldGuardIntegration(plugin);
    }
}
