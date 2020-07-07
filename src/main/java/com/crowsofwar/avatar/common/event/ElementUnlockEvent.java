package com.crowsofwar.avatar.common.event;

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.triggers.AvatarTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

public class ElementUnlockEvent extends BendingEvent {

	private BendingStyle element;

	public ElementUnlockEvent(EntityLivingBase entity, BendingStyle element) {
		super(entity);
		this.element = element;
		AvatarTriggers.UNLOCK_AN_ELEMENT.trigger((EntityPlayerMP) entity);
	}

	public BendingStyle getElement() {
		return element;
	}
}
