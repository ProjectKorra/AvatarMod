package com.crowsofwar.avatar.common.network.packets.glider;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.network.packets.AvatarPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketSyncGliderDataToClient extends AvatarPacket<PacketSyncGliderDataToClient> {
    public static class Handler implements AvatarPacket.Handler<PacketSyncGliderDataToClient> {

        @Override
        public IMessage onMessageRecieved(final PacketSyncGliderDataToClient message, MessageContext ctx) {

            Minecraft.getMinecraft().addScheduledTask(() -> {
                AvatarMod.proxy.getClientGliderCapability().deserializeNBT(message.nbt);
                AvatarLog.debug("** RECEIVED GLIDER SYNC INFO CLIENTSIDE **");
            });

            return null;
        }
    }

    private NBTTagCompound nbt;

    public PacketSyncGliderDataToClient() {}

    public PacketSyncGliderDataToClient(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public void avatarFromBytes(ByteBuf buf) {
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void avatarToBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbt);
    }

    @Override
    protected Side getReceivedSide() {
        return Side.CLIENT;
    }

    @Override
    protected AvatarPacket.Handler<PacketSyncGliderDataToClient> getPacketHandler() {
        return new Handler();
    }



}
