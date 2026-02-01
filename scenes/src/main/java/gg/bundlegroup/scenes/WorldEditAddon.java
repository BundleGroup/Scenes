package gg.bundlegroup.scenes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public interface WorldEditAddon extends Addon {
    @Nullable Set<Entity> getSelectedEntities(Player player);
}
