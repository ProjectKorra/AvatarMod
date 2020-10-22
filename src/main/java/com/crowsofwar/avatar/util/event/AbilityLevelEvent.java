package com.crowsofwar.avatar.util.event;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.common.triggers.AvatarTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

public class AbilityLevelEvent extends BendingEvent {
	private Ability ability;
	private int newLevel;
	private int oldLevel;

	//Creative/locked is 0. Level 1 is 1, Level 2 is 2, e.t.c

	public AbilityLevelEvent(EntityLivingBase entity, Ability ability, int oldLevel, int newLevel) {
		super(entity);
		this.ability = ability;
		this.oldLevel = oldLevel;
		this.newLevel = newLevel;
		if(entity instanceof EntityPlayerMP)
			AvatarTriggers.ABILITY_LEVEL.trigger((EntityPlayerMP) entity, ability, oldLevel, newLevel);
	}

	public Ability getAbility() {
		return ability;
	}

	public int getOldLevel() {
		return oldLevel;
	}

	public int getNewLevel() {
		return newLevel;
	}
}
