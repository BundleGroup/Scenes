package gg.bundlegroup.bundlescenes.conversion.converter;

import gg.bundlegroup.bundleentities.api.entity.VirtualEntity;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class EntityConverterRegistry {
    private final Map<Class<?>, EntityConverter<?, ?>> entityConverters = new HashMap<>();

    public <E extends Entity,
            V extends VirtualEntity>
    void register(Class<E> type, EntityConverter<E, V> converter) {
        entityConverters.put(type, converter);
    }

    @SuppressWarnings("unchecked")
    public <E extends Entity> @Nullable EntityConverter<E, ?> get(@Nullable Class<E> type) {
        if (type == null) {
            return null;
        }
        return (EntityConverter<E, ?>) entityConverters.get(type);
    }
}
