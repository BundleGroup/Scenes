package gg.bundlegroup.scenes.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import gg.bundlegroup.scenes.api.Scenes;
import gg.bundlegroup.scenes.plugin.MainHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import org.bukkit.entity.Entity;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class EntityTagSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
    private final MainHolder mainHolder;

    public EntityTagSuggestionProvider(MainHolder mainHolder) {
        this.mainHolder = mainHolder;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        EntitySelectorArgumentResolver entityResolver = context.getArgument("entity", EntitySelectorArgumentResolver.class);
        Scenes scenes = mainHolder.getMain().getScenes();
        Set<String> tags = new HashSet<>();
        for (Entity entity : entityResolver.resolve(context.getSource())) {
            tags.addAll(scenes.getEntityTags(entity));
        }
        for (String tag : tags) {
            builder.suggest(tag);
        }
        return builder.buildFuture();
    }
}
