package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;
import java.util.function.BiPredicate;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static java.lang.Math.toRadians;

public class AbilityWaterCannon extends Ability{
    public AbilityWaterCannon() {
        super(Waterbending.ID, "water_cannon");
    }

    @Override
    public void execute(AbilityContext ctx) {

        Bender bender = ctx.getBender();
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();

        boolean hasChi = bender.consumeChi(STATS_CONFIG.chiLightning);
        boolean hasLightningCharge = data.hasTickHandler(TickHandler.WATER_CHARGE);
        Vector targetPos = getClosestWaterBlock(entity, ctx.getLevel());

        if (targetPos != null || ctx.consumeWater(3)) {

            if (targetPos == null) {
                targetPos = Vector.getEyePos(entity).plus(Vector.getLookRectangular(entity).times(4));
            }

            if (hasChi && !hasLightningCharge) {
                ctx.getData().addTickHandler(TickHandler.WATER_CHARGE);
            }
        }
    }



    private Vector getClosestWaterBlock(EntityLivingBase entity, int level) {
        World world = entity.world;

        Vector eye = Vector.getEyePos(entity);

        double rangeMult = 0.6;
        if (level >= 1) {
            rangeMult = 1;
        }

        double range = STATS_CONFIG.waterArcSearchRadius * rangeMult;
        for (int i = 0; i < STATS_CONFIG.waterArcAngles; i++) {
            for (int j = 0; j < STATS_CONFIG.waterArcAngles; j++) {

                double yaw = entity.rotationYaw + i * 360.0 / STATS_CONFIG.waterArcAngles;
                double pitch = entity.rotationPitch + j * 360.0 / STATS_CONFIG.waterArcAngles;

                BiPredicate<BlockPos, IBlockState> isWater = (pos, state) -> state.getBlock() == Blocks.WATER
                        || state.getBlock() == Blocks.FLOWING_WATER;

                Vector angle = Vector.toRectangular(toRadians(yaw), toRadians(pitch));
                Raytrace.Result result = Raytrace.predicateRaytrace(world, eye, angle, range, isWater);
                if (result.hitSomething()) {
                    return result.getPosPrecise();
                }

            }

        }

        return null;

    }
}

