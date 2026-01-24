package gg.bundlegroup.scenes.entity;

import gg.bundlegroup.scenes.api.Viewable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class EntityViewable implements Viewable {
    private final Plugin plugin;
    private final Entity entity;

    public EntityViewable(Plugin plugin, Entity entity) {
        this.plugin = plugin;
        this.entity = entity;
    }

    @Override
    public void addViewer(Player player) {
        player.showEntity(plugin, entity);
    }

    @Override
    public void removeViewer(Player player) {
        player.hideEntity(plugin, entity);
    }
}
