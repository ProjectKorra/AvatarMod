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

package com.crowsofwar.avatar.common.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.data.TickHandler;

import io.netty.buffer.ByteBuf;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class Transmitters {
	
	public static final DataTransmitter<List<BendingController>, PlayerDataContext> CONTROLLER_LIST = new DataTransmitter<List<BendingController>, PlayerDataContext>() {
		
		@Override
		public void write(ByteBuf buf, List<BendingController> t) {
			buf.writeInt(t.size());
			for (BendingController controller : t)
				buf.writeInt(controller.getType().id());
		}
		
		@Override
		public List<BendingController> read(ByteBuf buf, PlayerDataContext ctx) {
			int size = buf.readInt();
			List<BendingController> out = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				out.add(BendingManager.getBending(BendingType.find(buf.readInt())));
			}
			return out;
		}
	};
	
	public static final DataTransmitter<Map<BendingAbility, AbilityData>, PlayerDataContext> ABILITY_DATA_MAP = new DataTransmitter<Map<BendingAbility, AbilityData>, PlayerDataContext>() {
		
		@Override
		public void write(ByteBuf buf, Map<BendingAbility, AbilityData> t) {
			Set<Map.Entry<BendingAbility, AbilityData>> entries = t.entrySet();
			buf.writeInt(entries.size());
			for (Map.Entry<BendingAbility, AbilityData> entry : entries) {
				entry.getValue().toBytes(buf);
			}
		}
		
		@Override
		public Map<BendingAbility, AbilityData> read(ByteBuf buf, PlayerDataContext ctx) {
			Map<BendingAbility, AbilityData> out = new HashMap<>();
			int size = buf.readInt();
			for (int i = 0; i < size; i++) {
				AbilityData data = AbilityData.createFromBytes(buf, ctx.getData());
				if (data == null) {
					AvatarLog.warn(WarningType.WEIRD_PACKET, "Invalid ability ID sent for ability data");
				} else {
					out.put(data.getAbility(), data);
				}
			}
			return out;
		}
	};
	
	public static final DataTransmitter<Set<StatusControl>, PlayerDataContext> STATUS_CONTROLS = new DataTransmitter<Set<StatusControl>, PlayerDataContext>() {
		
		@Override
		public void write(ByteBuf buf, Set<StatusControl> t) {
			buf.writeInt(t.size());
			for (StatusControl sc : t) {
				buf.writeInt(sc.id());
			}
		}
		
		@Override
		public Set<StatusControl> read(ByteBuf buf, PlayerDataContext ctx) {
			int size = buf.readInt();
			Set<StatusControl> out = new HashSet<>();
			for (int i = 0; i < size; i++) {
				StatusControl sc = StatusControl.lookup(buf.readInt());
				if (sc == null)
					AvatarLog.warn(WarningType.WEIRD_PACKET, "Invalid status control id");
				else
					out.add(sc);
			}
			return out;
		}
	};
	
	public static final DataTransmitter<Boolean, PlayerDataContext> BOOLEAN = new DataTransmitter<Boolean, PlayerDataContext>() {
		
		@Override
		public void write(ByteBuf buf, Boolean t) {
			buf.writeBoolean(t);
		}
		
		@Override
		public Boolean read(ByteBuf buf, PlayerDataContext ctx) {
			return buf.readBoolean();
		}
	};
	
	public static final DataTransmitter<Chi, PlayerDataContext> CHI = new DataTransmitter<Chi, PlayerDataContext>() {
		
		@Override
		public void write(ByteBuf buf, Chi t) {
			t.toBytes(buf);
		}
		
		@Override
		public Chi read(ByteBuf buf, PlayerDataContext ctx) {
			Chi chi = new Chi(ctx.getData());
			chi.fromBytes(buf);
			return chi;
		}
	};
	
	public static final DataTransmitter<List<TickHandler>, PlayerDataContext> TICK_HANDLERS = new DataTransmitter<List<TickHandler>, PlayerDataContext>() {
		
		@Override
		public void write(ByteBuf buf, List<TickHandler> list) {
			buf.writeInt(list.size());
			for (TickHandler handler : list) {
				buf.writeInt(handler.id());
			}
		}
		
		@Override
		public List<TickHandler> read(ByteBuf buf, PlayerDataContext ctx) {
			List<TickHandler> list = new ArrayList<>();
			int length = buf.readInt();
			for (int i = 0; i < length; i++) {
				list.add(TickHandler.fromBytes(buf));
			}
			return list;
		}
		
	};
	
}
