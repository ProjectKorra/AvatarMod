package com.crowsofwar.avatar.common.event;

import com.crowsofwar.avatar.common.bending.BendingStyle;
import net.minecraft.entity.EntityLivingBase;

public class BendingCycleEvent extends BendingEvent {

	private boolean right;
	private BendingStyle oldStyle;
	private BendingStyle newStyle;

	public BendingCycleEvent(EntityLivingBase entity, boolean right) {
		super(entity);
		this.right = right;
		//this.oldStyle = oldStyle;
		//this.newStyle = newStyle;
	}

	public boolean cycleRight() {
		return right;
	}

	public BendingStyle getOldStyle() {
		return oldStyle;
	}

	public BendingStyle getNewStyle() {
		return newStyle;
	}
}
