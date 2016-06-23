package com.maxandnoah.avatar.common.gui;

import com.maxandnoah.avatar.AvatarMod;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class AvatarGuiHandler implements IGuiHandler {
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		// TODO Send the client information about the radial menu by sending a packet here
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return AvatarMod.proxy.createClientGui(id, player, world, x, y, z);
	}
	
}
