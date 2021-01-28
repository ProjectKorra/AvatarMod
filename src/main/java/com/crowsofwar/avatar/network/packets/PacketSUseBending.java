package com.crowsofwar.avatar.network.packets;

import com.crowsofwar.avatar.network.PacketRedirector;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

//Simple Packet for opening the radial menu, fades
public class PacketSUseBending extends AvatarPacket<PacketSUseBending> {

    public PacketSUseBending() {
    }


    @Override
    public void avatarFromBytes(ByteBuf buf) {
    }

    @Override
    public void avatarToBytes(ByteBuf buf) {
    }

    @Override
    public Side getReceivedSide() {
        return Side.SERVER;
    }


    @Override
    protected AvatarPacket.Handler<PacketSUseBending> getPacketHandler() {
        return PacketRedirector::redirectMessage;
    }
}
