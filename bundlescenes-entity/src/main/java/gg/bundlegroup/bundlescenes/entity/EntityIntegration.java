package gg.bundlegroup.bundlescenes.entity;

import gg.bundlegroup.bundlescenes.api.SceneManager;
import gg.bundlegroup.bundlescenes.api.event.SceneClearEvent;
import gg.bundlegroup.bundlescenes.api.event.SceneCompleteEvent;
import gg.bundlegroup.bundlescenes.api.event.SceneHideEvent;
import gg.bundlegroup.bundlescenes.api.event.SceneShowEvent;
import gg.bundlegroup.bundlescenes.plugin.Integration;
import gg.bundlegroup.bundlescenes.plugin.ScenesPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.bukkit.parameters.EntitySelector;
import revxrsal.commands.node.ExecutionContext;

import java.util.*;

@SuppressWarnings("deprecation")
public class EntityIntegration implements Integration, Listener {
    private final ScenesPlugin plugin;
    private final NamespacedKey key;
    private final Map<String, Set<Entity>> sceneEntities = new HashMap<>();
    private final Map<Entity, String> entityScenes = new WeakHashMap<>();
    private final Set<EntityType> deniedTypes = Set.of(EntityType.PLAYER);

    public EntityIntegration(ScenesPlugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "scene");
    }

    @Override
    public void load() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void enable() {
        try {
            plugin.getServer().getPluginManager().registerEvent(
                    (Class<? extends EntityEvent>) Class.forName("com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent"),
                    this,
                    EventPriority.NORMAL,
                    (listener, event) -> {
                        if (event instanceof EntityEvent entityEvent) {
                            removeEntity(entityEvent.getEntity());
                        }
                    },
                    plugin
            );
        } catch (ClassNotFoundException ignored) {
            // Not a Paper server, cannot catch all entity removals
            // Entities are only stored in weak collections and validity is checked before hiding
            // This should be enough to prevent memory leaks
        }

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                updateEntity(entity);
            }
        }
    }

    @Override
    public void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Entity entity : entityScenes.keySet()) {
                player.showEntity(plugin, entity);
            }
        }
        sceneEntities.clear();
        entityScenes.clear();
    }

    private void updateEntity(Entity entity) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        String scene = container.get(key, PersistentDataType.STRING);
        if (scene == null) {
            removeEntity(entity);
            return;
        }
        String old = entityScenes.put(entity, scene);
        if (scene.equals(old)) {
            return;
        }
        if (old != null) {
            Set<Entity> entities = sceneEntities.get(old);
            entities.remove(entity);
            if (entities.isEmpty()) {
                sceneEntities.remove(old);
            }
        }
        sceneEntities.computeIfAbsent(scene, s -> Collections.newSetFromMap(new WeakHashMap<>())).add(entity);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (SceneManager.get().isVisible(player, scene)) {
                player.showEntity(plugin, entity);
            } else {
                player.hideEntity(plugin, entity);
            }
        }
    }

    private void removeEntity(Entity entity) {
        String scene = entityScenes.remove(entity);
        if (scene != null) {
            Set<Entity> entities = sceneEntities.get(scene);
            entities.remove(entity);
            if (entities.isEmpty()) {
                sceneEntities.remove(scene);
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showEntity(plugin, entity);
            }
        }
    }

    @EventHandler
    public void onLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            updateEntity(entity);
        }
    }

    @EventHandler
    public void onUnload(EntitiesUnloadEvent event) {
        for (Entity entity : event.getEntities()) {
            removeEntity(entity);
        }
    }

    @EventHandler
    public void onUnload(EntityDeathEvent event) {
        removeEntity(event.getEntity());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (Map.Entry<Entity, String> entry : entityScenes.entrySet()) {
            if (!SceneManager.get().isVisible(player, entry.getValue())) {
                player.hideEntity(plugin, entry.getKey());
            }
        }
    }

    @EventHandler
    public void onShow(SceneShowEvent event) {
        Set<Entity> entities = sceneEntities.get(event.getScene());
        if (entities != null) {
            Player player = event.getPlayer();
            for (Entity entity : entities) {
                player.showEntity(plugin, entity);
            }
        }
    }

    @EventHandler
    public void onHide(SceneHideEvent event) {
        Set<Entity> entities = sceneEntities.get(event.getScene());
        if (entities != null) {
            Player player = event.getPlayer();
            for (Entity entity : entities) {
                if (entity.isValid()) {
                    player.hideEntity(plugin, entity);
                }
            }
        }
    }

    @EventHandler
    public void onComplete(SceneCompleteEvent event) {
        List<String> list;
        if (event.isAsynchronous()) {
            try {
                list = plugin.getServer().getScheduler().callSyncMethod(plugin, () -> List.copyOf(sceneEntities.keySet())).get();
            } catch (Exception e) {
                list = List.of();
            }
        } else {
            list = List.copyOf(sceneEntities.keySet());
        }
        event.addScenes(list);
    }

    @EventHandler
    public void onClear(SceneClearEvent event) {
        Set<Entity> entities = sceneEntities.remove(event.getScene());
        if (entities == null) {
            return;
        }
        for (Entity entity : entities) {
            entityScenes.remove(entity);
            entity.getPersistentDataContainer().remove(key);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Entity entity : entities) {
                player.showEntity(plugin, entity);
            }
        }
    }

    @Command("scenes assign")
    @CommandPermission("scenes.assign.entity")
    public void assign(BukkitCommandActor sender,

                       @SuggestWith(ScenesList.class) String scene,
                        EntitySelector<Entity> entities
                       ) {
        int count = 0;
        for (Entity entity : entities) {
            if (deniedTypes.contains(entity.getType())) {
                continue;
            }
            PersistentDataContainer container = entity.getPersistentDataContainer();
            if (scene != null) {
                container.set(key, PersistentDataType.STRING, scene);
            } else {
                container.remove(key);
            }
            updateEntity(entity);
            count++;
        }
        if (scene != null) {
            plugin.getAudiences().sender(sender.asPlayer()).sendMessage(Component.text()
                    .content("Assigned ")
                    .append(Component.text(count, NamedTextColor.YELLOW))
                    .append(Component.text(" entities to scene "))
                    .append(Component.text(scene, NamedTextColor.YELLOW))
                    .color(NamedTextColor.GREEN));
        } else {
            plugin.getAudiences().sender(sender.asPlayer()).sendMessage(Component.text()
                    .content("Removed ")
                    .append(Component.text(count, NamedTextColor.YELLOW))
                    .append(Component.text(" entities from their scene"))
                    .color(NamedTextColor.GREEN));
        }
    }

    @Command("scenes showall")
    @CommandPermission("scenes.show.all")
    public void showAll(Player sender) {
        // Show all entities, even if they should be hidden
        for (Entity entity : entityScenes.keySet()) {
            sender.showEntity(plugin, entity);
        }
        plugin.getAudiences().sender(sender).sendMessage(
                Component.text("Showing all entities", NamedTextColor.GREEN));
    }

    @Command("scenes hideall")
    @CommandPermission("scenes.hide.all")
    public void hideAll(Player sender) {
        // Hide all entities which are supposed to be hidden
        for (Map.Entry<String, Set<Entity>> entry : sceneEntities.entrySet()) {
            if (!SceneManager.get().isVisible(sender, entry.getKey())) {
                for (Entity entity : entry.getValue()) {
                    sender.hideEntity(plugin, entity);
                }
            }
        }
        plugin.getAudiences().sender(sender).sendMessage(
                Component.text("No longer showing all entities", NamedTextColor.GREEN));
    }

    private static final class ScenesList implements SuggestionProvider<BukkitCommandActor> {
        @Override
        public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
            Set<String> scenes = new HashSet<>();
            Bukkit.getPluginManager().callEvent(new SceneCompleteEvent(scenes));
            return List.copyOf(scenes);
        }
    }
}
