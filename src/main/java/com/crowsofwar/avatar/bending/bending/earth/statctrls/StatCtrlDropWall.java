package com.crowsofwar.avatar.bending.bending.earth.statctrls;

import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.EntityWallSegment;
import com.crowsofwar.avatar.entity.data.WallBehavior;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.util.data.StatusControlController.*;

/**
 * @author CrowsOfWar
 */
public class StatCtrlDropWall extends StatusControl {

    public StatCtrlDropWall() {
        super(2, AvatarControl.CONTROL_MIDDLE_CLICK_DOWN, CrosshairPosition.BELOW_CROSSHAIR);
    }

    @Override
    public boolean execute(BendingContext ctx) {
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        List<EntityWallSegment> segments = world.getEntities(EntityWallSegment.class, seg -> seg != null && seg.getOwner() == entity);

        //Cooldown and chi cost are irrelevant here, due to what this does

        for (EntityWallSegment seg : segments) {
            seg.setBehavior(new WallBehavior.Drop());
        }

        ctx.getData().removeStatusControl(PLACE_WALL);
        ctx.getData().removeStatusControl(PUSH_WALL);
        ctx.getData().removeStatusControl(PULL_WALL);

        return true;
    }

}
