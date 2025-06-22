package gg.bundlegroup.bundlescenes.conversion;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EntityAutoConversionListener implements Listener {
    private final EntityConversion conversion;

    public EntityAutoConversionListener(EntityConversion conversion) {
        this.conversion = conversion;
    }

    @EventHandler
    public void onEntitiesLoaded(EntitiesLoadEvent event) {
        Chunk chunk = event.getChunk();
        if (chunk.isLoaded()) {
            conversion.convertEntities(chunk, event.getEntities());
        }
    }

    @EventHandler
    public void onChunkLoaded(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        conversion.convertEntities(chunk);
    }
}
