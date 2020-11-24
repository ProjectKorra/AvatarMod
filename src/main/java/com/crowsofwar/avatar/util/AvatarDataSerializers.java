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

package com.crowsofwar.avatar.util;

import com.crowsofwar.avatar.util.data.BenderInfo;
import com.crowsofwar.avatar.item.ItemBisonArmor.ArmorTier;
import com.crowsofwar.avatar.item.ItemBisonSaddle.SaddleTier;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

import java.io.IOException;

/**
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

		@Override
		public Block copyValue(Block block) {
			return block;
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

		@Override
		public Vector copyValue(Vector vec) {
			return vec;
		}
	};
	public static final DataSerializer<BenderInfo> SERIALIZER_BENDER = new AvatarSerializer<BenderInfo>() {

		@Override
		public void write(PacketBuffer buf, BenderInfo info) {
			info.writeToBytes(buf);
		}

		@Override
		public BenderInfo read(PacketBuffer buf) throws IOException {
			return BenderInfo.readFromBytes(buf);
		}

		@Override
		public DataParameter<BenderInfo> createKey(int id) {
			return new DataParameter<>(id, this);
		}

		@Override
		public BenderInfo copyValue(BenderInfo benderInfo) {
			return benderInfo;
		}

	};
	public static final DataSerializer<SaddleTier> SERIALIZER_SADDLE = new AvatarSerializer<SaddleTier>() {

		@Override
		public void write(PacketBuffer buf, SaddleTier value) {
			buf.writeInt(value == null ? -1 : value.id());
		}

		@Override
		public SaddleTier read(PacketBuffer buf) throws IOException {
			int id = buf.readInt();
			return id == -1 ? null : SaddleTier.get(id);
		}

		@Override
		public DataParameter<SaddleTier> createKey(int id) {
			return new DataParameter<>(id, this);
		}

		@Override
		public SaddleTier copyValue(SaddleTier tier) {
			return tier;
		}
	};
	public static final DataSerializer<ArmorTier> SERIALIZER_ARMOR = new AvatarSerializer<ArmorTier>() {

		@Override
		public void write(PacketBuffer buf, ArmorTier value) {
			buf.writeInt(value == null ? -1 : value.id());
		}

		@Override
		public ArmorTier read(PacketBuffer buf) throws IOException {
			int id = buf.readInt();
			return id == -1 ? null : ArmorTier.get(id);
		}

		@Override
		public DataParameter<ArmorTier> createKey(int id) {
			return new DataParameter<>(id, this);
		}

		@Override
		public ArmorTier copyValue(ArmorTier tier) {
			return tier;
		}
	};

	public static void register() {
		DataSerializers.registerSerializer(SERIALIZER_BLOCK);
		DataSerializers.registerSerializer(SERIALIZER_VECTOR);
		DataSerializers.registerSerializer(SERIALIZER_BENDER);
		DataSerializers.registerSerializer(SERIALIZER_SADDLE);
		DataSerializers.registerSerializer(SERIALIZER_ARMOR);
	}

	private static abstract class AvatarSerializer<T> implements DataSerializer<T> {

		protected AvatarSerializer() {
		}

	}

}
