package gg.bundlegroup.bundlescenes.conversion.converter;

import gg.bundlegroup.bundleentities.api.entity.VirtualEntity;
import gg.bundlegroup.bundleentities.api.entity.VirtualEntityFactory;
import gg.bundlegroup.bundleentities.api.tracker.EntityTracker;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface EntityConverter<E extends Entity, V extends VirtualEntity> {
    V convert(E entity, Location location, EntityTracker tracker, VirtualEntityFactory factory);
}
