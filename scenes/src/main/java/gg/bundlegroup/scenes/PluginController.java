package gg.bundlegroup.scenes;

import gg.bundlegroup.scenes.api.Controller;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class PluginController implements Controller {
    private final ScenesImpl scenes;
    private final Plugin plugin;
    private final MultiMap<Player, String> showingTags = new MultiMap<>();

    public PluginController(ScenesImpl scenes, Plugin plugin) {
        this.scenes = scenes;
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void showTag(Player player, String tag) {
        if (showingTags.add(player, tag)) {
            doShowTag(player, tag);
        }
    }

    @Override
    public void hideTag(Player player, String tag) {
        if (showingTags.remove(player, tag)) {
            doHideTag(player, tag);
        }
    }

    private void doShowTag(Player player, String tag) {
        scenes.addPlayerTag(this, player, tag);
    }

    private void doHideTag(Player player, String tag) {
        scenes.removePlayerTag(this, player, tag);
    }

    public void removePlayer(Player player) {
        showingTags.removeAll(player);
    }

    @Override
    public boolean isShowingTag(Player player, String tag) {
        return showingTags.contains(player, tag);
    }

    @Override
    public Set<String> getShownTags(Player player) {
        return Set.copyOf(showingTags.get(player));
    }

    @Override
    public void close() {
        showingTags.forEach(this::doHideTag);
        showingTags.clear();
        scenes.unregisterController(this);
    }
}
