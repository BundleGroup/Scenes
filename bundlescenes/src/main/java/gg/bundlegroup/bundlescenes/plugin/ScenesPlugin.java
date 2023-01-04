package gg.bundlegroup.bundlescenes.plugin;

import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import gg.bundlegroup.bundlescenes.api.SceneManagerProvider;
import gg.bundlegroup.bundlescenes.api.event.SceneClearEvent;
import gg.bundlegroup.bundlescenes.api.event.SceneCompleteEvent;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;

public class ScenesPlugin extends JavaPlugin {
    private final Map<String, Integration> integrations = new HashMap<>();
    private BukkitAudiences audiences;
    private PaperCommandManager<CommandSender> commandManager;
    private MinecraftHelp<CommandSender> minecraftHelp;
    private SceneManagerImpl sceneManager;
    private SceneControllerImpl manualController;

    @Override
    public void onLoad() {
        for (IntegrationProvider provider : ServiceLoader.load(IntegrationProvider.class, getClassLoader())) {
            if (provider.available()) {
                String name = provider.name();
                getLogger().info("Loading " + name + " integration");
                try {
                    Integration integration = provider.create(this);
                    integration.load();
                    Integration old = integrations.putIfAbsent(name, integration);
                    if (old != null) {
                        throw new IllegalArgumentException("Duplicate integration name: " + name);
                    }
                } catch (Throwable t) {
                    getLogger().log(Level.SEVERE, "Failed to load " + name + " integration", t);
                }
            }
        }
    }

    @Override
    public void onEnable() {
        audiences = BukkitAudiences.create(this);

        try {
            commandManager = new PaperCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            commandManager.registerBrigadier();
        }

        new MinecraftExceptionHandler<CommandSender>()
                .withDefaultHandlers()
                .apply(commandManager, audiences::sender);

        minecraftHelp = new MinecraftHelp<>("/scenes help", audiences::sender, commandManager);

        AnnotationParser<CommandSender> parser = new AnnotationParser<>(
                commandManager,
                CommandSender.class,
                p -> CommandMeta.simple().build()
        );
        parser.parse(this);

        sceneManager = new SceneManagerImpl();
        getServer().getPluginManager().registerEvents(sceneManager, this);
        SceneManagerProvider.set(sceneManager);

        manualController = sceneManager.createController(this, "manual visibility");

        for (Map.Entry<String, Integration> entry : integrations.entrySet()) {
            String name = entry.getKey();
            Integration integration = entry.getValue();
            getLogger().info("Enabling " + name + " integration");
            try {
                integration.enable();
                parser.parse(integration);
                getServer().getPluginManager().registerEvents(integration, this);
            } catch (Throwable t) {
                getLogger().log(Level.SEVERE, "Failed to enable " + name + " integration", t);
            }
        }
    }

    @Override
    public void onDisable() {
        for (Map.Entry<String, Integration> entry : integrations.entrySet()) {
            String name = entry.getKey();
            Integration integration = entry.getValue();
            try {
                integration.disable();
            } catch (Throwable t) {
                getLogger().log(Level.SEVERE, "Failed to disable " + name + " integration", t);
            }
        }
        integrations.clear();
    }

    @CommandMethod("scenes show <scene>")
    @CommandPermission("scenes.show")
    public void show(Player sender, @Argument(value = "scene", suggestions = "scene") String scene) {
        manualController.show(sender, scene);
        TextComponent.Builder message = Component.text()
                .content("Manually showing scene ")
                .append(Component.text(scene, NamedTextColor.YELLOW))
                .color(NamedTextColor.GREEN);
        if (sender.hasPermission("scenes.hide")) {
            message.append(Component.text(" (click to hide)"))
                    .clickEvent(ClickEvent.runCommand("/scenes hide " + scene));
        }
        audiences.sender(sender).sendMessage(message);
    }

    @CommandMethod("scenes hide <scene>")
    @CommandPermission("scenes.hide")
    public void hide(Player sender, @Argument(value = "scene", suggestions = "manual_scene") String scene) {
        manualController.hide(sender, scene);
        audiences.sender(sender).sendMessage(Component.text()
                .content("No longer manually showing scene ")
                .append(Component.text(scene, NamedTextColor.YELLOW))
                .color(NamedTextColor.GREEN));

        Set<SceneControllerImpl> controllers = sceneManager.getVisibilityReason(sender, scene);
        for (SceneControllerImpl controller : controllers) {
            String name = controller.name();
            TextComponent.Builder message = Component.text()
                    .content("Plugin ")
                    .append(Component.text(controller.plugin().getName(), NamedTextColor.YELLOW))
                    .append(Component.text(" is still showing it"))
                    .color(NamedTextColor.GRAY);
            if (name != null) {
                message.append(Component.text(": " + name));
            }
            audiences.sender(sender).sendMessage(message);
        }
    }

    @CommandMethod("scenes clear <scene>")
    @CommandPermission("scenes.clear")
    public void clear(CommandSender sender, @Argument(value = "scene", suggestions = "scene") String scene) {
        Bukkit.getPluginManager().callEvent(new SceneClearEvent(scene));
        audiences.sender(sender).sendMessage(Component.text()
                .content("Removed everything from scene ")
                .append(Component.text(scene, NamedTextColor.YELLOW))
                .color(NamedTextColor.GREEN));
    }

    @Suggestions("scene")
    public List<String> suggestScenes(CommandContext<CommandSender> context, String input) {
        Set<String> scenes = new HashSet<>();
        Bukkit.getPluginManager().callEvent(new SceneCompleteEvent(scenes));
        return List.copyOf(scenes);
    }

    @Suggestions("manual_scene")
    public List<String> suggestManualScenes(CommandContext<CommandSender> context, String input) {
        if (context.getSender() instanceof Player player) {
            return manualController.getShown(player);
        }
        return List.of();
    }

    @CommandMethod("scenes help [query]")
    @CommandPermission("scenes.help")
    public void help(CommandSender sender,
                     @Argument(value = "query", suggestions = "help_queries") @Greedy String query) {
        minecraftHelp.queryCommands(query != null ? query : "", sender);
    }

    @Suggestions("help_queries")
    public List<String> suggestHelpQueries(CommandContext<CommandSender> context, String input) {
        return commandManager.createCommandHelpHandler().queryRootIndex(context.getSender()).getEntries().stream().map(CommandHelpHandler.VerboseHelpEntry::getSyntaxString).toList();
    }

    public BukkitAudiences getAudiences() {
        return audiences;
    }

    public PaperCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }
}
