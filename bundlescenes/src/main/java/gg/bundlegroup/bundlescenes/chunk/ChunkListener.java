package gg.bundlegroup.bundlescenes.chunk;

import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ChunkListener implements Listener {
    private final ChunkManager manager;

    public ChunkListener(ChunkManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onUnload(ChunkUnloadEvent event) {
        manager.removeChunk(event.getChunk());
    }

    @EventHandler
    public void onPlayerChunkLoad(PlayerChunkLoadEvent event) {
        manager.getChunk(event.getChunk()).getEntityTracker().addViewer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChunkUnload(PlayerChunkUnloadEvent event) {
        manager.getChunk(event.getChunk()).getEntityTracker().removeViewer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (TrackedChunk chunk : manager.getChunks()) {
            chunk.getEntityTracker().removeViewer(player);
        }
    }
}
