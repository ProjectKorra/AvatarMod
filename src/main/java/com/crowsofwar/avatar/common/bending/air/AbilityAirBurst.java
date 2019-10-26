package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.TickHandlerController.AIRBURST_CHARGE_HANDLER;

public class AbilityAirBurst extends Ability {

	public AbilityAirBurst() {
		super(Airbending.ID, "air_burst");
	}

	@Override
	public void execute(AbilityContext ctx) {
		Bender bender = ctx.getBender();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();

		float chi = 6;
		//6
		//The charge status control adds the release status control, but the release status control doesn't activate until the right click button is released.

		boolean hasAirCharge = data.hasStatusControl(StatusControl.RELEASE_AIR_BURST);

		if (ctx.getLevel() == 1) {
			chi = 7;
		}

		if (ctx.getLevel() == 2) {
			chi = 9;
			//7
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			chi = STATS_CONFIG.chiAirBurst * 1.6F;
			//11
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			chi = STATS_CONFIG.chiAirBurst * 1.4F;
			//11
		}


		if (bender.consumeChi(chi) && !hasAirCharge) {
			data.addStatusControl(StatusControl.CHARGE_AIR_BURST);
		} else if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			if (!hasAirCharge) {
				data.addStatusControl(StatusControl.CHARGE_AIR_BURST);
			}
		}
	}

	@Override
	public int getTier() {
		return 4;
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiAirBurst(this, entity, bender);
	}
}
