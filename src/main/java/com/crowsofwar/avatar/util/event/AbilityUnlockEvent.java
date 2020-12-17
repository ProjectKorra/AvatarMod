package com.crowsofwar.avatar.util.event;

import com.crowsofwar.avatar.bending.bending.Ability;
import net.minecraft.entity.EntityLivingBase;

public class AbilityUnlockEvent extends BendingEvent {
	private Ability ability;

	public AbilityUnlockEvent(EntityLivingBase entity, Ability ability) {
		super(entity);
		this.ability = ability;
	}

	public Ability getAbility() {
		return this.ability;
	}
}
