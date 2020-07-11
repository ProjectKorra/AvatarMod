package com.crowsofwar.avatar.network.packets.glider;

import com.crowsofwar.avatar.network.PacketRedirector;
import com.crowsofwar.avatar.network.packets.AvatarPacket;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Message to sync the gliding capability on the client side for a given player.
 */
public class PacketCClientGliding extends AvatarPacket<PacketCClientGliding> {
    //the data sent
    public boolean isGliding;

    public PacketCClientGliding() {} //default constructor is necessary

    public PacketCClientGliding(boolean isGliding) {
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
    protected AvatarPacket.Handler<PacketCClientGliding> getPacketHandler() {
        return PacketRedirector::redirectMessage;
    }


}
