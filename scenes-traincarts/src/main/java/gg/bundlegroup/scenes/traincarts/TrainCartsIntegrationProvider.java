package gg.bundlegroup.scenes.traincarts;

import gg.bundlegroup.scenes.plugin.Integration;
import gg.bundlegroup.scenes.plugin.IntegrationProvider;
import gg.bundlegroup.scenes.plugin.ScenesPlugin;
import org.bukkit.Bukkit;

public class TrainCartsIntegrationProvider implements IntegrationProvider {
    @Override
    public String name() {
        return "TrainCarts";
    }

    @Override
    public boolean available() {
        return Bukkit.getPluginManager().isPluginEnabled("Train_Carts");
    }

    @Override
    public Integration create(ScenesPlugin plugin) {
        return new TrainCartsIntegration(plugin);
    }
}
