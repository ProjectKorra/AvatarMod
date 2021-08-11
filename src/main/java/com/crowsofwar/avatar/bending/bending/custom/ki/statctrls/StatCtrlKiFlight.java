package com.crowsofwar.avatar.bending.bending.custom.ki.statctrls;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.custom.ki.AbilityKiFlight;
import com.crowsofwar.avatar.bending.bending.custom.ki.Kibending;
import com.crowsofwar.avatar.bending.bending.custom.ki.tickhandlers.KiFlightHandler;
import com.crowsofwar.avatar.client.controls.AvatarControl;
import com.crowsofwar.avatar.entity.EntityShockwave;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
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
import static com.crowsofwar.avatar.util.data.TickHandlerController.KI_FLIGHT_HANDlER;

public class StatCtrlKiFlight extends StatusControl {

    public StatCtrlKiFlight() {
        super(15, AvatarControl.CONTROL_JUMP, CrosshairPosition.BELOW_CROSSHAIR);
    }


    @Override
    public boolean execute(BendingContext ctx) {

        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        World world = ctx.getWorld();

        AbilityData abilityData = data.getAbilityData("ki_flight");
        AbilityKiFlight flight = (AbilityKiFlight) Abilities.get("ki_flight");

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

                IBlockState state = world.getBlockState(entity.getPosition());
                Block currentBlock = state.getBlock();
                if (!(currentBlock instanceof BlockLiquid) && !state.isFullBlock() && !state.isFullCube()) {
               //     damageNearbyEntities(ctx);
                }


                data.addTickHandler(KI_FLIGHT_HANDlER, ctx);
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

    private void damageNearbyEntities(BendingContext ctx) {

        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = ctx.getData().getAbilityData("ki_flight");
        AbilityKiFlight flight = (AbilityKiFlight) Abilities.get("ki_flight");

        if (flight != null) {
            float speed = flight.getProperty(SPEED, abilityData).floatValue() / 10;
            float size = flight.getProperty(SIZE, abilityData).floatValue() / 2;
            int lifetime = (int) (speed / size * 10);
            float knockback = flight.getProperty(KNOCKBACK, abilityData).floatValue();
            float damage = flight.getProperty(DAMAGE, abilityData).floatValue();
            int performance = flight.getProperty(PERFORMANCE, abilityData).intValue() / 10;
            float chiHit = flight.getProperty(CHI_HIT, abilityData).floatValue() / 4;
            int r, g, b, fadeR, fadeG, fadeB;

            r = flight.getProperty(R, abilityData).intValue();
            g = flight.getProperty(G, abilityData).intValue();
            b = flight.getProperty(B, abilityData).intValue();
            fadeR = flight.getProperty(FADE_R, abilityData).intValue();
            fadeG = flight.getProperty(FADE_G, abilityData).intValue();
            fadeB = flight.getProperty(FADE_B, abilityData).intValue();

            speed *= abilityData.getDamageMult() * abilityData.getXpModifier();
            size *= abilityData.getDamageMult() * abilityData.getXpModifier();
            lifetime *= abilityData.getDamageMult() * abilityData.getXpModifier();
            knockback *= abilityData.getDamageMult() * abilityData.getXpModifier();
            damage *= abilityData.getDamageMult() * abilityData.getXpModifier();
            performance *= abilityData.getDamageMult() * abilityData.getXpModifier();
            chiHit *= abilityData.getDamageMult() * abilityData.getXpModifier();

            EntityShockwave wave = new EntityShockwave(world);
            wave.setOwner(entity);
            wave.setDamageSource("avatar_Ki_shockwave");
            wave.setPosition(AvatarEntityUtils.getBottomMiddleOfEntity(entity).add(0, 0.5, 0));
            wave.setFireTime(0);
            wave.setEntitySize(size / 5);
            wave.setElement(Kibending.ID);
            wave.setAbility(flight);
            wave.setDamage(damage);
            wave.setOwner(entity);
            wave.setSphere(false);
            wave.setSpeed(speed);
            wave.setRenderNormal(false);
            wave.setRange(size);
            wave.setLifeTime(lifetime);
            wave.setChiHit(chiHit);
            wave.setPerformanceAmount(performance);
            wave.setPush(knockback);
            wave.setParticleWaves(lifetime * 5);
            wave.setBehaviour(new KiFlightHandler.KiFlightShockwave());
            wave.setParticleSpeed(speed / 30F);
            wave.setParticleAmount(30);
            wave.setRGB(r, g, b);
            wave.setFade(fadeR, fadeG, fadeB);
            wave.setXp(flight.getProperty(XP_HIT, abilityData).floatValue());
            if (!world.isRemote)
                world.spawnEntity(wave);
        }
    }

}

