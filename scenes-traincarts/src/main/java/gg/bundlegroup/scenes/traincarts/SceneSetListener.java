package gg.bundlegroup.scenes.traincarts;

import com.bergerkiller.bukkit.tc.events.seat.MemberSeatEnterEvent;
import com.bergerkiller.bukkit.tc.events.seat.MemberSeatExitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;

public class SceneSetListener implements Listener {
    private final TrainCartsAddon addon;

    public SceneSetListener(TrainCartsAddon addon) {
        this.addon = addon;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMemberSeatEnter(MemberSeatEnterEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        addon.applyScenes(player, event.getMember().getProperties().get(addon.getProperty()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMemberSeatExit(MemberSeatExitEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (event.isSeatChange()) {
            // ignore, will be handled when entering the new seat
            return;
        }
        addon.applyScenes(player, Set.of());
    }
}
