package gg.bundlegroup.scenes.traincarts.action;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.api.IStringSetProperty;

public record ClearSceneAction() implements SceneAction {
    @Override
    public void execute(MinecartMember<?> member, IStringSetProperty property) {
        member.getProperties().set(property, null);
    }

    @Override
    public String getDescription() {
        return "stop showing all scenes";
    }
}
