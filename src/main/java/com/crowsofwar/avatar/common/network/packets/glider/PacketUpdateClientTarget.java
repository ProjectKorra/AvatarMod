package com.crowsofwar.avatar.common.network.packets.glider;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.api.helper.GliderHelper;
import com.crowsofwar.avatar.common.network.packets.AvatarPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketUpdateClientTarget extends AvatarPacket<PacketUpdateClientTarget> {
    private static class Handler implements AvatarPacket.Handler<PacketUpdateClientTarget> {
        @Override
        public IMessage onMessageRecieved(PacketUpdateClientTarget message, MessageContext ctx) {

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

    //the tracked entity to update
    private int targetEntityID;
    private boolean isGliding;

    public PacketUpdateClientTarget() {} //default constructor is necessary

    public PacketUpdateClientTarget(EntityPlayer target, boolean isGliding) {
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
    protected AvatarPacket.Handler<PacketUpdateClientTarget> getPacketHandler() {
        return new Handler();
    }


}
