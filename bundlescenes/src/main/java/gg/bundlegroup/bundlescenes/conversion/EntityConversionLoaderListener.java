package gg.bundlegroup.bundlescenes.conversion;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EntityConversionLoaderListener implements Listener {
    private final EntityConversion conversion;

    public EntityConversionLoaderListener(EntityConversion conversion) {
        this.conversion = conversion;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        conversion.loadEntities(chunk);
    }
}
