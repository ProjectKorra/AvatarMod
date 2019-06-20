package com.crowsofwar.avatar.common.event;

import com.crowsofwar.avatar.common.bending.Ability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * @author Aang23
 */
@Cancelable
public class AbilityUseEvent extends BendingEvent {
	private Ability ability;

	public AbilityUseEvent(EntityLivingBase entity, Ability ability) {
		super(entity);
		this.ability = ability;
	}

	public Ability getAbility() {
		return ability;
	}

}
