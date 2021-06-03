package com.crowsofwar.avatar.bending.bending.custom.ki.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.custom.ki.AbilityKiFlight;
import com.crowsofwar.avatar.bending.bending.custom.ki.Kibending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.EntityShockwave;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.gorecore.util.Vector.toRectangular;
import static java.lang.Math.toRadians;

public class KiFlightHandler extends TickHandler {

    public KiFlightHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase target = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData data = ctx.getData().getAbilityData(new AbilityKiFlight());
        AbilityKiFlight fly = (AbilityKiFlight) Abilities.get(new AbilityKiFlight().getName());
        Vector pos = Vector.getEntityPos(target).minusY(0.05);

        if (world.isRemote && fly != null) {
            double minY = target.getEntityBoundingBox().minY;
            pos = pos.plus(Vector.getVelocity(target).times(0.1));
            pos = pos.withY(Math.max(pos.y(), minY));
            float size = fly.getProperty(SIZE, data).floatValue() / 2;
            int r, g, b, fadeR, fadeG, fadeB;

            r = fly.getProperty(R, data).intValue();
            g = fly.getProperty(G, data).intValue();
            b = fly.getProperty(B, data).intValue();
            fadeR = fly.getProperty(FADE_R, data).intValue();
            fadeG = fly.getProperty(FADE_G, data).intValue();
            fadeB = fly.getProperty(FADE_B, data).intValue();

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
                        .scale(size).time(6 + AvatarUtils.getRandomNumberInRange(0, 6)).element(new Kibending()).collide(true)
                        .ability(fly).spawnEntity(target).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 25).spawn(world);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(r, g * 4, b, 215 + AvatarUtils.getRandomNumberInRange(0, 40))
                        .fade(rRandom, gRandom * 4, bRandom, 160 + AvatarUtils.getRandomNumberInRange(0, 40))
                        .pos(pos.toMinecraft()).vel(world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20)
                        .scale(size).time(6 + AvatarUtils.getRandomNumberInRange(0, 6)).element(new Kibending()).collide(true)
                        .ability(fly).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 25).spawnEntity(target).spawn(world);
            }
        }
        int duration = 40;
        if (fly != null) {
            duration = fly.getProperty(DURATION, data).intValue();
            duration *= data.getDamageMult() * data.getXpModifier();
        }

        if (fly != null && ctx.getData().getTickHandlerDuration(this) < duration) {

            if (bender.consumeChi(fly.getChiCost(data) / 20)) {
                double targetSpeed = fly.getProperty(SPEED, data).floatValue() / 4;
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

                AvatarUtils.afterVelocityAdded(target);
                if (target instanceof EntityBender || target instanceof EntityPlayer && !((EntityPlayer) target).isCreative())
                    data.addBurnout(fly.getBurnOut(data) / 20);
                if (target instanceof EntityPlayer)
                    ((EntityPlayer) target).addExhaustion(fly.getExhaustion(data) / 20);

            }
        }

        return target.isInWater() || target.isSneaking() || bender.isFlying() || duration <= ctx.getData().getTickHandlerDuration(this);

    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);

        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = ctx.getData().getAbilityData("ki_flight");
        AbilityKiFlight flight = (AbilityKiFlight) Abilities.get("ki_flight");

        if (flight != null && flight.getBooleanProperty(STOP_SHOCKWAVE, abilityData)) {
            float speed = flight.getProperty(SPEED, abilityData).floatValue() / 5;
            float size = flight.getProperty(SIZE, abilityData).floatValue() * 1.25F;
            int lifetime = (int) (speed / size * 10) / 2;
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
            wave.setEntitySize(size / 5F);
            wave.setElement(new Kibending());
            wave.setAbility(flight);
            wave.setDamage(damage);
            wave.setOwner(entity);
            wave.setSphere(false);
            wave.setSpeed(speed);
            wave.setRange(size * 1.5F);
            wave.setLifeTime(lifetime);
            wave.setChiHit(chiHit);
            wave.setPerformanceAmount(performance);
            wave.setPush(knockback);
            wave.setBehaviour(new KiFlightShockwave());
            wave.setParticleSpeed(speed / 45F);
            wave.setParticleAmount(20);
            wave.setRGB(r, g, b);
            wave.setFade(fadeR, fadeG, fadeB);
            wave.setRenderNormal(false);
            wave.setParticleWaves(lifetime * 2);
            if (!world.isRemote)
                world.spawnEntity(wave);
        }
        if (flight != null)
            abilityData.setAbilityCooldown(flight.getCooldown(abilityData));
    }

    //TODO: Fire entity for visual fx/sparks/embers from fire
    public static class KiFlightShockwave extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity instanceof EntityShockwave && entity.world.isRemote) {
                World world = entity.world;
                if (entity.getOwner() != null) {
                    EntityLivingBase owner = entity.getOwner();

                    if (entity.ticksExisted <= ((EntityShockwave) entity).getParticleWaves()) {
                        int[] fade = entity.getFade();
                        int[] rgb = entity.getRGB();
                        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / (((EntityShockwave) entity).getRange() *
                                ((EntityShockwave) entity).getParticleAmount()) * entity.ticksExisted) {
                            int rRandom = fade[0] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[0] * 2) : AvatarUtils.getRandomNumberInRange(fade[0] / 2,
                                    fade[0] * 2);
                            int gRandom = fade[1] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[1] * 2) : AvatarUtils.getRandomNumberInRange(fade[1] / 2,
                                    fade[1] * 2);
                            int bRandom = fade[2] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[2] * 2) : AvatarUtils.getRandomNumberInRange(fade[2] / 2,
                                    fade[2] * 2);

                            //Even though the maths is technically wrong, you use sin if you want a shockwave, and cos if you want a sphere (for x).
                            double x2 = entity.posX + (entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.sin(angle);
                            double y2 = entity.posY;
                            double z2 = entity.posZ + (entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.cos(angle);
                            Vector speed = new Vector((entity.ticksExisted * ((EntityShockwave) entity).getSpeed()) * Math.sin(angle) *
                                    (entity.getParticleSpeed() * 10), entity.getParticleSpeed() / 2, (entity.ticksExisted *
                                    ((EntityShockwave) entity).getSpeed()) * Math.cos(angle) * (entity.getParticleSpeed() * 10));
                            speed = speed.plus(world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20);

                            int time = 10;
                            time = Math.max(time, (entity.getLifeTime() - ((EntityShockwave) entity).getParticleWaves()) * 2);
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Kibending()).vel(speed.toMinecraft())
                                    .spawnEntity(owner).collide(true).collideParticles(true).clr(rgb[0], rgb[1], rgb[2], 180 + AvatarUtils.getRandomNumberInRange(0, 40)).
                                    fade(rRandom, gRandom, bRandom, 160 + AvatarUtils.getRandomNumberInRange(0, 40)).pos(x2, y2, z2).
                                    scale(entity.getAvgSize() * 2).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 25).time(time + AvatarUtils.getRandomNumberInRange(0, 2)).spawn(world);
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Kibending()).vel(speed.toMinecraft())
                                    .spawnEntity(owner).collide(true).collideParticles(true).clr(rgb[0], rgb[1] * 8, rgb[2] * 4, 180 + AvatarUtils.getRandomNumberInRange(0, 40)).
                                    fade(rRandom, gRandom * 2, bRandom, 160 + AvatarUtils.getRandomNumberInRange(0, 40)).pos(x2, y2, z2).
                                    scale(entity.getAvgSize() * 2).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 25).time(time + AvatarUtils.getRandomNumberInRange(0, 2)).spawn(world);
                        }
                    }
                }
            }
            return this;
        }

        @Override
        public void fromBytes(PacketBuffer buf) {

        }

        @Override
        public void toBytes(PacketBuffer buf) {

        }

        @Override
        public void load(NBTTagCompound nbt) {

        }

        @Override
        public void save(NBTTagCompound nbt) {

        }
    }
}

