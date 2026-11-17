package gg.bundlegroup.scenes;

import gg.bundlegroup.scenes.api.Controller;
import gg.bundlegroup.scenes.api.Element;
import gg.bundlegroup.scenes.api.Scenes;
import gg.bundlegroup.scenes.api.Viewable;
import gg.bundlegroup.scenes.entity.EntityManager;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScenesImpl implements Scenes, Closeable {
    private static final NamespacedKey TAGS_KEY = new NamespacedKey("scenes", "tags");
    private static final NamespacedKey LEGACY_TAGS_KEY = new NamespacedKey("bundlescenes", "tags");
    private static final NamespacedKey LEGACY_SCENE_KEY = new NamespacedKey("bundlescenes", "scene");
    private final Map<Plugin, PluginState> plugins = new HashMap<>();
    private final Map<String, TagState> tags = new HashMap<>();
    private final Map<PluginElement, ElementState> elements = new HashMap<>();
    private final Map<Player, PlayerState> players = new HashMap<>();
    private final MultiMap<Player, String> forceHidden = new MultiMap<>();
    private final Set<Player> forceHiddenAll = new HashSet<>();
    private final EntityManager entityManager;

    public ScenesImpl(Plugin plugin) {
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
        if (pdc.has(LEGACY_SCENE_KEY, PersistentDataType.STRING)) {
            String scene = pdc.get(LEGACY_SCENE_KEY, PersistentDataType.STRING);
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
            pdc.remove(LEGACY_SCENE_KEY);
        }
        if (pdc.has(LEGACY_TAGS_KEY, PersistentDataType.LIST.strings())) {
            List<String> tags = pdc.get(LEGACY_TAGS_KEY, PersistentDataType.LIST.strings());
            if (tags != null) {
                pdc.set(TAGS_KEY, PersistentDataType.LIST.strings(), tags);
                pdc.remove(LEGACY_TAGS_KEY);
            }
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
                if (isForceHidden(playerState.player, tagState.tag)) {
                    continue;
                }
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
                if (isForceHidden(playerState.player, tagState.tag)) {
                    continue;
                }
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
            if (!isForceHidden(player, tag)) {
                showElements(playerState, tagState);
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
            if (!isForceHidden(player, tag)) {
                hideElements(playerState, tagState);
            }
        }
        if (playerState.isEmpty()) {
            players.remove(player);
        }
        if (tagState.isEmpty()) {
            tags.remove(tag);
        }
    }

    private void showElements(PlayerState playerState, TagState tagState) {
        for (ElementState elementState : tagState.elements) {
            if (elementState.viewers.addAndCheckFirst(playerState, tagState)) {
                elementState.element.getViewable().addViewer(playerState.player);
            }
        }
    }

    private void hideElements(PlayerState playerState, TagState tagState) {
        for (ElementState elementState : tagState.elements) {
            if (elementState.viewers.removeAndCheckLast(playerState, tagState)) {
                elementState.element.getViewable().removeViewer(playerState.player);
            }
        }
    }

    /**
     * Returns the tags that at least one controller is currently showing to the player,
     * including tags that are force-hidden.
     */
    public Set<String> getPlayerTags(Player player) {
        PlayerState playerState = players.get(player);
        if (playerState == null) {
            return Set.of();
        }
        Set<String> result = new HashSet<>();
        for (TagState tagState : playerState.tags) {
            result.add(tagState.tag);
        }
        return result;
    }

    public boolean isForceHidden(Player player, String tag) {
        return forceHiddenAll.contains(player) || forceHidden.contains(player, tag);
    }

    public boolean isForceHidingAll(Player player) {
        return forceHiddenAll.contains(player);
    }

    /**
     * Returns the tags individually force-hidden for the player (does not include the hide-all switch).
     */
    public Set<String> getForceHiddenTags(Player player) {
        return Set.copyOf(forceHidden.get(player));
    }

    /**
     * Hides the tag from the player even if a controller (such as a WorldGuard region) is showing it.
     *
     * @return false if the tag was already force-hidden individually
     */
    public boolean forceHideTag(Player player, String tag) {
        boolean wasHidden = isForceHidden(player, tag);
        if (!forceHidden.add(player, tag)) {
            return false;
        }
        if (!wasHidden) {
            hideIfShownByController(player, tag);
        }
        return true;
    }

    /**
     * Lifts an individual force-hide. The tag stays hidden if the hide-all switch is on.
     *
     * @return false if the tag was not individually force-hidden
     */
    public boolean unforceHideTag(Player player, String tag) {
        if (!forceHidden.remove(player, tag)) {
            return false;
        }
        if (!isForceHidden(player, tag)) {
            showIfShownByController(player, tag);
        }
        return true;
    }

    /**
     * Force-hides every tag from the player, including tags created later.
     *
     * @return false if already hiding all
     */
    public boolean forceHideAll(Player player) {
        PlayerState playerState = players.get(player);
        List<TagState> toHide = new ArrayList<>();
        if (playerState != null) {
            for (TagState tagState : playerState.tags) {
                if (!isForceHidden(player, tagState.tag)) {
                    toHide.add(tagState);
                }
            }
        }
        if (!forceHiddenAll.add(player)) {
            return false;
        }
        for (TagState tagState : toHide) {
            hideElements(playerState, tagState);
        }
        return true;
    }

    /**
     * Lifts every force-hide for the player: the hide-all switch and all individual force-hides.
     *
     * @return false if nothing was force-hidden
     */
    public boolean clearForceHides(Player player) {
        PlayerState playerState = players.get(player);
        List<TagState> toShow = new ArrayList<>();
        if (playerState != null) {
            for (TagState tagState : playerState.tags) {
                if (isForceHidden(player, tagState.tag)) {
                    toShow.add(tagState);
                }
            }
        }
        boolean changed = forceHiddenAll.remove(player);
        changed |= forceHidden.removeAll(player);
        for (TagState tagState : toShow) {
            showElements(playerState, tagState);
        }
        return changed;
    }

    private void hideIfShownByController(Player player, String tag) {
        PlayerState playerState = players.get(player);
        TagState tagState = tags.get(tag);
        if (playerState != null && tagState != null && playerState.tags.contains(tagState)) {
            hideElements(playerState, tagState);
        }
    }

    private void showIfShownByController(Player player, String tag) {
        PlayerState playerState = players.get(player);
        TagState tagState = tags.get(tag);
        if (playerState != null && tagState != null && playerState.tags.contains(tagState)) {
            showElements(playerState, tagState);
        }
    }

    public void removePlayer(Player player) {
        PlayerState playerState = players.get(player);
        if (playerState == null) {
            forceHidden.removeAll(player);
            forceHiddenAll.remove(player);
            return;
        }
        for (TagState tagState : playerState.tags) {
            for (PluginController controller : tagState.viewers.get(playerState)) {
                controller.removePlayer(player);
            }
            tagState.viewers.removeAll(playerState);
            if (!isForceHidden(player, tagState.tag)) {
                hideElements(playerState, tagState);
            }
            if (tagState.isEmpty()) {
                tags.remove(tagState.tag);
            }
        }
        players.remove(player);
        forceHidden.removeAll(player);
        forceHiddenAll.remove(player);
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

    public int getTagCount() {
        return tags.size();
    }

    public int getElementCount() {
        return elements.size();
    }

    public int getPlayerCount() {
        return players.size();
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
