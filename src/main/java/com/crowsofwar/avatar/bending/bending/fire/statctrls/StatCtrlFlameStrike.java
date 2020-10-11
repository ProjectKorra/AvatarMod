package com.crowsofwar.avatar.bending.bending.fire.statctrls;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFlameStrike;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityFlames;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.fire.AbilityFlameStrike.STRIKES;
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


    @Override
    public boolean execute(BendingContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        World world = ctx.getWorld();
        AbilityData abilityData = ctx.getData().getAbilityData("flame_strike");
        Bender bender = Bender.get(entity);
        AbilityFlameStrike strike = (AbilityFlameStrike) Abilities.get(new AbilityFlameStrike().getName());

        if (strike == null || bender == null || !ctx.getData().hasTickHandler(FLAME_STRIKE_HANDLER))
            return true;

        if (!entity.getHeldItem(hand).isEmpty())
            return false;

        float size = strike.getProperty(Ability.SIZE, abilityData).floatValue();
        float accuracyMult = 0.05F;
        int particleCount = 3;

        double powerFactor = ctx.getBender().calcPowerRating(Firebending.ID) / 100D;
        float powerModifier = (float) abilityData.getDamageMult();
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

        float burnout = strike.getBurnOut(abilityData);
        float chiCost = strike.getChiCost(abilityData);
        float exhaustion = strike.getExhaustion(abilityData);
        int cooldown = strike.getCooldown(abilityData);


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
        size *= powerModifier * xpMod;
        damage *= powerModifier * xpMod;
        fireTime *= powerModifier * xpMod;
        performance *= (powerModifier * xpMod * 0.5 + 1);


        Vec3d look = entity.getLookVec();
        double eyePos = entity.getEyeHeight() + entity.getEntityBoundingBox().minY;

        //Sets it to 0 if the entity is in creative mode.
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
            chiCost = burnout = exhaustion = cooldown = 0;
        }

        System.out.println(chiCost);

        if (bender.consumeChi(chiCost)) {
            abilityData.addBurnout(abilityData.getBurnOut() + burnout);
            if (entity instanceof EntityPlayer)
                ((EntityPlayer) entity).addExhaustion(exhaustion);


            //Spawn particles
            for (int i = 0; i < 24 + particleCount * 2; i++) {
                double x1 = entity.posX + look.x * i / 50 + world.rand.nextGaussian() * accuracyMult + entity.motionX;
                double y1 = eyePos - 0.4F + world.rand.nextGaussian() * accuracyMult;
                double z1 = entity.posZ + look.z * i / 50 + world.rand.nextGaussian() * accuracyMult + entity.motionZ;

                //140, 90, 90 = rainbow of fun
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
                            .clr(r, g, b, 150).fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(50, 140)).collide(true).collideParticles(true)
                            .scale(size * 0.75F).time(lifeTime + AvatarUtils.getRandomNumberInRange(1, 5)).
                            spawn(world);
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
                            .clr(255, 60 + AvatarUtils.getRandomNumberInRange(0, 60), 10, 150).collide(true).collideParticles(true)
                            .scale(size * 0.75F).time(lifeTime + AvatarUtils.getRandomNumberInRange(1, 5))
                            .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(40, 140)).spawn(world);
                }
                if (i % 3 == 0) {
                    EntityFlames flames = new EntityFlames(world);
                    flames.setOwner(entity);
                    flames.setDynamicSpreadingCollision(false);
                    flames.setAbility(strike);
                    flames.setTier(strike.getCurrentTier(abilityData));
                    //Will need to be changed later as I go through and add in the new ability config
                    flames.setXp(xp);
                    flames.setLifeTime((int) (lifeTime * 0.785) + AvatarUtils.getRandomNumberInRange(0, 4));
                    //Make a property later
                    flames.setTrailingFires(strike.getBooleanProperty(Ability.SETS_FIRES, abilityData) && world.rand.nextBoolean());
                    flames.setFires(strike.getBooleanProperty(Ability.SETS_FIRES, abilityData) && world.rand.nextBoolean());
                    flames.setDamage(damage);
                    flames.setSmelts(strike.getBooleanProperty(Ability.SMELTS, abilityData));
                    flames.setFireTime(fireTime);
                    flames.setBehaviour(new FlameStrikeBehaviour());
                    flames.setPerformanceAmount(performance);
                    flames.setRGB(r, g, b);
                    flames.setFade(fadeR, fadeG, fadeB);
                    flames.setElement(new Firebending());
                    flames.setTier(strike.getCurrentTier(abilityData));
                    flames.setPosition(x1, y1, z1);
                    flames.setDamageSource("avatar_Fire_flameStrike");
                    flames.setChiHit(strike.getProperty(Ability.CHI_HIT, abilityData).floatValue());
                    flames.setVelocity(new Vec3d(look.x * mult + world.rand.nextGaussian() * accuracyMult + entity.motionX,
                            look.y * mult + world.rand.nextGaussian() * accuracyMult,
                            look.z * mult + world.rand.nextGaussian() * accuracyMult + entity.motionZ));
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


            world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.0F + Math.max(abilityData.getLevel() * 0.5F, 0),
                    1.25F * world.rand.nextFloat(), false);

            if (!world.isRemote && getTimesUsed(entity.getPersistentID()) >= strike.getProperty(STRIKES, abilityData).intValue()) {
                abilityData.setAbilityCooldown(cooldown);
                abilityData.setRegenBurnout(true);
                ctx.getData().removeTickHandler(FLAME_STRIKE_HANDLER, ctx);
                return true;
            }
        }
        else {
            abilityData.setAbilityCooldown(cooldown);
            abilityData.setRegenBurnout(true);
        }
        return true;
    }

    public static class FlameStrikeBehaviour extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity.getOwner() != null) {
                entity.motionX *= 0.95;
                entity.motionY *= 0.95;
                entity.motionZ *= 0.95;
                if (entity.world.isRemote && entity.ticksExisted > 1) {
                    int[] fade = entity.getFade();
                    int[] rgb = entity.getRGB();
                    for (double i = 0; i < entity.width; i += 0.1 * entity.getAvgSize() * 4) {
                        int rRandom = fade[0] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[0] * 2) : AvatarUtils.getRandomNumberInRange(fade[0] / 2,
                                fade[0] * 2);
                        int gRandom = fade[1] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[1] * 2) : AvatarUtils.getRandomNumberInRange(fade[1] / 2,
                                fade[1] * 2);
                        int bRandom = fade[2] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[2] * 2) : AvatarUtils.getRandomNumberInRange(fade[2] / 2,
                                fade[2] * 2);
                        Random random = new Random();
                        Vec3d box = AvatarEntityUtils.getMiddleOfEntity(entity);
                        AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
                        double spawnX = box.x + random.nextDouble() * 1.5 * (boundingBox.maxX - boundingBox.minX);
                        double spawnY = box.y + random.nextDouble() * 1.5 * (boundingBox.maxY - boundingBox.minY);
                        double spawnZ = box.z + random.nextDouble() * 1.5 * (boundingBox.maxZ - boundingBox.minZ);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(entity.world.rand.nextGaussian() / 30,
                                entity.world.rand.nextGaussian() / 30, entity.world.rand.nextGaussian() / 30).time(5 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(rgb[0], rgb[1], rgb[2])
                                .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175)).scale(entity.getAvgSize() * 2F).element(entity.getElement())
                                .ability(entity.getAbility()).spawnEntity(entity.getOwner()).collide(true).collideParticles(true).spawn(entity.world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(entity.world.rand.nextGaussian() / 30,
                                entity.world.rand.nextGaussian() / 30, entity.world.rand.nextGaussian() / 30).time(5 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(rgb[0], rgb[1], rgb[2])
                                .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175)).scale(entity.getAvgSize() * 2F).element(entity.getElement())
                                .ability(entity.getAbility()).spawnEntity(entity.getOwner()).collide(true).collideParticles(true).spawn(entity.world);
                        ParticleBuilder.create(ParticleBuilder.Type.FIRE).pos(spawnX, spawnY, spawnZ).vel(entity.world.rand.nextGaussian() / 50,
                                entity.world.rand.nextGaussian() / 50, entity.world.rand.nextGaussian() / 50).time(5 + AvatarUtils.getRandomNumberInRange(0, 2)).scale(entity.getAvgSize() / 2)
                                .element(entity.getElement()).ability(entity.getAbility()).spawnEntity(entity.getOwner()).collide(true).collideParticles(true).spawn(entity.world);
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
