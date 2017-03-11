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

package com.crowsofwar.avatar.common.util;

import java.io.IOException;

import com.crowsofwar.avatar.common.data.ctx.BenderInfo;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarDataSerializers {
	
	public static final DataSerializer<Block> SERIALIZER_BLOCK = new AvatarSerializer<Block>() {
		
		@Override
		public void write(PacketBuffer buf, Block value) {
			// TODO Find out if DataSerializer<Block> actually works...
			buf.writeInt(Block.getIdFromBlock(value));
		}
		
		@Override
		public Block read(PacketBuffer buf) throws IOException {
			return Block.getBlockById(buf.readInt());
		}
		
		@Override
		public DataParameter<Block> createKey(int id) {
			return new DataParameter<>(id, this);
		}
	};
	public static final DataSerializer<Vector> SERIALIZER_VECTOR = new AvatarSerializer<Vector>() {
		
		@Override
		public void write(PacketBuffer buf, Vector value) {
			buf.writeDouble(value.x());
			buf.writeDouble(value.y());
			buf.writeDouble(value.z());
		}
		
		@Override
		public Vector read(PacketBuffer buf) throws IOException {
			return new Vector(buf.readDouble(), buf.readDouble(), buf.readDouble());
		}
		
		@Override
		public DataParameter<Vector> createKey(int id) {
			return new DataParameter<>(id, this);
		}
	};
	public static final DataSerializer<BenderInfo> SERIALIZER_BENDER = new AvatarSerializer<BenderInfo>() {
		
		@Override
		public void write(PacketBuffer buf, BenderInfo info) {
			buf.writeBoolean(info.isPlayer());
			buf.writeUuid(info.getId());
		}
		
		@Override
		public BenderInfo read(PacketBuffer buf) throws IOException {
			return new BenderInfo(buf.readBoolean(), buf.readUuid());
		}
		
		@Override
		public DataParameter<BenderInfo> createKey(int id) {
			return new DataParameter<>(id, this);
		}
		
	};
	
	public static void register() {
		DataSerializers.registerSerializer(SERIALIZER_BLOCK);
		DataSerializers.registerSerializer(SERIALIZER_VECTOR);
		DataSerializers.registerSerializer(SERIALIZER_BENDER);
	}
	
	private static abstract class AvatarSerializer<T> implements DataSerializer<T> {
		
		protected AvatarSerializer() {}
		
	}
	
}
