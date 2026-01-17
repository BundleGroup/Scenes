package gg.bundlegroup.bundlescenes.plugin;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class MainHolder {
    private @Nullable Main main;

    public Main getMain() {
        return Objects.requireNonNull(main, "main");
    }

    public void setMain(@Nullable Main main) {
        this.main = main;
    }
}
