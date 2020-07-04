package com.crowsofwar.avatar.common.network.packets.glider;

import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.avatar.common.network.packets.AvatarPacket;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Message to sync the gliding capability on the client side for a given player.
 */
public class PacketClientGliding extends AvatarPacket<PacketClientGliding> {
    //the data sent
    public boolean isGliding;

    public PacketClientGliding() {} //default constructor is necessary

    public PacketClientGliding(boolean isGliding) {
        this.isGliding = isGliding;
    }

    @Override
    public void avatarFromBytes(ByteBuf buf){
        isGliding = buf.readBoolean();
    }

    @Override
    public void avatarToBytes(ByteBuf buf){
        buf.writeBoolean(isGliding);
    }

    @Override
    protected Side getReceivedSide() {
        return Side.CLIENT;
    }

    @Override
    protected AvatarPacket.Handler<PacketClientGliding> getPacketHandler() {
        return PacketRedirector::redirectMessage;
    }


}
