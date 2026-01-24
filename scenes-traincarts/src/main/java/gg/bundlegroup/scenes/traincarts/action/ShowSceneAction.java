package gg.bundlegroup.scenes.traincarts.action;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.api.IStringSetProperty;

import java.util.HashSet;
import java.util.Set;

public record ShowSceneAction(Set<String> scenes) implements SceneAction {
    @Override
    public void execute(MinecartMember<?> member, IStringSetProperty property) {
        Set<String> scenes = new HashSet<>(member.getProperties().get(property));
        if (scenes.addAll(this.scenes)) {
            member.getProperties().set(property, scenes);
        }
    }

    @Override
    public String getDescription() {
        return "start showing the specified scenes";
    }
}
