package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.air.powermods.SlipstreamPowerModifier;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import java.util.Objects;

import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.TickHandlerController.SLIPSTREAM_WALK_HANDLER;

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

		if (bender.consumeChi(chi)) {
			float xp = SKILLS_CONFIG.buffUsed;

			// 4s base + 1s per level
			int duration = abilityData.getLevel() > 0 ? 100 + 20 * abilityData.getLevel() : 80;

			int effectLevel = abilityData.getLevel() >= 2 ? 1 : 0;
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				effectLevel = 2;
				data.addTickHandler(SLIPSTREAM_WALK_HANDLER);
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
		}
		super.execute(ctx);

	}

	@Override
	public int getCooldown(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();

		int coolDown = 140;
		if (ctx.getLevel() == 1) {
			coolDown = 120;
		}
		if (ctx.getLevel() == 2) {
			coolDown = 100;
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			coolDown = 110;
		}
		if (ctx.isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			coolDown = 90;
		}

		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			coolDown = 0;
		}
		return coolDown;
	}

	@Override
	public int getBaseTier() {
		return 5;
	}
}



