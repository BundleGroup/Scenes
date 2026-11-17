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

import static net.kyori.adventure.text.Component.text;

public class ShowCommand implements Command<CommandSourceStack> {
    private final MainHolder mainHolder;
    private final boolean all;

    public ShowCommand(MainHolder mainHolder) {
        this(mainHolder, false);
    }

    /**
     * @param all apply to every tag ({@code /scenes show *}) instead of the {@code tag} argument
     */
    public ShowCommand(MainHolder mainHolder, boolean all) {
        this.mainHolder = mainHolder;
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
            // Same rule as a single tag: first use lifts force-hides, next use manually shows.
            if (scenes.clearForceHides(player)) {
                sender.sendMessage(text("No longer force-hiding any tags", MessageStyle.SUCCESS));
                return SINGLE_SUCCESS;
            }
            int count = 0;
            for (String tag : scenes.getTags()) {
                if (!manualController.isShowingTag(player, tag)) {
                    manualController.showTag(player, tag);
                    count++;
                }
            }
            if (count == 0) {
                sender.sendMessage(text("Already manually showing all tags", MessageStyle.ERROR));
                return SINGLE_SUCCESS;
            }
            sender.sendMessage(text("Manually showing ", MessageStyle.SUCCESS)
                    .append(text(count, MessageStyle.SUCCESS_ACCENT))
                    .append(text(count == 1 ? " tag" : " tags", MessageStyle.SUCCESS)));
            return SINGLE_SUCCESS;
        }

        String tag = context.getArgument("tag", String.class);
        if (scenes.unforceHideTag(player, tag)) {
            String note = scenes.isForceHidingAll(player) ? " (still hidden: all tags are force-hidden)" : "";
            sender.sendMessage(text("No longer force-hiding tag ", MessageStyle.SUCCESS)
                    .append(text(tag, MessageStyle.SUCCESS_ACCENT))
                    .append(text(note, MessageStyle.SUCCESS)));
            return SINGLE_SUCCESS;
        }
        if (scenes.isForceHidingAll(player)) {
            sender.sendMessage(text("All tags are force-hidden; use /scenes show * first", MessageStyle.ERROR));
            return SINGLE_SUCCESS;
        }
        if (manualController.isShowingTag(player, tag)) {
            sender.sendMessage(text("Already manually showing tag ", MessageStyle.ERROR)
                    .append(text(tag, MessageStyle.ERROR_ACCENT)));
            return SINGLE_SUCCESS;
        }
        manualController.showTag(player, tag);
        sender.sendMessage(text("Manually showing tag ", MessageStyle.SUCCESS)
                .append(text(tag, MessageStyle.SUCCESS_ACCENT)));
        return SINGLE_SUCCESS;
    }
}
