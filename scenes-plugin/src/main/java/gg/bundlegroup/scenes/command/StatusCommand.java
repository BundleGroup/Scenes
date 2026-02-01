package gg.bundlegroup.scenes.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import gg.bundlegroup.scenes.MessageStyle;
import gg.bundlegroup.scenes.ScenesImpl;
import gg.bundlegroup.scenes.plugin.Main;
import gg.bundlegroup.scenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

import static net.kyori.adventure.text.Component.text;

public class StatusCommand implements Command<CommandSourceStack> {
    private final MainHolder mainHolder;

    public StatusCommand(MainHolder mainHolder) {
        this.mainHolder = mainHolder;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        Main main = mainHolder.getMain();
        ScenesImpl scenes = main.getScenes();
        CommandSender sender = context.getSource().getSender();
        sender.sendMessage(text()
                .append(text("Currently tracking "))
                .append(text(scenes.getElementCount(), MessageStyle.INFO_ACCENT))
                .append(text(" elements, "))
                .append(text(scenes.getPlayerCount(), MessageStyle.INFO_ACCENT))
                .append(text(" players and "))
                .append(text(scenes.getTagCount(), MessageStyle.INFO_ACCENT))
                .append(text(" tags."))
                .style(MessageStyle.INFO));
        return 0;
    }
}
