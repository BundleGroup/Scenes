package gg.bundlegroup.bundlescenes.api;

import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public interface BundleScenes {
    Controller createController(Plugin plugin);

    Element createElement(Plugin plugin, Viewable viewable);

    Set<String> getTags();

    Set<String> getEntityTags(Entity entity);

    void setEntityTags(Entity entity, Set<String> tags);

    default boolean addEntityTag(Entity entity, String tag) {
        Set<String> tags = new HashSet<>(getEntityTags(entity));
        boolean added = tags.add(tag);
        if (added) {
            setEntityTags(entity, tags);
        }
        return added;
    }

    default boolean removeEntityTag(Entity entity, String tag) {
        Set<String> tags = new HashSet<>(getEntityTags(entity));
        boolean removed = tags.remove(tag);
        if (removed) {
            setEntityTags(entity, tags);
        }
        return removed;
    }

    static BundleScenes get() {
        return Objects.requireNonNull(BundleScenesProvider.instance);
    }
}
