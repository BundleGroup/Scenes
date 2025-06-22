package gg.bundlegroup.bundlescenes.conversion.command;

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
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static org.incendo.cloud.key.CloudKey.cloudKey;

@NullMarked
public class ConversionCommands implements CommandFactory<Source> {
    private final EntityConversionManager entityConversionManager;

    public ConversionCommands(EntityConversionManager entityConversionManager) {
        this.entityConversionManager = entityConversionManager;
    }

    @Override
    public List<Command<? extends Source>> createCommands(CommandManager<Source> commandManager) {
        Command.Builder<Source> root = commandManager.commandBuilder("scenes")
                .literal("conversion")
                .permission("bundlescenes.command.conversion");
        CloudKey<MultipleEntitySelector> entityKey = cloudKey("entity", MultipleEntitySelector.class);
        return List.of(
                root.literal("enable")
                        .handler(context -> {
                            if (entityConversionManager.isAutoConvert()) {
                                context.sender().source().sendMessage(text("Automatic entity conversion is already enabled", RED));
                                return;
                            }
                            entityConversionManager.setAutoConvert(true);
                            context.sender().source().sendMessage(text("Enabled automatic entity conversion", GREEN));
                        })
                        .build(),
                root.literal("disable")
                        .handler(context -> {
                            if (!entityConversionManager.isAutoConvert()) {
                                context.sender().source().sendMessage(text("Automatic entity conversion is already disabled", RED));
                                return;
                            }
                            entityConversionManager.setAutoConvert(false);
                            context.sender().source().sendMessage(text("Disabled automatic entity conversion", GREEN));
                        })
                        .build(),
                root.literal("convert")
                        .required(entityKey, MultipleEntitySelectorParser.multipleEntitySelectorParser())
                        .handler(context -> {
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

                            context.sender().source().sendMessage(text("Converted ", GREEN)
                                    .append(text(count, DARK_GREEN))
                                    .append(text(" entities")));
                        })
                        .build(),
                root.literal("restore")
                        .handler(context -> {
                            int count = 0;
                            for (World world : Bukkit.getServer().getWorlds()) {
                                for (Chunk chunk : world.getLoadedChunks()) {
                                    count += entityConversionManager.getConversion().restoreEntities(chunk);
                                }
                            }

                            context.sender().source().sendMessage(text("Restored ", GREEN)
                                    .append(text(count, DARK_GREEN))
                                    .append(text(" entities")));
                        })
                        .build());
    }
}
