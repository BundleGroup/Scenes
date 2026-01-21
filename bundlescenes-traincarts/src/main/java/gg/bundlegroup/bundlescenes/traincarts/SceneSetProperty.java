package gg.bundlegroup.bundlescenes.traincarts;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.api.ICartProperty;
import com.bergerkiller.bukkit.tc.properties.api.IStringSetProperty;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;

class SceneSetProperty implements ICartProperty<Set<String>>, IStringSetProperty {
    private static final String KEY = "bundlescenesTags";
    private final TrainCartsAddon addon;

    SceneSetProperty(TrainCartsAddon addon) {
        this.addon = addon;
    }

    @Override
    public Set<String> getDefault() {
        return Set.of();
    }

    @Override
    public Optional<Set<String>> readFromConfig(ConfigurationNode config) {
        return Util.getConfigStringSetOptional(config, KEY);
    }

    @Override
    public void writeToConfig(ConfigurationNode config, Optional<Set<String>> value) {
        Util.setConfigStringCollectionOptional(config, KEY, value);
    }

    @Override
    public void set(CartProperties properties, Set<String> value) {
        ICartProperty.super.set(properties, value);
        if (!properties.hasHolder()) {
            return;
        }
        for (Entity entity : properties.getHolder().getEntity().getPassengers()) {
            if (entity instanceof Player player) {
                addon.applyScenes(player, value);
            }
        }
    }
}
