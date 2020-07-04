package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.common.bending.air.statctrls.*;
import com.crowsofwar.avatar.common.bending.earth.statctrls.*;
import com.crowsofwar.avatar.common.bending.fire.statctrls.*;
import com.crowsofwar.avatar.common.bending.ice.StatCtrlShieldShatter;
import com.crowsofwar.avatar.common.bending.lightning.StatCtrlThrowLightningSpear;
import com.crowsofwar.avatar.common.bending.sand.StatCtrlSandstormRedirect;
import com.crowsofwar.avatar.common.bending.water.statctrls.*;
import net.minecraft.util.EnumHand;

import java.util.List;

public class StatusControlController {
	public static int nextId = 0;
	public static List<StatusControl> allControls;

	// @formatter:off
	public static final StatusControl
			AIR_JUMP = new StatCtrlAirJump(),
			BUBBLE_EXPAND = new StatCtrlBubbleExpand(),
			BUBBLE_CONTRACT = new StatCtrlBubbleContract(),
			CHARGE_AIR_BURST = new StatCtrlSetAirburstCharging(true),
			CHARGE_BUBBLE = new StatCtrlChargeBubble(),
			DROP_WALL = new StatCtrlDropWall(),
			PLACE_WALL = new StatCtrlPlaceWall(),
			SHOOT_WALL = new StatCtrlShootWall(),
			PUSH_WALL = new StatCtrlPushWall(),
			PULL_WALL = new StatCtrlPullWall(),
			FIRE_JUMP = new StatCtrlFireJump(),
			START_CHARGE_FLAME_STRIKE_MAIN = new StatCtrlChargeFlameStrike(EnumHand.MAIN_HAND, true),
			STOP_CHARGE_FLAME_STRIKE_MAIN = new StatCtrlChargeFlameStrike(EnumHand.MAIN_HAND, false),
			START_CHARGE_FLAME_STRIKE_OFF = new StatCtrlChargeFlameStrike(EnumHand.OFF_HAND, true),
			STOP_CHARGE_FLAME_STRIKE_OFF = new StatCtrlChargeFlameStrike(EnumHand.OFF_HAND, false),
			FLAME_STRIKE_MAIN = new StatCtrlFlameStrike(EnumHand.MAIN_HAND),
			FLAME_STRIKE_OFF = new StatCtrlFlameStrike(EnumHand.OFF_HAND),
			LOB_BUBBLE = new StatCtrlLobBubble(),
			PLACE_BLOCK = new StatCtrlPlaceBlock(),
			RELEASE_AIR_BURST = new StatCtrlSetAirburstCharging(false),
			SANDSTORM_REDIRECT = new StatCtrlSandstormRedirect(),
			SHIELD_SHATTER = new StatCtrlShieldShatter(),
			SHOOT_AIR_BURST = new StatCtrlShootAirburst(),
			SKATING_JUMP = new StatCtrlSkateJump(),
			SKATING_START = new StatCtrlSkateStart(),
			START_FLAMETHROW = new StatCtrlSetFlamethrowing(true),
			STOP_FLAMETHROW = new StatCtrlSetFlamethrowing(false),
			THROW_BLOCK = new StatCtrlThrowBlock(),
			THROW_CLOUDBURST = new StatCtrlThrowCloudBall(),
			THROW_FIREBALL = new StatCtrlThrowFireball(),
			THROW_LIGHTNINGSPEAR = new StatCtrlThrowLightningSpear(),
			THROW_WATER = new StatCtrlThrowWater();
	// @formatter:on

	public static StatusControl lookup(int id) {
		id--;
		return id >= 0 && id < allControls.size() ? allControls.get(id) : null;
	}
}
