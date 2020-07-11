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

package com.crowsofwar.avatar.network;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.bending.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.util.data.StatusControl;
import com.crowsofwar.avatar.util.data.*;
import io.netty.buffer.ByteBuf;

import java.util.*;

import static com.crowsofwar.gorecore.util.GoreCoreByteBufUtil.readUUID;
import static com.crowsofwar.gorecore.util.GoreCoreByteBufUtil.writeUUID;

/**
 * DataTransmitters are responsible for reading and writing certain parts of
 * player data to the network. For example, there is a transmitter for the
 * bending list, the ability data, and chi.
 *
 * @author CrowsOfWar
 */
public class DataTransmitters {

	public static final DataTransmitter<List<UUID>> BENDING_LIST = new
			DataTransmitter<List<UUID>>() {

				@Override
				public void write(ByteBuf buf, List<UUID> t) {
					buf.writeInt(t.size());
					for (UUID bendingId : t) {
						writeUUID(buf, bendingId);
					}
				}

				@Override
				public List<UUID> read(ByteBuf buf, BendingData data) {
					int size = buf.readInt();
					List<UUID> out = new ArrayList<>(size);
					for (int i = 0; i < size; i++) {
						out.add(readUUID(buf));
					}
					return out;
				}
			};

	public static final DataTransmitter<Map<String, AbilityData>> ABILITY_DATA = new
			DataTransmitter<Map<String, AbilityData>>() {

				@Override
				public void write(ByteBuf buf, Map<String, AbilityData> t) {
					Set<Map.Entry<String, AbilityData>> entries = t.entrySet();
					buf.writeInt(entries.size());
					for (Map.Entry<String, AbilityData> entry : entries) {
						entry.getValue().toBytes(buf);
					}
				}

				@Override
				public Map<String, AbilityData> read(ByteBuf buf, BendingData data) {
					Map<String, AbilityData> out = new HashMap<>();
					int size = buf.readInt();
					for (int i = 0; i < size; i++) {
						AbilityData abilityData = AbilityData.createFromBytes(buf, data);
						if (abilityData == null) {
							AvatarLog.warn(WarningType.WEIRD_PACKET, "Invalid ability ID sent for ability data");
						} else {
							out.put(abilityData.getAbilityName(), abilityData);
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
				StatusControl sc = StatusControlController.lookup(buf.readInt());
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
				if (handler != null) {
					buf.writeInt(handler.id());
				}
			}
		}

		@Override
		public List<TickHandler> read(ByteBuf buf, BendingData data) {
			int size = buf.readInt();
			List<TickHandler> out = new ArrayList<>();
			for (int i = 0; i < size; i++) {
				if (!buf.isReadable(4)) break;
				TickHandler list = TickHandlerController.fromId(buf.readInt());
				if (list == null)
					AvatarLog.warn(WarningType.WEIRD_PACKET, "Invalid tick handler id");
				else
					out.add(list);
			}
			return out;
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

	public static final DataTransmitter<UUID> ACTIVE_BENDING = new DataTransmitter<UUID>() {

		@Override
		public void write(ByteBuf buf, UUID t) {
			writeUUID(buf, t == null ? new UUID(0, 0) : t);
		}

		@Override
		public UUID read(ByteBuf buf, BendingData data) {
			UUID id = readUUID(buf);
			if (id.equals(new UUID(0, 0))) {
				return null;
			}
			return id;
		}
	};

	public static final DataTransmitter<Vision> VISION = new DataTransmitter<Vision>() {

		@Override
		public void write(ByteBuf buf, Vision vision) {
			buf.writeInt(vision == null ? -1 : vision.ordinal());
		}

		@Override
		public Vision read(ByteBuf buf, BendingData data) {
			int id = buf.readInt();
			return id == -1 ? null : Vision.values()[id];
		}

	};

	public static final DataTransmitter<BattlePerformanceScore> PERFORMANCE = new DataTransmitter<BattlePerformanceScore>() {

		@Override
		public BattlePerformanceScore read(ByteBuf buf, BendingData data) {
			return new BattlePerformanceScore(data, buf.readDouble());
		}

		@Override
		public void write(ByteBuf buf, BattlePerformanceScore o) {
			buf.writeDouble(o.getScore());
		}

	};

}
