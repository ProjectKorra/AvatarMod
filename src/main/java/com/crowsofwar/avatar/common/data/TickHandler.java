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

import java.util.HashMap;
import java.util.Map;

import com.crowsofwar.avatar.common.data.ctx.Bender;

import io.netty.buffer.ByteBuf;

/**
 * 
 * 
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
	
	public abstract void tick(BendingData data, Bender bender);
	
	public int id() {
		return id;
	}
	
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
	}
	
	public static TickHandler fromId(int id) {
		
	}
	
	public static TickHandler fromBytes(ByteBuf buf) {
		
	}
	
}
