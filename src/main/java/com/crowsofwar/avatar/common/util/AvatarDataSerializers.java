package com.crowsofwar.avatar.common.util;

import java.io.IOException;

import com.crowsofwar.gorecore.util.VectorD;

import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarDataSerializers {
	
	public static final DataSerializer<Block> SERIALIZER_BLOCK = new DataSerializer<Block>() {
		
		@Override
		public void write(PacketBuffer buf, Block value) {
			// TODO Find out if DataSerializer<Block> actually works...
			buf.writeString(value.getUnlocalizedName());
		}
		
		@Override
		public Block read(PacketBuffer buf) throws IOException {
			return Block.getBlockFromName(buf.readStringFromBuffer(20));
		}
		
		@Override
		public DataParameter<Block> createKey(int id) {
			return new DataParameter<>(id, this);
		}
	};
	public static final DataSerializer<VectorD> SERIALIZER_VECTOR = new DataSerializer<VectorD>() {
		
		@Override
		public void write(PacketBuffer buf, VectorD value) {
			buf.writeDouble(value.x());
			buf.writeDouble(value.y());
			buf.writeDouble(value.z());
		}
		
		@Override
		public VectorD read(PacketBuffer buf) throws IOException {
			return new VectorD(buf.readDouble(), buf.readDouble(), buf.readDouble());
		}
		
		@Override
		public DataParameter<VectorD> createKey(int id) {
			return new DataParameter<>(id, this);
		}
	};
	
}
