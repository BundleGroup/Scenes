package gg.bundlegroup.scenes.plugin;

import com.mojang.brigadier.arguments.StringArgumentType;
import gg.bundlegroup.scenes.WorldEditAddon;
import gg.bundlegroup.scenes.command.HideCommand;
import gg.bundlegroup.scenes.command.ShowCommand;
import gg.bundlegroup.scenes.command.StatusCommand;
import gg.bundlegroup.scenes.command.suggestion.EntityTagSuggestionProvider;
import gg.bundlegroup.scenes.command.suggestion.HideTagSuggestionProvider;
import gg.bundlegroup.scenes.command.suggestion.ShowTagSuggestionProvider;
import gg.bundlegroup.scenes.command.suggestion.TagSuggestionProvider;
import gg.bundlegroup.scenes.command.tag.entity.EntityTagAddCommand;
import gg.bundlegroup.scenes.command.tag.entity.EntityTagRemoveCommand;
import gg.bundlegroup.scenes.command.tag.worldedit.WorldEditTagAddCommand;
import gg.bundlegroup.scenes.command.tag.worldedit.WorldEditTagRemoveCommand;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.entity.Player;
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
                .requires(sender -> mainHolder.hasMain()
                        && sender.getSender().hasPermission("scenes.use"))
                .then(Commands.literal("status")
                        .requires(sender -> sender.getSender().hasPermission("scenes.status"))
                        .executes(new StatusCommand(mainHolder)))
                .then(Commands.literal("show")
                        .requires(sender -> sender.getSender().hasPermission("scenes.manual"))
                        .then(Commands.argument("tag", StringArgumentType.string())
                                .suggests(new ShowTagSuggestionProvider(mainHolder))
                                .executes(new ShowCommand(mainHolder))))
                .then(Commands.literal("hide")
                        .requires(sender -> sender.getSender().hasPermission("scenes.manual"))
                        .then(Commands.argument("tag", StringArgumentType.string())
                                .suggests(new HideTagSuggestionProvider(mainHolder))
                                .executes(new HideCommand(mainHolder))))
                .then(Commands.literal("entity")
                        .requires(sender -> sender.getSender().hasPermission("scenes.assign.entity"))
                        .then(Commands.argument("entity", ArgumentTypes.entities())
                                .then(Commands.literal("tag")
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("tag", StringArgumentType.string())
                                                        .suggests(new TagSuggestionProvider(mainHolder))
                                                        .executes(new EntityTagAddCommand(mainHolder))))
                                        .then(Commands.literal("remove")
                                                .then(Commands.argument("tag", StringArgumentType.string())
                                                        .suggests(new EntityTagSuggestionProvider(mainHolder))
                                                        .executes(new EntityTagRemoveCommand(mainHolder)))))))
                .then(Commands.literal("worldedit")
                        .requires(sender -> mainHolder.getMain().findAddon(WorldEditAddon.class) != null
                                && sender.getSender() instanceof Player
                                && sender.getSender().hasPermission("scenes.assign.worldedit"))
                        .then(Commands.literal("tag")
                                .then(Commands.literal("add")
                                        .then(Commands.argument("tag", StringArgumentType.string())
                                                .suggests(new TagSuggestionProvider(mainHolder))
                                                .executes(new WorldEditTagAddCommand(mainHolder))))
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("tag", StringArgumentType.string())
                                                .suggests(new TagSuggestionProvider(mainHolder))
                                                .executes(new WorldEditTagRemoveCommand(mainHolder))))))
                .build());
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        Main main = new Main();
        mainHolder.setMain(main);
        return main;
    }
}
