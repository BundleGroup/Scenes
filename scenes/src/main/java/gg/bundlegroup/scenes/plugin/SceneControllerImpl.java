package gg.bundlegroup.scenes.plugin;

import gg.bundlegroup.scenes.api.SceneController;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SceneControllerImpl implements SceneController {
    private final SceneManagerImpl manager;
    private final Plugin plugin;
    private final Map<Player, Set<String>> entries = new HashMap<>();
    private boolean removed;

    public SceneControllerImpl(SceneManagerImpl manager, Plugin plugin) {
        this.manager = manager;
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void show(Player player, String scene) {
        if (removed) {
            throw new IllegalStateException();
        }
        Set<String> scenes = entries.computeIfAbsent(player, p -> new HashSet<>());
        if (scenes.add(scene)) {
            manager.show(this, player, scene);
        }
    }

    @Override
    public void hide(Player player, String scene) {
        if (removed) {
            throw new IllegalStateException();
        }
        Set<String> scenes = entries.get(player);
        if (scenes != null) {
            if (scenes.remove(scene)) {
                manager.hide(this, player, scene);
                if (scenes.isEmpty()) {
                    entries.remove(player, scenes);
                }
            }
        }
    }

    @Override
    public void hideAll(Player player) {
        Set<String> scenes = entries.remove(player);
        if (scenes != null) {
            for (String scene : scenes) {
                manager.hide(this, player, scene);
            }
        }
    }

    @Override
    public void remove() {
        if (removed) {
            return;
        }
        manager.unregisterController(this);
        for (Map.Entry<Player, Set<String>> entry : entries.entrySet()) {
            Player player = entry.getKey();
            for (String scene : entry.getValue()) {
                manager.hide(this, player, scene);
            }
        }
        entries.clear();
        removed = true;
    }

    public @NotNull List<String> getShown(Player player) {
        Set<String> scenes = entries.get(player);
        if (scenes == null) {
            return List.of();
        }
        return List.copyOf(scenes);
    }
}
