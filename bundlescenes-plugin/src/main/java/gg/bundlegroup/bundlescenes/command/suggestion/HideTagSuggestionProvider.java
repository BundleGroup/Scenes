package gg.bundlegroup.bundlescenes.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import gg.bundlegroup.bundlescenes.api.Controller;
import gg.bundlegroup.bundlescenes.plugin.Main;
import gg.bundlegroup.bundlescenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

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
        for (String tag : main.getScenes().getTags()) {
            if (manualController.isShowingTag(player, tag)) {
                builder.suggest(tag);
            }
        }
        return builder.buildFuture();
    }
}
