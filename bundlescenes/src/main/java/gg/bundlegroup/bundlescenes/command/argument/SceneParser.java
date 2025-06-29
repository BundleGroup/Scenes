package gg.bundlegroup.bundlescenes.command.argument;

import gg.bundlegroup.bundlescenes.api.BundleScenes;
import gg.bundlegroup.bundlescenes.api.controller.Controller;
import gg.bundlegroup.bundlescenes.api.scene.Scene;
import gg.bundlegroup.bundlescenes.command.processor.BundleScenesInjector;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.incendo.cloud.suggestion.Suggestion;
import org.jspecify.annotations.NullMarked;

import java.util.function.Predicate;

import static org.incendo.cloud.suggestion.Suggestion.suggestion;

@NullMarked
public class SceneParser<C> implements ArgumentParser<C, Scene>, BlockingSuggestionProvider<C> {
    public static <C> ParserDescriptor<C, Scene> sceneParser() {
        return ParserDescriptor.of(new SceneParser<>(), Scene.class);
    }

    public static ParserDescriptor<PlayerSource, Scene> visibleSceneParser(Controller controller) {
        return ParserDescriptor.of(new VisibleSceneParser<>(controller), Scene.class);
    }

    public static ParserDescriptor<PlayerSource, Scene> hiddenSceneParser(Controller controller) {
        return ParserDescriptor.of(new HiddenSceneParser<>(controller), Scene.class);
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public ArgumentParseResult<Scene> parse(CommandContext<C> context, CommandInput input) {
        BundleScenes scenes = context.get(BundleScenesInjector.KEY);
        String sceneName = input.readString();
        Key sceneKey;
        try {
            sceneKey = Key.key(sceneName);
        } catch (InvalidKeyException e) {
            return ArgumentParseResult.failure(e);
        }
        Scene scene = scenes.scene(sceneKey);
        Predicate<Scene> predicate = createPredicate(context);
        if (!predicate.test(scene)) {
            return createPredicateFailureResult(context, scene);
        }
        return ArgumentParseResult.success(scene);
    }


    @Override
    public Iterable<? extends Suggestion> suggestions(CommandContext<C> context, CommandInput input) {
        BundleScenes scenes = context.get(BundleScenesInjector.KEY);
        return scenes.scenes()
                .stream()
                .filter(createPredicate(context))
                .map(this::createSuggestion)
                .toList();
    }

    protected Predicate<Scene> createPredicate(CommandContext<C> context) {
        return scene -> true;
    }

    protected ArgumentParseResult<Scene> createPredicateFailureResult(CommandContext<C> context, Scene scene) {
        return ArgumentParseResult.failure(new IllegalArgumentException());
    }

    private Suggestion createSuggestion(Scene scene) {
        return suggestion(scene.key().asMinimalString());
    }

    public static class VisibleSceneParser<C> extends SceneParser<C> {
        private final Controller controller;

        public VisibleSceneParser(Controller controller) {
            this.controller = controller;
        }

        @Override
        protected Predicate<Scene> createPredicate(CommandContext<C> context) {
            if (!(context.sender() instanceof PlayerSource playerSource)) {
                return scene -> false;
            }
            Player player = playerSource.source();
            return scene -> controller.isShowingScene(player, scene);
        }

        @Override
        protected ArgumentParseResult<Scene> createPredicateFailureResult(CommandContext<C> context, Scene scene) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Scene is not visible"));
        }
    }

    public static class HiddenSceneParser<C> extends SceneParser<C> {
        private final Controller controller;

        public HiddenSceneParser(Controller controller) {
            this.controller = controller;
        }

        @Override
        protected Predicate<Scene> createPredicate(CommandContext<C> context) {
            if (!(context.sender() instanceof PlayerSource playerSource)) {
                return scene -> false;
            }
            Player player = playerSource.source();
            return scene -> !controller.isShowingScene(player, scene);
        }

        @Override
        protected ArgumentParseResult<Scene> createPredicateFailureResult(CommandContext<C> context, Scene scene) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Scene is visible"));
        }
    }
}
