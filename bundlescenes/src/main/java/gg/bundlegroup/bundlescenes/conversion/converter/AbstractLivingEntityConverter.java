package gg.bundlegroup.bundlescenes.conversion.converter;

import gg.bundlegroup.bundleentities.api.entity.VirtualEntity;
import org.bukkit.entity.LivingEntity;

public abstract class AbstractLivingEntityConverter<E extends LivingEntity, V extends VirtualEntity> extends AbstractEntityConverter<E, V> {
    @Override
    protected void configure(V virtual, E entity) {
        super.configure(virtual, entity);
        virtual.setSwimming(entity.isSwimming());
    }
}
