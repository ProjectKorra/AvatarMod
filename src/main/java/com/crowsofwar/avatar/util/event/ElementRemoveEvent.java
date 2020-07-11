package com.crowsofwar.avatar.util.event;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import net.minecraft.entity.EntityLivingBase;

public class ElementRemoveEvent extends BendingEvent {

	private BendingStyle element;

	public ElementRemoveEvent(EntityLivingBase entity, BendingStyle element) {
		super(entity);
		this.element = element;
	}

	public BendingStyle getElement() {
		return this.element;
	}
}
