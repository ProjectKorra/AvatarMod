package com.crowsofwar.avatar.common.util;

import java.io.IOException;

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
	
	public static void register() {
		DataSerializers.registerSerializer(SERIALIZER_BLOCK);
		DataSerializers.registerSerializer(SERIALIZER_VECTOR);
	}
	
	private static abstract class AvatarSerializer<T> implements DataSerializer<T> {
		
		protected AvatarSerializer() {}
		
	}
	
}
