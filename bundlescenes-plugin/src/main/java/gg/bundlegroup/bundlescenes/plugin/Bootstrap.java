package gg.bundlegroup.bundlescenes.plugin;

import com.mojang.brigadier.arguments.StringArgumentType;
import gg.bundlegroup.bundlescenes.command.EntityTagAddCommand;
import gg.bundlegroup.bundlescenes.command.EntityTagRemoveCommand;
import gg.bundlegroup.bundlescenes.command.HideCommand;
import gg.bundlegroup.bundlescenes.command.ShowCommand;
import gg.bundlegroup.bundlescenes.command.suggestion.EntityTagSuggestionProvider;
import gg.bundlegroup.bundlescenes.command.suggestion.HideTagSuggestionProvider;
import gg.bundlegroup.bundlescenes.command.suggestion.ShowTagSuggestionProvider;
import gg.bundlegroup.bundlescenes.command.suggestion.TagSuggestionProvider;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableApiUsage")
public class Bootstrap implements PluginBootstrap {
    private final MainHolder mainHolder = new MainHolder();

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> registerCommands(event.registrar()));
    }

    private void registerCommands(Commands registrar) {
        registrar.register(Commands.literal("scenes")
                .then(Commands.literal("show")
                        .then(Commands.argument("tag", StringArgumentType.string())
                                .requires(sender -> sender.getSender().hasPermission("bundlescenes.manual"))
                                .suggests(new ShowTagSuggestionProvider(mainHolder))
                                .executes(new ShowCommand(mainHolder))))
                .then(Commands.literal("hide")
                        .then(Commands.argument("tag", StringArgumentType.string())
                                .requires(sender -> sender.getSender().hasPermission("bundlescenes.manual"))
                                .suggests(new HideTagSuggestionProvider(mainHolder))
                                .executes(new HideCommand(mainHolder))))
                .then(Commands.literal("entity")
                        .then(Commands.argument("entity", ArgumentTypes.entities())
                                .then(Commands.literal("tag")
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("tag", StringArgumentType.string())
                                                        .requires(sender -> sender.getSender().hasPermission("bundlescenes.assign.entity"))
                                                        .suggests(new TagSuggestionProvider(mainHolder))
                                                        .executes(new EntityTagAddCommand(mainHolder))))
                                        .then(Commands.literal("remove")
                                                .then(Commands.argument("tag", StringArgumentType.string())
                                                        .requires(sender -> sender.getSender().hasPermission("bundlescenes.assign.entity"))
                                                        .suggests(new EntityTagSuggestionProvider(mainHolder))
                                                        .executes(new EntityTagRemoveCommand(mainHolder)))))))
                .build());
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        Main main = new Main();
        mainHolder.setMain(main);
        return main;
    }
}
