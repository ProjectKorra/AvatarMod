package com.crowsofwar.avatar.common.event;

import com.crowsofwar.avatar.common.bending.Ability;
import net.minecraft.entity.EntityLivingBase;

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
