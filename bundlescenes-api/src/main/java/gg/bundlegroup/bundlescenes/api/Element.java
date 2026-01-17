package gg.bundlegroup.bundlescenes.api;

import java.util.Set;

public interface Element {
    Set<String> getTags();

    void addTag(String tag);

    void removeTag(String tag);

    void remove();
}
