package com.crowsofwar.avatar.bending.bending.fire.tickhandlers;

import com.crowsofwar.avatar.bending.bending.fire.statctrls.StatCtrlFlameStrike;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;

import java.util.UUID;

import static com.crowsofwar.avatar.util.data.StatusControlController.STOP_CHARGE_FLAME_STRIKE_MAIN;
import static com.crowsofwar.avatar.util.data.StatusControlController.STOP_CHARGE_FLAME_STRIKE_OFF;

public class ChargeFlameStrikeHandler extends TickHandler {

	public ChargeFlameStrikeHandler(int id) {
		super(id);
	}

	@Override
	public boolean tick(BendingContext ctx) {
		BendingData data = ctx.getData();
		UUID id = ctx.getBenderEntity().getPersistentID();
		if (data.getTickHandlerDuration(this) % 20 == 0 && data.getTickHandlerDuration(this) != 0) {
			if (StatCtrlFlameStrike.getChargeLevel(id) < 4)
				StatCtrlFlameStrike.setChargeLevel(id, StatCtrlFlameStrike.getChargeLevel(id));
			else if (StatCtrlFlameStrike.getChargeLevel(id) > 4)
				StatCtrlFlameStrike.setChargeLevel(id, 4);
		}
		return !data.hasStatusControl(STOP_CHARGE_FLAME_STRIKE_OFF) && !data.hasStatusControl(STOP_CHARGE_FLAME_STRIKE_MAIN);
	}
}
