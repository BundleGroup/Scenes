package gg.bundlegroup.bundlescenes.conversion.converter;

import gg.bundlegroup.bundleentities.api.entity.VirtualEntityFactory;
import gg.bundlegroup.bundleentities.api.entity.VirtualItemDisplay;
import gg.bundlegroup.bundleentities.api.tracker.EntityTracker;
import org.bukkit.entity.ItemDisplay;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ItemDisplayConverter extends AbstractDisplayConverter<ItemDisplay, VirtualItemDisplay> {
    @Override
    protected VirtualItemDisplay create(ItemDisplay entity, EntityTracker tracker, VirtualEntityFactory factory) {
        return factory.createItemDisplay(tracker);
    }

    @Override
    protected void configure(VirtualItemDisplay virtual, ItemDisplay entity) {
        super.configure(virtual, entity);
        virtual.setItem(entity.getItemStack());
        virtual.setItemDisplayTransform(entity.getItemDisplayTransform());
    }
}
