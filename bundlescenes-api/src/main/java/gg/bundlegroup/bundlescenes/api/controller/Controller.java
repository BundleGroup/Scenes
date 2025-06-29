package gg.bundlegroup.bundlescenes.api.controller;

import gg.bundlegroup.bundlescenes.api.scene.Scene;
import net.kyori.adventure.key.Keyed;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.io.Closeable;

@NullMarked
public interface Controller extends Keyed, Closeable {
    void showScene(Player player, Scene scene);

    void hideScene(Player player, Scene scene);

    boolean isShowingScene(Player player, Scene scene);

    @Override
    void close();
}
