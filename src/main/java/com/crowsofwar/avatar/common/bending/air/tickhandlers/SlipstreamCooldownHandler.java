package com.crowsofwar.avatar.common.bending.air.tickhandlers;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;


public class SlipstreamCooldownHandler extends TickHandler {

	public SlipstreamCooldownHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		int duration = data.getTickHandlerDuration(this);
		int coolDown = 140;
		AbilityData aD = data.getAbilityData("slipstream");

		if (aD.getLevel() == 1) {
			coolDown = 120;
		}
		if (aD.getLevel() == 2) {
			coolDown = 100;
		}
		if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			coolDown = 110;
		}
		if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			coolDown = 90;
		}

		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			coolDown = 0;
		}

		return duration >= coolDown;
	}
}
