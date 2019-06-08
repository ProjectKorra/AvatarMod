package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import java.util.Objects;

import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_SLIPSTREAM_COOLDOWN;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.TickHandlerController.SLIPSTREAM_COOLDOWN_HANDLER;

public class AbilitySlipstream extends Ability {

	public AbilitySlipstream() {
		super(Airbending.ID, "slipstream");
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
		if (abilityData.getLevel() == 1) {
			chi = STATS_CONFIG.chiBuffLvl2;
		} else if (abilityData.getLevel() == 2) {
			chi = STATS_CONFIG.chiBuffLvl3;
		} else if (abilityData.getLevel() == 3) {
			chi = STATS_CONFIG.chiBuffLvl4 * 1.5F;
		}

		if (data.hasTickHandler(SLIPSTREAM_COOLDOWN_HANDLER) && entity instanceof EntityPlayer) {
			MSG_SLIPSTREAM_COOLDOWN.send(entity);
		}

		if (bender.consumeChi(chi) && !data.hasTickHandler(SLIPSTREAM_COOLDOWN_HANDLER)) {
			float xp = SKILLS_CONFIG.buffUsed;

			// 4s base + 1s per level
			int duration = abilityData.getLevel() > 0 ? 80 + 20 * abilityData.getLevel() : 80;

			int effectLevel = abilityData.getLevel() >= 2 ? 1 : 0;
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				effectLevel = 2;
			}

			entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, effectLevel, false, false));
			data.getAbilityData("slipstream").addXp(xp);

			if (abilityData.getLevel() >= 1) {
				entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, duration, effectLevel, false, false));
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration, effectLevel, false, false));
			}

			SlipstreamPowerModifier modifier = new SlipstreamPowerModifier();
			modifier.setTicks(duration);
			Objects.requireNonNull(data.getPowerRatingManager(getBendingId())).addModifier(modifier, ctx);
			data.addTickHandler(SLIPSTREAM_COOLDOWN_HANDLER);

		}


	}

}



