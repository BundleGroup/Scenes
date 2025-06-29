package gg.bundlegroup.bundlescenes.entity;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityListener implements Listener {
    private final EntitySupport support;

    public EntityListener(EntitySupport support) {
        this.support = support;
    }

    @EventHandler
    public void onAdd(EntityAddToWorldEvent event) {
        support.loadEntity(event.getEntity());
    }

    @EventHandler
    public void onRemove(EntityRemoveFromWorldEvent event) {
        support.unloadEntity(event.getEntity());
    }
}
