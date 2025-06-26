package gg.bundlegroup.bundlescenes.worldguard.command.preprocessor;

import gg.bundlegroup.bundlescenes.worldguard.RegionSceneManager;
import org.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
import org.incendo.cloud.execution.preprocessor.CommandPreprocessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RegionSceneManagerPreprocessor<C> implements CommandPreprocessor<C> {
    private final RegionSceneManager regionSceneManager;

    public RegionSceneManagerPreprocessor(RegionSceneManager regionSceneManager) {
        this.regionSceneManager = regionSceneManager;
    }

    @Override
    public void accept(CommandPreprocessingContext<C> context) {
        context.commandContext().set(RegionSceneManager.KEY, regionSceneManager);
    }
}
