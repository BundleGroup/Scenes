package gg.bundlegroup.bundlescenes.worldguard.command.parser;

import gg.bundlegroup.bundlescenes.worldguard.RegionSceneManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.incendo.cloud.suggestion.Suggestion;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SceneArgumentParser<C> implements ArgumentParser<C, String>, BlockingSuggestionProvider<C> {
    public static <C> ParserDescriptor<C, String> sceneArgumentParser() {
        return ParserDescriptor.of(new SceneArgumentParser<>(), String.class);
    }

    @Override
    public ArgumentParseResult<String> parse(CommandContext<C> commandContext, CommandInput commandInput) {
        return ArgumentParseResult.success(commandInput.readString());
    }

    @Override
    public Iterable<? extends Suggestion> suggestions(CommandContext<C> context, CommandInput input) {
        RegionSceneManager regionSceneManager = context.get(RegionSceneManager.KEY);
        return regionSceneManager.getTrackers().keySet().stream().map(Suggestion::suggestion).toList();
    }
}
