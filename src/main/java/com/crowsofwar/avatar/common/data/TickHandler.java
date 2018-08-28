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
import com.crowsofwar.avatar.common.bending.earth.RestoreParticleHandler;
import com.crowsofwar.avatar.common.bending.earth.SpawnEarthspikesHandler;
import com.crowsofwar.avatar.common.bending.fire.*;
import com.crowsofwar.avatar.common.bending.lightning.LightningCreateHandler;
import com.crowsofwar.avatar.common.bending.lightning.LightningRedirectHandler;
import com.crowsofwar.avatar.common.bending.water.WaterChargeHandler;
import com.crowsofwar.avatar.common.bending.water.WaterParticleSpawner;
import com.crowsofwar.avatar.common.bending.water.WaterSkateHandler;
import com.crowsofwar.avatar.common.bending.water.WaterSmashHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.mob.BisonSummonHandler;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CrowsOfWar
 */
public abstract class TickHandler {

	public static TickHandler
			AIR_PARTICLE_SPAWNER = new AirParticleSpawner(),
			FIRE_PARTICLE_SPAWNER = new FireParticleSpawner(),
			FLAMETHROWER = new FlamethrowerUpdateTick(),
			WATER_SKATE = new WaterSkateHandler(),
			BISON_SUMMONER = new BisonSummonHandler(),
			SMASH_GROUND = new SmashGroundHandler(),
			LIGHTNING_CHARGE = new LightningCreateHandler(),
			WATER_CHARGE = new WaterChargeHandler(),
			LIGHTNING_REDIRECT = new LightningRedirectHandler(),
			SMASH_GROUND_FIRE = new FireSmashGroundHandler(),
			SMASH_GROUND_FIRE_BIG = new FireSmashGroundHandlerBig(),
			SMASH_GROUND_WATER = new WaterSmashHandler(),
			WATER_PARTICLE_SPAWNER = new WaterParticleSpawner(),
			INFERNO_PARTICLE_SPAWNER = new InfernoPunchParticleSpawner(),
			SPAWN_EARTHSPIKES_HANDLER = new SpawnEarthspikesHandler(),
			AIRBURST_CHARGE_HANDLER = new AirBurstHandler(),
			AIR_STATCTRL_HANDLER = new AirStatusControlHandler(),
			FIRE_STATCTRL_HANDLER = new FireStatusControlHandler(),
			AIR_DODGE = new AirDodgeHandler(),
			RESTORE_PARTICLE_SPAWNER = new RestoreParticleHandler(),
			PURIFY_PARTICLE_SPAWNER = new PurifyParticleHandler(),
			SLIPSTREAM_COOLDOWN_HANDLER = new SlipstreamCooldownHandler(),
			RENDER_ELEMENT_HANDLER = new RenderElementTickHandler(),
			FIRE_DEVOUR_HANDLER = new FireDevourTickHandler();

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
