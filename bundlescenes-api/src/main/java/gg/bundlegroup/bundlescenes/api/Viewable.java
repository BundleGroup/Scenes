package gg.bundlegroup.bundlescenes.api;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface Viewable {
    void addViewer(Player player);

    default void addViewers(Collection<Player> players) {
        players.forEach(this::addViewer);
    }

    void removeViewer(Player player);

    default void removeViewers(Collection<Player> players) {
        players.forEach(this::removeViewer);
    }
}
