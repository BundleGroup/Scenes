package gg.bundlegroup.scenes.command.tag.worldedit;

import com.mojang.brigadier.context.CommandContext;
import gg.bundlegroup.scenes.WorldEditAddon;
import gg.bundlegroup.scenes.command.tag.TagCommand;
import gg.bundlegroup.scenes.plugin.Main;
import gg.bundlegroup.scenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;

public abstract class WorldEditTagCommand extends TagCommand {
    private final MainHolder mainHolder;

    protected WorldEditTagCommand(MainHolder mainHolder) {
        this.mainHolder = mainHolder;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        Main main = mainHolder.getMain();
        WorldEditAddon addon = Objects.requireNonNull(main.findAddon(WorldEditAddon.class));
        String tag = context.getArgument("tag", String.class);
        Player sender = (Player) context.getSource().getSender();
        Set<Entity> entities = addon.getSelectedEntities(sender);
        if (entities == null) {
            sender.sendMessage(Component.text("No WorldEdit selection", NamedTextColor.RED));
            return 0;
        }
        return execute(context.getSource(), entities, main, tag);
    }
}
