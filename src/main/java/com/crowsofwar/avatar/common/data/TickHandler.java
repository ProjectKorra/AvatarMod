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



import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.crowsofwar.avatar.client.gui.RenderElementTickHandler.RENDER_ELEMENT_HANDLER;
import static com.crowsofwar.avatar.common.bending.air.AirBurstHandler.AIRBURST_CHARGE_HANDLER;
import static com.crowsofwar.avatar.common.bending.air.AirDodgeHandler.AIR_DODGE;
import static com.crowsofwar.avatar.common.bending.air.AirParticleSpawner.AIR_PARTICLE_SPAWNER;
import static com.crowsofwar.avatar.common.bending.air.AirStatusControlHandler.AIR_STATCTRL_HANDLER;
import static com.crowsofwar.avatar.common.bending.air.SlipstreamCooldownHandler.SLIPSTREAM_COOLDOWN_HANDLER;
import static com.crowsofwar.avatar.common.bending.air.SmashGroundHandler.SMASH_GROUND;
import static com.crowsofwar.avatar.common.bending.air.StaffGustCooldown.STAFF_GUST_HANDLER;
import static com.crowsofwar.avatar.common.bending.earth.RestoreCooldownHandler.RESTORE_COOLDOWN_HANDLER;
import static com.crowsofwar.avatar.common.bending.earth.RestoreParticleHandler.RESTORE_PARTICLE_SPAWNER;
import static com.crowsofwar.avatar.common.bending.earth.SpawnEarthspikesHandler.SPAWN_EARTHSPIKES_HANDLER;
import static com.crowsofwar.avatar.common.bending.fire.FireDevourTickHandler.FIRE_DEVOUR_HANDLER;
import static com.crowsofwar.avatar.common.bending.fire.FireParticleSpawner.FIRE_PARTICLE_SPAWNER;
import static com.crowsofwar.avatar.common.bending.fire.FireSmashGroundHandler.SMASH_GROUND_FIRE;
import static com.crowsofwar.avatar.common.bending.fire.FireSmashGroundHandlerBig.SMASH_GROUND_FIRE_BIG;
import static com.crowsofwar.avatar.common.bending.fire.FireStatusControlHandler.FIRE_STATCTRL_HANDLER;
import static com.crowsofwar.avatar.common.bending.fire.FlamethrowerUpdateTick.FLAMETHROWER;
import static com.crowsofwar.avatar.common.bending.fire.InfernoPunchParticleSpawner.INFERNO_PARTICLE_SPAWNER;
import static com.crowsofwar.avatar.common.bending.fire.PurifyCooldownHandler.PURIFY_COOLDOWN_HANDLER;
import static com.crowsofwar.avatar.common.bending.fire.PurifyParticleHandler.PURIFY_PARTICLE_SPAWNER;
import static com.crowsofwar.avatar.common.bending.lightning.LightningChargeHandler.LIGHTNING_CHARGE;
import static com.crowsofwar.avatar.common.bending.lightning.LightningRedirectHandler.LIGHTNING_REDIRECT;
import static com.crowsofwar.avatar.common.bending.water.CleanseCooldownHandler.CLEANSE_COOLDOWN_HANDLER;
import static com.crowsofwar.avatar.common.bending.water.WaterChargeHandler.WATER_CHARGE;
import static com.crowsofwar.avatar.common.bending.water.WaterParticleSpawner.WATER_PARTICLE_SPAWNER;
import static com.crowsofwar.avatar.common.bending.water.WaterParticleSpawner.WATER_SKATE;
import static com.crowsofwar.avatar.common.bending.water.WaterSmashHandler.SMASH_GROUND_WATER;
import static com.crowsofwar.avatar.common.entity.mob.BisonSummonHandler.BISON_SUMMONER;

/**
 * @author CrowsOfWar
 */
public abstract class TickHandler {

	private static int nextId = 0;
	public static List<TickHandler> allHandlers;
	private int id;


	public TickHandler() {
		if (allHandlers == null) allHandlers = new ArrayList<>();
		this.id = ++nextId;
		allHandlers.add(this);

	}

	 public static TickHandler fromId(int id) {
		 id--;
		 return id >= 0 && id < allHandlers.size() ? allHandlers.get(id) : null;
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
