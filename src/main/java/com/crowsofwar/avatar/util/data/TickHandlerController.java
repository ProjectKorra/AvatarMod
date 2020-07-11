package com.crowsofwar.avatar.util.data;

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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mahtaran
 */
public class TickHandlerController {
	// NOTE: DO NOT MOVE THIS.
	/** @formatter:off**/
	//formatter off
	static Map<Integer, TickHandler> allHandlers = new HashMap<>();
	/** @formatter:on**/

	public static TickHandler AIR_PARTICLE_SPAWNER = new AirParticleSpawner(0);
	public static TickHandler FIRE_PARTICLE_SPAWNER = new FireParticleSpawner(1);
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
	public static TickHandler PURIFY_PARTICLE_SPAWNER = new ImmolateParticleHandler(21);
	public static TickHandler RESTORE_PARTICLE_SPAWNER = new RestoreParticleHandler(22);
	public static TickHandler SLIPSTREAM_WALK_HANDLER = new SlipstreamAirWalkHandler(23);
	public static TickHandler WATERARC_COMBO_HANDLER = new WaterArcComboHandler(24);
	public static TickHandler SHOOT_AIRBURST = new ShootAirBurstHandler(25);


	public static TickHandler fromId(int id) {
		return allHandlers.get(id);
	}

	public static TickHandler fromBytes(ByteBuf buf) {
		return fromId(buf.readInt());
	}
}
