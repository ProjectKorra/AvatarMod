package com.crowsofwar.avatar.bending.bending.custom.abyss.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.custom.abyss.AbilityAbyssFlight;
import com.crowsofwar.avatar.bending.bending.custom.hyper.AbilityHyperFlight;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.avatar.util.data.TickHandlerController.ABYSS_FLIGHT_HANDLER;
import static com.crowsofwar.avatar.util.data.TickHandlerController.HYPER_FLIGHT_HANDLER;

public class StatCtrlAbyssFlight extends StatusControl {

    public StatCtrlAbyssFlight() {
        super(15, AvatarControl.CONTROL_JUMP, CrosshairPosition.BELOW_CROSSHAIR);
    }


    @Override
    public boolean execute(BendingContext ctx) {

        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        World world = ctx.getWorld();

        AbilityData abilityData = data.getAbilityData("abyss_flight");
        AbilityAbyssFlight flight = (AbilityAbyssFlight) Abilities.get("abyss_flight");

        if (flight != null) {
            float chiCost, exhaustion, burnOut;
            int cooldown;
            chiCost = flight.getChiCost(abilityData);
            exhaustion = flight.getExhaustion(abilityData);
            burnOut = flight.getBurnOut(abilityData);
            cooldown = flight.getCooldown(abilityData);


            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
                chiCost = exhaustion = burnOut = cooldown = 0;
            if (entity instanceof EntityBender)
                chiCost = 0;

            if (bender.consumeChi(chiCost)) {


                double jumpMultiplier = flight.getProperty(SPEED, abilityData).doubleValue() / 20;
                float fallAbsorption = flight.getProperty(FALL_ABSORPTION, abilityData).floatValue();


                // Calculate direction to flight -- in the direction the player is currently already going

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
                velocity = velocity.withY(velocity.y() * 0.25F);
                velocity = velocity.withZ(velocity.z() * 2);

                velocity = velocity.times(jumpMultiplier);
                entity.addVelocity(velocity.x(), velocity.y(), velocity.z());
                AvatarUtils.afterVelocityAdded(entity);


                data.addTickHandler(ABYSS_FLIGHT_HANDLER, ctx);
                data.getMiscData().setFallAbsorption(fallAbsorption);

                abilityData.addXp(flight.getProperty(XP_USE, abilityData).floatValue());

                entity.world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1, .7f);

                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).addExhaustion(exhaustion);
                abilityData.addBurnout(burnOut);
                //Ensure the ability can't be spammed after activating.
                abilityData.setAbilityCooldown(0);
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).capabilities.isFlying = true;

                return true;

            }
        }

        return false;

    }


}

