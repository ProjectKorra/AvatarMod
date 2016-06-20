package com.maxandnoah.avatar.client;

import com.maxandnoah.avatar.common.AvatarCommonProxy;
import com.maxandnoah.avatar.common.IKeybindingManager;
import com.maxandnoah.avatar.common.network.IPacketHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public class AvatarClientProxy implements AvatarCommonProxy {
	
	private AvatarKeybindings keybindings;
	private PacketHandlerClient packetHandler;
	private ClientInput inputHandler;
	
	@Override
	public void preInit() {
		keybindings = new AvatarKeybindings();
		packetHandler = new PacketHandlerClient();
		
		inputHandler = new ClientInput();
		FMLCommonHandler.instance().bus().register(inputHandler);
		MinecraftForge.EVENT_BUS.register(inputHandler);
		
	}
	
	@Override
	public IKeybindingManager getKeyHandler() {
		return keybindings;
	}
	
	@Override
	public IPacketHandler getClientPacketHandler() {
		return packetHandler;
	}

}
