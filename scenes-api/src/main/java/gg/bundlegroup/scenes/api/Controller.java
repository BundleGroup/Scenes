package gg.bundlegroup.scenes.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.io.Closeable;
import java.util.Set;

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
    default boolean isShowingTag(Player player, String tag) {
        return getShownTags(player).contains(tag);
    }

    /**
     * Returns all tags which this controller is currently showing to the player
     *
     * @param player the player
     * @return an unmodifiable set containing the shown tags
     */
    Set<String> getShownTags(Player player);

    /**
     * Closes this controller.
     */
    @Override
    void close();
}
