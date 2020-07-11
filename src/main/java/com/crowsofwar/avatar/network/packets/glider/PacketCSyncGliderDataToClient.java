package com.crowsofwar.avatar.network.packets.glider;

import com.crowsofwar.avatar.network.PacketRedirector;
import com.crowsofwar.avatar.network.packets.AvatarPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class PacketCSyncGliderDataToClient extends AvatarPacket<PacketCSyncGliderDataToClient> {
    public NBTTagCompound nbt;

    public PacketCSyncGliderDataToClient() {}

    public PacketCSyncGliderDataToClient(NBTTagCompound nbt) {
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
    protected AvatarPacket.Handler<PacketCSyncGliderDataToClient> getPacketHandler() {
        return PacketRedirector::redirectMessage;
    }



}
