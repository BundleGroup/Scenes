package gg.bundlegroup.scenes.command.tag.entity;

import gg.bundlegroup.scenes.command.tag.TagCommand;
import gg.bundlegroup.scenes.plugin.Main;
import gg.bundlegroup.scenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Entity;

public class EntityTagAddCommand extends EntityTagCommand {
    public EntityTagAddCommand(MainHolder mainHolder) {
        super(mainHolder);
    }

    @Override
    protected int execute(CommandSourceStack source, Iterable<Entity> entities, Main main, String tag) {
        return TagCommand.executeAdd(source, entities, main, tag);
    }
}
