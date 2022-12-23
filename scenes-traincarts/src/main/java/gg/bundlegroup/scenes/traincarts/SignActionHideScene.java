package gg.bundlegroup.scenes.traincarts;

import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import gg.bundlegroup.scenes.api.SceneController;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SignActionHideScene extends MemberSignAction {
    private final SceneController controller;

    public SignActionHideScene(SceneController controller) {
        this.controller = controller;
    }

    @Override
    public boolean match(SignActionEvent info) {
        return info.isType("hidescene");
    }

    @Override
    public void execute(SignActionEvent info, MinecartMember<?> member) {
        for (Entity passenger : member.getEntity().getPassengers()) {
            if (passenger instanceof Player player) {
                controller.hide(player, info.getLine(2));
            }
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create()
                .setPermission("scenes.traincarts.build.hide")
                .setName("scene trigger")
                .setDescription("hide a scene")
                .handle(event.getPlayer());
    }
}
