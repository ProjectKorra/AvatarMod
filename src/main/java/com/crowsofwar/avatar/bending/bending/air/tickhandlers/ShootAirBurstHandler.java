package com.crowsofwar.avatar.bending.bending.air.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.air.AbilityAirBurst;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityAirGust;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;

import static com.crowsofwar.avatar.bending.bending.air.tickhandlers.AirBurstHandler.AIRBURST_MOVEMENT_MODIFIER_ID;
import static com.crowsofwar.avatar.util.data.StatusControlController.CHARGE_AIR_BURST;
import static com.crowsofwar.avatar.util.data.TickHandlerController.AIRBURST_CHARGE_HANDLER;

public class ShootAirBurstHandler extends TickHandler {

    public ShootAirBurstHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityAirBurst burst = (AbilityAirBurst) Abilities.get("air_burst");
        AbilityData abilityData = ctx.getData().getAbilityData("air_burst");


        int duration = ctx.getData().getTickHandlerDuration(this);


        Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw),
                Math.toRadians(entity.rotationPitch));
        Vector pos = Vector.getEyePos(entity);

        if (burst != null && abilityData != null && bender.consumeChi(burst.getChiCost(abilityData))) {

            //Only used for determining the charge amount
            int durationToFire = burst.getProperty(Ability.CHARGE_TIME, abilityData).intValue();

            durationToFire *= (2 - abilityData.getDamageMult());
            durationToFire -= abilityData.getXpModifier() * 10;


            int lifetime = burst.getProperty(Ability.LIFETIME, abilityData).intValue();
            float damage = burst.getProperty(Ability.DAMAGE, abilityData).floatValue();
            float speed = burst.getProperty(Ability.SPEED, abilityData).floatValue() * 5;
            float size = burst.getProperty(Ability.SIZE, abilityData).floatValue();
            int performance = burst.getProperty(Ability.PERFORMANCE, abilityData).intValue();
            float xp = burst.getProperty(Ability.XP_HIT, abilityData).intValue();
            int charge;

            float exhaustion, burnout;
            int cooldown;
            exhaustion = burst.getExhaustion(abilityData);
            burnout = burst.getBurnOut(abilityData);
            cooldown = burst.getCooldown(abilityData);

            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
                exhaustion = burnout = cooldown = 0;
            }
            //Copies the charge calculations
            //Makes sure the charge is never 0.
            charge = Math.max((3 * (duration / durationToFire)) + 1, 1);
            charge = Math.min(charge, 4);
            //We don't want the charge going over 4

            size *= abilityData.getDamageMult() * abilityData.getXpModifier();
            damage *= abilityData.getDamageMult() * abilityData.getXpModifier();
            speed *= abilityData.getDamageMult() * abilityData.getXpModifier();
            lifetime *= abilityData.getDamageMult() * abilityData.getXpModifier();


            damage *= (0.5 + 0.125 * charge);
            size *= (0.5 + 0.125 * charge);
            speed *= (0.5 + 0.125 * charge);
            lifetime *= (0.80 + 0.05 * charge);

            EntityAirGust gust = new EntityAirGust(world);
            gust.setVelocity(look.times(speed));
            gust.setPosition(pos.minusY(0.5));
            gust.setOwner(entity);
            gust.setEntitySize(size);
            gust.setAbility(Objects.requireNonNull(Abilities.get("air_burst")));
            gust.setDamage(damage);
            gust.setChiHit((float) (burst.getProperty(Ability.CHI_HIT, abilityData).floatValue() * abilityData.getDamageMult() * abilityData.getXpModifier()));
            gust.setPerformanceAmount(performance);
            gust.setXp(xp);
            gust.setLifeTime(lifetime);
            gust.rotationPitch = entity.rotationPitch;
            gust.rotationYaw = entity.rotationYaw;
            gust.setPushStone(true);
            gust.setPushIronDoor(true);
            gust.setPushIronTrapDoor(true);
            gust.setDestroyProjectiles(true);
            gust.setDynamicSpreadingCollision(true);
            gust.setPush(speed / 20);
            gust.setPiercing(abilityData.getLevel() >= 2);
            gust.setTier(burst.getCurrentTier(abilityData));
            gust.setBehaviour(new AirBurstBeamBehaviour());
            gust.setDamageSource("avatar_Air");
            if (!world.isRemote)
                world.spawnEntity(gust);

            abilityData.addBurnout(burnout);
            if (entity instanceof EntityPlayer)
                ((EntityPlayer) entity).addExhaustion(exhaustion);
            abilityData.setAbilityCooldown(cooldown);

            entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_FIREWORK_LAUNCH, entity.getSoundCategory(),
                    1.0F + Math.max(abilityData.getLevel(), 0) / 2F, 0.9F + world.rand.nextFloat() / 10);
            entity.world.playSound(null, new BlockPos(entity), SoundEvents.ENTITY_LIGHTNING_IMPACT, entity.getSoundCategory(), 2.0F, 3.0F);

            AttributeModifier modifier = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(AIRBURST_MOVEMENT_MODIFIER_ID);
            if (modifier != null && entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(modifier)) {
                entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(modifier);
            }
        }
        ctx.getData().removeTickHandler(AIRBURST_CHARGE_HANDLER, ctx);
        ctx.getData().removeStatusControl(CHARGE_AIR_BURST);
        return true;
    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        AbilityData abilityData = AbilityData.get(ctx.getBenderEntity(), "air_burst");
        if (abilityData != null)
            abilityData.setRegenBurnout(true);
    }

    public static class AirBurstBeamBehaviour extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            World world = entity.world;
            if (world.isRemote && entity.getOwner() != null && entity instanceof EntityAirGust) {
                int rings = (int) (entity.getAvgSize() * 2) + 4;
                float size = entity.getAvgSize() * (1 / entity.getAvgSize() + 0.5F);
                int particles = (int) (Math.min((int) (entity.getAvgSize() * Math.PI), 2) + (entity.velocity().magnitude() / 20));
                Vec3d centre = AvatarEntityUtils.getMiddleOfEntity(entity);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(BendingStyles.get(Airbending.ID)).collide(true)
                        .clr(0.95F, 0.95F, 0.95F, 0.05F).time(16)
                        .scale(size * 0.875F).spawnEntity(entity).swirl(rings, particles, entity.getAvgSize(),
                        size / 4F, (float) (entity.velocity().sqrMagnitude() * entity.getAvgSize()), (-1F / size), entity,
                        world, true, centre, ParticleBuilder.SwirlMotionType.IN,
                        false, true);
                int max = (int) (entity.getAvgSize() * 4);
                for (int i = 0; i < max; i++) {
                    Vec3d pos = Vector.getOrthogonalVector(entity.getLookVec(), i * (360F / max) + (entity.ticksExisted % 360) * 20 *
                            (1 / entity.getAvgSize()), entity.getAvgSize() / 1.5F).toMinecraft();
                    Vec3d velocity;
                    Vec3d entityPos = AvatarEntityUtils.getMiddleOfEntity(entity);

                    pos = pos.add(entityPos);
                    velocity = pos.subtract(entityPos).normalize();
                    velocity = velocity.scale(entity.velocity().sqrMagnitude() / 400000);
                    double spawnX = pos.x;
                    double spawnY = pos.y;
                    double spawnZ = pos.z;
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x,
                            world.rand.nextGaussian() / 80 + velocity.y, world.rand.nextGaussian() / 80 + velocity.z)
                            .time(4 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(0.95F, 0.95F, 0.95F, 0.1F).spawnEntity(entity)
                            .scale(1.25F * entity.getAvgSize() * (1 / entity.getAvgSize())).element(BendingStyles.get(Airbending.ID)).collide(true).collideParticles(true).spawn(world);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x,
                            world.rand.nextGaussian() / 80 + velocity.y, world.rand.nextGaussian() / 80 + velocity.z)
                            .time(16 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(0.95F, 0.95F, 0.95F, 0.1F).spawnEntity(entity)
                            .scale(1.25F * entity.getAvgSize() * (1 / entity.getAvgSize())).element(BendingStyles.get(Airbending.ID)).collide(true).collideParticles(true).spawn(world);

                }
            }
            float expansionRate = 1f / 100;
            if (entity.ticksExisted % 4 == 0)
                entity.setEntitySize(entity.getAvgSize() + expansionRate);
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
