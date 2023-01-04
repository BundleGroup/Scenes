package gg.bundlegroup.bundlescenes.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

/**
 * Controls which players are able to see certain scenes.
 */
@ApiStatus.NonExtendable
public interface SceneController {
    String name();

    void show(Player player, String scene);

    void hide(Player player, String scene);

    void hideAll(Player player);

    void remove();
}
