package gg.bundlegroup.bundlescenes.chunk;

import gg.bundlegroup.bundleentities.api.BundleEntities;
import gg.bundlegroup.bundleentities.api.entity.VirtualEntity;
import gg.bundlegroup.bundleentities.api.tracker.PlayerListEntityTracker;
import org.bukkit.Chunk;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NullMarked
public class TrackedChunk {
    private final Chunk chunk;
    private final PlayerListEntityTracker entityTracker;
    private final Map<UUID, VirtualEntity> convertedEntities = new HashMap<>();

    public TrackedChunk(Chunk chunk) {
        this.chunk = chunk;
        this.entityTracker = BundleEntities.get().entityTrackerFactory().createPlayerListTracker();
    }

    public Chunk getChunk() {
        return chunk;
    }

    public PlayerListEntityTracker getEntityTracker() {
        return entityTracker;
    }

    public void addConvertedEntity(UUID id, VirtualEntity entity) {
        VirtualEntity old = convertedEntities.put(id, entity);
        if (old != null) {
            old.hide();
        }
    }

    public @Nullable VirtualEntity removeConvertedEntity(UUID id) {
        return convertedEntities.remove(id);
    }

    public boolean isValid() {
        return chunk.isLoaded();
    }

    public void update() {
        entityTracker.update();
    }

    public void remove() {
        for (VirtualEntity entity : convertedEntities.values()) {
            entity.hide();
        }
        convertedEntities.clear();
        entityTracker.close();
    }
}
