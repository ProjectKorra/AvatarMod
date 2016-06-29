package com.crowsofwar.avatar.common;

import com.mojang.realmsclient.dto.McoServer.WorldType;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;

/**
 * Provides utilities for getting the time passed since
 * the last tick, for both client and server thread.
 *
 */
public class DeltaTime {
	
	private Side side;
	private long last;
	private double deltaTime;
	
	public DeltaTime(Side side) {
		this.side = side;
		last = -1L;
		deltaTime = 1;
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void onTick(TickEvent e) {
		boolean isValid = e.phase == Phase.END;
		if (!isValid) return;
		Side runningSide = FMLCommonHandler.instance().getEffectiveSide();
		if (side != runningSide) return;
		if (runningSide == Side.CLIENT && e.type == Type.CLIENT && Minecraft.getMinecraft().thePlayer != null) {
			//nothing
		} else if (e.type == Type.WORLD) {
			//nothing
		} else {
			isValid = false;
		}
		
		if (isValid) {
			long current = System.nanoTime();
			if (last != -1) deltaTime = (current - last) / 1000000000.0;
			last = current;
		}
	}
	
	public Side getSide() {
		return side;
	}
	
	/**
	 * Get the elapsed time since last tick in seconds.
	 */
	public double deltaTime() {
		return deltaTime;
	}
	
	public void cleanup() {
		FMLCommonHandler.instance().bus().unregister(this);
	}
	
}
