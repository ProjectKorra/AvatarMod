package com.crowsofwar.avatar.common.bending.fire.statctrls;

import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.Raytrace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.EnumHand;

import java.util.HashMap;
import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK;
import static com.crowsofwar.avatar.common.data.StatusControlController.FLAME_STRIKE_MAIN;
import static com.crowsofwar.avatar.common.data.StatusControlController.FLAME_STRIKE_OFF;

public class StatCtrlFlameStrike extends StatusControl {

	private static HashMap<UUID, Integer> timesUsed = new HashMap<>();
	EnumHand hand;

	public StatCtrlFlameStrike(EnumHand hand) {
		super(18, hand == EnumHand.MAIN_HAND ? CONTROL_LEFT_CLICK : CONTROL_RIGHT_CLICK,
				hand == EnumHand.MAIN_HAND ? CrosshairPosition.LEFT_OF_CROSSHAIR : CrosshairPosition.RIGHT_OF_CROSSHAIR);
		this.hand = hand;
	}

	public static int getTimesUsed(UUID id) {
		return timesUsed.getOrDefault(id, 0);
	}

	public static void setTimesUsed(UUID id, int times) {
		if (timesUsed.containsKey(id))
			timesUsed.replace(id, times);
		else timesUsed.put(id, times);
	}

	@Override
	public boolean execute(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");

		double reach = Raytrace.getReachDistance(entity);
		float powerModifier = (float) (ctx.getBender().getDamageMult(Firebending.ID));
		float xpMod = abilityData.getXpModifier();

		float damage = STATS_CONFIG.infernoPunchSettings.damage;
		int performance = STATS_CONFIG.infernoPunchSettings.performanceAmount;
		float knockBack = STATS_CONFIG.infernoPunchSettings.knockbackMult;
		int fireTime = STATS_CONFIG.infernoPunchSettings.fireTime;
		float xp = SKILLS_CONFIG.infernoPunchHit;

		if (abilityData.getLevel() == 1) {
			damage *= 4 / 3F;
			knockBack *= 1.125F;
			fireTime += 2;
			performance += 2;
			xp -= 1;
		}
		if (abilityData.getLevel() >= 2) {
			damage *= 6 / 3F;
			knockBack *= 1.25F;
			fireTime += 4;
			performance += 5;
			xp -= 2;
		}

		damage *= powerModifier * xpMod;
		knockBack *= powerModifier * xpMod;
		fireTime *= powerModifier * xpMod;
		performance *= powerModifier * xpMod;


		ctx.getData().addStatusControl(hand == EnumHand.MAIN_HAND ? FLAME_STRIKE_OFF : FLAME_STRIKE_MAIN);
		setTimesUsed(entity.getPersistentID(), getTimesUsed(entity.getPersistentID()) + 1);
		return true;
	}

	public boolean canCollideWith(Entity entity) {
		if (entity instanceof EntityEnderCrystal) {
			return true;
		} else
			return (entity.canBePushed() && entity.canBeCollidedWith()) || entity instanceof EntityLivingBase;
	}
}
