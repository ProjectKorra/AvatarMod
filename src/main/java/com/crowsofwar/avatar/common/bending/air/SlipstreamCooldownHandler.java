package com.crowsofwar.avatar.common.bending.air;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;

public class SlipstreamCooldownHandler extends TickHandler {

	public SlipstreamCooldownHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		int duration = data.getTickHandlerDuration(this);
		int coolDown = 160;
		AbilityData aD = data.getAbilityData("slipstream");

		if (aD.getLevel() == 1) {
			coolDown = 140;
		}
		if (aD.getLevel() == 2) {
			coolDown = 120;
		}
		if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			coolDown = 130;
		}
		if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			coolDown = 110;
		}

		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			coolDown = 0;
		}

		return duration >= coolDown;
	}
}
