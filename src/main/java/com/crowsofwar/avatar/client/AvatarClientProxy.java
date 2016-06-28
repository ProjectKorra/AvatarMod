package com.crowsofwar.avatar.client;

import static com.crowsofwar.avatar.common.AvatarAbility.*;
import static com.crowsofwar.avatar.common.controls.AvatarControl.*;
import static com.crowsofwar.avatar.common.gui.AvatarGuiIds.*;

import com.crowsofwar.avatar.client.controls.ClientInput;
import com.crowsofwar.avatar.client.gui.RadialMenu;
import com.crowsofwar.avatar.common.AvatarCommonProxy;
import com.crowsofwar.avatar.common.controls.IControlsHandler;
import com.crowsofwar.avatar.common.entity.EntityFlame;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.gui.IAvatarGui;
import com.crowsofwar.avatar.common.network.IPacketHandler;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class AvatarClientProxy implements AvatarCommonProxy {
	
	private Minecraft mc;
	private PacketHandlerClient packetHandler;
	private ClientInput inputHandler;
	
	@Override
	public void preInit() {
		mc = Minecraft.getMinecraft();
		
		packetHandler = new PacketHandlerClient();
		
		inputHandler = new ClientInput();
		FMLCommonHandler.instance().bus().register(inputHandler);
		MinecraftForge.EVENT_BUS.register(inputHandler);
		
		
	}
	
	@Override
	public IControlsHandler getKeyHandler() {
		return inputHandler;
	}
	
	@Override
	public IPacketHandler getClientPacketHandler() {
		return packetHandler;
	}
	
	@Override
	public double getPlayerReach() {
		PlayerControllerMP pc = mc.playerController;
		double reach = pc.getBlockReachDistance();
		if (pc.extendedReach()) reach = 6;
		return reach;
	}

	@Override
	public void init() {
		RenderingRegistry.registerEntityRenderingHandler(EntityFloatingBlock.class, new RenderFloatingBlock());
		RenderingRegistry.registerEntityRenderingHandler(EntityFlame.class, new RenderFlame());
	}

	@Override
	public IAvatarGui createClientGui(int id, EntityPlayer player, World world, int x, int y, int z) {
		return new RadialMenu(id);
	}

}
