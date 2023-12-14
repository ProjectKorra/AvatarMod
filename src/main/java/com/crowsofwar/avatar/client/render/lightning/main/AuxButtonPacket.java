package com.crowsofwar.avatar.client.render.lightning.main;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AuxButtonPacket implements IMessage {

	int x;
	int y;
	int z;
	int value;
	int id;

	public AuxButtonPacket()
	{
		
	}

	public AuxButtonPacket(int x, int y, int z, int value, int id)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.value = value;
		this.id = id;
	}
	
	public AuxButtonPacket(BlockPos pos, int value, int id){
		this(pos.getX(), pos.getY(), pos.getZ(), value, id);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		value = buf.readInt();
		id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(value);
		buf.writeInt(id);
	}

	public static class Handler implements IMessageHandler<AuxButtonPacket, IMessage> {

		@Override
		public IMessage onMessage(AuxButtonPacket m, MessageContext ctx) {
			ctx.getServerHandler().player.getServer().addScheduledTask(() -> {
				EntityPlayer p = ctx.getServerHandler().player;
				BlockPos pos = new BlockPos(m.x, m.y, m.z);

				if(m.value == 1000){
					NBTTagCompound perDat = p.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
					int lightning = perDat.getInteger("lightningCharge");
					if(lightning == 0){
						perDat.setInteger("lightningCharge", 1);
					}
				}
			});
			return null;
		}
	}

}
