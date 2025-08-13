package gg.bundlegroup.bundlescenes.plugin;

import gg.bundlegroup.bundlescenes.BundleScenesImpl;
import gg.bundlegroup.bundlescenes.MessageStyle;
import gg.bundlegroup.bundlescenes.api.BundleScenesProvider;
import gg.bundlegroup.bundlescenes.api.controller.Controller;
import gg.bundlegroup.bundlescenes.api.scene.Scene;
import gg.bundlegroup.bundlescenes.chunk.PlayerChunkTrackerListener;
import gg.bundlegroup.bundlescenes.command.argument.SceneParser;
import gg.bundlegroup.bundlescenes.command.processor.BundleScenesInjector;
import gg.bundlegroup.bundlescenes.controller.ControllerManagerListener;
import gg.bundlegroup.bundlescenes.controller.PlayerSceneTracker;
import gg.bundlegroup.bundlescenes.controller.PlayerSceneTrackerListener;
import gg.bundlegroup.bundlescenes.controller.PluginController;
import gg.bundlegroup.bundlescenes.entity.EntitySupport;
import gg.bundlegroup.bundlescenes.plugin.worldguard.WorldGuardSupport;
import io.leangen.geantyref.TypeToken;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.parser.standard.BooleanParser;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

import static gg.bundlegroup.bundlescenes.command.argument.SceneParser.*;
import static net.kyori.adventure.text.Component.text;
import static org.incendo.cloud.key.CloudKey.cloudKey;

@NullMarked
public class Main extends JavaPlugin {
    private @Nullable BundleScenesImpl scenes;
    private @Nullable Controller viewRangeChunkController;
    private @Nullable Controller manualController;

    private @Nullable WorldGuardSupport worldGuardSupport;
    private @Nullable EntitySupport entitySupport;

    @Override
    public void onLoad() {
        scenes = new BundleScenesImpl();
        BundleScenesProvider.setInstance(scenes);

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuardSupport = new WorldGuardSupport(this, scenes);
            worldGuardSupport.load();
        }

