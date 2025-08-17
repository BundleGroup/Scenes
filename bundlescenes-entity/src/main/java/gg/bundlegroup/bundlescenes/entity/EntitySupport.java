package gg.bundlegroup.bundlescenes.entity;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import gg.bundlegroup.bundlescenes.MessageStyle;
import gg.bundlegroup.bundlescenes.api.BundleScenes;
import gg.bundlegroup.bundlescenes.api.scene.Scene;
import gg.bundlegroup.bundlescenes.command.argument.SceneParser;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static net.kyori.adventure.text.Component.text;
import static org.incendo.cloud.key.CloudKey.cloudKey;

@NullMarked
public class EntitySupport {
    public static final NamespacedKey SCENE_KEY = new NamespacedKey("bundlescenes", "scene");

    private static final String METADATA_KEY = "bundlescenes_entity_viewable";
    private final Plugin plugin;
    private final BundleScenes scenes;

    public EntitySupport(Plugin plugin, BundleScenes scenes) {
        this.plugin = plugin;
        this.scenes = scenes;
    }

    public void load() {
    }

    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(new EntityListener(this), plugin);
        for (World world : plugin.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                loadEntity(entity);
            }
        }
    }

    public void registerCommands(CommandManager<Source> commandManager, Command.Builder<Source> root) {
        Command.Builder<Source> editNode = root.permission("bundlescenes.edit");
        Command.Builder<Source> setNode = editNode.literal("set");
        Command.Builder<Source> unsetNode = editNode.literal("unset");
        CloudKey<MultipleEntitySelector> entityKey = cloudKey("entity", MultipleEntitySelector.class);
        CloudKey<Scene> sceneKey = cloudKey("scene", Scene.class);
        commandManager.command(setNode
                .literal("entity")
                .required(entityKey, MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .required(sceneKey, SceneParser.sceneParser())
                .handler(context ->
                        assignEntities(context, context.get(entityKey).values(), context.get(sceneKey))));
        commandManager.command(unsetNode
                .literal("entity")
                .required(entityKey, MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .handler(context ->
                        unassignEntities(context, context.get(entityKey).values())));
        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            commandManager.command(setNode
                    .literal("worldedit")
                    .required(sceneKey, SceneParser.sceneParser())
                    .senderType(PlayerSource.class)
                    .handler(context -> {
                        Set<Entity> entities = getSelectedEntities(context.sender().source());
                        if (entities != null) {
                            assignEntities(context, entities, context.get(sceneKey));
                        }
                    }));
            commandManager.command(unsetNode
                    .literal("worldedit")
                    .senderType(PlayerSource.class)
                    .handler(context -> {
                        Set<Entity> entities = getSelectedEntities(context.sender().source());
                        if (entities != null) {
                            unassignEntities(context, entities);
                        }
                    }));
        }
    }

    private @Nullable Set<Entity> getSelectedEntities(Player player) {
        BukkitPlayer bukkitPlayer = BukkitAdapter.adapt(player);
        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(bukkitPlayer);
        com.sk89q.worldedit.world.World selectionWorld = localSession.getSelectionWorld();
        Region selection;
        try {
            if (selectionWorld == null) {
                throw new IncompleteRegionException();
            }
            selection = localSession.getSelection(selectionWorld);
        } catch (IncompleteRegionException e) {
            player.sendMessage(Component.text("No WorldEdit selection", NamedTextColor.RED));
            return null;
        }
        World world = BukkitAdapter.adapt(selectionWorld);
        Set<Entity> entities = new HashSet<>();
        for (BlockVector2 chunkPos : selection.getChunks()) {
            Chunk chunk = world.getChunkAt(chunkPos.x(), chunkPos.z());
            for (Entity entity : chunk.getEntities()) {
                Location location = entity.getLocation();
                BlockVector3 pos = BlockVector3.at(
                        location.getBlockX(),
                        location.getBlockY(),
                        location.getBlockZ());
                if (selection.contains(pos)) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }

    private void assignEntities(CommandContext<? extends Source> context, Collection<Entity> entities, Scene scene) {
        String sceneName = scene.key().asMinimalString();
        int count = 0;
        for (Entity entity : entities) {
            if (entity instanceof Player) {
                continue;
            }

            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            String previousSceneName = pdc.get(SCENE_KEY, PersistentDataType.STRING);
            if (Objects.equals(sceneName, previousSceneName)) {
                continue;
            }
            unloadEntity(entity);
            pdc.set(SCENE_KEY, PersistentDataType.STRING, sceneName);
            loadEntity(entity);
            count++;
        }
        context.sender().source().sendMessage(text("Assigned ", MessageStyle.SUCCESS)
                .append(text(count, MessageStyle.SUCCESS_ACCENT))
                .append(text(" entities to scene "))
                .append(text(sceneName, MessageStyle.SUCCESS_ACCENT))
                .append(text(".")));
    }

    private void unassignEntities(CommandContext<? extends Source> context, Collection<Entity> entities) {
        int count = 0;
        for (Entity entity : entities) {
            PersistentDataContainer pdc = entity.getPersistentDataContainer();
            if (!pdc.has(SCENE_KEY)) {
                continue;
            }
            unloadEntity(entity);
            pdc.remove(SCENE_KEY);
            count++;
        }
        context.sender().source().sendMessage(text("Unassigned ", MessageStyle.SUCCESS)
                .append(text(count, MessageStyle.SUCCESS_ACCENT))
                .append(text(" entities from their scenes.")));
    }

    public void disable() {
    }

    public void loadEntity(Entity entity) {
        Scene scene = getScene(entity);
        if (scene == null) {
            return;
        }

        if (entity instanceof Player) {
            entity.getPersistentDataContainer().remove(SCENE_KEY);
            return;
        }

        EntityViewable viewable = new EntityViewable(plugin, entity);
        entity.setVisibleByDefault(false);
        entity.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, viewable));
        scene.addViewable(viewable);
    }

    public void unloadEntity(Entity entity) {
        EntityViewable viewable = getViewable(entity);
        if (viewable != null) {
            unloadEntity(entity, viewable);
        }
    }

    public void unloadEntity(Entity entity, EntityViewable viewable) {
        entity.removeMetadata(METADATA_KEY, plugin);
        Scene scene = getScene(entity);
        if (scene != null) {
            scene.removeViewable(viewable);
        }
        entity.setVisibleByDefault(true);
    }

    public @Nullable EntityViewable getViewable(Entity entity) {
        List<MetadataValue> values = entity.getMetadata(METADATA_KEY);
        for (MetadataValue value : values) {
            if (value.getOwningPlugin() == plugin) {
                if (value.value() instanceof EntityViewable viewable) {
                    return viewable;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("PatternValidation")
    public @Nullable Scene getScene(Entity entity) {
        String sceneName = entity.getPersistentDataContainer().get(SCENE_KEY, PersistentDataType.STRING);
        if (Key.parseable(sceneName)) {
            Key sceneKey = Key.key(sceneName);
            if (sceneKey.namespace().equals(Key.MINECRAFT_NAMESPACE)) {
                // migrate to WorldGuard naming scheme
                sceneKey = Key.key("bundlescenes", "worldguard/" + sceneKey.value());
                entity.getPersistentDataContainer().set(SCENE_KEY, PersistentDataType.STRING, sceneKey.asString());
            }
            return scenes.scene(sceneKey);
        } else {
            return null;
        }
    }
}
