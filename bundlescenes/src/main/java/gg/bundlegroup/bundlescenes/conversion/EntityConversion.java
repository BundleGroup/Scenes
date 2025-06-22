package gg.bundlegroup.bundlescenes.conversion;

import gg.bundlegroup.bundleentities.api.BundleEntities;
import gg.bundlegroup.bundleentities.api.entity.VirtualEntity;
import gg.bundlegroup.bundleentities.api.entity.VirtualEntityFactory;
import gg.bundlegroup.bundleentities.api.tracker.EntityTracker;
import gg.bundlegroup.bundlescenes.Main;
import gg.bundlegroup.bundlescenes.chunk.ChunkManager;
import gg.bundlegroup.bundlescenes.chunk.TrackedChunk;
import gg.bundlegroup.bundlescenes.conversion.converter.EntityConverter;
import gg.bundlegroup.bundlescenes.conversion.converter.EntityConverterRegistry;
import gg.bundlegroup.bundlescenes.data.LocationDataType;
import gg.bundlegroup.bundlescenes.data.UUIDDataType;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@NullMarked
public class EntityConversion {
    private static final NamespacedKey ENTITIES_KEY = new NamespacedKey(Main.NAMESPACE, "entities");
    private static final NamespacedKey UUID_KEY = new NamespacedKey(Main.NAMESPACE, "uuid");
    private static final NamespacedKey SNAPSHOT_KEY = new NamespacedKey(Main.NAMESPACE, "snapshot");
    private static final NamespacedKey LOCATION_KEY = new NamespacedKey(Main.NAMESPACE, "location");

    private final EntityConverterRegistry registry;
    private final ChunkManager chunkManager;

    public EntityConversion(EntityConverterRegistry registry, ChunkManager chunkManager) {
        this.registry = registry;
        this.chunkManager = chunkManager;
    }

    public int convertEntities(Chunk chunk) {
        return convertEntities(chunk, List.of(chunk.getEntities()));
    }

