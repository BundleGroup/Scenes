package gg.bundlegroup.scenes.traincarts;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.properties.api.IStringSetProperty;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import gg.bundlegroup.scenes.traincarts.action.SceneAction;

public class SignActionScene extends SignAction {
    private final IStringSetProperty property;

    public SignActionScene(IStringSetProperty property) {
        this.property = property;
    }

    @Override
    public boolean match(SignActionEvent info) {
        return info.isType("scene");
    }

    @Override
    public void execute(SignActionEvent info) {
        if (info.isTrainSign() && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)) {
            if (info.hasRailedMember() && info.isPowered()) {
                SceneAction action = SceneAction.parse(info);
                handleGroup(action, info.getGroup());
            }
        } else if (info.isCartSign() && info.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON)) {
            if (info.hasRailedMember() && info.isPowered()) {
                SceneAction action = SceneAction.parse(info);
                handleMember(action, info.getMember());
            }
        } else if (info.isRCSign() && info.isAction(SignActionType.REDSTONE_ON)) {
            for (MinecartGroup group : info.getRCTrainGroups()) {
                SceneAction action = SceneAction.parse(info);
                handleGroup(action, group);
            }
        }
    }

    private void handleGroup(SceneAction action, MinecartGroup group) {
        for (MinecartMember<?> member : group) {
            handleMember(action, member);
        }
    }

    private void handleMember(SceneAction action, MinecartMember<?> member) {
        action.execute(member, property);
    }

    @Override
    public boolean canSupportRC() {
        return true;
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        SceneAction action = SceneAction.parse(event);
        return SignBuildOptions.create()
                .setPermission("scenes.traincarts.build")
                .setName("scene trigger")
                .setDescription(action.getDescription())
                .handle(event);
    }
}
