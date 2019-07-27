package com.crowsofwar.avatar.client;

import com.crowsofwar.avatar.api.capabilities.IGliderCapabilityHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {

    public void preInit(FMLPreInitializationEvent event);

    public void init(FMLInitializationEvent event);

    public void postInit(FMLPostInitializationEvent event);

    public EntityPlayer getClientPlayer();

    public World getClientWorld();

    public IGliderCapabilityHandler getClientGliderCapability();
}
