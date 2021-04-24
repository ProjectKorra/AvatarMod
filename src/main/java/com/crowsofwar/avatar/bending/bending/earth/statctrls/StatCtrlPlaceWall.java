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

import static com.crowsofwar.avatar.util.data.StatusControlController.*;

/**
 * @author CrowsOfWar
 * @author Aang23
 */
public class StatCtrlPlaceWall extends StatusControl {

    public StatCtrlPlaceWall() {
        super(21, AvatarControl.CONTROL_RIGHT_CLICK, CrosshairPosition.RIGHT_OF_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {

        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = ctx.getData().getAbilityData("wall");
        BendingData data = ctx.getData();
        // TODO: When upgrade to a5.0 , call setOwner on the wall itself , then lookup
        // based on wall
        //FD: I'm sorry wot crows???

        // Wall has no owner so we go for segments
        if (!data.hasBendingId(Earthbending.ID))
            return true;

        EntityWallSegment wallSegment = AvatarEntity.lookupOwnedEntity(world, EntityWallSegment.class, entity);

        if (wallSegment != null && wallSegment.getBehavior() != null && wallSegment.getBehavior() instanceof WallBehavior.Waiting &&
                abilityData.getAbilityCooldown(entity) <= 0) {

            List<EntityWallSegment> segments = world.getEntities(EntityWallSegment.class,
                    segment -> segment.getOwner() == entity);

            for (EntityWallSegment segment : segments) {
                if (segment.getBehavior().getClass().equals(WallBehavior.Waiting.class))
                    segment.setBehavior(new WallBehavior.Place());
            }

            ctx.getData().removeStatusControl(DROP_WALL);
            ctx.getData().removeStatusControl(SHOOT_WALL);
            ctx.getData().removeStatusControl(PUSH_WALL);
            ctx.getData().removeStatusControl(PULL_WALL);

            return true;
        } else {
            return false;
        }
    }

}
