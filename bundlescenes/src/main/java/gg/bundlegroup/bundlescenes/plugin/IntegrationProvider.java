package gg.bundlegroup.bundlescenes.plugin;

public interface IntegrationProvider {
    String name();

    boolean available();

    Integration create(ScenesPlugin plugin);
}
