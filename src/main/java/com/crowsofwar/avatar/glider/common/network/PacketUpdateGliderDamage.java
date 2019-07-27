package com.crowsofwar.avatar.glider.common.network;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.api.helper.GliderHelper;
import com.crowsofwar.avatar.api.item.IGlider;
import com.crowsofwar.avatar.glider.common.config.ConfigHandler;
import com.crowsofwar.avatar.glider.common.helper.GliderPlayerHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateGliderDamage implements IMessage{

    public PacketUpdateGliderDamage() {} //default constructor is necessary

    @Override
    public void fromBytes(ByteBuf buf){
    }

    @Override
    public void toBytes(ByteBuf buf){
    }

    public static class Handler implements IMessageHandler<PacketUpdateGliderDamage, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateGliderDamage message, MessageContext ctx) {

            //have to use threading system since 1.8
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = AvatarMod.proxy.getClientPlayer();
                if (player != null) {
                    ItemStack glider = GliderPlayerHelper.getGlider(player);
                    if (glider != null && !glider.isEmpty()) {
                        glider.damageItem(ConfigHandler.durabilityPerUse, player);
                        if (((IGlider)glider.getItem()).isBroken(glider)) { //broken item
                            GliderHelper.setIsGliderDeployed(player, false);
                        }
                    }
                }
            });
            //no return message
            return null;
        }

    }

}