    public int convertEntities(Chunk chunk, List<Entity> entities) {
        PersistentDataContainer pdc = chunk.getPersistentDataContainer();

        List<PersistentDataContainer> entityContainers = pdc.get(ENTITIES_KEY, PersistentDataType.LIST.dataContainers());
        if (entityContainers == null) {
            entityContainers = new ArrayList<>();
        } else {
            entityContainers = new ArrayList<>(entityContainers);
        }

        TrackedChunk trackedChunk = chunkManager.getChunk(chunk);
        EntityTracker tracker = trackedChunk.getEntityTracker();
        VirtualEntityFactory factory = BundleEntities.get().virtualEntityFactory();

        int count = 0;
        for (Entity entity : entities) {
            UUID uuid = entity.getUniqueId();
            entityContainers.removeIf(c -> Objects.equals(c.get(UUID_KEY, UUIDDataType.INSTANCE), uuid));

            PersistentDataContainer entityContainer = convertEntity(trackedChunk, entity, tracker, factory, pdc.getAdapterContext());
            if (entityContainer != null) {
                entityContainers.add(entityContainer);
                entity.remove();
                count++;
            }
        }

        pdc.set(ENTITIES_KEY, PersistentDataType.LIST.dataContainers(), entityContainers);
        return count;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private @Nullable PersistentDataContainer convertEntity(TrackedChunk trackedChunk, Entity entity, EntityTracker tracker, VirtualEntityFactory factory, PersistentDataAdapterContext dataAdapterContext) {
        EntityConverter converter = registry.get(entity.getType().getEntityClass());
        if (converter == null) {
            return null;
        }

        PersistentDataContainer entityContainer = saveEntity(entity, dataAdapterContext);
        if (entityContainer == null) {
            return null;
        }

        VirtualEntity virtualEntity = converter.convert(entity, entity.getLocation(), tracker, factory);
        trackedChunk.addConvertedEntity(entity.getUniqueId(), virtualEntity);
        entity.remove();
        virtualEntity.show();
        return entityContainer;
    }

    @SuppressWarnings("UnstableApiUsage")
    private @Nullable PersistentDataContainer saveEntity(Entity entity, PersistentDataAdapterContext dataAdapterContext) {
        EntitySnapshot snapshot = entity.createSnapshot();
        if (snapshot == null) {
            return null;
        }

        PersistentDataContainer entityContainer = dataAdapterContext.newPersistentDataContainer();
        entityContainer.set(UUID_KEY, UUIDDataType.INSTANCE, entity.getUniqueId());
        entityContainer.set(SNAPSHOT_KEY, PersistentDataType.STRING, snapshot.getAsString());
        entityContainer.set(LOCATION_KEY, LocationDataType.INSTANCE, entity.getLocation());
        return entityContainer;
    }

    @SuppressWarnings({"UnstableApiUsage", "rawtypes", "unchecked"})
    public int loadEntities(Chunk chunk) {
        PersistentDataContainer pdc = chunk.getPersistentDataContainer();

        List<PersistentDataContainer> entityContainers = pdc.get(ENTITIES_KEY, PersistentDataType.LIST.dataContainers());
        if (entityContainers == null) {
            return 0;
        }

        TrackedChunk trackedChunk = chunkManager.getChunk(chunk);
        EntityTracker tracker = trackedChunk.getEntityTracker();
        VirtualEntityFactory factory = BundleEntities.get().virtualEntityFactory();

        int count = 0;
        List<PersistentDataContainer> remainingContainers = new ArrayList<>();
        for (PersistentDataContainer entityContainer : entityContainers) {
            UUID uuid = entityContainer.get(UUID_KEY, UUIDDataType.INSTANCE);
            String snapshot = entityContainer.get(SNAPSHOT_KEY, PersistentDataType.STRING);
            Location location = entityContainer.get(LOCATION_KEY, LocationDataType.INSTANCE);
            if (uuid != null && snapshot != null && location != null) {
                EntitySnapshot entitySnapshot = Bukkit.getEntityFactory().createEntitySnapshot(snapshot);
                Entity entity = entitySnapshot.createEntity(chunk.getWorld());
                location.setWorld(chunk.getWorld());

                EntityConverter converter = registry.get(entity.getType().getEntityClass());
                if (converter == null) {
                    if (entity.spawnAt(location)) {
                        VirtualEntity virtualEntity = trackedChunk.removeConvertedEntity(uuid);
                        if (virtualEntity != null) {
                            virtualEntity.hide();
                        }
                    } else {
                        remainingContainers.add(entityContainer);
                    }
                    continue;
                }

                VirtualEntity virtualEntity = converter.convert(entity, location, tracker, factory);
                trackedChunk.addConvertedEntity(uuid, virtualEntity);
                virtualEntity.show();
                remainingContainers.add(entityContainer);
                count++;
            }
        }

        pdc.set(ENTITIES_KEY, PersistentDataType.LIST.dataContainers(), remainingContainers);
        return count;
    }

    @SuppressWarnings("UnstableApiUsage")
    public int restoreEntities(Chunk chunk) {
        PersistentDataContainer pdc = chunk.getPersistentDataContainer();

        List<PersistentDataContainer> entityContainers = pdc.get(ENTITIES_KEY, PersistentDataType.LIST.dataContainers());
        if (entityContainers == null) {
            return 0;
        }

        TrackedChunk trackedChunk = chunkManager.getChunk(chunk);

        int count = 0;
        List<PersistentDataContainer> remainingContainers = new ArrayList<>();
        for (PersistentDataContainer entityContainer : entityContainers) {
            UUID uuid = entityContainer.get(UUID_KEY, UUIDDataType.INSTANCE);
            String snapshot = entityContainer.get(SNAPSHOT_KEY, PersistentDataType.STRING);
            Location location = entityContainer.get(LOCATION_KEY, LocationDataType.INSTANCE);
            if (uuid != null && snapshot != null && location != null) {
                EntitySnapshot entitySnapshot = Bukkit.getEntityFactory().createEntitySnapshot(snapshot);
                Entity entity = entitySnapshot.createEntity(chunk.getWorld());
                location.setWorld(chunk.getWorld());
                if (entity.spawnAt(location)) {
                    VirtualEntity virtualEntity = trackedChunk.removeConvertedEntity(uuid);
                    if (virtualEntity != null) {
                        virtualEntity.hide();
                    }
                    count++;
                    continue;
                }
            }
            remainingContainers.add(entityContainer);
        }

        pdc.set(ENTITIES_KEY, PersistentDataType.LIST.dataContainers(), remainingContainers);
        return count;
    }
}
