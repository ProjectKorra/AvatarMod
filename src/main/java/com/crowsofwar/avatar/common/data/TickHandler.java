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
