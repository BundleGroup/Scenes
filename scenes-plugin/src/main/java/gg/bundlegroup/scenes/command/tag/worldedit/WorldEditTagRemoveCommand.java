package gg.bundlegroup.scenes.command.tag.worldedit;

import gg.bundlegroup.scenes.plugin.Main;
import gg.bundlegroup.scenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Entity;

public class WorldEditTagRemoveCommand extends WorldEditTagCommand {
    public WorldEditTagRemoveCommand(MainHolder mainHolder) {
        super(mainHolder);
    }

    @Override
    protected int execute(CommandSourceStack source, Iterable<Entity> entities, Main main, String tag) {
        return executeRemove(source, entities, main, tag);
    }
}
