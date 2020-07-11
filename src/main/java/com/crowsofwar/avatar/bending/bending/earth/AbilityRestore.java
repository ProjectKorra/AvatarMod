package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.earth.powermods.RestorePowerModifier;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.TickHandlerController.RESTORE_PARTICLE_SPAWNER;

public class AbilityRestore extends Ability {
	public AbilityRestore() {
		super(Earthbending.ID, "restore");
	}

	@Override
	public boolean isBuff() {
		return true;
	}

	// Note: Restore does not use power rating since it's designed as a buff ability, and it could result in
	// "overpowering" for buffs to enhance more buffs

	@Override
	public void execute(AbilityContext ctx) {

		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData(this);
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();

		float chi = STATS_CONFIG.chiBuff;
		if (abilityData.getLevel() == 1) {
			chi = STATS_CONFIG.chiBuffLvl2;
		} else if (abilityData.getLevel() == 2) {
			chi = STATS_CONFIG.chiBuffLvl3;
		} else if (abilityData.getLevel() == 3) {
			chi = STATS_CONFIG.chiBuffLvl4;
		}

		if (bender.consumeChi(chi)) {

			abilityData.addXp(SKILLS_CONFIG.buffUsed);

			// 3s + 1.5s per level
			int duration = ctx.getLevel() > 0 ? 60 + 30 * ctx.getLevel() : 60;
			int effectLevel = 0;
			int slownessLevel = abilityData.getLevel() >= 2 ? 1 : 2;
			int regenLevel = abilityData.getLevel() >= 2 ? 1 : 0;

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				effectLevel = 1;
			}

			// Add potion effects

			entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, duration, effectLevel));
			entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, duration, slownessLevel));

			if (abilityData.getLevel() >= 1) {
				entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, regenLevel));
			}
			if (abilityData.getLevel() >= 2) {
				entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration, effectLevel));
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH));
				entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, duration));
			}

			// Apply power rating modifier

			RestorePowerModifier modifier = new RestorePowerModifier();
			modifier.setTicks(duration);

			// Ignore warning; we know manager != null if they have the bending style
			//noinspection ConstantConditions
			data.getPowerRatingManager(getBendingId()).addModifier(modifier, ctx);
			data.addTickHandler(RESTORE_PARTICLE_SPAWNER);

		}
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
			coolDown = 150;
		}
		if (ctx.getLevel() == 2) {
			coolDown = 140;
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			coolDown = 130;
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			coolDown = 140;
		}

		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			coolDown = 0;
		}
		return coolDown;
	}
}



