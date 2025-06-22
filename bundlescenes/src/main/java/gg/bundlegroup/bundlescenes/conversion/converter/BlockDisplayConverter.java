package gg.bundlegroup.bundlescenes.conversion.converter;

import gg.bundlegroup.bundleentities.api.entity.VirtualBlockDisplay;
import gg.bundlegroup.bundleentities.api.entity.VirtualEntityFactory;
import gg.bundlegroup.bundleentities.api.tracker.EntityTracker;
import org.bukkit.entity.BlockDisplay;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BlockDisplayConverter extends AbstractDisplayConverter<BlockDisplay, VirtualBlockDisplay> {
    @Override
    protected VirtualBlockDisplay create(BlockDisplay entity, EntityTracker tracker, VirtualEntityFactory factory) {
        return factory.createBlockDisplay(tracker);
    }

    @Override
    protected void configure(VirtualBlockDisplay virtual, BlockDisplay entity) {
        super.configure(virtual, entity);
        virtual.setBlock(entity.getBlock());
    }
}
