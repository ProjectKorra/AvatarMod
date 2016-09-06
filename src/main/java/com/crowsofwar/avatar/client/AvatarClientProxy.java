package com.crowsofwar.avatar.client;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.controls.ClientInput;
import com.crowsofwar.avatar.client.gui.RadialMenu;
import com.crowsofwar.avatar.client.particles.AvatarParticleFlames;
import com.crowsofwar.avatar.client.render.RenderAirGust;
import com.crowsofwar.avatar.client.render.RenderControlPoint;
import com.crowsofwar.avatar.client.render.RenderFireArc;
import com.crowsofwar.avatar.client.render.RenderFlames;
import com.crowsofwar.avatar.client.render.RenderFloatingBlock;
import com.crowsofwar.avatar.client.render.RenderRavine;
import com.crowsofwar.avatar.client.render.RenderWaterArc;
import com.crowsofwar.avatar.common.AvatarCommonProxy;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.controls.IControlsHandler;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityAirGust;
import com.crowsofwar.avatar.common.entity.EntityControlPoint;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.EntityFlames;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityRavine;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.gui.IAvatarGui;
import com.crowsofwar.avatar.common.network.IPacketHandler;
import com.crowsofwar.avatar.common.network.packets.PacketSRequestData;
import com.crowsofwar.gorecore.data.PlayerDataFetcher;
import com.crowsofwar.gorecore.data.PlayerDataFetcherClient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AvatarClientProxy implements AvatarCommonProxy {
	
	private Minecraft mc;
	private PacketHandlerClient packetHandler;
	private ClientInput inputHandler;
	private PlayerDataFetcher<AvatarPlayerData> clientFetcher;
	
	@Override
	public void preInit() {
		mc = Minecraft.getMinecraft();
		
		packetHandler = new PacketHandlerClient();
		
		inputHandler = new ClientInput();
		FMLCommonHandler.instance().bus().register(inputHandler);
		MinecraftForge.EVENT_BUS.register(inputHandler);
		
		clientFetcher = new PlayerDataFetcherClient<AvatarPlayerData>(AvatarPlayerData.class, (data) -> {
			AvatarMod.network.sendToServer(new PacketSRequestData(data.getPlayerID()));
		});
		
		RenderingRegistry.registerEntityRenderingHandler(EntityFloatingBlock.class, RenderFloatingBlock::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFireArc.class, RenderFireArc::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityWaterArc.class, RenderWaterArc::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityControlPoint.class, RenderControlPoint::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityAirGust.class, RenderAirGust::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityRavine.class, RenderRavine::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityFlames.class, RenderFlames::new);
		
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
		Minecraft.getMinecraft().effectRenderer.registerParticle(
				AvatarParticles.getParticleFlames().getParticleID(), AvatarParticleFlames::new);
	}
	
	@Override
	public IAvatarGui createClientGui(int id, EntityPlayer player, World world, int x, int y, int z) {
		return new RadialMenu(id);
	}
	
	@Override
	public PlayerDataFetcher<AvatarPlayerData> getClientDataFetcher() {
		return clientFetcher;
	}
	
	@Override
	public IThreadListener getClientThreadListener() {
		return mc;
	}
	
}
