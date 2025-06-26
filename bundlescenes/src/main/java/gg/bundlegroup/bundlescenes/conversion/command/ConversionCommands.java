package gg.bundlegroup.bundlescenes.conversion.command;

import gg.bundlegroup.bundlescenes.Main;
import gg.bundlegroup.bundlescenes.MessageStyle;
import gg.bundlegroup.bundlescenes.conversion.EntityConversionManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandFactory;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.paper.util.sender.Source;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.kyori.adventure.text.Component.text;
import static org.incendo.cloud.key.CloudKey.cloudKey;

@NullMarked
public class ConversionCommands implements CommandFactory<Source> {
    @Override
    public List<Command<? extends Source>> createCommands(CommandManager<Source> commandManager) {
        Command.Builder<Source> root = commandManager.commandBuilder("scenes")
                .literal("conversion")
                .permission("bundlescenes.command.conversion");
        CloudKey<MultipleEntitySelector> entityKey = cloudKey("entity", MultipleEntitySelector.class);
        List<Command<? extends Source>> commands = new ArrayList<>();
        commands.add(root.literal("convert")
                .required(entityKey, MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .handler(context -> {
                    EntityConversionManager entityConversionManager = context.get(EntityConversionManager.KEY);
                    MultipleEntitySelector entitySelector = context.get(entityKey);

                    Map<Chunk, List<Entity>> entitiesByChunk = new HashMap<>();
                    for (Entity entity : entitySelector.values()) {
                        entitiesByChunk.computeIfAbsent(entity.getChunk(), c -> new ArrayList<>())
                                .add(entity);
                    }

                    int count = 0;
                    for (Map.Entry<Chunk, List<Entity>> entry : entitiesByChunk.entrySet()) {
                        count += entityConversionManager.getConversion()
                                .convertEntities(entry.getKey(), entry.getValue());
                    }

                    context.sender().source().sendMessage(text("Converted ", MessageStyle.SUCCESS)
                            .append(text(count, MessageStyle.SUCCESS_ACCENT))
                            .append(text(" entities")));
                })
                .build());
        commands.add(root.literal("restore")
                .handler(context -> {
                    EntityConversionManager entityConversionManager = context.get(EntityConversionManager.KEY);
                    entityConversionManager.setAutoConvert(false);

                    int count = 0;
                    for (World world : Bukkit.getServer().getWorlds()) {
                        for (Chunk chunk : world.getLoadedChunks()) {
                            count += entityConversionManager.getConversion().restoreEntities(chunk);
                        }
                    }

                    context.sender().source().sendMessage(text("Restored ", MessageStyle.SUCCESS)
                            .append(text(count, MessageStyle.SUCCESS_ACCENT))
                            .append(text(" entities")));
                })
                .build());
        if (Main.isAutoConvertEnabled()) {
            commands.add(root.literal("enable")
                    .handler(context -> {
                        EntityConversionManager entityConversionManager = context.get(EntityConversionManager.KEY);
                        if (entityConversionManager.isAutoConvert()) {
                            context.sender().source().sendMessage(text("Automatic entity conversion is already enabled", MessageStyle.ERROR));
                            return;
                        }
                        entityConversionManager.setAutoConvert(true);
                        context.sender().source().sendMessage(text("Enabled automatic entity conversion", MessageStyle.SUCCESS));
                    })
                    .build());
            commands.add(root.literal("disable")
                    .handler(context -> {
                        EntityConversionManager entityConversionManager = context.get(EntityConversionManager.KEY);
                        if (!entityConversionManager.isAutoConvert()) {
                            context.sender().source().sendMessage(text("Automatic entity conversion is already disabled", MessageStyle.ERROR));
                            return;
                        }
                        entityConversionManager.setAutoConvert(false);
                        context.sender().source().sendMessage(text("Disabled automatic entity conversion", MessageStyle.SUCCESS));
                    })
                    .build());
        }
        return commands;
    }
}
