package com.crowsofwar.avatar.bending.bending.fire.statctrls;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFlameStrike;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.damageutils.DamageUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityFlame;
import com.crowsofwar.avatar.entity.EntityShield;
import com.crowsofwar.avatar.entity.IShieldEntity;
import com.crowsofwar.avatar.util.event.ParticleCollideEvent;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_LEFT_CLICK;
import static com.crowsofwar.avatar.client.controls.AvatarControl.CONTROL_RIGHT_CLICK;
import static com.crowsofwar.avatar.util.data.StatusControlController.FLAME_STRIKE_MAIN;
import static com.crowsofwar.avatar.util.data.StatusControlController.FLAME_STRIKE_OFF;
import static com.crowsofwar.avatar.util.data.TickHandlerController.FLAME_STRIKE_HANDLER;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class StatCtrlFlameStrike extends StatusControl {

    private static final HashMap<UUID, Integer> timesUsed = new HashMap<>();
    private static final HashMap<UUID, Integer> chargeLevel = new HashMap<>();
    EnumHand hand;

    public StatCtrlFlameStrike(EnumHand hand) {
        super(18, hand == EnumHand.MAIN_HAND ? CONTROL_LEFT_CLICK : CONTROL_RIGHT_CLICK,
                hand == EnumHand.MAIN_HAND ? CrosshairPosition.LEFT_OF_CROSSHAIR : CrosshairPosition.RIGHT_OF_CROSSHAIR);
        this.hand = hand;
    }

    public static int getTimesUsed(UUID id) {
        return timesUsed.getOrDefault(id, 0);
    }

    public static void setTimesUsed(UUID id, int times) {
        if (timesUsed.containsKey(id))
            timesUsed.replace(id, times);
        else timesUsed.put(id, times);
    }

    public static int getChargeLevel(UUID id) {
        return chargeLevel.getOrDefault(id, 1);
    }

    public static void setChargeLevel(UUID id, int level) {
        if (chargeLevel.containsKey(id)) {
            chargeLevel.replace(id, level);
        } else chargeLevel.put(id, level);
    }


    @SubscribeEvent
    public static void particleCollision(ParticleCollideEvent event) {
        if (event.getAbility() instanceof AbilityFlameStrike) {
            if (event.getSpawner() != event.getEntity()) {
                if (event.getSpawner() instanceof EntityLivingBase && event.getEntity() != null && event.getVelocity() != null) {
                    if (AvatarUtils.getMagnitude(event.getVelocity()) > 0.5)
                        attackEntity((EntityLivingBase) event.getSpawner(), event.getEntity(), event.getVelocity());
                }
            }
        }
    }

    private static boolean attackEntity(EntityLivingBase attacker, Entity target, Vec3d vel) {
        AbilityData abilityData = AbilityData.get(attacker, new AbilityFlameStrike().getName());
        Bender bender = Bender.get(attacker);
        World world = attacker.world;
        if (abilityData != null && bender != null && !world.isRemote) {
            float powerModifier = (float) (bender.getDamageMult(Firebending.ID));
            float xpMod = abilityData.getXpModifier();

            float damage = STATS_CONFIG.flameStrikeSettings.damage;
            int performance = STATS_CONFIG.flameStrikeSettings.performanceAmount;
            float knockBack = STATS_CONFIG.flameStrikeSettings.knockback;
            int fireTime = STATS_CONFIG.flameStrikeSettings.fireTime;
            float xp = SKILLS_CONFIG.flameStrikeHit;

            if (abilityData.getLevel() == 1) {
                damage *= 1.25F;
                knockBack *= 1.125F;
                fireTime += 2;
                performance += 2;
            }
            if (abilityData.getLevel() == 2) {
                damage *= 2F;
                knockBack *= 1.25F;
                fireTime += 4;
                performance += 5;
            }
            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
                damage *= 2.5F;
                performance += 10;
                fireTime += 3;
            }
            if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
                damage *= 4;
                performance += 2;
                fireTime += 5;
            }

            damage *= powerModifier * xpMod;
            knockBack *= powerModifier * xpMod;
            fireTime *= powerModifier * xpMod;
            performance *= powerModifier * xpMod;

            vel = vel.scale(0.0005).scale(knockBack);


            if (canDamageEntity(target, attacker)) {
                if (!(target instanceof EntityLivingBase) || ((EntityLivingBase) target).attackable() &&
                        ((EntityLivingBase) target).hurtTime == 0)
                    DamageUtils.attackEntity(attacker, target, AvatarDamageSource.causeFireDamage(target, attacker), damage, performance,
                            new AbilityFlameStrike(), xp);
                else {
                    //NOTE: Add velocity like this is great for stuff like a water blast!
                    target.addVelocity(vel.x, vel.y + 0.15, vel.z);
                    target.motionY = Math.min(0.15, target.motionY);
                }

            } else if (canCollideWithEntity(target, attacker)) {
                //NOTE: Add velocity like this is great for stuff like a water blast!
                target.addVelocity(vel.x, vel.y + 0.15, vel.z);
                target.motionY = Math.min(0.15, target.motionY);
            }
            target.setFire(fireTime);

        }
        return false;
    }

    private static boolean canCollideWithEntity(Entity entity, Entity owner) {
        if (entity instanceof AvatarEntity) {
            if (((AvatarEntity) entity).getOwner() == owner)
                return false;
            else if (!entity.canBeCollidedWith())
                return false;
            else if (entity instanceof EntityShield)
                return true;
        } else if (entity.isOnSameTeam(owner))
            return false;
        else if (entity instanceof EntityTameable && ((EntityTameable) entity).getOwner() == owner)
            return false;
        else if (entity.getRidingEntity() == owner)
            return false;
        return entity instanceof EntityLivingBase || entity instanceof EntityEnderCrystal || entity.canBeCollidedWith() || entity instanceof EntityArrow
                || entity instanceof EntityThrowable;
    }

    private static boolean canDamageEntity(Entity entity, Entity owner) {
        if (entity instanceof AvatarEntity) {
            if (((AvatarEntity) entity).getOwner() == owner)
                return false;
            else if (!entity.canBeCollidedWith())
                return false;
            else if (entity instanceof EntityShield || entity instanceof IShieldEntity)
                return true;
        } else if (entity.isOnSameTeam(owner))
            return false;
        else if (entity instanceof EntityTameable && ((EntityTameable) entity).getOwner() == owner)
            return false;
        else if (entity.getRidingEntity() == owner)
            return false;
        return entity instanceof EntityLivingBase || entity instanceof EntityEnderCrystal || entity.canBeCollidedWith() && entity.canBeAttackedWithItem();
    }

    @Override
    public boolean execute(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        AbilityData abilityData = ctx.getData().getAbilityData("flame_strike");
        Bender bender = Bender.get(entity);

        if (!ctx.getData().hasTickHandler(FLAME_STRIKE_HANDLER))
            return true;
        if (!entity.getHeldItem(hand).isEmpty())
            return false;

        float size = STATS_CONFIG.flameStrikeSettings.size;
        float dist = STATS_CONFIG.flameStrikeSettings.maxDistance;
        float accuracyMult = 0.05F;
        int particleCount = 3;
        float mult = 0.5F;
        double powerFactor = ctx.getBender().calcPowerRating(Firebending.ID) / 100D;
        float powerModifier = (float) (bender.getDamageMult(Firebending.ID));
        float xpMod = abilityData.getXpModifier();

        float damage = STATS_CONFIG.flameStrikeSettings.damage;
        int performance = STATS_CONFIG.flameStrikeSettings.performanceAmount;
        int fireTime = STATS_CONFIG.flameStrikeSettings.fireTime;
        float xp = SKILLS_CONFIG.flameStrikeHit;


        if (abilityData.getLevel() == 1) {
            particleCount += 2;
            size *= 1.125;
            mult += 0.0125F;
            damage *= 1.25F;
            fireTime += 2;
            performance += 2;
        }
        if (abilityData.getLevel() == 2) {
            particleCount += 4;
            size *= 1.25;
            dist = 4;
            mult += 0.1F;
            accuracyMult *= 0.75F;
            damage *= 1.5F;
            fireTime += 4;
            performance += 5;
        }
        if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
            size *= 0.75F;
            accuracyMult = 0.04F;
            dist = 7;
            mult = 0.7F;
            damage *= 1.75F;
            performance += 10;
            particleCount -= 2;
            fireTime += 3;
        }
        if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
            size *= 1.75F;
            particleCount -= 5;
            accuracyMult *= 2;
            mult = 1F;
            damage *= 2.5;
            performance += 2;
            fireTime += 5;
        }

        int lifeTime = (int) dist * 2 + 4;

        lifeTime += powerFactor * 3;
        mult += powerFactor / 10;
        size *= (1 + powerFactor * 0.5);
        damage *= powerModifier * xpMod;
        fireTime *= powerModifier * xpMod;
        performance *= powerModifier * xpMod;


        Vec3d look = entity.getLookVec();
        double eyePos = entity.getEyeHeight() + entity.getEntityBoundingBox().minY;


        //Spawn particles

        for (int i = 0; i < 24 + particleCount * 2; i++) {
            double x1 = entity.posX + look.x * i / 50 + world.rand.nextGaussian() * accuracyMult;
            double y1 = eyePos - 0.4F + world.rand.nextGaussian() * accuracyMult;
            double z1 = entity.posZ + look.z * i / 50 + world.rand.nextGaussian() * accuracyMult;
            if (world.isRemote) {
                if (abilityData.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
                    //Using the random function each time ensures a different number for every value, making the ability "feel" better.
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
                            look.y * mult + world.rand.nextGaussian() * accuracyMult,
                            look.z * mult + world.rand.nextGaussian() * accuracyMult)
                            .element(new Firebending()).ability(new AbilityFlameStrike()).spawnEntity(entity)
                            .clr(255, 15, 5).collide(true).scale(size / 2).time(lifeTime + AvatarUtils.getRandomNumberInRange(1, 5))
                            .fade(AvatarUtils.getRandomNumberInRange(75, 200), AvatarUtils.getRandomNumberInRange(1, 180),
                                    AvatarUtils.getRandomNumberInRange(1, 180), AvatarUtils.getRandomNumberInRange(100, 175)).spawn(world);
                    //Using the random function each time ensures a different number for every value, making the ability "feel" better.
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
                            look.y * mult + world.rand.nextGaussian() * accuracyMult,
                            look.z * mult + world.rand.nextGaussian() * accuracyMult)
                            .element(new Firebending()).ability(new AbilityFlameStrike()).spawnEntity(entity)
                            .clr(255, 60 + AvatarUtils.getRandomNumberInRange(0, 60), 10).collide(true)
                            .scale(size / 2).time(lifeTime + AvatarUtils.getRandomNumberInRange(1, 5))
                            .fade(AvatarUtils.getRandomNumberInRange(75, 200), AvatarUtils.getRandomNumberInRange(1, 180),
                                    AvatarUtils.getRandomNumberInRange(1, 180), AvatarUtils.getRandomNumberInRange(100, 175)).spawn(world);
                }
                //Using the random function each time ensures a different number for every value, making the ability "feel" better.
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
                        look.y * mult + world.rand.nextGaussian() * accuracyMult,
                        look.z * mult + world.rand.nextGaussian() * accuracyMult)
                        .element(new Firebending()).ability(new AbilityFlameStrike()).spawnEntity(entity)
                        .clr(255, 15, 5).collide(true).scale(size / 2).time(lifeTime + AvatarUtils.getRandomNumberInRange(1, 5)).spawn(world);
                //Using the random function each time ensures a different number for every value, making the ability "feel" better.
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
                        look.y * mult + world.rand.nextGaussian() * accuracyMult,
                        look.z * mult + world.rand.nextGaussian() * accuracyMult)
                        .element(new Firebending()).ability(new AbilityFlameStrike()).spawnEntity(entity)
                        .clr(255, 60 + AvatarUtils.getRandomNumberInRange(0, 60), 10).collide(true)
                        .scale(size / 2).time(lifeTime + AvatarUtils.getRandomNumberInRange(1, 5)).spawn(world);
            }
            if (i % 2 == 0) {
                EntityFlame flames = new EntityFlame(world);
                flames.setOwner(entity);
                flames.setDynamicSpreadingCollision(true);
                flames.setEntitySize(0.1F, 0.1F);
                flames.setAbility(new AbilityFlameStrike());
                flames.setTier(new AbilityFlameStrike().getCurrentTier(abilityData));
                //Will need to be changed later as I go through and add in the new ability config
                flames.setXp(xp);
                flames.setLifeTime((int) (lifeTime * 0.75) + AvatarUtils.getRandomNumberInRange(0, 4));
                flames.setTrailingFire(abilityData.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND));
                flames.setDamage(damage);
                flames.setSmelt(true);
                flames.setFireTime(fireTime);
                flames.setPerformanceAmount(performance);
                flames.setElement(new Firebending());
                flames.setPosition(x1, y1, z1);
                flames.setTrailingFire(abilityData.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST));
                flames.setVelocity(new Vec3d(look.x * mult + world.rand.nextGaussian() * accuracyMult,
                        look.y * mult + world.rand.nextGaussian() * accuracyMult,
                        look.z * mult + world.rand.nextGaussian() * accuracyMult));
                flames.setEntitySize(size / 8);
                if (!world.isRemote)
                    world.spawnEntity(flames);
            }
        }


        if (hand == EnumHand.OFF_HAND)
            entity.swingArm(hand);

        if (!world.isRemote)
            setTimesUsed(entity.getPersistentID(), getTimesUsed(entity.getPersistentID()) + 1);

        if (ctx.getData().hasTickHandler(FLAME_STRIKE_HANDLER))
            ctx.getData().addStatusControl(hand == EnumHand.MAIN_HAND ? FLAME_STRIKE_OFF : FLAME_STRIKE_MAIN);

        return true;
    }
}
