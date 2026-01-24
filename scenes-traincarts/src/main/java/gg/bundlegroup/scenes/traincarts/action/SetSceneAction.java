package gg.bundlegroup.scenes.traincarts.action;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.api.IStringSetProperty;

import java.util.Set;

public record SetSceneAction(Set<String> scenes) implements SceneAction {
    @Override
    public void execute(MinecartMember<?> member, IStringSetProperty property) {
        member.getProperties().set(property, scenes);
    }

    @Override
    public String getDescription() {
        return "show only the specified scenes";
    }
}
