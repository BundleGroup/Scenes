package gg.bundlegroup.scenes.entity;

import gg.bundlegroup.scenes.plugin.Integration;
import gg.bundlegroup.scenes.plugin.IntegrationProvider;
import gg.bundlegroup.scenes.plugin.ScenesPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class EntityIntegrationProvider implements IntegrationProvider {
    @Override
    public String name() {
        return "entity";
    }

    @Override
    public boolean available() {
        try {
            Player.class.getDeclaredMethod("hideEntity", Plugin.class, Entity.class);
            Player.class.getDeclaredMethod("showEntity", Plugin.class, Entity.class);
            return true;
        } catch (ReflectiveOperationException t) {
            return false;
        }
    }

    @Override
    public Integration create(ScenesPlugin plugin) {
        return new EntityIntegration(plugin);
    }
}
