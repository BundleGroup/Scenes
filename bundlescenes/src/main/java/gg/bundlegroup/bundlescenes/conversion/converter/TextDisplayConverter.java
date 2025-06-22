package gg.bundlegroup.bundlescenes.conversion.converter;

import gg.bundlegroup.bundleentities.api.entity.VirtualEntityFactory;
import gg.bundlegroup.bundleentities.api.entity.VirtualTextDisplay;
import gg.bundlegroup.bundleentities.api.tracker.EntityTracker;
import org.bukkit.entity.TextDisplay;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TextDisplayConverter extends AbstractDisplayConverter<TextDisplay, VirtualTextDisplay> {
    @Override
    protected VirtualTextDisplay create(TextDisplay entity, EntityTracker tracker, VirtualEntityFactory factory) {
        return factory.createTextDisplay(tracker);
    }

    @Override
    protected void configure(VirtualTextDisplay virtual, TextDisplay entity) {
        super.configure(virtual, entity);
        virtual.setText(entity.text());
        virtual.setLineWidth(entity.getLineWidth());
        virtual.setBackgroundColor(entity.getBackgroundColor());
        virtual.setTextOpacity(entity.getTextOpacity());
        virtual.setShadow(entity.isShadowed());
        virtual.setSeeThrough(entity.isSeeThrough());
        virtual.setDefaultBackground(entity.isDefaultBackground());
        virtual.setTextAlignment(entity.getAlignment());
    }
}
