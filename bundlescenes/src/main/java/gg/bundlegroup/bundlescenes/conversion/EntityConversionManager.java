package gg.bundlegroup.bundlescenes.conversion;

import gg.bundlegroup.bundlescenes.chunk.ChunkManager;
import gg.bundlegroup.bundlescenes.conversion.converter.EntityConverterRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class EntityConversionManager {
    private final Plugin plugin;
    private final EntityConverterRegistry registry;
    private final EntityConversion conversion;
    private @Nullable EntityAutoConversionListener autoConversionListener;

    public EntityConversionManager(Plugin plugin, ChunkManager chunkManager) {
        this.plugin = plugin;
        this.registry = new EntityConverterRegistry();
        this.conversion = new EntityConversion(registry, chunkManager);
        plugin.getServer().getPluginManager().registerEvents(new EntityConversionLoaderListener(conversion), plugin);
    }

    public EntityConversion getConversion() {
        return conversion;
    }

    public EntityConverterRegistry getRegistry() {
        return registry;
    }

    public void loadAllEntities() {
        int count = 0;
        for (World world : plugin.getServer().getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                count += conversion.loadEntities(chunk);
            }
        }
        if (count > 0) {
            plugin.getComponentLogger().info("Loaded {} virtual entities", count);
        }
    }

    public boolean isAutoConvert() {
        return autoConversionListener != null;
    }

    public void setAutoConvert(boolean autoConvert) {
        if (autoConvert) {
            if (autoConversionListener == null) {
                autoConversionListener = new EntityAutoConversionListener(conversion);
                plugin.getServer().getPluginManager().registerEvents(autoConversionListener, plugin);
                plugin.getLogger().info("Enabled automatic entity conversion");
                for (World world : Bukkit.getServer().getWorlds()) {
                    for (Chunk chunk : world.getLoadedChunks()) {
                        conversion.convertEntities(chunk);
                    }
                }
            }
        } else {
            if (autoConversionListener != null) {
                HandlerList.unregisterAll(autoConversionListener);
                plugin.getLogger().info("Disabled automatic entity conversion");
                autoConversionListener = null;
            }
        }
    }
}
