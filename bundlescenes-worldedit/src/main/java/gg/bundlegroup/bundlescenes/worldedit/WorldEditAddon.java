package gg.bundlegroup.bundlescenes.worldedit;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import gg.bundlegroup.bundlescenes.Addon;
import gg.bundlegroup.bundlescenes.BundleScenesImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class WorldEditAddon implements Addon {
    private final Plugin plugin;
    private final BundleScenesImpl scenes;

    public WorldEditAddon(Plugin plugin, BundleScenesImpl scenes) {
        this.plugin = plugin;
        this.scenes = scenes;
    }

    @Override
    public void load() {

    }

    private static @Nullable Set<Entity> getSelectedEntities(Player player) {
        BukkitPlayer bukkitPlayer = BukkitAdapter.adapt(player);
        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(bukkitPlayer);
        com.sk89q.worldedit.world.World selectionWorld = localSession.getSelectionWorld();
        Region selection;
        try {
            if (selectionWorld == null) {
                throw new IncompleteRegionException();
            }
            selection = localSession.getSelection(selectionWorld);
        } catch (IncompleteRegionException e) {
            player.sendMessage(Component.text("No WorldEdit selection", NamedTextColor.RED));
            return null;
        }
        World world = BukkitAdapter.adapt(selectionWorld);
        Set<Entity> entities = new HashSet<>();
        for (BlockVector2 chunkPos : selection.getChunks()) {
            Chunk chunk = world.getChunkAt(chunkPos.x(), chunkPos.z());
            for (Entity entity : chunk.getEntities()) {
                Location location = entity.getLocation();
                BlockVector3 pos = BlockVector3.at(
                        location.getBlockX(),
                        location.getBlockY(),
                        location.getBlockZ());
                if (selection.contains(pos)) {
                    entities.add(entity);
                }
            }
        }
        return entities;
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }
}
