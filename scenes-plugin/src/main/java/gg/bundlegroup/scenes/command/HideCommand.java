package gg.bundlegroup.scenes.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import gg.bundlegroup.scenes.MessageStyle;
import gg.bundlegroup.scenes.ScenesImpl;
import gg.bundlegroup.scenes.api.Controller;
import gg.bundlegroup.scenes.plugin.Main;
import gg.bundlegroup.scenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

import static net.kyori.adventure.text.Component.text;

public class HideCommand implements Command<CommandSourceStack> {
    private final MainHolder mainHolder;
    private final boolean force;
    private final boolean all;

    public HideCommand(MainHolder mainHolder) {
        this(mainHolder, false, false);
    }

    public HideCommand(MainHolder mainHolder, boolean force) {
        this(mainHolder, force, false);
    }

    /**
     * @param force hide even if a region, train or other integration is showing the tag
     * @param all   apply to every tag ({@code /scenes hide *}) instead of the {@code tag} argument
     */
    public HideCommand(MainHolder mainHolder, boolean force, boolean all) {
        this.mainHolder = mainHolder;
        this.force = force;
        this.all = all;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getExecutor() instanceof Player player)) {
            return 0;
        }
        CommandSender sender = context.getSource().getSender();
        Main main = mainHolder.getMain();
        Controller manualController = main.getManualController();
        ScenesImpl scenes = main.getScenes();

        if (all) {
            Set<String> manual = manualController.getShownTags(player);
            for (String tag : manual) {
                manualController.hideTag(player, tag);
            }
            if (force) {
                if (!scenes.forceHideAll(player)) {
                    sender.sendMessage(text("Already force-hiding all tags", MessageStyle.ERROR));
                    return SINGLE_SUCCESS;
                }
                sender.sendMessage(text("Force-hiding all tags (use /scenes show * to undo)", MessageStyle.SUCCESS));
                return SINGLE_SUCCESS;
            }
            if (manual.isEmpty()) {
                sender.sendMessage(text("Not manually showing any tags", MessageStyle.ERROR));
                return SINGLE_SUCCESS;
            }
            sender.sendMessage(text("Stopped manually showing ", MessageStyle.SUCCESS)
                    .append(text(manual.size(), MessageStyle.SUCCESS_ACCENT))
                    .append(text(manual.size() == 1 ? " tag" : " tags", MessageStyle.SUCCESS)));
            return SINGLE_SUCCESS;
        }

        String tag = context.getArgument("tag", String.class);
        if (force) {
            // Drop any manual show too, so lifting the force-hide later falls back to region/train behaviour.
            manualController.hideTag(player, tag);
            if (!scenes.forceHideTag(player, tag)) {
                sender.sendMessage(text("Already force-hiding tag ", MessageStyle.ERROR)
                        .append(text(tag, MessageStyle.ERROR_ACCENT)));
                return SINGLE_SUCCESS;
            }
            sender.sendMessage(text("Force-hiding tag ", MessageStyle.SUCCESS)
                    .append(text(tag, MessageStyle.SUCCESS_ACCENT))
                    .append(text(" (use /scenes show " + tag + " to undo)", MessageStyle.SUCCESS)));
            return SINGLE_SUCCESS;
        }
        if (!manualController.isShowingTag(player, tag)) {
            sender.sendMessage(text("Not manually showing tag ", MessageStyle.ERROR)
                    .append(text(tag, MessageStyle.ERROR_ACCENT)));
            return SINGLE_SUCCESS;
        }
        manualController.hideTag(player, tag);
        sender.sendMessage(text("Stopped manually showing tag ", MessageStyle.SUCCESS)
                .append(text(tag, MessageStyle.SUCCESS_ACCENT)));
        return SINGLE_SUCCESS;
    }
}
