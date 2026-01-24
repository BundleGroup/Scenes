package gg.bundlegroup.scenes.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import gg.bundlegroup.scenes.plugin.Main;
import gg.bundlegroup.scenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.concurrent.CompletableFuture;

public class TagSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    private final MainHolder mainHolder;

    public TagSuggestionProvider(MainHolder mainHolder) {
        this.mainHolder = mainHolder;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        Main main = mainHolder.getMain();
        for (String tag : main.getScenes().getTags()) {
            builder.suggest(tag);
        }
        return builder.buildFuture();
    }
}
