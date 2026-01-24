package gg.bundlegroup.scenes;

import gg.bundlegroup.scenes.api.Element;
import gg.bundlegroup.scenes.api.Viewable;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class PluginElement implements Element {
    private final ScenesImpl scenes;
    private final Plugin plugin;
    private final Viewable viewable;
    private final Set<String> tags = new HashSet<>();

    public PluginElement(ScenesImpl scenes, Plugin plugin, Viewable viewable) {
        this.scenes = scenes;
        this.plugin = plugin;
        this.viewable = viewable;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Viewable getViewable() {
        return viewable;
    }

    @Override
    public Set<String> getTags() {
        return Set.copyOf(tags);
    }

    @Override
    public void addTag(String tag) {
        if (tags.add(tag)) {
            doAddTag(tag);
        }
    }

    @Override
    public void removeTag(String tag) {
        if (tags.remove(tag)) {
            doRemoveTag(tag);
        }
    }

    private void doAddTag(String tag) {
        scenes.addElementTag(this, tag);
    }

    private void doRemoveTag(String tag) {
        scenes.removeElementTag(this, tag);
    }

    @Override
    public void remove() {
        tags.forEach(this::doRemoveTag);
        tags.clear();
        scenes.unregisterElement(this);
    }
}
