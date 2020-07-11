package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.BendingAi;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.CHARGE_AIR_BURST;
import static com.crowsofwar.avatar.util.data.StatusControlController.RELEASE_AIR_BURST;

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

		boolean hasAirCharge = data.hasStatusControl(RELEASE_AIR_BURST);

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
			data.addStatusControl(CHARGE_AIR_BURST);
		} else if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			if (!hasAirCharge) {
				data.addStatusControl(CHARGE_AIR_BURST);
			}
		}
		super.execute(ctx);
	}

	@Override
	public int getBaseTier() {
		return 4;
	}

	@Override
	public boolean isChargeable() {
		return true;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiAirBurst(this, entity, bender);
	}
}
