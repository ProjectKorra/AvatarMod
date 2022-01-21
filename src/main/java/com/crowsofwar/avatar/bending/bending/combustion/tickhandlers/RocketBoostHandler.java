package com.crowsofwar.avatar.bending.bending.combustion.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.combustion.AbilityRocketBoost;
import com.crowsofwar.avatar.bending.bending.combustion.Combustionbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.gorecore.util.Vector.toRectangular;
import static java.lang.Math.toRadians;

public class RocketBoostHandler extends TickHandler {

    public RocketBoostHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase target = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData data = ctx.getData().getAbilityData("rocket_boost");
        AbilityRocketBoost flight = (AbilityRocketBoost) Abilities.get("rocket_boost");
        Vector pos = Vector.getEntityPos(target).minusY(0.05);

        if (world.isRemote && flight != null) {
            double minY = target.getEntityBoundingBox().minY;
            pos = pos.plus(Vector.getVelocity(target).times(0.1));
            pos = pos.withY(Math.max(pos.y(), minY));
            float size = flight.getProperty(SIZE, data).floatValue() / 2;
            int r, g, b, fadeR, fadeG, fadeB;

            r = flight.getProperty(R, data).intValue();
            g = flight.getProperty(G, data).intValue();
            b = flight.getProperty(B, data).intValue();
            fadeR = flight.getProperty(FADE_R, data).intValue();
            fadeG = flight.getProperty(FADE_G, data).intValue();
            fadeB = flight.getProperty(FADE_B, data).intValue();

            size *= data.getDamageMult() * data.getXpModifier();

            for (int i = 0; i < 8 + AvatarUtils.getRandomNumberInRange(2, 4); i++) {
                int rRandom = fadeR < 100 ? AvatarUtils.getRandomNumberInRange(0, fadeR * 2) : AvatarUtils.getRandomNumberInRange(fadeR / 2,
                        fadeR * 2);
                int gRandom = fadeG < 100 ? AvatarUtils.getRandomNumberInRange(0, fadeG * 2) : AvatarUtils.getRandomNumberInRange(fadeG / 2,
                        fadeG * 2);
                int bRandom = fadeB < 100 ? AvatarUtils.getRandomNumberInRange(0, fadeB * 2) : AvatarUtils.getRandomNumberInRange(fadeB / 2,
                        fadeB * 2);

                ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(r, g, b, 215 + AvatarUtils.getRandomNumberInRange(0, 40))
                        .fade(rRandom, gRandom, bRandom, 160 + AvatarUtils.getRandomNumberInRange(0, 40))
                        .pos(pos.toMinecraft()).vel(world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20)
                        .scale(size).time(6 + AvatarUtils.getRandomNumberInRange(0, 6)).element(BendingStyles.get(Combustionbending.ID)).collide(world.rand.nextBoolean())
                        .ability(flight).spawnEntity(target).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 50).spawn(world);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(r, g * 4, b, 215 + AvatarUtils.getRandomNumberInRange(0, 40))
                        .fade(255, 120, 30, 160 + AvatarUtils.getRandomNumberInRange(0, 40))
                        .pos(pos.toMinecraft()).vel(world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20)
                        .scale(size).time(6 + AvatarUtils.getRandomNumberInRange(0, 6)).element(BendingStyles.get(Combustionbending.ID))
                        .ability(flight).spawnEntity(target).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 50).spawn(world);

            }
        }
        int duration = 40;
        if (flight != null) {
            duration = flight.getProperty(DURATION, data).intValue();
            duration *= data.getDamageMult() * data.getXpModifier();
        }

        if (flight != null && ctx.getData().getTickHandlerDuration(this) < duration) {

            if (bender.consumeChi(flight.getChiCost(data) / 20)) {

                if (!ctx.getData().hasStatusControl(StatusControlController.ROCKET_BOOST) && ctx.getBenderEntity().ticksExisted % 4 == 0)
                    ctx.getData().addStatusControl(StatusControlController.ROCKET_BOOST);

                double targetSpeed = flight.getProperty(SPEED, data).floatValue() / 4;
                targetSpeed *= data.getDamageMult() * data.getXpModifier();

                if (target.moveForward != 0) {
                    if (target.moveForward < 0) {
                        targetSpeed /= 2;
                    } else {
                        targetSpeed *= 1.3;
                    }
                }

                double posY = target.onGround ? target.getEntityBoundingBox().minY + 0.25 : target.getEntityBoundingBox().minY;
                target.setPosition(target.posX, posY, target.posZ);
                Vector currentVelocity = new Vector(target.motionX, target.motionY, target.motionZ);
                Vector targetVelocity = toRectangular(toRadians(target.rotationYaw), 0).times(targetSpeed);

                double targetWeight = 0.1;
                currentVelocity = currentVelocity.times(1 - targetWeight);
                targetVelocity = targetVelocity.times(targetWeight);

                double targetSpeedWeight = 0.2;
                double speed = currentVelocity.magnitude() * (1 - targetSpeedWeight)
                        + targetSpeed * targetSpeedWeight;

                Vector newVelocity = currentVelocity.plus(targetVelocity).normalize().times(speed);

                Vector playerMovement = toRectangular(toRadians(target.rotationYaw - 90),
                        toRadians(target.rotationPitch)).times(target.moveStrafing * 0.02);

                newVelocity = newVelocity.plus(playerMovement);

                target.motionX = newVelocity.x();
                if (target.onGround)
                    target.motionY += 0.1;
                else
                    target.motionY += Math.max(target.getLookVec().scale(speed / 5).y * 2, 0.05);
                target.motionZ = newVelocity.z();

                target.motionY *= 0.5;

                if (!target.onGround)
                    target.isAirBorne = true;

                if (target instanceof EntityBender || target instanceof EntityPlayer && !((EntityPlayer) target).isCreative())
                    data.addBurnout(flight.getBurnOut(data) / 20);
                if (target instanceof EntityPlayer)
                    ((EntityPlayer) target).addExhaustion(flight.getExhaustion(data) / 20);

            }
        }

        return bender.isFlying() || duration <= ctx.getData().getTickHandlerDuration(this);

    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
    }
}

