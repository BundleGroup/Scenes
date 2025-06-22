package gg.bundlegroup.bundlescenes.data;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class UUIDDataType implements PersistentDataType<String, UUID> {
    public static final UUIDDataType INSTANCE = new UUIDDataType();

    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public Class<UUID> getComplexType() {
        return UUID.class;
    }

    @Override
    public String toPrimitive(UUID complex, PersistentDataAdapterContext context) {
        return complex.toString();
    }

    @Override
    public UUID fromPrimitive(String primitive, PersistentDataAdapterContext context) {
        return UUID.fromString(primitive);
    }
}
