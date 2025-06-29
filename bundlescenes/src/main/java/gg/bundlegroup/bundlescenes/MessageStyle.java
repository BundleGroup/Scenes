package gg.bundlegroup.bundlescenes;

import net.kyori.adventure.text.format.Style;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.Style.style;

public final class MessageStyle {
    private MessageStyle() {
    }

    public static final Style SUCCESS = style(GREEN);
    public static final Style SUCCESS_ACCENT = style(DARK_GREEN);

    public static final Style ERROR = style(RED);
    public static final Style ERROR_ACCENT = style(DARK_RED);

    public static final Style INFO = style(BLUE);
    public static final Style INFO_ACCENT = style(AQUA);
}