        entitySupport = new EntitySupport(this, scenes);
        entitySupport.load();
    }

    @Override
    public void onEnable() {
        if (scenes == null) {
            return;
        }

        viewRangeChunkController = scenes.createController(this, Key.key("bundlescenes", "chunks/view-range"));
        manualController = scenes.createController(this, Key.key("bundlescenes", "manual"));

        CommandManager<Source> commandManager = createCommandManager();
        commandManager.registerCommandPreProcessor(new BundleScenesInjector<>(scenes));

        Command.Builder<Source> rootCommand = commandManager.commandBuilder("scenes");
        registerCommands(commandManager, rootCommand);

        if (worldGuardSupport != null) {
            getComponentLogger().info("Enabling WorldGuard integration");
            worldGuardSupport.enable();
            worldGuardSupport.registerCommands(commandManager, rootCommand);
        }

        if (entitySupport != null) {
            getComponentLogger().info("Enabling entity integration");
            entitySupport.enable();
            entitySupport.registerCommands(commandManager, rootCommand);
        }

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerChunkTrackerListener(viewRangeChunkController, scenes), this);
        pluginManager.registerEvents(new ControllerManagerListener(scenes.getControllerManager()), this);
        pluginManager.registerEvents(new PlayerSceneTrackerListener(scenes.getPlayerSceneTrackerManager()), this);

        BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.runTaskTimer(this, scenes.getSceneManager()::removeQueuedIfEmpty, 0, 1);
    }

    private void registerCommands(CommandManager<Source> commandManager, Command.Builder<Source> root) {
        Objects.requireNonNull(manualController);
        Command.Builder<Source> manualRoot = root.permission("bundlescenes.manual");

        CloudKey<Scene> sceneKey = cloudKey("scene", Scene.class);
        commandManager.command(manualRoot.literal("show")
                .senderType(PlayerSource.class)
                .required(sceneKey, hiddenSceneParser(manualController))
                .handler(context -> {
                    Objects.requireNonNull(manualController);
                    Player player = context.sender().source();
                    Scene scene = context.get(sceneKey);
                    manualController.showScene(player, scene);
                    player.sendMessage(text("Manually showing scene ", MessageStyle.SUCCESS)
                            .append(text(scene.key().asMinimalString(), MessageStyle.SUCCESS_ACCENT))
                            .append(text(".")));
                }));
        commandManager.command(manualRoot.literal("hide")
                .senderType(PlayerSource.class)
                .required(sceneKey, visibleSceneParser(manualController))
                .handler(context -> {
                    Objects.requireNonNull(manualController);
                    Player player = context.sender().source();
                    Scene scene = context.get(sceneKey);
                    manualController.hideScene(player, scene);
                    player.sendMessage(text("No longer manually showing scene ", MessageStyle.SUCCESS)
                            .append(text(scene.key().asMinimalString(), MessageStyle.SUCCESS_ACCENT))
                            .append(text(".")));
                }));
        commandManager.command(manualRoot.literal("hideall")
                .senderType(PlayerSource.class)
                .required("value", BooleanParser.booleanParser())
                .handler(context -> {
                    Objects.requireNonNull(scenes);
                    Player player = context.sender().source();
                    PlayerSceneTracker tracker = scenes.getPlayerSceneTrackerManager().getOrCreatePlayer(player);
                    boolean value = context.get("value");
                    tracker.setHideAll(value);
                    if (value) {
                        player.sendMessage(text("Forcefully hiding all scenes from you.", MessageStyle.SUCCESS));
                    } else {
                        player.sendMessage(text("No longer hiding all scenes from you.", MessageStyle.SUCCESS));
                    }
                }));
        commandManager.command(root.literal("status")
                .permission("bundlescenes.status")
                .senderType(PlayerSource.class)
                .required(sceneKey, sceneParser())
                .handler(context -> {
                    Objects.requireNonNull(scenes);
                    Player player = context.sender().source();
                    Scene scene = context.get(sceneKey);
                    PlayerSceneTracker tracker = scenes.getPlayerSceneTrackerManager().getOrCreatePlayer(player);
                    Set<PluginController> controllers = tracker.getControllersShowingScene(scene);
                    if (controllers.isEmpty()) {
                        player.sendMessage(text("Scene ", MessageStyle.INFO)
                                .append(text(scene.key().asMinimalString(), MessageStyle.INFO_ACCENT))
                                .append(text(" is not visible.")));
                        return;
                    }
                    for (PluginController controller : controllers) {
                        player.sendMessage(text("Controller ", MessageStyle.INFO)
                                .append(text(controller.key().asMinimalString(), MessageStyle.INFO_ACCENT))
                                .append(text(" of plugin "))
                                .append(text(controller.getPlugin().getName(), MessageStyle.INFO_ACCENT))
                                .append(text(" is showing scene "))
                                .append(text(scene.key().asMinimalString(), MessageStyle.INFO_ACCENT))
                                .append(text(".")));
                    }
                }));
    }

    @SuppressWarnings("UnstableApiUsage")
    private CommandManager<Source> createCommandManager() {
        PaperCommandManager<Source> commandManager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(this);

        commandManager.brigadierManager().registerMapping(new TypeToken<SceneParser<Source>>() {
        }, builder -> builder.cloudSuggestions().toConstant(ArgumentTypes.namespacedKey()));
        commandManager.brigadierManager().registerMapping(new TypeToken<VisibleSceneParser<Source>>() {
        }, builder -> builder.cloudSuggestions().toConstant(ArgumentTypes.namespacedKey()));
        commandManager.brigadierManager().registerMapping(new TypeToken<HiddenSceneParser<Source>>() {
        }, builder -> builder.cloudSuggestions().toConstant(ArgumentTypes.namespacedKey()));

        return commandManager;
    }

    @Override
    public void onDisable() {
        if (scenes == null) {
            return;
        }

        if (manualController != null) {
            manualController.close();
            manualController = null;
        }

        if (viewRangeChunkController != null) {
            viewRangeChunkController.close();
            viewRangeChunkController = null;
        }

        if (worldGuardSupport != null) {
            worldGuardSupport.disable();
            worldGuardSupport = null;
        }

        if (entitySupport != null) {
            entitySupport.disable();
        }

        scenes.getControllerManager().controllers().forEach(controller -> {
            Plugin plugin = controller.getPlugin();
            getComponentLogger().warn("Plugin {} did not unregister controller {}", plugin.getName(), controller.key());
        });

        scenes.close();
        scenes = null;
        BundleScenesProvider.setInstance(null);
    }
}
