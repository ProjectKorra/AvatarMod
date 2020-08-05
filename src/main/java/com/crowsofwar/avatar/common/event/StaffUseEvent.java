package com.crowsofwar.avatar.common.event;

import net.minecraft.entity.EntityLivingBase;

public class StaffUseEvent extends BendingEvent {

	private boolean isGust;

	public StaffUseEvent(EntityLivingBase entity, boolean isGust) {
		super(entity);
		this.isGust = isGust;
	}

	public boolean isGust() {
		return isGust;
	}
}
