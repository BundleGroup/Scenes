package gg.bundlegroup.bundlescenes.conversion.command.preprocessor;

import gg.bundlegroup.bundlescenes.conversion.EntityConversionManager;
import org.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
import org.incendo.cloud.execution.preprocessor.CommandPreprocessor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EntityConversionManagerPreprocessor<C> implements CommandPreprocessor<C> {
    private final EntityConversionManager entityConversionManager;

    public EntityConversionManagerPreprocessor(EntityConversionManager entityConversionManager) {
        this.entityConversionManager = entityConversionManager;
    }

    @Override
    public void accept(CommandPreprocessingContext<C> context) {
        context.commandContext().set(EntityConversionManager.KEY, entityConversionManager);
    }
}
