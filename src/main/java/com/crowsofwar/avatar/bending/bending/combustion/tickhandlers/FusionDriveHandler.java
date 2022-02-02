package com.crowsofwar.avatar.bending.bending.combustion.tickhandlers;

import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class FusionDriveHandler extends TickHandler {

    public FusionDriveHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        int duration = data.getTickHandlerDuration(this);

        if (duration == 1) {
            if (world.isRemote)
                swingArm(entity, false);
            if (!world.isRemote)
                pullEntities(world, entity, 1, 1);
        }
        if (duration == 20) {
            if (world.isRemote)
                swingArm(entity, true);
            if (!world.isRemote)
                pullEntities(world, entity, -1, 1);

        }
        return duration > 25;
    }

    private void swingArm(EntityLivingBase entity, boolean main) {
        if (main) entity.swingArm(EnumHand.MAIN_HAND);
        else entity.swingArm(EnumHand.OFF_HAND);
    }

    private void pullEntities(World world, EntityLivingBase entity, float pullForce, float range) {
        Vec3d look = entity.getLookVec();
        Vec3d pos = look.add(entity.getPositionVector().add(0, entity.getEyeHeight() * 0.75, 0));
        AxisAlignedBB box = new AxisAlignedBB(pos.x + range, pos.y + range, pos.z + range, pos.x - range, pos.y - range, pos.z - range);
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, box);
        if (!entities.isEmpty()) {
            for (Entity target : entities) {
                if (DamageUtils.canCollideWith(entity, target) && target != entity) {
                    Vec3d vel = pos.subtract(target.getPositionVector()).scale(pullForce);
                    target.motionX = vel.x;
                    target.motionY = vel.y + 0.15;
                    target.motionZ = vel.z;
                    target.isAirBorne = true;
                }
            }
        }
    }
}
