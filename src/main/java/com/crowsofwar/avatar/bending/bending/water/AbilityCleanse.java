package com.crowsofwar.avatar.bending.bending.water;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.water.tickhandlers.CleansePowerModifier;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

/**
 * @author FavouriteDragon
 * <p>
 * Desc: Heal yourself and nearby allies, while improving your bending! Also extinguishes flame.
 * <p>
 * Level 1 - Heal and buff!
 * Level 2 - Stronger healing, bigger radius! Movement speed.
 * Level 3 - Removes basic negative effects. Applies saturation.
 * Level 4 Path 1 - Ocean's Grace: Regeneration and Instant Health
 * Level 4 Path 2 - Water's Fall: Speed and Bending Power
 */
public class AbilityCleanse extends Ability {

    public static final String
            //For bad effects
            HIGHEST_LEVEL_CLEANSED = "highestLevelCleansed";

    public AbilityCleanse() {
        super(Waterbending.ID, "cleanse");
    }

    @Override
    public boolean isBuff() {
        return true;
    }

    @Override
    public void init() {
        super.init();
        addProperties(SPEED_DURATION, SPEED_LEVEL,
                SATURATION_DURATION, SATURATION_LEVEL, RADIUS, REGEN_DURATION, REGEN_LEVEL,
                INSTANT_HEALTH_LEVEL, SOURCE_RANGE, SOURCE_ANGLES, HIGHEST_LEVEL_CLEANSED, WATER_LEVEL,
                ABSORPTION_DURATION, ABSORPTION_LEVEL);
    }

    @Override
    public void execute(AbilityContext ctx) {

        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        AbilityData abilityData = data.getAbilityData(this);
        AbilityCleanse cleanse = (AbilityCleanse) Abilities.get("cleanse");


        Vector targetPos = Waterbending.getClosestWaterbendableBlock(entity, this, ctx);

        if (cleanse != null && (bender.consumeChi(getChiCost(ctx)) && targetPos != null || (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative())
                || ctx.consumeWater(4))) {

            //TODO: Water breathing??
            int duration = cleanse.getProperty(DURATION, ctx).intValue();
            duration = (int) powerModify(duration, abilityData);

            int radius = cleanse.getProperty(RADIUS, ctx).intValue();
            int regenLevel = cleanse.getProperty(REGEN_LEVEL, ctx).intValue();
            int regenDuration = cleanse.getProperty(REGEN_DURATION, ctx).intValue();
            int saturationLevel = cleanse.getProperty(SATURATION_LEVEL, ctx).intValue();
            int saturationDuration = cleanse.getProperty(SATURATION_DURATION, ctx).intValue();
            int speedLevel = cleanse.getProperty(SPEED_LEVEL, ctx).intValue();
            int speedDuration = cleanse.getProperty(SPEED_DURATION, ctx).intValue();
            int healthLevel = cleanse.getProperty(INSTANT_HEALTH_LEVEL, ctx).intValue();
            int absorptionLevel = cleanse.getProperty(ABSORPTION_LEVEL, ctx).intValue();
            int absorptionDuration = cleanse.getProperty(ABSORPTION_DURATION, ctx).intValue();


            //Potion effects
            PotionEffect regen = new PotionEffect(MobEffects.REGENERATION, regenDuration, regenLevel);
            PotionEffect saturation = new PotionEffect(MobEffects.SATURATION, saturationDuration, saturationLevel);
            PotionEffect speed = new PotionEffect(MobEffects.SPEED, speedDuration, speedLevel);
            PotionEffect health = new PotionEffect(MobEffects.INSTANT_HEALTH, 1, healthLevel);
            PotionEffect absorption = new PotionEffect(MobEffects.ABSORPTION, absorptionDuration, absorptionLevel);

            //Potion effects to the player
            entity.addPotionEffect(regen);
            entity.addPotionEffect(saturation);
            entity.addPotionEffect(speed);
            entity.addPotionEffect(health);
            entity.addPotionEffect(absorption);

            //Potion effects to nearby players
            applyGroupEffect(ctx, radius, player -> player.addPotionEffect(regen));
            applyGroupEffect(ctx, radius, player -> player.addPotionEffect(saturation));
            applyGroupEffect(ctx, radius, player -> player.addPotionEffect(speed));
            applyGroupEffect(ctx, radius, player -> player.addPotionEffect(health));
            applyGroupEffect(ctx, radius, player -> player.addPotionEffect(absorption));

            //Power modifiers
            //Initially to self
            applyPowerMod(entity, ctx, duration);
            //Now to group (frickin lambdas)
            int finalDuration = duration;
            applyGroupEffect(ctx, radius, player -> applyPowerMod(player, ctx, finalDuration));
            abilityData.addXp(cleanse.getProperty(XP_USE, ctx).floatValue());

        } else {
            bender.sendMessage("avatar.waterSourceFail");
        }

    }

    /**
     * Applies the given effect to all nearby players in the given range, excluding the
     * caster. Range is in blocks.
     */
    private void applyGroupEffect(AbilityContext ctx, int radius, Consumer<EntityPlayer> effect) {

        World world = ctx.getWorld();
        EntityLivingBase entity = ctx.getBenderEntity();
        AxisAlignedBB aabb = new AxisAlignedBB(
                entity.posX - radius, entity.posY - radius, entity.posZ - radius,
                entity.posX + radius, entity.posY + radius, entity.posZ + radius);

        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, aabb);

        for (EntityPlayer player : players) {

            // Initial aabb check was rectangular, need to check distance for truly circular radius
            if (player.getDistanceSq(entity) > radius * radius) {
                continue;
            }

            // Ignore the caster
            if (player == entity) {
                continue;
            }

            effect.accept(player);

        }

    }

    /**
     * Grants the player a chi bonus
     */
    private void addChiBonus(EntityPlayer player) {

        BendingData data = BendingData.getFromEntity(player);
        if (data != null) {
            data.chi().changeTotalChi(STATS_CONFIG.cleanseChiGroupBonus);
            data.chi().changeAvailableChi(STATS_CONFIG.cleanseChiGroupBonus);
        }
    }

    private void applyPowerMod(EntityLivingBase entity, AbilityContext ctx, int duration) {
        BendingData data = BendingData.getFromEntity(entity);
        CleansePowerModifier modifier = new CleansePowerModifier();
        modifier.setTicks(duration);
        if (data != null && data.getPowerRatingManager(Waterbending.ID) != null)
            Objects.requireNonNull(data.getPowerRatingManager(Waterbending.ID)).addModifier(modifier, ctx);
    }

    @Override
    public int getBaseTier() {
        return 5;
    }

    @Override
    public int getCooldown(AbilityContext ctx) {
        EntityLivingBase entity = ctx.getBenderEntity();
        int coolDown = 160;

        if (ctx.getLevel() == 1) {
            coolDown = 140;
        }
        if (ctx.getLevel() == 2) {
            coolDown = 120;
        }
        if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
            coolDown = 130;
        }
        if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
            coolDown = 110;
        }

        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
            coolDown = 0;
        }

        return coolDown;
    }


}

