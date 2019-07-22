package com.crowsofwar.avatar.glider.common.network;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.glider.api.helper.GliderHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Message to sync the gliding capability on the client side for a given player.
 */
public class PacketClientGliding implements IMessage{

    //the data sent
    private boolean isGliding;

    //use variables so I can just send over
    public final static byte IS_GLIDING = 0;
    public final static byte IS_NOT_GLIDING = 1;

    public PacketClientGliding() {} //default constructor is necessary

    public PacketClientGliding(boolean isGliding) {
        this.isGliding = isGliding;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        isGliding = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf){
        buf.writeBoolean(isGliding);
    }

    public static class Handler implements IMessageHandler<PacketClientGliding, IMessage> {

        @Override
        public IMessage onMessage(PacketClientGliding message, MessageContext ctx) {

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
}
