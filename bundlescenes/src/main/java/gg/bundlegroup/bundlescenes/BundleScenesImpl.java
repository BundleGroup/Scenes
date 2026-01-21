package gg.bundlegroup.bundlescenes;

import gg.bundlegroup.bundlescenes.api.BundleScenes;
import gg.bundlegroup.bundlescenes.api.Controller;
import gg.bundlegroup.bundlescenes.api.Element;
import gg.bundlegroup.bundlescenes.api.Viewable;
import gg.bundlegroup.bundlescenes.entity.EntityManager;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.io.Closeable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BundleScenesImpl implements BundleScenes, Closeable {
    private static final NamespacedKey TAGS_KEY = new NamespacedKey("bundlescenes", "tags");
    private static final NamespacedKey SCENE_KEY = new NamespacedKey("bundlescenes", "scene");
    private final Map<Plugin, PluginState> plugins = new HashMap<>();
    private final Map<String, TagState> tags = new HashMap<>();
    private final Map<PluginElement, ElementState> elements = new HashMap<>();
    private final Map<Player, PlayerState> players = new HashMap<>();
    private final EntityManager entityManager;

    public BundleScenesImpl(Plugin plugin) {
        this.entityManager = new EntityManager(plugin, this);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Controller createController(Plugin plugin) {
        PluginState pluginState = plugins.computeIfAbsent(plugin, PluginState::new);
        PluginController controller = new PluginController(this, plugin);
        pluginState.controllers.add(controller);
        return controller;
    }

    public void unregisterController(PluginController controller) {
        PluginState pluginState = plugins.get(controller.getPlugin());
        if (pluginState == null) {
            return;
        }
        pluginState.controllers.remove(controller);
        if (pluginState.isEmpty()) {
            plugins.remove(pluginState.plugin);
        }
    }

    @Override
    public Element createElement(Plugin plugin, Viewable viewable) {
        PluginState pluginState = plugins.computeIfAbsent(plugin, PluginState::new);
        PluginElement element = new PluginElement(this, plugin, viewable);
        pluginState.elements.add(element);
        return element;
    }

    @Override
    public Set<String> getTags() {
        return Set.copyOf(tags.keySet());
    }

    @Override
    public Set<String> getEntityTags(Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        migrateData(pdc);
        List<String> tags = pdc.get(TAGS_KEY, PersistentDataType.LIST.strings());
        if (tags == null) {
            return Set.of();
        }
        return Set.copyOf(tags);
    }

    @Override
    public void setEntityTags(Entity entity, Set<String> tags) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (tags.isEmpty()) {
            pdc.remove(TAGS_KEY);
        } else {
            pdc.set(TAGS_KEY, PersistentDataType.LIST.strings(), List.copyOf(tags));
        }
        entityManager.refreshEntity(entity);
    }

    @SuppressWarnings("PatternValidation")
    private void migrateData(PersistentDataContainer pdc) {
        if (pdc.has(SCENE_KEY, PersistentDataType.STRING)) {
            String scene = pdc.get(SCENE_KEY, PersistentDataType.STRING);
            if (scene != null) {
                if (Key.parseable(scene)) {
                    Key sceneKey = Key.key(scene);
                    if (sceneKey.namespace().equals("bundlescenes")) {
                        String value = sceneKey.value();
                        String prefix = "worldguard/";
                        if (value.startsWith(prefix)) {
                            String tag = value.substring(prefix.length());
                            pdc.set(TAGS_KEY, PersistentDataType.LIST.strings(), List.of(tag));
                        }
                    }
                }
            }
            pdc.remove(SCENE_KEY);
        }
    }

    public void unregisterElement(PluginElement element) {
        PluginState pluginState = plugins.get(element.getPlugin());
        if (pluginState == null) {
            return;
        }
        pluginState.elements.remove(element);
        if (pluginState.isEmpty()) {
            plugins.remove(pluginState.plugin);
        }
    }

    public void addElementTag(PluginElement element, String tag) {
        ElementState elementState = elements.computeIfAbsent(element, ElementState::new);
        TagState tagState = tags.computeIfAbsent(tag, TagState::new);
        if (elementState.tags.add(tagState)) {
            tagState.elements.add(elementState);
            for (PlayerState playerState : tagState.viewers.keySet()) {
                elementState.viewers.addAndCheckFirst(playerState, tagState);
            }
        }
    }

    public void removeElementTag(PluginElement element, String tag) {
        ElementState elementState = elements.get(element);
        TagState tagState = tags.get(tag);
        if (elementState == null || tagState == null) {
            return;
        }
        if (elementState.tags.remove(tagState)) {
            tagState.elements.remove(elementState);
            for (PlayerState playerState : tagState.viewers.keySet()) {
                elementState.viewers.removeAndCheckLast(playerState, tagState);
            }
        }
        if (elementState.isEmpty()) {
            elements.remove(element);
        }
        if (tagState.isEmpty()) {
            tags.remove(tag);
        }
    }

    public void addPlayerTag(PluginController controller, Player player, String tag) {
        PlayerState playerState = players.computeIfAbsent(player, PlayerState::new);
        TagState tagState = tags.computeIfAbsent(tag, TagState::new);
        if (tagState.viewers.addAndCheckFirst(playerState, controller)) {
            playerState.tags.add(tagState);
            for (ElementState elementState : tagState.elements) {
                if (elementState.viewers.addAndCheckFirst(playerState, tagState)) {
                    elementState.element.getViewable().addViewer(player);
                }
            }
        }
    }

    public void removePlayerTag(PluginController controller, Player player, String tag) {
        PlayerState playerState = players.get(player);
        TagState tagState = tags.get(tag);
        if (playerState == null || tagState == null) {
            return;
        }
        if (tagState.viewers.removeAndCheckLast(playerState, controller)) {
            playerState.tags.remove(tagState);
            for (ElementState elementState : tagState.elements) {
                if (elementState.viewers.removeAndCheckLast(playerState, tagState)) {
                    elementState.element.getViewable().removeViewer(player);
                }
            }
        }
        if (playerState.isEmpty()) {
            players.remove(player);
        }
        if (tagState.isEmpty()) {
            tags.remove(tag);
        }
    }

    public void removePlayer(Player player) {
        PlayerState playerState = players.get(player);
        if (playerState == null) {
            return;
        }
        for (TagState tagState : playerState.tags) {
            for (PluginController controller : tagState.viewers.get(playerState)) {
                controller.removePlayer(player);
            }
            tagState.viewers.removeAll(playerState);
            for (ElementState elementState : tagState.elements) {
                if (elementState.viewers.removeAndCheckLast(playerState, tagState)) {
                    elementState.element.getViewable().removeViewer(player);
                }
            }
            if (tagState.isEmpty()) {
                tags.remove(tagState.tag);
            }
        }
        players.remove(player);
    }

    @Override
    public void close() {
        for (PluginState pluginState : plugins.values()) {
            for (PluginElement element : pluginState.elements) {
                element.remove();
            }
            for (PluginController controller : pluginState.controllers) {
                controller.close();
            }
        }
        plugins.clear();
    }

    public void removePlugin(Plugin plugin) {
        PluginState pluginState = plugins.remove(plugin);
        if (pluginState == null) {
            return;
        }
        for (PluginElement element : pluginState.elements) {
            element.remove();
        }
        for (PluginController controller : pluginState.controllers) {
            controller.close();
        }
    }

    private static class PluginState {
        private final Plugin plugin;
        private final Set<PluginController> controllers = new HashSet<>();
        private final Set<PluginElement> elements = new HashSet<>();

        private PluginState(Plugin plugin) {
            this.plugin = plugin;
        }

        private boolean isEmpty() {
            return controllers.isEmpty() && elements.isEmpty();
        }
    }

    private static class TagState {
        private final String tag;
        private final MultiMap<PlayerState, PluginController> viewers = new MultiMap<>();
        private final Set<ElementState> elements = new HashSet<>();

        private TagState(String tag) {
            this.tag = tag;
        }

        private boolean isEmpty() {
            return viewers.isEmpty() && elements.isEmpty();
        }
    }

    private static class ElementState {
        private final PluginElement element;
        private final Set<TagState> tags = new HashSet<>();
        private final MultiMap<PlayerState, TagState> viewers = new MultiMap<>();

        private ElementState(PluginElement element) {
            this.element = element;
        }

        private boolean isEmpty() {
            return tags.isEmpty();
        }
    }

    private static class PlayerState {
        private final Player player;
        private final Set<TagState> tags = new HashSet<>();

        private PlayerState(Player player) {
            this.player = player;
        }

        private boolean isEmpty() {
            return tags.isEmpty();
        }
    }
}
