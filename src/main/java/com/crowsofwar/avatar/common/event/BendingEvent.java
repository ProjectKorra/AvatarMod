package com.crowsofwar.avatar.common.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;

public class BendingEvent extends LivingEvent {

	public BendingEvent(EntityLivingBase entity) {
		super(entity);
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
