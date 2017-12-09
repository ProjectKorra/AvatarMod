package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static net.minecraft.init.MobEffects.*;

public class AbilityPurify extends Ability {

	public AbilityPurify() {
		super(Firebending.ID, "purify");
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
		if (abilityData.getLevel() == 1){
			chi *= 1.5f;
		} else if (abilityData.getLevel() ==2 ){
			chi *= 2f;
		} else if (abilityData.getLevel() == 3) {
			chi *= 2.5f;
		}

		if (bender.consumeChi(chi)) {

			// 3s base + 2s per level
			int duration = 60 + 40 * abilityData.getLevel();
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				duration = 200;
			}

			int effectLevel = abilityData.getLevel() >= 2 ? 1 : 0;
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				effectLevel = 2;
			}

			entity.addPotionEffect(new PotionEffect(STRENGTH, duration, effectLevel + 1));

			if (abilityData.getLevel() < 2) {
				entity.setFire(1);
			}

			if (abilityData.getLevel() >= 1) {
				entity.addPotionEffect(new PotionEffect(SPEED, duration, effectLevel));
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				entity.addPotionEffect(new PotionEffect(HEALTH_BOOST, duration, effectLevel));
			}

			if (data.hasBendingId(getBendingId())) {

				PurifyPowerModifier modifier = new PurifyPowerModifier();
				modifier.setTicks(duration);

				// Ignore warning; we know manager != null if they have the bending style
				//noinspection ConstantConditions
				data.getPowerRatingManager(getBendingId()).addModifier(modifier, ctx);

			}

			abilityData.addXp(SKILLS_CONFIG.buffUsed);

		}
	}
}
