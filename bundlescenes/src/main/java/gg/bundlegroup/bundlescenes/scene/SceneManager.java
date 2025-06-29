package gg.bundlegroup.bundlescenes.scene;

import gg.bundlegroup.bundlescenes.api.scene.Scene;
import net.kyori.adventure.key.Key;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NullMarked
public class SceneManager {
    private final Map<Key, ViewableScene> scenes = new HashMap<>();
    private final Set<ViewableScene> removalQueue = new HashSet<>();

    public ViewableScene getScene(Key key) {
        return scenes.computeIfAbsent(key, this::createScene);
    }

    private ViewableScene createScene(Key key) {
        ViewableScene scene = new ViewableScene(this, key);
        removeIfEmpty(scene);
        return scene;
    }

    public void clear() {
        scenes.clear();
    }

    public Collection<Scene> getScenes() {
        return Collections.unmodifiableCollection(scenes.values());
    }

    public void removeIfEmpty(ViewableScene scene) {
        removalQueue.add(scene);
    }

    public void removeQueuedIfEmpty() {
        for (ViewableScene scene : removalQueue) {
            if (scene.isEmpty()) {
                scenes.remove(scene.key(), scene);
            }
        }
        removalQueue.clear();
    }
}
