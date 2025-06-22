package gg.bundlegroup.bundlescenes;

import gg.bundlegroup.bundleentities.api.tracker.EntityTracker;
import gg.bundlegroup.bundlescenes.api.BundleScenes;
import gg.bundlegroup.bundlescenes.api.BundleScenesProvider;
import gg.bundlegroup.bundlescenes.chunk.ChunkManager;
import gg.bundlegroup.bundlescenes.conversion.EntityConversionManager;
import gg.bundlegroup.bundlescenes.conversion.command.ConversionCommands;
import gg.bundlegroup.bundlescenes.conversion.converter.ArmorStandConverter;
import gg.bundlegroup.bundlescenes.conversion.converter.BlockDisplayConverter;
import gg.bundlegroup.bundlescenes.conversion.converter.ItemDisplayConverter;
import gg.bundlegroup.bundlescenes.conversion.converter.TextDisplayConverter;
import gg.bundlegroup.bundlescenes.worldguard.WorldGuardSupport;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper;
import org.incendo.cloud.paper.util.sender.Source;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class Main extends JavaPlugin implements BundleScenes {
    public static final String NAMESPACE = "bundlescenes";

    private @Nullable ChunkManager chunkManager;
    private @Nullable WorldGuardSupport worldGuardSupport;

    public static boolean isAutoConvertEnabled() {
        return Objects.equals(System.getenv("BUNDLE_SCENES_AUTO_CONVERT"), "true");
    }

    @Override
    public void onLoad() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuardSupport = new WorldGuardSupport(this);
            worldGuardSupport.load();
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onEnable() {
        chunkManager = new ChunkManager(this);

        if (worldGuardSupport != null) {
            worldGuardSupport.enable();
        }

        PaperCommandManager<Source> commandManager = PaperCommandManager.builder(PaperSimpleSenderMapper.simpleSenderMapper())
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(this);

        BundleScenesProvider.setInstance(this);

        EntityConversionManager entityConversionManager = new EntityConversionManager(this, chunkManager);
        entityConversionManager.getRegistry().register(ArmorStand.class, new ArmorStandConverter());
        entityConversionManager.getRegistry().register(BlockDisplay.class, new BlockDisplayConverter());
        entityConversionManager.getRegistry().register(ItemDisplay.class, new ItemDisplayConverter());
        entityConversionManager.getRegistry().register(TextDisplay.class, new TextDisplayConverter());
        entityConversionManager.setAutoConvert(Main.isAutoConvertEnabled());
        entityConversionManager.loadAllEntities();
        commandManager.command(new ConversionCommands(entityConversionManager));
    }

    @Override
    public void onDisable() {
        if (worldGuardSupport != null) {
            worldGuardSupport.disable();
            worldGuardSupport = null;
        }
        if (chunkManager != null) {
            chunkManager.close();
            chunkManager = null;
        }
    }

    @Override
    public EntityTracker getChunkEntityTracker(Chunk chunk) {
        return Objects.requireNonNull(chunkManager).getChunk(chunk).getEntityTracker();
    }

    @Override
    public @Nullable EntityTracker getSceneEntityTracker(String name) {
        if (worldGuardSupport != null) {
            return worldGuardSupport.getSceneManager().getTracker(name);
        } else {
            return null;
        }
    }
}
