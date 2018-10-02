package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.client.gui.RenderElementTickHandler;
import com.crowsofwar.avatar.common.bending.air.*;
import com.crowsofwar.avatar.common.bending.earth.RestoreCooldownHandler;
import com.crowsofwar.avatar.common.bending.earth.RestoreParticleHandler;
import com.crowsofwar.avatar.common.bending.earth.SpawnEarthspikesHandler;
import com.crowsofwar.avatar.common.bending.fire.*;
import com.crowsofwar.avatar.common.bending.lightning.LightningCreateHandler;
import com.crowsofwar.avatar.common.bending.lightning.LightningRedirectHandler;
import com.crowsofwar.avatar.common.bending.water.*;
import com.crowsofwar.avatar.common.entity.mob.BisonSummonHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLLog;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mahtaran
 */
public class TickHandlerController {
	public static int AIR_PARTICLE_SPAWNER_ID = 0;
	public static int FIRE_PARTICLE_SPAWNER_ID = 1;
	public static int FLAMETHROWER_ID = 2;
	public static int WATER_SKATE_ID = 3;
	public static int BISON_SUMMONER_ID = 4;
	public static int SMASH_GROUND_ID = 5;
	public static int LIGHTNING_CHARGE_ID = 6;
	public static int WATER_CHARGE_ID = 7;
	public static int LIGHTNING_REDIRECT_ID = 8;
	public static int SMASH_GROUND_FIRE_ID = 9;
	public static int SMASH_GROUND_FIRE_BIG_ID = 10;
	public static int SMASH_GROUND_WATER_ID = 11;
	public static int WATER_PARTICLE_SPAWNER_ID = 12;
	public static int INFERNO_PARTICLE_SPAWNER_ID = 13;
	public static int SPAWN_EARTHSPIKES_HANDLER_ID = 14;
	public static int AIRBURST_CHARGE_HANDLER_ID = 15;
	public static int AIR_STATCTRL_HANDLER_ID = 16;
	public static int FIRE_STATCTRL_HANDLER_ID = 17;
	public static int AIR_DODGE_ID = 18;
	public static int RENDER_ELEMENT_HANDLER_ID = 19;
	public static int STAFF_GUST_HANDLER_ID = 20;
	public static int SLIPSTREAM_COOLDOWN_HANDLER_ID = 21;
	public static int PURIFY_COOLDOWN_HANDLER_ID = 22;
	public static int PURIFY_PARTICLE_SPAWNER_ID = 23;
	public static int FIRE_DEVOUR_HANDLER_ID = 24;
	public static int CLEANSE_COOLDOWN_HANDLER_ID = 25;
	public static int RESTORE_COOLDOWN_HANDLER_ID = 26;
	public static int RESTORE_PARTICLE_SPAWNER_ID = 27;
	static Map<Integer, TickHandler> allHandlers = new HashMap<>();

	static {
		new AirParticleSpawner(AIR_PARTICLE_SPAWNER_ID);
		new FireParticleSpawner(FIRE_PARTICLE_SPAWNER_ID);
		new FlamethrowerUpdateTick(FLAMETHROWER_ID);
		new WaterSkateHandler(WATER_SKATE_ID);
		new BisonSummonHandler(BISON_SUMMONER_ID);
		new SmashGroundHandler(SMASH_GROUND_ID);
		new LightningCreateHandler(LIGHTNING_CHARGE_ID);
		new WaterChargeHandler(WATER_CHARGE_ID);
		new LightningRedirectHandler(LIGHTNING_REDIRECT_ID);
		new FireSmashGroundHandler(SMASH_GROUND_FIRE_ID);
		new FireSmashGroundHandlerBig(SMASH_GROUND_FIRE_BIG_ID);
		new WaterSmashHandler(SMASH_GROUND_WATER_ID);
		new WaterParticleSpawner(WATER_PARTICLE_SPAWNER_ID);
		new InfernoPunchParticleSpawner(INFERNO_PARTICLE_SPAWNER_ID);
		new SpawnEarthspikesHandler(SPAWN_EARTHSPIKES_HANDLER_ID);
		new AirBurstHandler(AIRBURST_CHARGE_HANDLER_ID);
		new AirStatusControlHandler(AIR_STATCTRL_HANDLER_ID);
		new FireStatusControlHandler(FIRE_STATCTRL_HANDLER_ID);
		new AirDodgeHandler(AIR_DODGE_ID);
		new RenderElementTickHandler(RENDER_ELEMENT_HANDLER_ID);
		new StaffGustCooldown(STAFF_GUST_HANDLER_ID);
		new SlipstreamCooldownHandler(SLIPSTREAM_COOLDOWN_HANDLER_ID);
		new PurifyCooldownHandler(PURIFY_COOLDOWN_HANDLER_ID);
		new PurifyParticleHandler(PURIFY_PARTICLE_SPAWNER_ID);
		new FireDevourTickHandler(FIRE_DEVOUR_HANDLER_ID);
		new CleanseCooldownHandler(CLEANSE_COOLDOWN_HANDLER_ID);
		new RestoreCooldownHandler(RESTORE_COOLDOWN_HANDLER_ID);
		new RestoreParticleHandler(RESTORE_PARTICLE_SPAWNER_ID);
	}

	public static TickHandler fromId(int id) {
		System.out.println(id);
		//FMLLog.info("allHandlers = %s", allHandlers);
		return allHandlers.get(id);
	}

	public static TickHandler fromBytes(ByteBuf buf) {
		System.out.println(buf.readInt());
		return fromId(buf.readInt());
	}
}
