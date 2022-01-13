package com.crowsofwar.avatar.util.data;

import com.crowsofwar.avatar.bending.bending.air.statctrls.*;
import com.crowsofwar.avatar.bending.bending.custom.dark.statctrls.StatCtrlDeathDescent;
import com.crowsofwar.avatar.bending.bending.custom.dark.statctrls.StatCtrlSetOblivionBeam;
import com.crowsofwar.avatar.bending.bending.custom.dark.statctrls.StatCtrlShadeBurst;
import com.crowsofwar.avatar.bending.bending.custom.demonic.statctrls.StatCtrlDemonWings;
import com.crowsofwar.avatar.bending.bending.custom.demonic.statctrls.StatCtrlHellBastion;
import com.crowsofwar.avatar.bending.bending.custom.demonic.statctrls.StatCtrlInfernalField;
import com.crowsofwar.avatar.bending.bending.custom.ki.statctrls.StatCtrlKiFlight;
import com.crowsofwar.avatar.bending.bending.custom.ki.statctrls.StatCtrlSetKamehameha;
import com.crowsofwar.avatar.bending.bending.custom.ki.statctrls.StatCtrlSpiritBomb;
import com.crowsofwar.avatar.bending.bending.custom.light.statctrls.StatCtrlSetHolyProtection;
import com.crowsofwar.avatar.bending.bending.custom.light.statctrls.StatCtrlHeavenlyFlight;
import com.crowsofwar.avatar.bending.bending.custom.light.statctrls.StatCtrlSetDivineBeginning;
import com.crowsofwar.avatar.bending.bending.earth.statctrls.*;
import com.crowsofwar.avatar.bending.bending.fire.statctrls.*;
import com.crowsofwar.avatar.bending.bending.ice.StatCtrlShieldShatter;
import com.crowsofwar.avatar.bending.bending.ice.statctrls.StatCtrlGlacialGlide;
import com.crowsofwar.avatar.bending.bending.ice.statctrls.StatCtrlSetIceLanceCharging;
import com.crowsofwar.avatar.bending.bending.ice.statctrls.StatCtrlSetIceRaze;
import com.crowsofwar.avatar.bending.bending.lightning.StatCtrlThrowLightningSpear;
import com.crowsofwar.avatar.bending.bending.sand.StatCtrlSandstormRedirect;
import com.crowsofwar.avatar.bending.bending.water.statctrls.*;
import com.crowsofwar.avatar.bending.bending.water.statctrls.waterarc.StatCtrlThrowWater;
import com.crowsofwar.avatar.bending.bending.water.statctrls.waterblast.StatCtrlChargeWaterBlast;
import com.crowsofwar.avatar.bending.bending.water.statctrls.waterbubble.StatCtrlLobBubble;
import com.crowsofwar.avatar.bending.bending.water.statctrls.waterskate.StatCtrlSkateJump;
import com.crowsofwar.avatar.bending.bending.water.statctrls.waterskate.StatCtrlSkateStart;
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
			THROW_WATER = new StatCtrlThrowWater(),
			CHARGE_WATER = new StatCtrlChargeWaterBlast(true),
			RELEASE_WATER = new StatCtrlChargeWaterBlast(false),
			REDIRECT_FIRE = new StatCtrlFireRedirect(),
			SPLIT_FIRE = new StatCtrlFireSplit(),
			EARTH_REDIRECT = new StatCtrlEarthRedirect(),
			CHARGE_EARTH_SPIKE = new StatCtrlChargeEarthspike(true),
			RELEASE_EARTH_SPIKE = new StatCtrlChargeEarthspike(false),
			DEMON_WINGS = new StatCtrlDemonWings(),
			HEAVENLY_FLIGHT = new StatCtrlHeavenlyFlight(),
			KI_FLIGHT = new StatCtrlKiFlight(),
			DEATH_DESCENT = new StatCtrlDeathDescent(),
			GLACIAL_GLIDE = new StatCtrlGlacialGlide(),
			START_ICE_RAZE = new StatCtrlSetIceRaze(true),
			STOP_ICE_RAZE = new StatCtrlSetIceRaze(false),
			CHARGE_ICE_LANE = new StatCtrlSetIceLanceCharging(true),
			RELEASE_ICE_LANCE = new StatCtrlSetIceLanceCharging(false),
			START_OBLIVION_BEAM = new StatCtrlSetOblivionBeam(true),
			STOP_OBLIVION_BEAM = new StatCtrlSetOblivionBeam(false),
			CHARGE_SHADE_BURST = new StatCtrlShadeBurst(true),
			SHOOT_SHADE_BURST = new StatCtrlShadeBurst(false),
			CHARGE_HOLY_PROTECTION = new StatCtrlSetHolyProtection(true),
			RELEASE_HOLY_PROTECTION = new StatCtrlSetHolyProtection(false),
			CHARGE_INFERNAL_FIELD = new StatCtrlInfernalField(true),
			RELEASE_INFERNAL_FIELD = new StatCtrlSetHolyProtection(false),
			CHARGE_DIVINE_BEGINNING = new StatCtrlSetDivineBeginning(true),
			RELEASE_DIVINE_BEGINNING = new StatCtrlSetDivineBeginning(false),
			CHARGE_HELL_BASTION = new StatCtrlHellBastion(true),
			RELEASE_HELL_BASTION = new StatCtrlHellBastion(false),
			CHARGE_KAMEHAMEHA = new StatCtrlSetKamehameha(true),
			RELEASE_KAMEHAMEHA = new StatCtrlSetKamehameha(false),
			CHARGE_SPIRIT_BOMB = new StatCtrlSpiritBomb(true),
			RELEASE_SPIRIT_BOMB = new StatCtrlSpiritBomb(false);
	// @formatter:on

	public static StatusControl lookup(int id) {
		id--;
		return id >= 0 && id < allControls.size() ? allControls.get(id) : null;
	}
}
