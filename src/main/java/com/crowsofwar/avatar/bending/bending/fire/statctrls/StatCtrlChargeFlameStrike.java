package com.crowsofwar.avatar.bending.bending.fire.statctrls;

import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import net.minecraft.util.EnumHand;

import static com.crowsofwar.avatar.client.controls.AvatarControl.*;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.LEFT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.util.data.StatusControl.CrosshairPosition.RIGHT_OF_CROSSHAIR;
import static com.crowsofwar.avatar.util.data.StatusControlController.STOP_CHARGE_FLAME_STRIKE_MAIN;
import static com.crowsofwar.avatar.util.data.StatusControlController.STOP_CHARGE_FLAME_STRIKE_OFF;

public class StatCtrlChargeFlameStrike extends StatusControl {

	private final EnumHand hand;
	private final boolean setting;

	public StatCtrlChargeFlameStrike(EnumHand hand, boolean setting) {
		super(setting ? 4 : 5, hand == EnumHand.MAIN_HAND ? setting ?
						CONTROL_RIGHT_CLICK_DOWN : CONTROL_RIGHT_CLICK_UP : setting ? CONTROL_LEFT_CLICK_DOWN : CONTROL_LEFT_CLICK_UP,
				hand == EnumHand.MAIN_HAND ? RIGHT_OF_CROSSHAIR : LEFT_OF_CROSSHAIR);
		this.hand = hand;
		this.setting = setting;
	}

	@Override
	public boolean execute(BendingContext ctx) {
		if (setting) {
			if (hand == EnumHand.MAIN_HAND)
				ctx.getData().addStatusControl(STOP_CHARGE_FLAME_STRIKE_MAIN);
			else ctx.getData().addStatusControl(STOP_CHARGE_FLAME_STRIKE_OFF);
		}
		return true;
	}
}
