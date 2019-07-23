package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_RESTORE_COOLDOWN;
import static com.crowsofwar.avatar.common.data.TickHandlerController.RESTORE_COOLDOWN_HANDLER;
import static com.crowsofwar.avatar.common.data.TickHandlerController.RESTORE_PARTICLE_SPAWNER;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

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

		if (data.hasTickHandler(RESTORE_COOLDOWN_HANDLER) && entity instanceof EntityPlayer) {
			MSG_RESTORE_COOLDOWN.send(entity);
		}

		if (bender.consumeChi(chi) && !data.hasTickHandler(RESTORE_COOLDOWN_HANDLER)) {

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
			data.addTickHandler(RESTORE_COOLDOWN_HANDLER);

		}
	}

	@Override
	public int getTier() {
		return 4;
	}
}



