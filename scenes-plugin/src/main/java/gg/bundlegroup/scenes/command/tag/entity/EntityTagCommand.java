package gg.bundlegroup.scenes.command.tag.entity;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import gg.bundlegroup.scenes.command.tag.TagCommand;
import gg.bundlegroup.scenes.plugin.Main;
import gg.bundlegroup.scenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;

public abstract class EntityTagCommand extends TagCommand {
    private final MainHolder mainHolder;

    public EntityTagCommand(MainHolder mainHolder) {
        this.mainHolder = mainHolder;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Main main = mainHolder.getMain();
        EntitySelectorArgumentResolver entityResolver = context.getArgument("entity", EntitySelectorArgumentResolver.class);
        String tag = context.getArgument("tag", String.class);
        return execute(context.getSource(), entityResolver.resolve(context.getSource()), main, tag);
    }
}
