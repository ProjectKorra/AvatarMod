package com.crowsofwar.avatar.common.bending.avatar;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilityAvatarState extends Ability {

	public AbilityAvatarState() {
		super(Avatarbending.ID, "avatar_state");
	}

	@Override
	public boolean isBuff() {
		return true;
	}


	@Override
	public void execute(AbilityContext ctx) {
		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData(this);
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		float chi = STATS_CONFIG.chiBuff;

		if (bender.consumeChi(chi)) {
			float xp = SKILLS_CONFIG.buffUsed;

			// 5s base + 1s per level
			int duration = 100 + 20 * abilityData.getLevel();
			int savehealth = 5;
			int effectLevel = abilityData.getLevel() >= 0 ? abilityData.getLevel() : 0;

			entity.clearActivePotions();

			entity.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, duration, effectLevel));
			entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, effectLevel));
			entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration, effectLevel));

			if (ctx.getLevel() >= 1){
				entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, duration, effectLevel));
				savehealth = 8;
			}

			if (ctx.getLevel() >= 2){
				entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, effectLevel));
				savehealth = 10;
			}

			if (entity.getHealth() <= savehealth){
				entity.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 1, effectLevel));
			}


		}
	}
}
