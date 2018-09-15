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

import java.util.HashMap;
import java.util.Map;

/**
 * @author CrowsOfWar
 */
public abstract class TickHandler {

	public static final TickHandler AIR_PARTICLE_SPAWNER = new AirParticleSpawner();
	public static final TickHandler FIRE_PARTICLE_SPAWNER = new FireParticleSpawner();
	public static final TickHandler FLAMETHROWER = new FlamethrowerUpdateTick();
	public static final TickHandler WATER_SKATE = new WaterSkateHandler();
	public static final TickHandler BISON_SUMMONER = new BisonSummonHandler();
	public static final TickHandler SMASH_GROUND = new SmashGroundHandler();
	public static final TickHandler LIGHTNING_CHARGE = new LightningCreateHandler();
	public static final TickHandler WATER_CHARGE = new WaterChargeHandler();
	public static final TickHandler LIGHTNING_REDIRECT = new LightningRedirectHandler();
	public static final TickHandler SMASH_GROUND_FIRE = new FireSmashGroundHandler();
	public static final TickHandler SMASH_GROUND_FIRE_BIG = new FireSmashGroundHandlerBig();
	public static final TickHandler SMASH_GROUND_WATER = new WaterSmashHandler();
	public static final TickHandler WATER_PARTICLE_SPAWNER = new WaterParticleSpawner();
	public static final TickHandler INFERNO_PARTICLE_SPAWNER = new InfernoPunchParticleSpawner();
	public static final TickHandler SPAWN_EARTHSPIKES_HANDLER = new SpawnEarthspikesHandler();
	public static final TickHandler AIRBURST_CHARGE_HANDLER = new AirBurstHandler();
	public static final TickHandler AIR_STATCTRL_HANDLER = new AirStatusControlHandler();
	public static final TickHandler FIRE_STATCTRL_HANDLER = new FireStatusControlHandler();
	public static final TickHandler AIR_DODGE = new AirDodgeHandler();
	public static final TickHandler RENDER_ELEMENT_HANDLER = new RenderElementTickHandler();
	public static final TickHandler STAFF_GUST_HANDLER = new StaffGustCooldown();
	public static final TickHandler SLIPSTREAM_COOLDOWN_HANDLER = new SlipstreamCooldownHandler();
	public static final TickHandler PURIFY_COOLDOWN_HANDLER = new PurifyCooldownHandler();
	public static final TickHandler PURIFY_PARTICLE_SPAWNER = new PurifyParticleHandler();
	public static final TickHandler FIRE_DEVOUR_HANDLER = new FireDevourTickHandler();
	public static final TickHandler CLEANSE_COOLDOWN_HANDLER = new CleanseCooldownHandler();
	public static final TickHandler RESTORE_COOLDOWN_HANDLER = new RestoreCooldownHandler();
	public static final TickHandler RESTORE_PARTICLE_SPAWNER = new RestoreParticleHandler();


	private static int nextId = 1;
	private static Map<Integer, TickHandler> allHandlers;
	private final int id;

	public TickHandler() {
		if (allHandlers == null) allHandlers = new HashMap<>();

		id = nextId++;
		allHandlers.put(id, this);

	}

	public static TickHandler fromId(int id) {
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