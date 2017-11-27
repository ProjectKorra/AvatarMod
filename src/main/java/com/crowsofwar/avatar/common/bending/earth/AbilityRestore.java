package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityRestore extends Ability {
	public AbilityRestore() {
		super(Earthbending.ID, "restore");
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
			chi *= 1.5f;
		}
		if (abilityData.getLevel() == 2) {
			chi *= 2f;
		}
		if (abilityData.getLevel() == 3) {
			chi *= 2.5F;
		}

		if (bender.consumeChi(chi)) {

			abilityData.addXp(SKILLS_CONFIG.buffUsed);

			// 3s + 2.5s per level
			int duration = 60 + 50 * abilityData.getLevel();
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
				entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 1, regenLevel));
			}
			if (abilityData.getLevel() >= 2) {
				entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration, effectLevel));
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, duration));
				entity.addPotionEffect(new PotionEffect(MobEffects.SATURATION, duration));
			}

			// Apply power rating modifier

			RestorePowerModifier modifier = new RestorePowerModifier();
			modifier.setTicks(duration);

			// Ignore warning; we know manager != null if they have the bending style
			//noinspection ConstantConditions
			data.getPowerRatingManager(getBendingId()).addModifier(modifier, ctx);

		}

	}
}



