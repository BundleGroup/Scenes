package gg.bundlegroup.scenes.plugin;

public interface IntegrationProvider {
    String name();

    boolean available();

    Integration create(ScenesPlugin plugin);
}
