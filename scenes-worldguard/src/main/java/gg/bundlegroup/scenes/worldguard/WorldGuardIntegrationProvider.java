package gg.bundlegroup.scenes.worldguard;

import gg.bundlegroup.scenes.plugin.Integration;
import gg.bundlegroup.scenes.plugin.IntegrationProvider;
import gg.bundlegroup.scenes.plugin.ScenesPlugin;
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
