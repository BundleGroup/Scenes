package gg.bundlegroup.bundlescenes.api.viewable;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;

@NullMarked
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
