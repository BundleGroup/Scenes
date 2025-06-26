package gg.bundlegroup.bundlescenes.worldguard.command;

import gg.bundlegroup.bundlescenes.MessageStyle;
import gg.bundlegroup.bundlescenes.conversion.EntityConversion;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandFactory;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.paper.util.sender.Source;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

import static gg.bundlegroup.bundlescenes.worldguard.command.parser.SceneArgumentParser.sceneArgumentParser;
import static net.kyori.adventure.text.Component.text;
import static org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser.multipleEntitySelectorParser;
import static org.incendo.cloud.key.CloudKey.cloudKey;

@NullMarked
public class AssignmentCommands implements CommandFactory<Source> {
    @Override
    public List<Command<? extends Source>> createCommands(CommandManager<Source> commandManager) {
        Command.Builder<Source> root = commandManager.commandBuilder("scenes")
                .literal("assignment")
                .permission("bundlescenes.command.assignment");
        CloudKey<MultipleEntitySelector> entityKey = cloudKey("entity", MultipleEntitySelector.class);
        CloudKey<String> sceneKey = cloudKey("scene", String.class);
        return List.of(
                root.literal("set")
                        .required(entityKey, multipleEntitySelectorParser())
                        .required(sceneKey, sceneArgumentParser())
                        .handler(context -> {
                            MultipleEntitySelector selector = context.get(entityKey);
                            String scene = context.get(sceneKey);
                            int count = 0;
                            for (Entity entity : selector.values()) {
                                PersistentDataContainer pdc = entity.getPersistentDataContainer();
                                String previous = pdc.get(EntityConversion.SCENE_KEY, PersistentDataType.STRING);
                                if (!Objects.equals(previous, scene)) {
                                    pdc.set(EntityConversion.SCENE_KEY, PersistentDataType.STRING, scene);
                                    count++;
                                }
                            }
                            context.sender().source().sendMessage(text()
                                    .append(text("Added "))
                                    .append(text(count, MessageStyle.SUCCESS_ACCENT))
                                    .append(text(" entities to scene "))
                                    .append(text(scene, MessageStyle.SUCCESS_ACCENT))
                                    .style(MessageStyle.SUCCESS));
                        })
                        .build(),
                root.literal("reset")
                        .required(entityKey, multipleEntitySelectorParser())
                        .handler(context -> {
                            MultipleEntitySelector selector = context.get(entityKey);
                            int count = 0;
                            for (Entity entity : selector.values()) {
                                PersistentDataContainer pdc = entity.getPersistentDataContainer();
                                if (pdc.has(EntityConversion.SCENE_KEY)) {
                                    pdc.remove(EntityConversion.SCENE_KEY);
                                    count++;
                                }
                            }
                            context.sender().source().sendMessage(text()
                                    .append(text("Removed "))
                                    .append(text(count, MessageStyle.SUCCESS_ACCENT))
                                    .append(text(" entities from their scenes"))
                                    .style(MessageStyle.SUCCESS));
                        })
                        .build()
        );
    }
}
