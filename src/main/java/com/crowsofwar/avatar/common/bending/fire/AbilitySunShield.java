package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.air.AiAirBubble;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityAirBubble;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.FIRST;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.SECOND;

public class AbilitySunShield extends Ability {
	public AbilitySunShield() {
		super(Firebending.ID, "sun_shield");
	}

	@Override
	public void execute(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData(this);

		float chi = STATS_CONFIG.chiAirBubble;
		if (abilityData.getLevel() == 1) {
			chi = STATS_CONFIG.chiBuffLvl2;
		} else if (abilityData.getLevel() == 2) {
			chi = STATS_CONFIG.chiBuffLvl3;
		} else if (abilityData.getLevel() == 3) {
			chi = STATS_CONFIG.chiBuffLvl4;
		}

		if (bender.consumeChi(chi)) {
			float xp = SKILLS_CONFIG.buffUsed;

			// 4s base + 1s per level
			int duration = 80 + 20 * abilityData.getLevel();

			int effectLevel = abilityData.getLevel() >= 2 ? 1 : 0;

			if (world.isDaytime()) {
				entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, effectLevel, duration));
				if (abilityData.getLevel() >= 1) {
					entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, effectLevel, duration));
				}
				if (abilityData.getLevel() >= 2) {
					entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, effectLevel, duration));
					entity.addPotionEffect(new PotionEffect(MobEffects.GLOWING, effectLevel, duration));
				}
			}
		}
	}
}
