package gg.bundlegroup.bundlescenes.scene;

import gg.bundlegroup.bundlescenes.api.scene.Scene;
import gg.bundlegroup.bundlescenes.api.viewable.Viewable;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NullMarked
public class ViewableScene implements Scene {
    private final SceneManager manager;
    private final Key key;
    private final Set<Viewable> viewables = new HashSet<>();
    private final Set<Player> viewers = new HashSet<>();

    public ViewableScene(SceneManager manager, Key key) {
        this.manager = manager;
        this.key = key;
    }

    @Override
    public Key key() {
        return key;
    }

    @Override
    public void addViewable(Viewable viewable) {
        if (viewables.add(viewable)) {
            viewable.addViewers(Collections.unmodifiableSet(viewers));
        }
    }

    @Override
    public void removeViewable(Viewable viewable) {
        if (viewables.remove(viewable)) {
            viewable.removeViewers(Collections.unmodifiableSet(viewers));
            removeIfEmpty();
        }
    }

    public void addViewer(Player player) {
        if (viewers.add(player)) {
            for (Viewable viewable : viewables) {
                viewable.addViewer(player);
            }
        }
    }

    public void removeViewer(Player player) {
        if (viewers.remove(player)) {
            for (Viewable viewable : viewables) {
                viewable.removeViewer(player);
            }
            removeIfEmpty();
        }
    }

    public boolean isEmpty() {
        return viewers.isEmpty() && viewables.isEmpty();
    }

    private void removeIfEmpty() {
        if (isEmpty()) {
            manager.removeIfEmpty(this);
        }
    }
}
