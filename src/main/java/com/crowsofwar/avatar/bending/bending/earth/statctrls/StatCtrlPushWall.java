package com.crowsofwar.avatar.bending.bending.earth.statctrls;

import com.crowsofwar.avatar.bending.bending.earth.Earthbending;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityWallSegment;
import com.crowsofwar.avatar.entity.data.WallBehavior;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.util.data.StatusControlController.PLACE_WALL;

/**
 * @author Aang23
 */
public class StatCtrlPushWall extends StatusControl {

    public StatCtrlPushWall() {
        super(23, AvatarControl.CONTROL_SHIFT, CrosshairPosition.LEFT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        AbilityData abilityData = ctx.getData().getAbilityData("wall");
        EntityWallSegment wallSegment = AvatarEntity.lookupOwnedEntity(world, EntityWallSegment.class, entity);

        if (!data.hasBendingId(Earthbending.ID))
            return true;

        if (wallSegment != null && wallSegment.getOwner() != null &&
                wallSegment.getBehavior().getClass() == WallBehavior.Waiting.class && abilityData.getAbilityCooldown(entity) <= 0) {

            List<EntityWallSegment> segments = world.getEntities(EntityWallSegment.class,
                    segment -> segment.getOwner() == entity);

            for (EntityWallSegment segment : segments) {
                if (segment.getBehavior().getClass().equals(WallBehavior.Waiting.class))
                    segment.setBehavior(new WallBehavior.Push());
            }

            ctx.getData().removeStatusControl(PLACE_WALL);

            return true;
        } else {
            return false;
        }
    }
}
