package gg.bundlegroup.scenes.command.tag;

import com.mojang.brigadier.Command;
import gg.bundlegroup.scenes.MessageStyle;
import gg.bundlegroup.scenes.api.Scenes;
import gg.bundlegroup.scenes.plugin.Main;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;

public abstract class TagCommand implements Command<CommandSourceStack> {
    protected abstract int execute(CommandSourceStack source, Iterable<Entity> entities, Main main, String tag);

    public static int executeAdd(CommandSourceStack source, Iterable<Entity> entities, Main main, String tag) {
        Scenes scenes = main.getScenes();
        int count = 0;
        int totalCount = 0;
        for (Entity entity : entities) {
            if (entity instanceof Player) {
                continue;
            }
            if (scenes.addEntityTag(entity, tag)) {
                count++;
            }
            totalCount++;
        }
        if (count > 0) {
            source.getSender().sendMessage(text()
                    .append(text("Added tag "))
                    .append(text(tag, MessageStyle.SUCCESS_ACCENT))
                    .append(text(" to "))
                    .append(text(count, MessageStyle.SUCCESS_ACCENT))
                    .append(text(" " + (count == 1 ? "entity" : "entities")))
                    .style(MessageStyle.SUCCESS)
                    .build());
        } else if (totalCount > 0) {
            source.getSender().sendMessage(text()
                    .append(text("Tag "))
                    .append(text(tag, MessageStyle.ERROR_ACCENT))
                    .append(text(" is already present"))
                    .style(MessageStyle.ERROR)
                    .build());
        } else {
            source.getSender().sendMessage(text("No entities found", MessageStyle.ERROR));
        }
        return count;
    }

    public static int executeRemove(CommandSourceStack source, Iterable<Entity> entities, Main main, String tag) {
        Scenes scenes = main.getScenes();
        int count = 0;
        for (Entity entity : entities) {
            if (scenes.removeEntityTag(entity, tag)) {
                count++;
            }
        }
        if (count > 0) {
            source.getSender().sendMessage(text()
                    .append(text("Removed tag "))
                    .append(text(tag, MessageStyle.SUCCESS_ACCENT))
                    .append(text(" from "))
                    .append(text(count, MessageStyle.SUCCESS_ACCENT))
                    .append(text(" " + (count == 1 ? "entity" : "entities")))
                    .style(MessageStyle.SUCCESS)
                    .build());
        } else {
            source.getSender().sendMessage(text()
                    .append(text("Tag "))
                    .append(text(tag, MessageStyle.ERROR_ACCENT))
                    .append(text(" is not present"))
                    .style(MessageStyle.ERROR)
                    .build());
        }
        return count;
    }
}
