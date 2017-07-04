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
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.data.DataCategory;
import com.crowsofwar.avatar.common.data.MiscData;
import com.crowsofwar.avatar.common.data.TickHandler;

import io.netty.buffer.ByteBuf;

/**
 * DataTransmitters are responsible for reading and writing certain parts of
 * player data to the network. For example, there is a transmitter for the
 * bending list, the ability data, and chi.
 * 
 * @author CrowsOfWar
 */
public class DataTransmitters {
	
	public static final DataTransmitter<List<BendingStyle>> BENDING_LIST = new DataTransmitter<List<BendingStyle>>() {
		
		@Override
		public void write(ByteBuf buf, List<BendingStyle> t) {
			buf.writeInt(t.size());
			for (BendingStyle controller : t)
				buf.writeInt(controller.getId());
		}
		
		@Override
		public List<BendingStyle> read(ByteBuf buf, BendingData data) {
			int size = buf.readInt();
			List<BendingStyle> out = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				out.add(BendingManager.getBending(buf.readInt()));
			}
			return out;
		}
	};
	
	public static final DataTransmitter<Map<Ability, AbilityData>> ABILITY_DATA = new DataTransmitter<Map<Ability, AbilityData>>() {
		
		@Override
		public void write(ByteBuf buf, Map<Ability, AbilityData> t) {
			Set<Map.Entry<Ability, AbilityData>> entries = t.entrySet();
			buf.writeInt(entries.size());
			for (Map.Entry<Ability, AbilityData> entry : entries) {
				entry.getValue().toBytes(buf);
			}
		}
		
		@Override
		public Map<Ability, AbilityData> read(ByteBuf buf, BendingData data) {
			Map<Ability, AbilityData> out = new HashMap<>();
			int size = buf.readInt();
			for (int i = 0; i < size; i++) {
				AbilityData abilityData = AbilityData.createFromBytes(buf, data);
				if (abilityData == null) {
					AvatarLog.warn(WarningType.WEIRD_PACKET, "Invalid ability ID sent for ability data");
				} else {
					out.put(abilityData.getAbility(), abilityData);
				}
			}
			return out;
		}
	};
	
	public static final DataTransmitter<List<StatusControl>> STATUS_CONTROLS = new DataTransmitter<List<StatusControl>>() {
		
		@Override
		public void write(ByteBuf buf, List<StatusControl> t) {
			buf.writeInt(t.size());
			for (StatusControl sc : t) {
				buf.writeInt(sc.id());
			}
		}
		
		@Override
		public List<StatusControl> read(ByteBuf buf, BendingData data) {
			int size = buf.readInt();
			List<StatusControl> out = new ArrayList<>();
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
	
	public static final DataTransmitter<Boolean> BOOLEAN = new DataTransmitter<Boolean>() {
		
		@Override
		public void write(ByteBuf buf, Boolean t) {
			buf.writeBoolean(t);
		}
		
		@Override
		public Boolean read(ByteBuf buf, BendingData data) {
			return buf.readBoolean();
		}
	};
	
	public static final DataTransmitter<Chi> CHI = new DataTransmitter<Chi>() {
		
		@Override
		public void write(ByteBuf buf, Chi t) {
			t.toBytes(buf);
		}
		
		@Override
		public Chi read(ByteBuf buf, BendingData data) {
			Chi chi = new Chi(data);
			chi.fromBytes(buf);
			return chi;
		}
	};
	
	public static final DataTransmitter<List<TickHandler>> TICK_HANDLERS = new DataTransmitter<List<TickHandler>>() {
		
		@Override
		public void write(ByteBuf buf, List<TickHandler> list) {
			buf.writeInt(list.size());
			for (TickHandler handler : list) {
				buf.writeInt(handler.id());
			}
		}
		
		@Override
		public List<TickHandler> read(ByteBuf buf, BendingData data) {
			List<TickHandler> list = new ArrayList<>();
			int length = buf.readInt();
			for (int i = 0; i < length; i++) {
				list.add(TickHandler.fromBytes(buf));
			}
			return list;
		}
		
	};
	
	public static final DataTransmitter<MiscData> MISC_DATA = new DataTransmitter<MiscData>() {
		
		@Override
		public void write(ByteBuf buf, MiscData t) {
			t.toBytes(buf);
		}
		
		@Override
		public MiscData read(ByteBuf buf, BendingData data) {
			MiscData misc = new MiscData(() -> data.save(DataCategory.MISC_DATA));
			misc.fromBytes(buf);
			return misc;
		}
	};
	
	public static final DataTransmitter<BendingStyle> ACTIVE_BENDING = new DataTransmitter<BendingStyle>() {
		
		@Override
		public void write(ByteBuf buf, BendingStyle t) {
			buf.writeInt(t == null ? -1 : t.getId());
		}
		
		@Override
		public BendingStyle read(ByteBuf buf, BendingData data) {
			int id = buf.readInt();
			if (id == -1) {
				return null;
			}
			return BendingManager.getBending(id);
		}
	};
	
}
