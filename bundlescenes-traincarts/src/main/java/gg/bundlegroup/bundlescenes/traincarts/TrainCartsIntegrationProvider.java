package gg.bundlegroup.bundlescenes.traincarts;

import gg.bundlegroup.bundlescenes.plugin.Integration;
import gg.bundlegroup.bundlescenes.plugin.IntegrationProvider;
import gg.bundlegroup.bundlescenes.plugin.ScenesPlugin;
import org.bukkit.Bukkit;

public class TrainCartsIntegrationProvider implements IntegrationProvider {
    @Override
    public String name() {
        return "TrainCarts";
    }

    @Override
    public boolean available() {
        return Bukkit.getPluginManager().getPlugin("Train_Carts") != null;
    }

    @Override
    public Integration create(ScenesPlugin plugin) {
        return new TrainCartsIntegration(plugin);
    }
}
