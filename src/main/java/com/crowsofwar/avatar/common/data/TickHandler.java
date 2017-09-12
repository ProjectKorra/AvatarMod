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

import com.crowsofwar.avatar.common.bending.SmashGroundHandler;
import com.crowsofwar.avatar.common.bending.air.AirParticleSpawner;
import com.crowsofwar.avatar.common.bending.fire.FlamethrowerUpdateTick;
import com.crowsofwar.avatar.common.bending.ice.IceWalkHandler;
import com.crowsofwar.avatar.common.bending.lightning.LightningChargeHandler;
import com.crowsofwar.avatar.common.bending.lightning.LightningRedirectHandler;
import com.crowsofwar.avatar.common.bending.water.WaterSkateHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.mob.BisonSummonHandler;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class TickHandler {
	
	public static TickHandler AIR_PARTICLE_SPAWNER = new AirParticleSpawner();
	public static TickHandler FLAMETHROWER = new FlamethrowerUpdateTick();
	public static TickHandler WATER_SKATE = new WaterSkateHandler();
	public static TickHandler BISON_SUMMONER = new BisonSummonHandler();
	public static TickHandler SMASH_GROUND = new SmashGroundHandler();
	public static TickHandler ICE_WALK = new IceWalkHandler();
	public static TickHandler LIGHTNING_CHARGE = new LightningChargeHandler();
	public static TickHandler LIGHTNING_REDIRECT = new LightningRedirectHandler();
	
	private static int nextId = 1;
	private static Map<Integer, TickHandler> allHandlers;
	private final int id;
	
	public TickHandler() {
		if (allHandlers == null) allHandlers = new HashMap<>();
		
		id = nextId++;
		allHandlers.put(id, this);
		
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
	
	public static TickHandler fromId(int id) {
		return allHandlers.get(id);
	}
	
	public static TickHandler fromBytes(ByteBuf buf) {
		return fromId(buf.readInt());
	}
	
}
