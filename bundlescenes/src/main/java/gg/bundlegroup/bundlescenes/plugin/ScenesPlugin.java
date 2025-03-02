package gg.bundlegroup.bundlescenes.plugin;

//import cloud.commandframework.CommandHelpHandler;
//import cloud.commandframework.annotations.AnnotationParser;
//import cloud.commandframework.annotations.Argument;
//import cloud.commandframework.annotations.CommandMethod;
//import cloud.commandframework.annotations.CommandPermission;
//import cloud.commandframework.annotations.specifier.Greedy;
//import cloud.commandframework.annotations.suggestions.Suggestions;
//import cloud.commandframework.bukkit.CloudBukkitCapabilities;
//import cloud.commandframework.context.CommandContext;
//import cloud.commandframework.execution.CommandExecutionCoordinator;
//import cloud.commandframework.meta.CommandMeta;
//import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
//import cloud.commandframework.minecraft.extras.MinecraftHelp;
//import cloud.commandframework.paper.PaperCommandManager;
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
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.Lamp;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;

public class ScenesPlugin extends JavaPlugin {
    private final Map<String, Integration> integrations = new HashMap<>();
    private BukkitAudiences audiences;

    private Lamp<BukkitCommandActor> lamp;

//    private PaperCommandManager<CommandSender> commandManager;
//    private MinecraftHelp<CommandSender> minecraftHelp;
    private SceneManagerImpl sceneManager;
    private static SceneControllerImpl manualController;

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
            lamp = BukkitLamp.builder(this).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
//            commandManager.registerBrigadier();
//        }

//        new MinecraftExceptionHandler<CommandSender>()
//                .withDefaultHandlers()
//                .apply(commandManager, audiences::sender);
//
//        minecraftHelp = new MinecraftHelp<>("/scenes help", audiences::sender, commandManager);
//
//        AnnotationParser<CommandSender> parser = new AnnotationParser<>(
//                commandManager,
//                CommandSender.class,
//                p -> CommandMeta.simple().build()
//        );
//        parser.parse(this);
//
        lamp.register(new Commands());

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
                lamp.register(integration);
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

    public class Commands {


        @Command("scenes show")
        @CommandPermission("scenes.show")
        public void show(BukkitCommandActor sender, @SuggestWith(ScenesList.class) String scene) {
            manualController.show(sender.asPlayer(), scene);
            TextComponent.Builder message = Component.text()
                    .content("Manually showing scene ")
                    .append(Component.text(scene, NamedTextColor.YELLOW))
                    .color(NamedTextColor.GREEN);
            if (sender.asPlayer().hasPermission("scenes.hide")) {
                message.append(Component.text(" (click to hide)"))
                        .clickEvent(ClickEvent.runCommand("/scenes hide " + scene));
            }
            audiences.sender(sender.asPlayer()).sendMessage(message);
        }

        @Command("scenes hide")
        @CommandPermission("scenes.hide")
        public void hide(BukkitCommandActor sender,
                         @SuggestWith(ScenesList.class) String scene) {
            manualController.hide(sender.asPlayer(), scene);
            audiences.sender(sender.asPlayer()).sendMessage(Component.text()
                    .content("No longer manually showing scene ")
                    .append(Component.text(scene, NamedTextColor.YELLOW))
                    .color(NamedTextColor.GREEN));

            Set<SceneControllerImpl> controllers = sceneManager.getVisibilityReason(sender.asPlayer(), scene);
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
                audiences.sender(sender.asPlayer()).sendMessage(message);
            }
        }

        @Command("scenes clear")
        @CommandPermission("scenes.clear")
        public void clear(BukkitCommandActor sender, @SuggestWith(ScenesList.class) String scene) {
            Bukkit.getPluginManager().callEvent(new SceneClearEvent(scene));
            audiences.sender(sender.asPlayer()).sendMessage(Component.text()
                    .content("Removed everything from scene ")
                    .append(Component.text(scene, NamedTextColor.YELLOW))
                    .color(NamedTextColor.GREEN));
        }

        private static final class ScenesList implements SuggestionProvider<BukkitCommandActor> {
            @Override
            public @NotNull List<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
                Set<String> scenes = new HashSet<>();
                Bukkit.getPluginManager().callEvent(new SceneCompleteEvent(scenes));
                return List.copyOf(scenes);
            }
        }

        private static final class ManualScenesList implements SuggestionProvider<BukkitCommandActor> {
            @Override
            public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> executionContext) {
                if (executionContext.actor() instanceof Player) {
                    return manualController.getShown(executionContext.actor().asPlayer());
                }
                return List.of();
            }
        }
    }

    public BukkitAudiences getAudiences() {
        return audiences;
    }

    public Lamp<BukkitCommandActor> getCommandManager() {
        return lamp;
    }
}
