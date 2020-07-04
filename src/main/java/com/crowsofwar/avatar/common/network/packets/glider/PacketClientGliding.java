package com.crowsofwar.avatar.common.network.packets.glider;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.api.helper.GliderHelper;
import com.crowsofwar.avatar.common.network.packets.AvatarPacket;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Message to sync the gliding capability on the client side for a given player.
 */
public class PacketClientGliding extends AvatarPacket<PacketClientGliding> {
    public static class Handler implements AvatarPacket.Handler<PacketClientGliding> {

        @Override
        public IMessage onMessageRecieved(PacketClientGliding message, MessageContext ctx) {

            //have to use threading system since 1.8
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = AvatarMod.proxy.getClientPlayer();
                if (player != null) {
                    GliderHelper.setIsGliderDeployed(player, message.isGliding);
                }
            });

            return null; //no return message
        }
    }
    //the data sent
    private boolean isGliding;

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
        return new Handler();
    }


}
