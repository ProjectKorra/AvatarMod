package com.crowsofwar.avatar.common.util;

import java.io.IOException;

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
			// TODO Auto-generated method stub
			return null;
		}
	};
	
}
