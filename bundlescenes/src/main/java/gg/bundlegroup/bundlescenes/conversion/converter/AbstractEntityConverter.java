package gg.bundlegroup.bundlescenes.conversion.converter;

import gg.bundlegroup.bundleentities.api.entity.VirtualEntity;
import gg.bundlegroup.bundleentities.api.entity.VirtualEntityFactory;
import gg.bundlegroup.bundleentities.api.tracker.EntityTracker;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.joml.Vector3d;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractEntityConverter<E extends Entity, V extends VirtualEntity> implements EntityConverter<E, V> {
    @Override
    public final V convert(E entity, Location location, EntityTracker tracker, VirtualEntityFactory factory) {
        V virtual = create(entity, tracker, factory);
        virtual.setPosition(new Vector3d(location.getX(), location.getY(), location.getZ()));
        virtual.setYaw(location.getYaw());
        virtual.setPitch(location.getPitch());
        configure(virtual, entity);
        return virtual;
    }

    protected abstract V create(E entity, EntityTracker tracker, VirtualEntityFactory factory);

    protected void configure(V virtual, E entity) {
        virtual.setOnFire(entity.getFireTicks() > 0);
        virtual.setSneaking(entity.isSneaking());
        virtual.setInvisible(entity.isInvisible());
        virtual.setGlowing(entity.isGlowing());
        virtual.setCustomName(entity.customName());
        virtual.setCustomNameVisible(entity.isCustomNameVisible());
        virtual.setSilent(entity.isSilent());
        virtual.setNoGravity(!entity.hasGravity());
        virtual.setPose(entity.getPose());
        virtual.setFrozenTicks(entity.getFreezeTicks());
    }
}
