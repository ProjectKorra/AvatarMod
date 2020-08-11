package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.util.Raytrace;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.FIRE_JUMP;
import static com.crowsofwar.avatar.util.data.TickHandlerController.FIRE_PARTICLE_SPAWNER;

public class AbilityFireJump extends Ability {

	private static final String JET_STREAM = "jetStream";

	public AbilityFireJump() {
		super(Firebending.ID, "fire_jump");
	}

	@Override
	public void init() {
		super.init();
		addProperties(JUMPS, JUMP_HEIGHT, FIRE_R, FIRE_G, FIRE_B, FADE_R, FADE_G, FADE_B);
		addBooleanProperties(STOP_SHOCKWAVE, JET_STREAM);
	}

	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public boolean isOffensive() {
		return true;
	}

	@Override
	public void execute(AbilityContext ctx) {

		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();
		AbilityData abilityData = ctx.getAbilityData();

		if (!data.hasStatusControl(FIRE_JUMP) && bender.consumeChi(getChiCost(abilityData) / 4)) {

			data.addStatusControl(FIRE_JUMP);
			if (data.hasTickHandler(FIRE_PARTICLE_SPAWNER)) {
				StatusControl sc = FIRE_JUMP;
				Raytrace.Result raytrace = Raytrace.getTargetBlock(ctx.getBenderEntity(), -1);
				if (sc.execute(
						new BendingContext(data, ctx.getBenderEntity(), ctx.getBender(), raytrace))) {
					data.removeStatusControl(sc);
				}
			}

		}
	}

	//Override the AbilityContext inhibitors as we don't want them to be applied upon executing the abilit
	@Override
	public int getCooldown(AbilityContext ctx) {
		return 0;
	}

	@Override
	public float getBurnOut(AbilityContext ctx) {
		return 0;
	}

	@Override
	public float getExhaustion(AbilityContext ctx) {
		return 0;
	}
}

