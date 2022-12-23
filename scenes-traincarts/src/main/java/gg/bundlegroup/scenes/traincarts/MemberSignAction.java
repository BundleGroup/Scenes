package gg.bundlegroup.scenes.traincarts;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;

public abstract class MemberSignAction extends SignAction {
    @Override
    public void execute(SignActionEvent info) {
        if (!info.isPowered()) {
            return;
        }

        if (info.isTrainSign() && info.isAction(SignActionType.REDSTONE_ON, SignActionType.GROUP_ENTER) && info.hasGroup()) {
            for (MinecartMember<?> member : info.getGroup()) {
                execute(info, member);
            }
        } else if (info.isCartSign() && info.isAction(SignActionType.REDSTONE_ON, SignActionType.MEMBER_ENTER) && info.hasMember()) {
            execute(info, info.getMember());
        } else if (info.isRCSign() && info.isAction(SignActionType.REDSTONE_ON)) {
            for (MinecartGroup group : info.getRCTrainGroups()) {
                for (MinecartMember<?> member : group) {
                    execute(info, member);
                }
            }
        }
    }

    public abstract void execute(SignActionEvent info, MinecartMember<?> member);
}
