package gg.bundlegroup.bundlescenes.chunk;

import gg.bundlegroup.bundlescenes.api.BundleScenes;
import gg.bundlegroup.bundlescenes.api.controller.Controller;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.event.packet.PlayerChunkUnloadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerChunkTrackerListener implements Listener {
    private final Controller controller;
    private final BundleScenes scenes;

    public PlayerChunkTrackerListener(Controller controller, BundleScenes scenes) {
        this.controller = controller;
        this.scenes = scenes;
    }

    @EventHandler
    public void onPlayerChunkLoad(PlayerChunkLoadEvent event) {
        controller.showScene(event.getPlayer(), scenes.chunk(event.getChunk()).viewRangeScene());
    }

    @EventHandler
    public void onPlayerChunkUnload(PlayerChunkUnloadEvent event) {
        controller.hideScene(event.getPlayer(), scenes.chunk(event.getChunk()).viewRangeScene());
    }
}
