package gg.bundlegroup.scenes.traincarts;

import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.properties.api.IStringSetProperty;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.google.common.collect.Sets;
import gg.bundlegroup.scenes.Addon;
import gg.bundlegroup.scenes.api.Scenes;
import gg.bundlegroup.scenes.api.Controller;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class TrainCartsAddon implements Addon {
    private final IStringSetProperty property = new SceneSetProperty(this);
    private final SignActionScene signAction = new SignActionScene(property);
    private final SceneSetListener listener = new SceneSetListener(this);
    private final Plugin plugin;
    private final Scenes scenes;
    private @Nullable Controller controller;

    public TrainCartsAddon(Plugin plugin, Scenes scenes) {
        this.plugin = plugin;
        this.scenes = scenes;
    }

    @Override
    public void load() {
        TrainCarts.plugin.getPropertyRegistry().register(property);
        SignAction.register(signAction);
    }

    @Override
    public void enable() {
        controller = scenes.createController(plugin);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(listener);
        SignAction.unregister(signAction);
        if (controller != null) {
            controller.close();
            controller = null;
        }
    }

    public IStringSetProperty getProperty() {
        return property;
    }

    public void applyScenes(Player player, Set<String> scenes) {
        if (controller == null) {
            return;
        }
        Set<String> shownTags = controller.getShownTags(player);
        for (String tag : Sets.difference(scenes, shownTags)) {
            controller.showTag(player, tag);
        }
        for (String tag : Sets.difference(shownTags, scenes)) {
            controller.hideTag(player, tag);
        }
    }
}
