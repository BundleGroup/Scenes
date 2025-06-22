package gg.bundlegroup.bundlescenes.data;

import gg.bundlegroup.bundlescenes.Main;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LocationDataType implements PersistentDataType<PersistentDataContainer, Location> {
    public static final LocationDataType INSTANCE = new LocationDataType();

    private static final NamespacedKey X_KEY = new NamespacedKey(Main.NAMESPACE, "x");
    private static final NamespacedKey Y_KEY = new NamespacedKey(Main.NAMESPACE, "y");
    private static final NamespacedKey Z_KEY = new NamespacedKey(Main.NAMESPACE, "z");
    private static final NamespacedKey YAW_KEY = new NamespacedKey(Main.NAMESPACE, "yaw");
    private static final NamespacedKey PITCH_KEY = new NamespacedKey(Main.NAMESPACE, "pitch");

    @Override
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public Class<Location> getComplexType() {
        return Location.class;
    }

    @Override
    public PersistentDataContainer toPrimitive(Location complex, PersistentDataAdapterContext context) {
        PersistentDataContainer container = context.newPersistentDataContainer();
        container.set(X_KEY, PersistentDataType.DOUBLE, complex.getX());
        container.set(Y_KEY, PersistentDataType.DOUBLE, complex.getY());
        container.set(Z_KEY, PersistentDataType.DOUBLE, complex.getZ());
        container.set(YAW_KEY, PersistentDataType.FLOAT, complex.getYaw());
        container.set(PITCH_KEY, PersistentDataType.FLOAT, complex.getPitch());
        return container;
    }

    @Override
    public Location fromPrimitive(PersistentDataContainer primitive, PersistentDataAdapterContext context) {
        double x = primitive.getOrDefault(X_KEY, PersistentDataType.DOUBLE, 0.0);
        double y = primitive.getOrDefault(Y_KEY, PersistentDataType.DOUBLE, 0.0);
        double z = primitive.getOrDefault(Z_KEY, PersistentDataType.DOUBLE, 0.0);
        float yaw = primitive.getOrDefault(YAW_KEY, PersistentDataType.FLOAT, 0f);
        float pitch = primitive.getOrDefault(PITCH_KEY, PersistentDataType.FLOAT, 0f);
        return new Location(null, x, y, z, yaw, pitch);
    }
}
