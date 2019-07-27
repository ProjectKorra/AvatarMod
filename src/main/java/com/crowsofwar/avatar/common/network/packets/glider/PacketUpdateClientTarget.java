package com.crowsofwar.avatar.common.network.packets.glider;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.api.helper.GliderHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateClientTarget implements IMessage{

    //the tracked entity to update
    private int targetEntityID;
    private boolean isGliding;

    public PacketUpdateClientTarget() {} //default constructor is necessary

    public PacketUpdateClientTarget(EntityPlayer target, boolean isGliding) {
        this.targetEntityID = target.getEntityId();
        this.isGliding = isGliding;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        targetEntityID = buf.readInt();
        isGliding = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf){
        buf.writeInt(targetEntityID);
        buf.writeBoolean(isGliding);
    }

    public static class Handler implements IMessageHandler<PacketUpdateClientTarget, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateClientTarget message, MessageContext ctx) {

            Minecraft.getMinecraft().addScheduledTask(() -> {
                World world = AvatarMod.proxy.getClientWorld();
                EntityPlayer targetEntity = (EntityPlayer) world.getEntityByID(message.targetEntityID);
                if (targetEntity != null) {
                    GliderHelper.setIsGliderDeployed(targetEntity, message.isGliding);
                }
            });
            return null;

        }
    }
}
