package gg.bundlegroup.scenes.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import gg.bundlegroup.scenes.MessageStyle;
import gg.bundlegroup.scenes.api.Scenes;
import gg.bundlegroup.scenes.plugin.Main;
import gg.bundlegroup.scenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;

public class EntityTagAddCommand implements Command<CommandSourceStack> {
    private final MainHolder mainHolder;

    public EntityTagAddCommand(MainHolder mainHolder) {
        this.mainHolder = mainHolder;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Main main = mainHolder.getMain();
        Scenes scenes = main.getScenes();
        EntitySelectorArgumentResolver entityResolver = context.getArgument("entity", EntitySelectorArgumentResolver.class);
        String tag = context.getArgument("tag", String.class);
        int count = 0;
        int totalCount = 0;
        for (Entity entity : entityResolver.resolve(context.getSource())) {
            if (entity instanceof Player) {
                continue;
            }
            if (scenes.addEntityTag(entity, tag)) {
                count++;
            }
            totalCount++;
        }
        if (count > 0) {
            context.getSource().getSender().sendMessage(text()
                    .append(text("Added tag "))
                    .append(text(tag, MessageStyle.SUCCESS_ACCENT))
                    .append(text(" to "))
                    .append(text(count, MessageStyle.SUCCESS_ACCENT))
                    .append(text(" " + (count == 1 ? "entity" : "entities")))
                    .style(MessageStyle.SUCCESS)
                    .build());
        } else if (totalCount > 0) {
            context.getSource().getSender().sendMessage(text()
                    .append(text("Tag "))
                    .append(text(tag, MessageStyle.ERROR_ACCENT))
                    .append(text(" is already present"))
                    .style(MessageStyle.ERROR)
                    .build());
        } else {
            context.getSource().getSender().sendMessage(text("No entities found", MessageStyle.ERROR));
        }
        return count;
    }
}
