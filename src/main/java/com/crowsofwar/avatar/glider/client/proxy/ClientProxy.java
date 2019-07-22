package com.crowsofwar.avatar.glider.client.proxy;

import com.crowsofwar.avatar.glider.api.capabilities.CapabilityHelper;
import com.crowsofwar.avatar.glider.api.capabilities.IGliderCapabilityHandler;
import com.crowsofwar.avatar.glider.client.event.ClientEventHandler;
import com.crowsofwar.avatar.glider.client.renderer.LayerGlider;
import com.crowsofwar.avatar.glider.common.proxy.IProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void init(FMLInitializationEvent event) {
        //Add rendering layer
        LayerGlider.addLayer();

        //register client events
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public EntityPlayer getClientPlayer(){
        return Minecraft.getMinecraft().player;
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public IGliderCapabilityHandler getClientGliderCapability() {
        return getClientPlayer().getCapability(CapabilityHelper.GLIDER_CAPABILITY, null);
    }
}
