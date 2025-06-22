package gg.bundlegroup.bundlescenes.conversion;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EntityConversionLoaderListener implements Listener {
    private final Plugin plugin;
    private final EntityConversion conversion;

    public EntityConversionLoaderListener(Plugin plugin, EntityConversion conversion) {
        this.plugin = plugin;
        this.conversion = conversion;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        int count = conversion.loadEntities(chunk);
        sendSuccess(chunk, count);
    }

    private void sendSuccess(Chunk chunk, int count) {
        if (count > 0) {
            plugin.getComponentLogger().info("Loaded {} virtual entities in chunk {}/{}/{} in tick {}", count, chunk.getWorld().getName(), chunk.getX(), chunk.getZ(), plugin.getServer().getCurrentTick());
        }
    }
}
