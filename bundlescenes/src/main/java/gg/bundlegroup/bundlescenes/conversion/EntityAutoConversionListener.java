package gg.bundlegroup.bundlescenes.conversion;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EntityAutoConversionListener implements Listener {
    private final Plugin plugin;
    private final EntityConversion conversion;

    public EntityAutoConversionListener(Plugin plugin, EntityConversion conversion) {
        this.plugin = plugin;
        this.conversion = conversion;
    }

    @EventHandler
    public void onEntitiesLoaded(EntitiesLoadEvent event) {
        Chunk chunk = event.getChunk();
        if (chunk.isLoaded()) {
            int count = conversion.convertEntities(chunk, event.getEntities());
            sendSuccess(chunk, count);
        }
    }

    @EventHandler
    public void onChunkLoaded(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        int count = conversion.convertEntities(chunk);
        sendSuccess(chunk, count);
    }

    private void sendSuccess(Chunk chunk, int count) {
        if (count > 0) {
            plugin.getComponentLogger().info("Converted {} entities in chunk {}/{}/{}", count, chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        }
    }
}
