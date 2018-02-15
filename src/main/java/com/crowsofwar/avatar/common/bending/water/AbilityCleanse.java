package com.crowsofwar.avatar.common.bending.water;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityCleanse extends Ability {

	public AbilityCleanse() {
		super(Waterbending.ID, "cleanse");
	}

	@Override
	public boolean isBuff() {
		return true;
	}

	@Override
	public void execute(AbilityContext ctx) {

		BendingData data = ctx.getData();
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		AbilityData abilityData = data.getAbilityData(this);

		float chi = STATS_CONFIG.chiBuff;
		if (abilityData.getLevel() == 1) {
			chi = STATS_CONFIG.chiBuffLvl2;
		} else if (abilityData.getLevel() == 2) {
			chi = STATS_CONFIG.chiBuffLvl3;
		} else if (abilityData.getLevel() == 3) {
			chi = STATS_CONFIG.chiBuffLvl4;
		}

		if (bender.consumeChi(chi)) {

			// Duration: 5-10s
			int duration = abilityData.getLevel() < 2 ? 100 : 200;
			int regenLevel = MathHelper.clamp(abilityData.getLevel(), 0, 2);

			abilityData.addXp(SKILLS_CONFIG.buffUsed);

			// Apply basic potion effects

			entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, regenLevel));

			if (abilityData.getLevel() >= 2) {
				entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, duration));
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, duration, 1));
				entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, duration, 1));
				entity.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 0, 0));
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, duration));
				entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, 1));
			}

			// Perform group heal?

			if (abilityData.getLevel() >= 1) {

				int groupLevel = abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)
						? 1 : 0;
				int groupDuration = abilityData.getLevel() == 3 ? 100 : 60;
				int groupRadius = abilityData.getLevel() >= 2 ? 6 : 4;

				PotionEffect groupEffect = new PotionEffect(MobEffects.REGENERATION, groupDuration,
						groupLevel);
				applyGroupEffect(ctx, groupRadius, player -> player.addPotionEffect(groupEffect));
				applyGroupEffect(ctx, groupRadius, this::addChiBonus);

			}

			// Apply power modifier

			CleansePowerModifier modifier = new CleansePowerModifier();
			modifier.setTicks(duration);
			// Ignore warning; we know they have the bending, so manager for that bending != null
			//noinspection ConstantConditions
			data.getPowerRatingManager(getBendingId()).addModifier(modifier, ctx);

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
			if (player.getDistanceSqToEntity(entity) > radius * radius) {
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

		BendingData data = BendingData.get(player);
		data.chi().changeTotalChi(STATS_CONFIG.cleanseChiGroupBonus);
		data.chi().changeAvailableChi(STATS_CONFIG.cleanseChiGroupBonus);

	}

}

