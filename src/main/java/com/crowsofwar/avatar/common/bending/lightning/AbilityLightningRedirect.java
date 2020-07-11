package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;

/**
 * @author CrowsOfWar
 */
public class AbilityLightningRedirect extends Ability {

	public AbilityLightningRedirect() {
		super(Lightningbending.ID, "lightning_redirect");
	}

	@Override
	public void execute(AbilityContext ctx) {
		// Redirection ability solely used to store data; never executed
		// For lightning redirection code, see PlayerBender#redirectLightning
	}

	@Override
	public boolean isVisibleInRadial() {
		return false;
	}


	@Override
	public int getBaseParentTier() {
		return 3;
	}

	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public void init() {
		super.init();
		addProperties(PERFORMANCE);
	}
}
