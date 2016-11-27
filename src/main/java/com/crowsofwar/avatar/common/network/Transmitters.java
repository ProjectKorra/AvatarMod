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
import com.crowsofwar.avatar.common.data.BendingState;

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
	
	public static final DataTransmitter<BendingState, PlayerDataContext> STATE_SINGLE = new DataTransmitter<BendingState, PlayerDataContext>() {
		
		@Override
		public void write(ByteBuf buf, BendingState t) {
			buf.writeInt(t.getType().id());
			t.toBytes(buf);
		}
		
		@Override
		public BendingState read(ByteBuf buf, PlayerDataContext ctx) {
			BendingState state = BendingManager.getBending(BendingType.find(buf.readInt()))
					.createState(ctx.getData());
			state.fromBytes(buf);
			return state;
		}
	};
	
	public static final DataTransmitter<List<BendingState>, PlayerDataContext> STATE_LIST = new DataTransmitter<List<BendingState>, PlayerDataContext>() {
		
		@Override
		public void write(ByteBuf buf, List<BendingState> t) {
			buf.writeInt(t.size());
			for (BendingState state : t) {
				buf.writeInt(state.getType().id());
				state.toBytes(buf);
			}
		}
		
		@Override
		public List<BendingState> read(ByteBuf buf, PlayerDataContext ctx) {
			int size = buf.readInt();
			ArrayList out = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				BendingState state = BendingManager.getBending(BendingType.find(buf.readInt()))
						.createState(ctx.getData());
				state.fromBytes(buf);
				out.add(state);
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
				StatusControl sc = StatusControl.lookup(i);
				if (sc == null)
					AvatarLog.warn(WarningType.WEIRD_PACKET, "Invalid status control id");
				else
					out.add(sc);
			}
			return out;
		}
	};
}
