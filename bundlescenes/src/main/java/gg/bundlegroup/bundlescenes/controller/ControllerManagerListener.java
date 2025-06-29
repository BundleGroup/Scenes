package gg.bundlegroup.bundlescenes.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ControllerManagerListener implements Listener {
    private final ControllerManager controllerManager;

    public ControllerManagerListener(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        controllerManager.removePlayer(event.getPlayer());
    }
}
