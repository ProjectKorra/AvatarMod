package com.crowsofwar.avatar.common.network.packets.glider;

import com.crowsofwar.avatar.common.network.PacketRedirector;
import com.crowsofwar.avatar.common.network.packets.AvatarPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class PacketCUpdateClientTarget extends AvatarPacket<PacketCUpdateClientTarget> {
    //the tracked entity to update
    public int targetEntityID;
    public boolean isGliding;

    public PacketCUpdateClientTarget() {} //default constructor is necessary

    public PacketCUpdateClientTarget(EntityPlayer target, boolean isGliding) {
        this.targetEntityID = target.getEntityId();
        this.isGliding = isGliding;
    }

    @Override
    public void avatarFromBytes(ByteBuf buf){
        targetEntityID = buf.readInt();
        isGliding = buf.readBoolean();
    }

    @Override
    public void avatarToBytes(ByteBuf buf){
        buf.writeInt(targetEntityID);
        buf.writeBoolean(isGliding);
    }

    @Override
    protected Side getReceivedSide() {
        return Side.CLIENT;
    }

    @Override
    protected AvatarPacket.Handler<PacketCUpdateClientTarget> getPacketHandler() {
        return PacketRedirector::redirectMessage;
    }


}
