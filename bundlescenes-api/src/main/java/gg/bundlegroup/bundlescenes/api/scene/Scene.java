package gg.bundlegroup.bundlescenes.api.scene;

import gg.bundlegroup.bundlescenes.api.viewable.Viewable;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

@NullMarked
@ApiStatus.NonExtendable
public interface Scene extends Keyed {
    void addViewable(Viewable viewable);

    void removeViewable(Viewable viewable);
}
