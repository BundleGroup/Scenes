package gg.bundlegroup.bundlescenes.conversion.converter;

import gg.bundlegroup.bundleentities.api.entity.VirtualDisplay;
import org.bukkit.entity.Display;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractDisplayConverter<E extends Display, V extends VirtualDisplay> extends AbstractEntityConverter<E, V> {
    @Override
    protected void configure(V virtual, E entity) {
        super.configure(virtual, entity);
        virtual.setTransformation(entity.getTransformation());
        virtual.setTransformationInterpolationDuration(entity.getInterpolationDuration());
        virtual.setPositionInterpolationDuration(entity.getTeleportDuration());
        virtual.setBillboard(entity.getBillboard());
        virtual.setBrightness(entity.getBrightness());
        virtual.setViewRange(entity.getViewRange());
        virtual.setShadowRadius(entity.getShadowRadius());
        virtual.setShadowStrength(entity.getShadowStrength());
        virtual.setWidth(entity.getDisplayWidth());
        virtual.setHeight(entity.getDisplayHeight());
        virtual.setGlowColorOverride(entity.getGlowColorOverride());
    }
}
