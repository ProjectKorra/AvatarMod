package com.crowsofwar.avatar.bending.bending.earth.statctrls;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityWallSegment;
import com.crowsofwar.avatar.entity.data.WallBehavior;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author Aang23
 */
public class StatCtrlPullWall extends StatusControl {

    public StatCtrlPullWall() {
        super(24, AvatarControl.CONTROL_LEFT_CLICK_DOWN, CrosshairPosition.LEFT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = ctx.getData().getAbilityData("wall");
        EntityWallSegment wallSegment = AvatarEntity.lookupOwnedEntity(world, EntityWallSegment.class, entity);

        if (wallSegment != null && wallSegment.getBehavior() != null &&
                wallSegment.getBehavior().getClass() == WallBehavior.Waiting.class
                && abilityData.getAbilityCooldown(entity) <= 0) {

            List<EntityWallSegment> segments = world.getEntities(EntityWallSegment.class,
                    segment -> segment.getOwner() == entity);

            for (EntityWallSegment segment : segments) {
                if (segment.getBehavior().getClass().equals(WallBehavior.Waiting.class))
                    segment.setBehavior(new WallBehavior.Pull());
            }

            return true;
        } else {
            return false;
        }
    }
}
