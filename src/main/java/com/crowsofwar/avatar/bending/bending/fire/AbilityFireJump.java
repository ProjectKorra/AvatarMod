package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.Ability;
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
	public AbilityFireJump() {
		super(Firebending.ID, "fire_jump");
	}

	@Override
	public boolean isUtility() {
		return true;
	}

	@Override
	public void execute(AbilityContext ctx) {

		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();

		if (!data.hasStatusControl(FIRE_JUMP) && bender.consumeChi(STATS_CONFIG.chiAirJump)) {

			data.addStatusControl(FIRE_JUMP);
			if (data.hasTickHandler(FIRE_PARTICLE_SPAWNER)) {
				StatusControl sc = FIRE_JUMP;
				Raytrace.Result raytrace = Raytrace.getTargetBlock(ctx.getBenderEntity(), -1);
				if (FIRE_JUMP.execute(
						new BendingContext(data, ctx.getBenderEntity(), ctx.getBender(), raytrace))) {
					data.removeStatusControl(FIRE_JUMP);
				}
			}

		}
	}


	@Override
	public boolean isOffensive() {
		return true;
	}
}

