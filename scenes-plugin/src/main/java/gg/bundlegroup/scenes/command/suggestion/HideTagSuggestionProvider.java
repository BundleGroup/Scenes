package gg.bundlegroup.scenes.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import gg.bundlegroup.scenes.ScenesImpl;
import gg.bundlegroup.scenes.api.Controller;
import gg.bundlegroup.scenes.plugin.Main;
import gg.bundlegroup.scenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class HideTagSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    private final MainHolder mainHolder;

    public HideTagSuggestionProvider(MainHolder mainHolder) {
        this.mainHolder = mainHolder;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        if (!(context.getSource().getExecutor() instanceof Player player)) {
            return builder.buildFuture();
        }
        Main main = mainHolder.getMain();
        Controller manualController = main.getManualController();
        ScenesImpl scenes = main.getScenes();
        // Suggest anything currently visible to the player (manual, region, train...), since -f can hide any of them.
        Set<String> visible = new HashSet<>(scenes.getPlayerTags(player));
        visible.addAll(manualController.getShownTags(player));
        for (String tag : visible) {
            if (!scenes.isForceHidden(player, tag)) {
                builder.suggest(tag);
            }
        }
        return builder.buildFuture();
    }
}
