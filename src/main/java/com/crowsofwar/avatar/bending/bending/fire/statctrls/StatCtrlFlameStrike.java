package com.crowsofwar.avatar.bending.bending.fire.statctrls;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.fire.AbilityFlameStrike.STRIKES;
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
        AbilityFlameStrike strike = (AbilityFlameStrike) Abilities.get(new AbilityFlameStrike().getName());

        if (!ctx.getData().hasTickHandler(FLAME_STRIKE_HANDLER))
            return true;
        if (!entity.getHeldItem(hand).isEmpty())
            return false;

        float size = strike.getProperty(Ability.SIZE, abilityData).floatValue();
        float accuracyMult = 0.05F;
        int particleCount = 3;

        double powerFactor = ctx.getBender().calcPowerRating(Firebending.ID) / 100D;
        float powerModifier = (float) (bender.getDamageMult(Firebending.ID));
        float xpMod = abilityData.getXpModifier();

        float damage = strike.getProperty(Ability.DAMAGE, abilityData).floatValue();
        int performance = strike.getProperty(Ability.PERFORMANCE, abilityData).intValue();
        int fireTime = strike.getProperty(Ability.FIRE_TIME, abilityData).intValue();
        float xp = strike.getProperty(Ability.XP_HIT, abilityData).floatValue();
        float mult = strike.getProperty(Ability.SPEED, abilityData).floatValue() / 10F;
        int lifeTime = strike.getProperty(Ability.LIFETIME, abilityData).intValue();

        int r, g, b, fadeR, fadeG, fadeB;
        r = strike.getProperty(Ability.FIRE_R, abilityData).intValue();
        g = strike.getProperty(Ability.FIRE_G, abilityData).intValue();
        b = strike.getProperty(Ability.FIRE_B, abilityData).intValue();
        fadeR = strike.getProperty(Ability.FADE_R, abilityData).intValue();
        fadeG = strike.getProperty(Ability.FADE_G, abilityData).intValue();
        fadeB = strike.getProperty(Ability.FADE_B, abilityData).intValue();

        float burnout = strike.getProperty(Ability.BURNOUT, abilityData).floatValue();
        float chiCost = strike.getChiCost(abilityData);
        float exhaustion = strike.getProperty(Ability.EXHAUSTION, abilityData).floatValue();
        int cooldown = strike.getProperty(Ability.COOLDOWN, abilityData).intValue();


        if (abilityData.getLevel() == 1) {
            particleCount += 2;
        }
        if (abilityData.getLevel() == 2) {
            particleCount += 4;
            accuracyMult *= 0.95F;
        }
        if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
            particleCount -= 2;
            accuracyMult = 0.04F;
        }
        if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
            particleCount += 2;
            accuracyMult *= 2;
        }

        //Boosting factors
        lifeTime += powerFactor * 3 * xpMod;
        mult += powerFactor / 10 * xpMod;
        size *= (1 + powerFactor * 0.5 * xpMod);
        damage *= powerModifier * xpMod;
        fireTime *= powerModifier * xpMod;
        performance *= (powerModifier * xpMod * 0.5 + 1);

        //Inhibitor factors
        cooldown -= cooldown * powerModifier * xpMod * 0.5;
        exhaustion -= exhaustion * powerModifier * xpMod * 0.25;
        burnout -= burnout * powerModifier * xpMod * 0.25;


        Vec3d look = entity.getLookVec();
        double eyePos = entity.getEyeHeight() + entity.getEntityBoundingBox().minY;

        //Sets it to 0 if the entity is in creative mode.
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
            chiCost = burnout = exhaustion = cooldown = 0;
        }

        if (bender.consumeChi(chiCost)) {
            abilityData.setBurnOut(abilityData.getBurnOut() + burnout);
            if (entity instanceof EntityPlayer)
                ((EntityPlayer) entity).addExhaustion(exhaustion);


            //Spawn particles
            for (int i = 0; i < 24 + particleCount * 2; i++) {
                double x1 = entity.posX + look.x * i / 50 + world.rand.nextGaussian() * accuracyMult;
                double y1 = eyePos - 0.4F + world.rand.nextGaussian() * accuracyMult;
                double z1 = entity.posZ + look.z * i / 50 + world.rand.nextGaussian() * accuracyMult;

                //140, 90, 90
                int rRandom = fadeR < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeR * 2) : AvatarUtils.getRandomNumberInRange(fadeR / 2,
                        fadeR * 2);
                int gRandom = fadeG < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeG * 2) : AvatarUtils.getRandomNumberInRange(fadeG / 2,
                        fadeG * 2);
                int bRandom = fadeB < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeB * 2) : AvatarUtils.getRandomNumberInRange(fadeB / 2,
                        fadeB * 2);

                if (world.isRemote) {
                    //Using the random function each time ensures a different number for every value, making the ability "feel" better.
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
                            look.y * mult + world.rand.nextGaussian() * accuracyMult,
                            look.z * mult + world.rand.nextGaussian() * accuracyMult)
                            .element(new Firebending()).ability(strike).spawnEntity(entity)
                            .clr(r, g, b).collide(true).scale(size * 0.75F).time(lifeTime + AvatarUtils.getRandomNumberInRange(1, 5))
                            .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175)).spawn(world);
                    //Using the random function each time ensures a different number for every value, making the ability "feel" better.
                    rRandom = fadeR < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeR * 2) : AvatarUtils.getRandomNumberInRange(fadeR / 2,
                            fadeR * 2);
                    gRandom = fadeG < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeG * 2) : AvatarUtils.getRandomNumberInRange(fadeG / 2,
                            fadeG * 2);
                    bRandom = fadeB < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeB * 2) : AvatarUtils.getRandomNumberInRange(fadeB / 2,
                            fadeB * 2);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1, y1, z1).vel(look.x * mult + world.rand.nextGaussian() * accuracyMult,
                            look.y * mult + world.rand.nextGaussian() * accuracyMult,
                            look.z * mult + world.rand.nextGaussian() * accuracyMult)
                            .element(new Firebending()).ability(strike).spawnEntity(entity)
                            .clr(255, 60 + AvatarUtils.getRandomNumberInRange(0, 60), 10).collide(true)
                            .scale(size * 0.75F).time(lifeTime + AvatarUtils.getRandomNumberInRange(1, 5))
                            .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175)).spawn(world);
                }
                if (i % 3 == 0) {
                    EntityFlame flames = new EntityFlame(world);
                    flames.setOwner(entity);
                    flames.setDynamicSpreadingCollision(false);
                    flames.setAbility(strike);
                    flames.setTier(strike.getCurrentTier(abilityData));
                    //Will need to be changed later as I go through and add in the new ability config
                    flames.setXp(xp);
                    flames.setLifeTime((int) (lifeTime * 0.785) + AvatarUtils.getRandomNumberInRange(0, 4));
                    //Make a property later
                    flames.setTrailingFire(strike.getBooleanProperty(Ability.SETS_FIRES, abilityData) && world.rand.nextBoolean());
                    flames.setDamage(damage);
                    flames.setSmelt(strike.getBooleanProperty(Ability.SMELTS, abilityData));
                    flames.setFireTime(fireTime);
                    flames.setPerformanceAmount(performance);
                    flames.setElement(new Firebending());
                    flames.setPosition(x1, y1, z1);
                    flames.setChiHit(strike.getProperty(Ability.CHI_HIT, abilityData).floatValue());
                    flames.setTrailingFire(strike.getBooleanProperty(Ability.SETS_FIRES, abilityData) && world.rand.nextBoolean());
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

            if (ctx.getData().hasTickHandler(FLAME_STRIKE_HANDLER))
                ctx.getData().addStatusControl(hand == EnumHand.MAIN_HAND ? FLAME_STRIKE_OFF : FLAME_STRIKE_MAIN);

            if (!world.isRemote)
                setTimesUsed(entity.getPersistentID(), getTimesUsed(entity.getPersistentID()) + 1);

            if (!world.isRemote && getTimesUsed(entity.getPersistentID()) >= strike.getProperty(STRIKES, abilityData).intValue())
                abilityData.setAbilityCooldown(cooldown);

        }
        return true;
    }
}
