package com.crowsofwar.avatar.common.event;

import com.crowsofwar.avatar.common.capabilities.PlayerShoulderProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler
{
	
	 @SubscribeEvent
	 public void attachCapability(AttachCapabilitiesEvent<Entity> event)
	 {
		 if (!(event.getObject() instanceof EntityPlayer)) return;
		 event.addCapability(null, new PlayerShoulderProvider());
	 }
}