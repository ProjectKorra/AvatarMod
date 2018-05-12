package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;

import static com.crowsofwar.avatar.common.bending.StatusControl.INFERNO_PUNCH;


public class AbilityInfernoPunch extends Ability {
	public AbilityInfernoPunch() {
		super(Firebending.ID, "inferno_punch");
	}

	private int punchesLeft;

	public int getPunchesLeft() {
		return this.punchesLeft;
	}

	@Override
	public void execute(AbilityContext ctx) {

		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();
		float chi = 3;

		if (ctx.getLevel() >= 1) {
			chi = 4;
		}
		if (ctx.getLevel() >= 2) {
			chi = 5;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			chi = 8;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			chi = 2F;
		}
		if (!data.hasStatusControl(INFERNO_PUNCH) && bender.consumeChi(chi)) {
			data.addStatusControl(INFERNO_PUNCH);
			 punchesLeft = 1;
			AbilityData abilityData = ctx.getData().getAbilityData("inferno_punch");

			if (abilityData.getLevel() >= 2) {
				punchesLeft = 2;
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				punchesLeft = 1;
				//Creates a bunch of fire blocks around the target
			}
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				punchesLeft = 3;
			}

		}

	}


}



