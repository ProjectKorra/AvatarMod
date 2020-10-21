package com.crowsofwar.avatar.common.event;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.triggers.AvatarTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

public class AbilityUnlockEvent extends BendingEvent {
	private Ability ability;

	public AbilityUnlockEvent(EntityLivingBase entity, Ability ability) {
		super(entity);
		this.ability = ability;
		if(entity instanceof EntityPlayerMP)
			AvatarTriggers.ABILITY_USE.trigger((EntityPlayerMP)entity, ability);
	}

	public Ability getAbility() {
		return this.ability;
	}
}
