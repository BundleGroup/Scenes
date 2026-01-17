package gg.bundlegroup.bundlescenes.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import gg.bundlegroup.bundlescenes.MessageStyle;
import gg.bundlegroup.bundlescenes.api.BundleScenes;
import gg.bundlegroup.bundlescenes.plugin.Main;
import gg.bundlegroup.bundlescenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import org.bukkit.entity.Entity;

import static net.kyori.adventure.text.Component.text;

public class EntityTagRemoveCommand implements Command<CommandSourceStack> {
    private final MainHolder mainHolder;

    public EntityTagRemoveCommand(MainHolder mainHolder) {
        this.mainHolder = mainHolder;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Main main = mainHolder.getMain();
        BundleScenes scenes = main.getScenes();
        EntitySelectorArgumentResolver entityResolver = context.getArgument("entity", EntitySelectorArgumentResolver.class);
        String tag = context.getArgument("tag", String.class);
        int count = 0;
        for (Entity entity : entityResolver.resolve(context.getSource())) {
            if (scenes.removeEntityTag(entity, tag)) {
                count++;
            }
        }
        if (count > 0) {
            context.getSource().getSender().sendMessage(text()
                    .append(text("Removed tag "))
                    .append(text(tag, MessageStyle.SUCCESS_ACCENT))
                    .append(text(" from "))
                    .append(text(count, MessageStyle.SUCCESS_ACCENT))
                    .append(text(" " + (count == 1 ? "entity" : "entities")))
                    .style(MessageStyle.SUCCESS)
                    .build());
        } else {
            context.getSource().getSender().sendMessage(text()
                    .append(text("Tag "))
                    .append(text(tag, MessageStyle.ERROR_ACCENT))
                    .append(text(" is not present"))
                    .style(MessageStyle.ERROR)
                    .build());
        }
        return count;
    }
}
