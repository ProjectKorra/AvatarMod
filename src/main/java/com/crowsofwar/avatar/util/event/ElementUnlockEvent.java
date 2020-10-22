package com.crowsofwar.avatar.util.event;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.common.triggers.AvatarTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

public class ElementUnlockEvent extends BendingEvent {

	private BendingStyle element;

	public ElementUnlockEvent(EntityLivingBase entity, BendingStyle element) {
		super(entity);
		this.element = element;
		if(entity instanceof EntityPlayerMP)
			AvatarTriggers.UNLOCK_ELEMENT.trigger((EntityPlayerMP) entity, element);
	}

	public BendingStyle getElement() {
		return element;
	}
}
