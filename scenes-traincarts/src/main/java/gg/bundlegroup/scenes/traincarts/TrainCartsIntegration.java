package gg.bundlegroup.scenes.traincarts;

import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import gg.bundlegroup.scenes.api.SceneController;
import gg.bundlegroup.scenes.api.SceneManager;
import gg.bundlegroup.scenes.plugin.Integration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class TrainCartsIntegration implements Integration, Listener {
    private final List<SignAction> signActions = new ArrayList<>();
    private final SceneController controller;

    public TrainCartsIntegration(Plugin plugin) {
        controller = SceneManager.get().createController(plugin);
        signActions.add(new SignActionShowScene(controller));
        signActions.add(new SignActionHideScene(controller));
        for (SignAction signAction : signActions) {
            SignAction.register(signAction);
        }
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Bukkit.getPluginManager().addPermission(new Permission(
                "scenes.traincarts.build.show",
                "Allows building showscene signs"));
        Bukkit.getPluginManager().addPermission(new Permission(
                "scenes.traincarts.build.hide",
                "Allows building hidescene signs"));
    }

    @Override
    public void unregister() {
        HandlerList.unregisterAll(this);
        for (SignAction signAction : signActions) {
            SignAction.unregister(signAction);
        }
        signActions.clear();
        controller.remove();
    }

    @EventHandler
    public void onSeatExit(MemberSeatExitEvent event) {
        if (event.isMemberVehicleChange()) {
            if (event.getEntity() instanceof Player player) {
                controller.hideAll(player);
            }
        }
    }
}
