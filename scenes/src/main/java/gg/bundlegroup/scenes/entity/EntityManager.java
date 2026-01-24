package gg.bundlegroup.scenes.entity;

import com.google.common.collect.Sets;
import gg.bundlegroup.scenes.api.Scenes;
import gg.bundlegroup.scenes.api.Element;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EntityManager {
    private final Plugin plugin;
    private final Scenes scenes;
    private final Map<Entity, Element> entities = new HashMap<>();

    public EntityManager(Plugin plugin, Scenes scenes) {
        this.plugin = plugin;
        this.scenes = scenes;
    }

    public void refreshAllEntities() {
        for (World world : plugin.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                refreshEntity(entity);
            }
        }
    }

    public void refreshEntity(Entity entity) {
        Set<String> tags = scenes.getEntityTags(entity);
        if (tags.isEmpty()) {
            unloadEntity(entity);
            return;
        }

        entity.setVisibleByDefault(false);
        Element element = entities.computeIfAbsent(entity, this::createElement);
        Set<String> previousTags = element.getTags();
        for (String tag : Sets.difference(previousTags, tags)) {
            element.removeTag(tag);
        }
        for (String tag : Sets.difference(tags, previousTags)) {
            element.addTag(tag);
        }
    }

    public void unloadEntity(Entity entity) {
        Element element = entities.remove(entity);
        if (element != null) {
            element.remove();
            entity.setVisibleByDefault(true);
        }
    }

    private Element createElement(Entity entity) {
        return scenes.createElement(plugin, new EntityViewable(plugin, entity));
    }
}
