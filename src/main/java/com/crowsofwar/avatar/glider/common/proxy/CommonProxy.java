package com.crowsofwar.avatar.glider.common.proxy;

import com.crowsofwar.avatar.glider.api.capabilities.IGliderCapabilityHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public EntityPlayer getClientPlayer(){
        return null; //nothing on server
    }

    @Override
    public World getClientWorld() {
        return null; //Nothing on server
    }

    @Override
    public IGliderCapabilityHandler getClientGliderCapability() {
        return null; //nothing on server
    }
}
