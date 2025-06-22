package gg.bundlegroup.bundlescenes.conversion.converter;

import gg.bundlegroup.bundleentities.api.entity.VirtualArmorStand;
import gg.bundlegroup.bundleentities.api.entity.VirtualEntityFactory;
import gg.bundlegroup.bundleentities.api.tracker.EntityTracker;
import org.bukkit.entity.ArmorStand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ArmorStandConverter extends AbstractLivingEntityConverter<ArmorStand, VirtualArmorStand> {
    @Override
    protected VirtualArmorStand create(ArmorStand entity, EntityTracker tracker, VirtualEntityFactory factory) {
        return factory.createArmorStand(tracker);
    }

    @Override
    protected void configure(VirtualArmorStand virtual, ArmorStand entity) {
        super.configure(virtual, entity);
        virtual.setNoBasePlate(!entity.hasBasePlate());
        virtual.setShowArms(entity.hasArms());
        virtual.setSmall(entity.isSmall());
        virtual.setMarker(entity.isMarker());
        virtual.setHeadPose(entity.getHeadRotations());
        virtual.setBodyPose(entity.getBodyRotations());
        virtual.setLeftArmPose(entity.getLeftArmRotations());
        virtual.setRightArmPose(entity.getRightArmRotations());
        virtual.setLeftLegPose(entity.getLeftLegRotations());
        virtual.setRightLegPose(entity.getRightLegRotations());
        configure(virtual.getEquipment(), entity.getEquipment());
    }
}
