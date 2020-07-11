package com.crowsofwar.avatar.util.event;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import net.minecraft.entity.EntityLivingBase;

public class BendingUseEvent extends BendingEvent {
	//Although similar in name to AbilityUseEvent, this event is called when the radial menu is opened. Used for GUI and permissions purposes
	// (if servers want to configure that).

	private BendingStyle opened;

	public BendingUseEvent(EntityLivingBase entity, BendingStyle opened) {
		super(entity);
		this.opened = opened;
	}

	public BendingStyle getOpenedBending() {
		return opened;
	}
}
