package gg.bundlegroup.bundlescenes.chunk;

import org.bukkit.Chunk;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@NullMarked
public class ChunkManager {
    private final Plugin plugin;
    private final Map<Chunk, TrackedChunk> chunks = new HashMap<>();

    public ChunkManager(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new ChunkListener(this), plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::update, 0, 1);
    }

    public Collection<TrackedChunk> getChunks() {
        return Collections.unmodifiableCollection(chunks.values());
    }

    public TrackedChunk getChunk(Chunk chunk) {
        if (!plugin.getServer().isPrimaryThread()) {
            throw new IllegalStateException("Not on main thread");
        }
        if (!chunk.isLoaded()) {
            throw new IllegalArgumentException("Chunk not loaded");
        }
        return chunks.computeIfAbsent(chunk, TrackedChunk::new);
    }

    public void removeChunk(Chunk chunk) {
        TrackedChunk c = chunks.remove(chunk);
        if (c != null) {
            c.remove();
        }
    }

    public void update() {
        chunks.values().removeIf(c -> {
            if (!c.isValid()) {
                Chunk chunk = c.getChunk();
                plugin.getComponentLogger().warn("Forgetting invalid chunk: {}/{}/{}", chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
                c.remove();
                return true;
            }
            c.update();
            return false;
        });
    }

    public void close() {
        for (TrackedChunk chunk : chunks.values()) {
            chunk.remove();
        }
        chunks.clear();
    }
}
