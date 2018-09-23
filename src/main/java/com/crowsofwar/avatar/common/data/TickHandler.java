/*
  This file is part of AvatarMod.

  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/
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
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.mob.BisonSummonHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLLog;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CrowsOfWar
 */
public abstract class TickHandler {
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
	static {
		new AirParticleSpawner(AIR_PARTICLE_SPAWNER_ID);
		new FireParticleSpawner(FIRE_PARTICLE_SPAWNER_ID);
		new FlamethrowerUpdateTick(FLAMETHROWER_ID);
		new WaterSkateHandler(WATER_SKATE_ID);
	}
	/*
	public static TickHandler BISON_SUMMONER = new BisonSummonHandler();
	public static TickHandler SMASH_GROUND = new SmashGroundHandler();
	public static TickHandler LIGHTNING_CHARGE = new LightningCreateHandler();
	public static TickHandler WATER_CHARGE = new WaterChargeHandler();
	public static TickHandler LIGHTNING_REDIRECT = new LightningRedirectHandler();
	public static TickHandler SMASH_GROUND_FIRE = new FireSmashGroundHandler();
	public static TickHandler SMASH_GROUND_FIRE_BIG = new FireSmashGroundHandlerBig();
	public static TickHandler SMASH_GROUND_WATER = new WaterSmashHandler();
	public static TickHandler WATER_PARTICLE_SPAWNER = new WaterParticleSpawner();
	public static TickHandler INFERNO_PARTICLE_SPAWNER = new InfernoPunchParticleSpawner();
	public static TickHandler SPAWN_EARTHSPIKES_HANDLER = new SpawnEarthspikesHandler();
	public static TickHandler AIRBURST_CHARGE_HANDLER = new AirBurstHandler();
	public static TickHandler AIR_STATCTRL_HANDLER = new AirStatusControlHandler();
	public static TickHandler FIRE_STATCTRL_HANDLER = new FireStatusControlHandler();
	public static TickHandler AIR_DODGE = new AirDodgeHandler();
	public static TickHandler RENDER_ELEMENT_HANDLER = new RenderElementTickHandler();
	public static TickHandler STAFF_GUST_HANDLER = new StaffGustCooldown();
	public static TickHandler SLIPSTREAM_COOLDOWN_HANDLER = new SlipstreamCooldownHandler();
	public static TickHandler PURIFY_COOLDOWN_HANDLER = new PurifyCooldownHandler();
	public static TickHandler PURIFY_PARTICLE_SPAWNER = new PurifyParticleHandler();
	public static TickHandler FIRE_DEVOUR_HANDLER = new FireDevourTickHandler();
	public static TickHandler CLEANSE_COOLDOWN_HANDLER = new CleanseCooldownHandler();
	public static TickHandler RESTORE_COOLDOWN_HANDLER = new RestoreCooldownHandler();
	public static TickHandler RESTORE_PARTICLE_SPAWNER = new RestoreParticleHandler();
	**/
	
	private static Map<Integer, TickHandler> allHandlers = new HashMap<>();
	private final int id;
	
	public TickHandler(int id) {
		this.id = id;
		allHandlers.put(id, this);

	}

	public static TickHandler fromId(int id) {
		FMLLog.info("allHandlers = %s", allHandlers);
		return allHandlers.get(id);
	}

	public static TickHandler fromBytes(ByteBuf buf) {
		return fromId(buf.readInt());
	}

	/**
	 * Ticks and returns whether to remove (false to stay)
	 */
	public abstract boolean tick(BendingContext ctx);

	public int id() {
		return id;
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
	}

}
