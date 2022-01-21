package com.crowsofwar.avatar.bending.bending.combustion.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.combustion.AbilityRocketBoost;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.*;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.bending.bending.Ability.*;

public class StatCtrlRocketBoost extends StatusControl {

    public StatCtrlRocketBoost() {
        super(15, AvatarControl.CONTROL_JUMP, CrosshairPosition.BELOW_CROSSHAIR);
    }


    @Override
    public boolean execute(BendingContext ctx) {

        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        World world = ctx.getWorld();

        AbilityData abilityData = data.getAbilityData("rocket_boost");
        AbilityRocketBoost rocketBoost = (AbilityRocketBoost) Abilities.get("rocket_boost");

        if (rocketBoost != null) {
            float chiCost, exhaustion, burnOut;
            int cooldown;
            chiCost = rocketBoost.getChiCost(abilityData);
            exhaustion = rocketBoost.getExhaustion(abilityData);
            burnOut = rocketBoost.getBurnOut(abilityData);
            cooldown = rocketBoost.getCooldown(abilityData);


            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
                chiCost = exhaustion = burnOut = cooldown = 0;
            if (entity instanceof EntityBender)
                chiCost = 0;

            if (bender.consumeChi(chiCost)) {


                double jumpMultiplier = rocketBoost.getProperty(SPEED, abilityData).doubleValue() / 20;
                float fallAbsorption = rocketBoost.getProperty(FALL_ABSORPTION, abilityData).floatValue();


                // Calculate direction to rocket_boost -- in the direction the player is currently already going

                // For some reason, velocity is 0 here when player is walking, so must instead
                // calculate using delta position
                Vector deltaPos = new Vector(entity.posX - entity.lastTickPosX, 0, entity.posZ - entity.lastTickPosZ);
                double currentYaw = Vector.getRotationTo(Vector.ZERO, deltaPos).y();

                // Just go forwards if not moving right now
                if (deltaPos.sqrMagnitude() <= 0.001) {
                    currentYaw = Math.toRadians(entity.rotationYaw);
                }

                float pitch = entity.rotationPitch;
                if (pitch < -45) {
                    pitch = -45;
                }

                Vector rotations = new Vector(Math.toRadians(pitch), currentYaw, 0);

                // Calculate velocity to move bender

                Vector velocity = rotations.toRectangular();

                velocity = velocity.withX(velocity.x() * 2);
                velocity = velocity.withY(velocity.y() * 1F + 0.15);
                velocity = velocity.withZ(velocity.z() * 2);

                velocity = velocity.times(jumpMultiplier);
                entity.addVelocity(velocity.x(), velocity.y(), velocity.z());
                AvatarUtils.afterVelocityAdded(entity);

                IBlockState state = world.getBlockState(entity.getPosition());
                Block currentBlock = state.getBlock();
                if (!(currentBlock instanceof BlockLiquid) && !state.isFullBlock() && !state.isFullCube()) {
                    //    damageNearbyEntities(ctx);
                }


                data.removeTickHandler(TickHandlerController.ROCKET_BOOST_HANDLER, ctx);
                data.addTickHandler(TickHandlerController.ROCKET_BOOST_HANDLER, ctx);
                data.getMiscData().setFallAbsorption(fallAbsorption);

                abilityData.addXp(rocketBoost.getProperty(XP_USE, abilityData).floatValue());

                entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 2, .7f);

                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addExhaustion(exhaustion);
                abilityData.addBurnout(burnOut);
                //Ensure the ability can't be spammed after activating.
                abilityData.setAbilityCooldown(0);

                //Rather than making the player fly, you continually boost yourself
                return true;

            }
        }

        return false;

    }

}

