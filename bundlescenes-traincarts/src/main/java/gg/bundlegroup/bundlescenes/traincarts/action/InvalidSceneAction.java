package gg.bundlegroup.bundlescenes.traincarts.action;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.api.IStringSetProperty;
import org.jspecify.annotations.Nullable;

public record InvalidSceneAction() implements SceneAction {
    @Override
    public void execute(MinecartMember<?> member, IStringSetProperty property) {
    }

    @Override
    public @Nullable String getDescription() {
        return null;
    }
}
