package com.maxandnoah.avatar.common;

import com.maxandnoah.avatar.AvatarMod;
import com.maxandnoah.avatar.common.gui.IAvatarGui;
import com.maxandnoah.avatar.common.network.IPacketHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Allows calling of side-specific code by using a common
 * base class and side-specific subclasses. It can be referenced
 * via {@link AvatarMod#proxy}. All classes or values accessed
 * from here are safe to use on either side.
 * <br /><br />
 * Is using Client proxy if running from a minecraft client. Uses
 * server proxy is running from server.
 * <br /><br />
 *
 */
public interface AvatarCommonProxy {
	
	/**
	 * Called from the main class, subclasses should initialize themselves here (fields, etc).
	 */
	public void preInit();
	
	public IKeybindingManager getKeyHandler();
	
	/**
	 * Get a client-side packet handler safely. When the machine
	 * is running a minecraft client (even if in the integrated
	 * server thread), returns the packet handler for the client.
	 * Otherwise (this only happens on dedicated servers), returns
	 * null.
	 */
	public IPacketHandler getClientPacketHandler();
	
	/**
	 * Get client player's reach. Returns 0 on server.
	 */
	double getPlayerReach();

	void init();
	
	IAvatarGui createClientGui(int id, EntityPlayer player, World world, int x, int y, int z);
	
}
