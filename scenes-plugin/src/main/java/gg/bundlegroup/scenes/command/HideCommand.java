package gg.bundlegroup.scenes.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import gg.bundlegroup.scenes.MessageStyle;
import gg.bundlegroup.scenes.ScenesImpl;
import gg.bundlegroup.scenes.api.Controller;
import gg.bundlegroup.scenes.plugin.Main;
import gg.bundlegroup.scenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;

public class HideCommand implements Command<CommandSourceStack> {
    private final MainHolder mainHolder;
    private final boolean force;

    public HideCommand(MainHolder mainHolder) {
        this(mainHolder, false);
    }

    public HideCommand(MainHolder mainHolder, boolean force) {
        this.mainHolder = mainHolder;
        this.force = force;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player player)) {
            return 0;
        }
        String tag = context.getArgument("tag", String.class);
        Main main = mainHolder.getMain();
        Controller manualController = main.getManualController();
        if (force) {
            ScenesImpl scenes = main.getScenes();
            // Drop any manual show too, so lifting the force-hide later falls back to region/train behaviour.
            manualController.hideTag(player, tag);
            if (!scenes.forceHideTag(player, tag)) {
                context.getSource().getSender().sendMessage(text("Already force-hiding tag ", MessageStyle.ERROR)
                        .append(text(tag, MessageStyle.ERROR_ACCENT)));
                return SINGLE_SUCCESS;
            }
            context.getSource().getSender().sendMessage(text("Force-hiding tag ", MessageStyle.SUCCESS)
                    .append(text(tag, MessageStyle.SUCCESS_ACCENT))
                    .append(text(" (use /scenes show " + tag + " to undo)", MessageStyle.SUCCESS)));
            return SINGLE_SUCCESS;
        }
        if (!manualController.isShowingTag(player, tag)) {
            context.getSource().getSender().sendMessage(text("Not manually showing tag ", MessageStyle.ERROR)
                    .append(text(tag, MessageStyle.ERROR_ACCENT)));
            return SINGLE_SUCCESS;
        }
        manualController.hideTag(player, tag);
        context.getSource().getSender().sendMessage(text("Stopped manually showing tag ", MessageStyle.SUCCESS)
                .append(text(tag, MessageStyle.SUCCESS_ACCENT)));
        return SINGLE_SUCCESS;
    }
}
