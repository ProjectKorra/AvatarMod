package com.crowsofwar.avatar.common.network.packets.glider;

import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.avatar.common.network.packets.AvatarPacket;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

/**
 * [UNUSED]
 * Syncs the gliding capability on the server side to a given player.
 */
public class PacketServerGliding extends AvatarPacket<PacketServerGliding> {
    //the data sent
    public byte isGliding;

    public final static byte IS_GLIDING = 0;
    public final static byte IS_NOT_GLIDING = 1;

    public PacketServerGliding() {} //default constructor is necessary

    public PacketServerGliding(byte gliding) {
        this.isGliding = gliding;
    }

    @Override
    public void avatarFromBytes(ByteBuf buf){
        isGliding = (byte) ByteBufUtils.readVarShort(buf);
    }

    @Override
    public void avatarToBytes(ByteBuf buf){
        ByteBufUtils.writeVarShort(buf, isGliding);
    }

    @Override
    protected Side getReceivedSide() {
        return Side.SERVER;
    }

    @Override
    protected AvatarPacket.Handler<PacketServerGliding> getPacketHandler() {
        return PacketRedirector::redirectMessage;
    }
}

