package com.crowsofwar.avatar.common.event;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.triggers.AvatarTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;

public class ElementUnlockEvent extends BendingEvent {

	private BendingStyle element;

	public ElementUnlockEvent(EntityLivingBase entity, BendingStyle element) {
		super(entity);
		this.element = element;
		AvatarLog.info("triggering element unlock");
		AvatarTriggers.UNLOCK_ELEMENT.trigger((EntityPlayerMP) entity, element);
	}

	public BendingStyle getElement() {
		return element;
	}
}
