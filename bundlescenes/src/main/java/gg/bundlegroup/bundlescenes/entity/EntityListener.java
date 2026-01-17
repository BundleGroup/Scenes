package gg.bundlegroup.bundlescenes.entity;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityListener implements Listener {
    private final EntityManager manager;

    public EntityListener(EntityManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onAdd(EntityAddToWorldEvent event) {
        manager.refreshEntity(event.getEntity());
    }

    @EventHandler
    public void onRemove(EntityRemoveFromWorldEvent event) {
        manager.unloadEntity(event.getEntity());
    }
}
