package gg.bundlegroup.bundlescenes.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.io.Closeable;

/**
 * Used to control which tags are visible.
 */
@ApiStatus.NonExtendable
public interface Controller extends Closeable {
    /**
     * Start showing elements with the specified tag to the player.
     *
     * @param player the player
     * @param tag    the tag to show
     */
    void showTag(Player player, String tag);

    /**
     * Stop showing elements with the specified tag to the player.
     *
     * @param player the player
     * @param tag    the tag to hide
     */
    void hideTag(Player player, String tag);

    /**
     * Returns whether this controller is showing the tag to the player.
     *
     * @param player the player
     * @param tag    the tag
     * @return true if this controller is showing the tag to the player
     */
    boolean isShowingTag(Player player, String tag);

    /**
     * Closes this controller.
     */
    @Override
    void close();
}
