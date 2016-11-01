package com.crowsofwar.avatar.client;

import static net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler;

import java.util.HashSet;
import java.util.Set;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.particles.AvatarParticleAir;
import com.crowsofwar.avatar.client.particles.AvatarParticleFlames;
import com.crowsofwar.avatar.client.render.RenderAirGust;
import com.crowsofwar.avatar.client.render.RenderFireArc;
import com.crowsofwar.avatar.client.render.RenderFlames;
import com.crowsofwar.avatar.client.render.RenderFloatingBlock;
import com.crowsofwar.avatar.client.render.RenderRavine;
import com.crowsofwar.avatar.client.render.RenderWaterArc;
import com.crowsofwar.avatar.client.render.RenderWave;
import com.crowsofwar.avatar.common.AvatarCommonProxy;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.IControlsHandler;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityAirGust;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.entity.EntityFlames;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityRavine;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.entity.EntityWave;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.network.IPacketHandler;
import com.crowsofwar.avatar.common.network.packets.PacketSRequestData;
import com.crowsofwar.avatar.common.particle.ClientParticleSpawner;
import com.crowsofwar.gorecore.data.PlayerDataFetcher;
import com.crowsofwar.gorecore.data.PlayerDataFetcherClient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AvatarClientProxy implements AvatarCommonProxy {
	
	private Minecraft mc;
	private PacketHandlerClient packetHandler;
	private ClientInput inputHandler;
	private PlayerDataFetcher<AvatarPlayerData> clientFetcher;
	private Set<StatusControl> statusControls;
	
	@Override
	public void preInit() {
		mc = Minecraft.getMinecraft();
		
		packetHandler = new PacketHandlerClient();
		AvatarUiRenderer.instance = new AvatarUiRenderer();
		
		inputHandler = new ClientInput();
		MinecraftForge.EVENT_BUS.register(inputHandler);
		MinecraftForge.EVENT_BUS.register(AvatarUiRenderer.instance);
		
		clientFetcher = new PlayerDataFetcherClient<AvatarPlayerData>(AvatarPlayerData.class, (data) -> {
			AvatarMod.network.sendToServer(new PacketSRequestData(data.getPlayerID()));
			AvatarLog.debug("Client: Requesting data for " + data.getPlayerEntity() + "");
		});
		
		statusControls = new HashSet<>();
		
		registerEntityRenderingHandler(EntityFloatingBlock.class, RenderFloatingBlock::new);
		registerEntityRenderingHandler(EntityFireArc.class, RenderFireArc::new);
		registerEntityRenderingHandler(EntityWaterArc.class, RenderWaterArc::new);
		registerEntityRenderingHandler(EntityAirGust.class, RenderAirGust::new);
		registerEntityRenderingHandler(EntityRavine.class, RenderRavine::new);
		registerEntityRenderingHandler(EntityFlames.class,
				rm -> new RenderFlames(rm, new ClientParticleSpawner()));
		registerEntityRenderingHandler(EntityWave.class, RenderWave::new);
		
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
		mc.effectRenderer.registerParticle(AvatarParticles.getParticleAir().getParticleID(),
				AvatarParticleAir::new);
	}
	
	@Override
	public AvatarGui createClientGui(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
	
	@Override
	public PlayerDataFetcher<AvatarPlayerData> getClientDataFetcher() {
		return clientFetcher;
	}
	
	@Override
	public IThreadListener getClientThreadListener() {
		return mc;
	}
	
	@Override
	public int getParticleAmount() {
		return mc.gameSettings.particleSetting;
	}
	
	@Override
	public void addStatusControl(StatusControl control) {
		statusControls.add(control);
	}
	
	@Override
	public void removeStatusControl(StatusControl control) {
		statusControls.remove(control);
	}
	
	@Override
	public Set<StatusControl> getAllStatusControls() {
		return statusControls;
	}
	
}
