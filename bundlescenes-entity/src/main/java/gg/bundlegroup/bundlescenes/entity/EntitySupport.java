package gg.bundlegroup.bundlescenes.entity;

import gg.bundlegroup.bundlescenes.MessageStyle;
import gg.bundlegroup.bundlescenes.api.BundleScenes;
import gg.bundlegroup.bundlescenes.api.scene.Scene;
import gg.bundlegroup.bundlescenes.command.argument.SceneParser;
import net.kyori.adventure.key.Key;
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
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.paper.util.sender.Source;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

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
        Command.Builder<Source> entityNode = root.literal("entity");
        CloudKey<MultipleEntitySelector> entityKey = cloudKey("entity", MultipleEntitySelector.class);
        CloudKey<Scene> sceneKey = cloudKey("scene", Scene.class);
        commandManager.command(entityNode.literal("set")
                .required(entityKey, MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .required(sceneKey, SceneParser.sceneParser())
                .permission("bundlescenes.entity.set")
                .handler(context -> {
                    MultipleEntitySelector selector = context.get(entityKey);
                    Scene scene = context.get(sceneKey);
                    String sceneName = scene.key().asMinimalString();
                    int count = 0;
                    for (Entity entity : selector.values()) {
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
                }));
        commandManager.command(entityNode.literal("unset")
                .required(entityKey, MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .permission("bundlescenes.entity.set")
                .handler(context -> {
                    MultipleEntitySelector selector = context.get(entityKey);
                    int count = 0;
                    for (Entity entity : selector.values()) {
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
                }));
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
