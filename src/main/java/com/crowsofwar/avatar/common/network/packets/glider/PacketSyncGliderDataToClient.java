package com.crowsofwar.avatar.common.network.packets.glider;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncGliderDataToClient implements IMessage {

    private NBTTagCompound nbt;

    public PacketSyncGliderDataToClient() {}

    public PacketSyncGliderDataToClient(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbt);
    }

    public static class Handler implements IMessageHandler<PacketSyncGliderDataToClient, IMessage> {

        @Override
        public IMessage onMessage(final PacketSyncGliderDataToClient message, MessageContext ctx) {

            Minecraft.getMinecraft().addScheduledTask(() -> {
                AvatarMod.proxy.getClientGliderCapability().deserializeNBT(message.nbt);
                AvatarLog.debug("** RECEIVED GLIDER SYNC INFO CLIENTSIDE **");
            });

            return null;
        }
    }

}
