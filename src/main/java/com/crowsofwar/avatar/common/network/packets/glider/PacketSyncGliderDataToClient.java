package com.crowsofwar.avatar.common.network.packets.glider;

import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.avatar.common.network.packets.AvatarPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class PacketSyncGliderDataToClient extends AvatarPacket<PacketSyncGliderDataToClient> {
    public NBTTagCompound nbt;

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
        return PacketRedirector::redirectMessage;
    }



}
