package gg.bundlegroup.bundlescenes.conversion.converter;

import gg.bundlegroup.bundleentities.api.entity.VirtualLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractLivingEntityConverter<E extends LivingEntity, V extends VirtualLivingEntity> extends AbstractEntityConverter<E, V> {
    @Override
    protected void configure(V virtual, E entity) {
        super.configure(virtual, entity);
        virtual.setSwimming(entity.isSwimming());
    }
}
