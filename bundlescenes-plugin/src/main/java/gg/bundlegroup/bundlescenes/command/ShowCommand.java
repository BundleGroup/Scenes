package gg.bundlegroup.bundlescenes.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import gg.bundlegroup.bundlescenes.MessageStyle;
import gg.bundlegroup.bundlescenes.api.Controller;
import gg.bundlegroup.bundlescenes.plugin.Main;
import gg.bundlegroup.bundlescenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;

public class ShowCommand implements Command<CommandSourceStack> {
    private final MainHolder mainHolder;

    public ShowCommand(MainHolder mainHolder) {
        this.mainHolder = mainHolder;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player player)) {
            return 0;
        }
        String tag = context.getArgument("tag", String.class);
        Main main = mainHolder.getMain();
        Controller manualController = main.getManualController();
        if (manualController.isShowingTag(player, tag)) {
            context.getSource().getSender().sendMessage(text("Already manually showing tag ", MessageStyle.ERROR)
                    .append(text(tag, MessageStyle.ERROR_ACCENT)));
            return SINGLE_SUCCESS;
        }
        manualController.showTag(player, tag);
        context.getSource().getSender().sendMessage(text("Manually showing tag ", MessageStyle.SUCCESS)
                .append(text(tag, MessageStyle.SUCCESS_ACCENT)));
        return SINGLE_SUCCESS;
    }
}
