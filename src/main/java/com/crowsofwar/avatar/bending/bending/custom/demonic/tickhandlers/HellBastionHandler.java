package com.crowsofwar.avatar.bending.bending.custom.demonic.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.BendingStyles;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.bending.bending.custom.demonic.AbilityHellBastion;
import com.crowsofwar.avatar.bending.bending.custom.demonic.Demonbending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.*;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.avatar.bending.bending.custom.demonic.AbilityHellBastion.SLOW_MULT;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_HELL_BASTION;

public class HellBastionHandler extends TickHandler {
    public static final UUID HELL_BASTION_MOVEMENT_MOD_ID = UUID.randomUUID();

    public HellBastionHandler(int id) {
        super(id);
    }

    @Override
    public boolean tick(BendingContext ctx) {
        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        Bender bender = ctx.getBender();
        AbilityData abilityData = ctx.getData().getAbilityData("hell_bastion");
        AbilityHellBastion hellBastion = (AbilityHellBastion) Abilities.get("hell_bastion");

        float charge;
        //4 stages, max charge of 4.

        if (abilityData != null && hellBastion != null) {


            float powerMod = (float) abilityData.getDamageMult();
            float xpMod = abilityData.getXpModifier();

            int duration = data.getTickHandlerDuration(this);
            float damage = hellBastion.getProperty(EFFECT_DAMAGE, abilityData).floatValue();
            float slowMult = hellBastion.getProperty(SLOW_MULT, abilityData).floatValue();


            float knockBack = hellBastion.getProperty(KNOCKBACK, abilityData).floatValue() / 4;
            float radius = hellBastion.getProperty(EFFECT_RADIUS, abilityData).floatValue();
            float speed = hellBastion.getProperty(SPEED, abilityData).floatValue() / 5;
            float maxEntitySize = hellBastion.getProperty(SIZE, abilityData).floatValue();
            int performanceAmount = hellBastion.getProperty(PERFORMANCE, abilityData).intValue();
            float shockwaveSpeed;

            float exhaustion, burnout;
            int cooldown;
            exhaustion = hellBastion.getExhaustion(abilityData);
            burnout = hellBastion.getBurnOut(abilityData);
            cooldown = hellBastion.getCooldown(abilityData);

            if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
                exhaustion = burnout = cooldown = 0;
            }

            //Makes sure the charge is never 0.
//            charge = Math.max((int) (3 * (duration / durationToFire)) + 1, 1);
//            charge = Math.min(charge, 4);
            //We don't want the charge going over 4.

            charge = 4;
            maxEntitySize *= powerMod * xpMod;
            damage *= powerMod * xpMod;
            radius *= powerMod * xpMod;
            knockBack *= powerMod * xpMod;
            slowMult *= powerMod * xpMod;
            speed *= powerMod * xpMod;

            float movementMultiplier = slowMult - 0.7f * MathHelper.sqrt(duration / 40F);

            //how fast the shockwave's particle speed is.
            shockwaveSpeed = knockBack;
            //Affect things by the charge. The charge, at stage 3, should set everything to its max.
            damage *= (0.20 + 0.20 * charge);
            //Results in a bigger radius so that it blocks projectiles.
            radius *= (0.60 + 0.10 * charge);
            knockBack *= (0.60 + 0.10 * charge);
            speed *= (1F + charge / 4F);
            performanceAmount *= (0.20 + 0.20 * charge);


            applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));
            //Field of particles/death
            if (world.isRemote && entity.ticksExisted % 4 == 0) {
                Vec3d pos = AvatarEntityUtils.getBottomMiddleOfEntity(entity).add(0, entity.getEyeHeight() / 2, 0);
                //Size starts small gets big
                float size;
                //In case you forgot year 7 maths, radius * 2 * pi = circumference
                int particles;
                int rings;
                //Rings around the player (not around your finger; the police want you)
                // C u l t u r e


                Vec3d centre = AvatarEntityUtils.getBottomMiddleOfEntity(entity).add(0, radius / 2, 0);
                size = radius;
                rings = (int) (radius * 10);
                particles = (int) (radius * Math.PI);


                ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(12 + AvatarUtils.getRandomNumberInRange(0, 4))
                        .element(BendingStyles.get(Demonbending.ID)).clr(1F, 0.05F, 0.05F, 0.10F).spawnEntity(entity).glow(world.rand.nextBoolean())
                        .swirl(rings, particles, radius, size * 20, radius * 20F, 4 * (-1 / size),
                                entity, world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true);

            }

            //Misc effects
            //Used for increasing severity of potion effects and charge speed of the ball
            int mobs = 0;

            if (!world.isRemote) {
                List<Entity> targets = world.getEntitiesWithinAABB(Entity.class, entity.getEntityBoundingBox().grow(radius));
                mobs = targets.size() - 1;
                for (Entity mob : targets) {
                    if (DamageUtils.canDamage(entity, mob)) {
                        mobs++;
                        //potion effects
                        if (mob instanceof EntityLivingBase) {
                            ((EntityLivingBase) mob).addPotionEffect(new PotionEffect(MobEffects.WITHER, 60, 1));
                            ((EntityLivingBase) mob).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 120, 2));
                            ((EntityLivingBase) mob).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 120, 2));
                        }
                        //Stops mobs from leaving the field by pulling them towards the centre
                        if (mob.getDistance(entity) >= radius - radius / 10) {
                            pullEntities(mob, entity, 0.025);
                        }
                    }
                }

                entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 120, (mobs / 3)));
                entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 120, (mobs / 3)));
                entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 120, (mobs / 3)));
            }


            //Some kind of sound effect
            if (entity.ticksExisted % 10 == 0)
                world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.25F * charge, 0.4F + world.rand.nextFloat() / 10);

            //Charging the ball
            EntityInfernalBall ball = AvatarEntity.lookupEntity(world, EntityInfernalBall.class, entityInfernalBall -> entityInfernalBall.getOwner() == entity);
            if (ball != null) {
                ball.setDamage(ball.getDamage() + damage + mobs);
                ball.setEntitySize(ball.getAvgSize() < maxEntitySize ? ball.getAvgSize() + 0.1F : maxEntitySize);
                ball.setTier(hellBastion.getCurrentTier(abilityData));
                ball.setChiHit(3);
                ball.setXp(3);
                ball.setPerformanceAmount(performanceAmount);
            }
            //Dropping the ball
            if (!data.hasStatusControl(RELEASE_HELL_BASTION)) {

                //Behaviour to drop it
                if (ball != null) {
                    ball.setBehaviour(new HellBombBehaviour());
                    ball.setExplosionDamage(damage);
                    ball.setExplosionSize(radius * 3);
                }
                entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(HELL_BASTION_MOVEMENT_MOD_ID);

                world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
                        SoundCategory.BLOCKS, 1, 0.5F);

                return true;
            }
            return !data.hasStatusControl(RELEASE_HELL_BASTION);
        } else {
            return true;
        }
    }

    private void pullEntities(Entity collided, Entity attacker, double suction) {
        Vector velocity = Vector.getEntityPos(collided).minus(Vector.getEntityPos(attacker));
        velocity = velocity.times(suction).times(-1);

        double x = (velocity.x());
        double y = (velocity.y());
        double z = (velocity.z());

        if (!collided.world.isRemote) {
            collided.addVelocity(x, y, z);

            if (collided instanceof AvatarEntity) {
                if (!(collided instanceof EntityWall) && !(collided instanceof EntityWallSegment) && !(collided instanceof EntityIcePrison) && !(collided instanceof EntitySandPrison)) {
                    AvatarEntity avent = (AvatarEntity) collided;
                    avent.addVelocity(x, y, z);
                }
                collided.isAirBorne = true;
                AvatarUtils.afterVelocityAdded(collided);
            }
        }
    }


    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes
                .MOVEMENT_SPEED);

        moveSpeed.removeModifier(HELL_BASTION_MOVEMENT_MOD_ID);

        moveSpeed.applyModifier(new AttributeModifier(HELL_BASTION_MOVEMENT_MOD_ID,
                "Hell Bastion charge modifier", multiplier - 1, 1));

    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        AbilityData abilityData = AbilityData.get(ctx.getBenderEntity(), "hell_bastion");
        if (abilityData != null)
            abilityData.setRegenBurnout(true);
        if (ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(HELL_BASTION_MOVEMENT_MOD_ID) != null)
            ctx.getBenderEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(HELL_BASTION_MOVEMENT_MOD_ID);

    }

    public static class HellBombBehaviour extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            entity.motionX = 0;
            entity.motionY -= 0.005;
            entity.motionZ = 0;
            World world = entity.world;
            if (world.isRemote && entity.getOwner() != null) {
                Vec3d centre = AvatarEntityUtils.getMiddleOfEntity(entity);
                float size = 0.75F * entity.getAvgSize() * (1 / entity.getAvgSize());
                int rings = (int) (entity.getAvgSize() * 4);
                int particles = (int) (entity.getAvgSize() * Math.PI);

                ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(8 + AvatarUtils.getRandomNumberInRange(0, 4)).glow(true)
                        .element(BendingStyles.get(entity.getElement())).clr(120, 40, 40).spawnEntity(entity).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 30)
                        .swirl(rings, particles, entity.getAvgSize() * 1.1F, size * 15, entity.getAvgSize() * 10, (-1 / size),
                                entity, world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).scale(size).time(8 + AvatarUtils.getRandomNumberInRange(0, 4))
                        .element(BendingStyles.get(entity.getElement())).clr(10, 10, 10).spawnEntity(entity).glow(AvatarUtils.getRandomNumberInRange(1, 100) > 60)
                        .swirl(rings, particles, entity.getAvgSize() * 1.1F, size * 15, entity.getAvgSize() * 10, (-1 / size),
                                entity, world, false, centre, ParticleBuilder.SwirlMotionType.OUT, false, true);
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
