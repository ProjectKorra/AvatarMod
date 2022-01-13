package com.crowsofwar.avatar.util.data;

import com.crowsofwar.avatar.bending.bending.custom.dark.tickhandlers.*;
import com.crowsofwar.avatar.bending.bending.custom.demonic.tickhandlers.DemonWingsHandler;
import com.crowsofwar.avatar.bending.bending.custom.demonic.tickhandlers.DemonicAuraHandler;
import com.crowsofwar.avatar.bending.bending.custom.demonic.tickhandlers.HellBastionHandler;
import com.crowsofwar.avatar.bending.bending.custom.demonic.tickhandlers.InfernalFieldHandler;
import com.crowsofwar.avatar.bending.bending.custom.ki.tickhandlers.*;
import com.crowsofwar.avatar.bending.bending.custom.light.tickhandlers.*;
import com.crowsofwar.avatar.bending.bending.earth.tickhandlers.EarthSpikeHandler;
import com.crowsofwar.avatar.bending.bending.ice.tickhandlers.*;
import com.crowsofwar.avatar.client.gui.RenderElementHandler;
import com.crowsofwar.avatar.bending.bending.air.tickhandlers.*;
import com.crowsofwar.avatar.bending.bending.earth.tickhandlers.RestoreParticleHandler;
import com.crowsofwar.avatar.bending.bending.fire.*;
import com.crowsofwar.avatar.bending.bending.fire.tickhandlers.*;
import com.crowsofwar.avatar.bending.bending.lightning.LightningCreateHandler;
import com.crowsofwar.avatar.bending.bending.lightning.LightningRedirectHandler;
import com.crowsofwar.avatar.bending.bending.water.tickhandlers.*;
import com.crowsofwar.avatar.entity.mob.BisonSummonHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumHand;
import sun.security.provider.ConfigFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mahtaran
 */
public class TickHandlerController {
	// NOTE: DO NOT MOVE THIS.
	/** @formatter:off **/
	static Map<Integer, TickHandler> allHandlers = new HashMap<>();
	/** @formatter:on **/

	public static TickHandler AIR_PARTICLE_SPAWNER = new AirParticleSpawner(0);
	public static TickHandler FLAME_GLIDE_HANDLER = new FlameGlideHandler(1);
	public static TickHandler FLAMETHROWER = new FlamethrowerUpdateTick(2);
	public static TickHandler WATER_SKATE = new WaterSkateHandler(3);
	public static TickHandler BISON_SUMMONER = new BisonSummonHandler(4);
	public static TickHandler SMASH_GROUND = new SmashGroundHandler(5);
	public static TickHandler LIGHTNING_CHARGE = new LightningCreateHandler(6);
	public static TickHandler WATER_CHARGE = new WaterChargeHandler(7);
	public static TickHandler LIGHTNING_REDIRECT = new LightningRedirectHandler(8);
	public static TickHandler SMASH_GROUND_FIRE = new FireSmashGroundHandler(9);
	public static TickHandler SMASH_GROUND_FIRE_BIG = new FireSmashGroundHandlerBig(10);
	public static TickHandler SMASH_GROUND_WATER = new WaterSmashHandler(11);
	public static TickHandler WATER_PARTICLE_SPAWNER = new WaterParticleSpawner(12);
	public static TickHandler FLAME_STRIKE_HANDLER = new FlameStrikeHandler(13);
	public static TickHandler AIRBURST_CHARGE_HANDLER = new AirBurstHandler(15);
	public static TickHandler AIR_STATCTRL_HANDLER = new AirStatusControlHandler(16);
	public static TickHandler FIRE_STATCTRL_HANDLER = new FireStatusControlHandler(17);
	//public static TickHandler AIR_DODGE = new AirDodgeHandler(18);
	public static TickHandler RENDER_ELEMENT_HANDLER = new RenderElementHandler(19);
	public static TickHandler STAFF_GUST_HANDLER = new StaffGustCooldown(20);
	public static TickHandler IMMOLATE_HANDLER = new ImmolateParticleHandler(21);
	public static TickHandler RESTORE_PARTICLE_SPAWNER = new RestoreParticleHandler(22);
	public static TickHandler SLIPSTREAM_WALK_HANDLER = new SlipstreamAirWalkHandler(23);
	public static TickHandler RELEASE_EARTHSPIKE = new EarthSpikeHandler(24);
	public static TickHandler SHOOT_AIRBURST = new ShootAirBurstHandler(25);
	public static TickHandler DEMONIC_AURA_HANDLER = new DemonicAuraHandler(26);
	public static TickHandler PURIFY_HANDLER = new PurifyHandler(27);
	public static TickHandler KAIO_KEN_HANDLER = new KaioKenHandler(28);
	public static TickHandler CORRUPT_HANDLER = new CorruptHandler(29);
	public static TickHandler DEMON_WINGS_HANDLER = new DemonWingsHandler(30);
	public static TickHandler HEAVENLY_FLIGHT_HANDLER = new HeavenlyFlightHandler(31);
	public static TickHandler KI_FLIGHT_HANDlER = new KiFlightHandler(32);
	public static TickHandler DEATH_DESCENT_HANDLER = new DeathDescentHandler(33);
	public static TickHandler FROST_FORM_HANDLER = new FrostFormHandler(34);
	public static TickHandler GLACIAL_GLIDE_HANDLER = new GlacialGlideHandler(35);
	public static TickHandler ICE_RAZE_HANDLER = new IceRazeHandler(36);
	public static TickHandler ICE_LANCE_HANDLER = new IceLanceHandler(37);
	public static TickHandler FROST_CLAW_MAIN_HAND_HANDLER = new FrostClawHandler(38, EnumHand.MAIN_HAND);
	public static TickHandler FROST_CLAW_OFF_HAND_HANDLER = new FrostClawHandler(39, EnumHand.OFF_HAND);
	public static TickHandler OBLIVION_BEAM_HANDLER = new OblivionBeamHandler(40);
	public static TickHandler OBLIVION_BEAM_CHARGER = new ChargeOblivionBeam(41);
	public static TickHandler SHADE_BURST_CHARGE = new ShadeBurstHandler(42);
	public static TickHandler HOLY_PROTECTION_HANDLER = new HolyProtectionHandler(43);
	public static TickHandler INFERNAL_FIELD_HANDLER = new InfernalFieldHandler(44);
	public static TickHandler DIVINE_BEGINNING_CHARGER = new ChargeDivineBeginning(45);
	public static TickHandler DIVINE_BEGINNING_HANDLER = new DivineBeginningHandler(46);
	public static TickHandler HELL_BASTION_HANDLER = new HellBastionHandler(47);
	public static TickHandler CHARGE_KAMEHAMEHA = new ChargeKamehameha(48);
	public static TickHandler KAMEHAMEHA_HANDLER = new KamehamehaHandler(49);
	public static TickHandler SPIRIT_BOMB_HANDLER = new SpiritBombHandler(50);



	public static TickHandler fromId(int id) {
		return allHandlers.get(id);
	}

	public static TickHandler fromBytes(ByteBuf buf) {
		return fromId(buf.readInt());
	}
}
