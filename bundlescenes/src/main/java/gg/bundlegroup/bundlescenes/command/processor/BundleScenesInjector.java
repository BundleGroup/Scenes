package gg.bundlegroup.bundlescenes.command.processor;

import gg.bundlegroup.bundlescenes.api.BundleScenes;
import org.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
import org.incendo.cloud.execution.preprocessor.CommandPreprocessor;
import org.incendo.cloud.key.CloudKey;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BundleScenesInjector<C> implements CommandPreprocessor<C> {
    public static final CloudKey<BundleScenes> KEY = CloudKey.cloudKey("bundlescenes", BundleScenes.class);
    private final BundleScenes scenes;

    public BundleScenesInjector(BundleScenes scenes) {
        this.scenes = scenes;
    }

    @Override
    public void accept(CommandPreprocessingContext<C> context) {
        context.commandContext().store(KEY, scenes);
    }
}
