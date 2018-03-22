/* 
  This file is part of AvatarMod.
	
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.client;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.AnalyticsWarningGui;
import com.crowsofwar.avatar.client.gui.AvatarUiRenderer;
import com.crowsofwar.avatar.client.gui.GuiBisonChest;
import com.crowsofwar.avatar.client.gui.DevelopmentWarningGui;
import com.crowsofwar.avatar.client.gui.skills.GetBendingGui;
import com.crowsofwar.avatar.client.gui.skills.SkillsGui;
import com.crowsofwar.avatar.client.particles.AvatarParticleAir;
import com.crowsofwar.avatar.client.particles.AvatarParticleFlames;
import com.crowsofwar.avatar.client.render.*;
import com.crowsofwar.avatar.client.render.iceprison.RenderIcePrison;
import com.crowsofwar.avatar.common.AvatarCommonProxy;
import com.crowsofwar.avatar.common.AvatarParticles;
import com.crowsofwar.avatar.common.block.AvatarBlocks;
import com.crowsofwar.avatar.common.controls.IControlsHandler;
import com.crowsofwar.avatar.common.controls.KeybindingWrapper;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.*;
import com.crowsofwar.avatar.common.entity.mob.*;
import com.crowsofwar.avatar.common.gui.AvatarGui;
import com.crowsofwar.avatar.common.gui.AvatarGuiHandler;
import com.crowsofwar.avatar.common.network.IPacketHandler;
import com.crowsofwar.avatar.common.network.packets.PacketSRequestData;
import com.crowsofwar.avatar.common.particle.ClientParticleSpawner;
import com.crowsofwar.gorecore.data.PlayerDataFetcher;
import com.crowsofwar.gorecore.data.PlayerDataFetcherClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.common.config.ConfigAnalytics.ANALYTICS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler;

@SideOnly(Side.CLIENT)
public class AvatarClientProxy implements AvatarCommonProxy {

	private Minecraft mc;
	private PacketHandlerClient packetHandler;
	private ClientInput inputHandler;
	private PlayerDataFetcher<AvatarPlayerData> clientFetcher;
	private boolean displayedMainMenu;
	private List<KeyBinding> allKeybindings;

	@Override
	public void preInit() {
		mc = Minecraft.getMinecraft();

		displayedMainMenu = false;

		packetHandler = new PacketHandlerClient();
		AvatarUiRenderer.instance = new AvatarUiRenderer();

		inputHandler = new ClientInput();
		MinecraftForge.EVENT_BUS.register(inputHandler);
		MinecraftForge.EVENT_BUS.register(AvatarUiRenderer.instance);
		MinecraftForge.EVENT_BUS.register(this);
		AvatarInventoryOverride.register();
		AvatarFovChanger.register();

		clientFetcher = new PlayerDataFetcherClient<>(AvatarPlayerData.class, (data) -> {
			AvatarMod.network.sendToServer(new PacketSRequestData(data.getPlayerID()));
			AvatarLog.debug("Client: Requesting data for " + data.getPlayerEntity() + "");
		});

		registerEntityRenderingHandler(EntityFloatingBlock.class, RenderFloatingBlock::new);
		registerEntityRenderingHandler(EntityFireArc.class, RenderFireArc::new);
		registerEntityRenderingHandler(EntityWaterArc.class, RenderWaterArc::new);
		registerEntityRenderingHandler(EntityAirGust.class, RenderAirGust::new);
		registerEntityRenderingHandler(EntityRavine.class, RenderRavine::new);
		registerEntityRenderingHandler(EntityFlames.class,
				rm -> new RenderFlames(rm, new ClientParticleSpawner()));
		registerEntityRenderingHandler(EntityWave.class, RenderWave::new);
		registerEntityRenderingHandler(EntityWaterBubble.class, RenderWaterBubble::new);
		registerEntityRenderingHandler(EntityWallSegment.class, RenderWallSegment::new);
		registerEntityRenderingHandler(EntityFireball.class, RenderFireball::new);
		registerEntityRenderingHandler(EntityAirblade.class, RenderAirblade::new);
		registerEntityRenderingHandler(EntityAirBubble.class, RenderAirBubble::new);
		registerEntityRenderingHandler(EntitySkyBison.class, RenderSkyBison::new);
		registerEntityRenderingHandler(EntityOtterPenguin.class, RenderOtterPenguin::new);
		registerEntityRenderingHandler(EntityIceShard.class, RenderIceShard::new);
		registerEntityRenderingHandler(EntityOstrichHorse.class, RenderOstrichHorse::new);
		registerEntityRenderingHandler(EntityIcePrison.class, RenderIcePrison::new);
		registerEntityRenderingHandler(EntityLightningArc.class, RenderLightningArc::new);
		registerEntityRenderingHandler(EntitySandPrison.class, RenderSandPrison::new);
		registerEntityRenderingHandler(EntityIceShield.class, RenderIceShield::new);
		registerEntityRenderingHandler(EntityCloudBall.class, RenderCloudburst::new);
		registerEntityRenderingHandler(EntityEarthspike.class, RenderEarthspikes::new);
		registerEntityRenderingHandler(EntityLightningSpear.class, RenderLightningSpear::new);
		registerEntityRenderingHandler(EntityEarthspikeSpawner.class, RenderEarthspikeSpawner::new);
		registerEntityRenderingHandler(EntityWaterCannon.class, RenderWaterCannon::new);
		registerEntityRenderingHandler(EntitySandstorm.class, RenderSandstorm::new);
		registerEntityRenderingHandler(EntityElementshard.class, RenderElementShard::new);
		registerEntityRenderingHandler(EntityExplosionSpawner.class, RenderNothing::new);

		registerEntityRenderingHandler(EntityAirbender.class,
				rm -> new RenderHumanBender(rm, "airbender", 7));
		registerEntityRenderingHandler(EntityFirebender.class,
				rm -> new RenderHumanBender(rm, "firebender", 1));
		registerEntityRenderingHandler(EntityWaterbender.class,
				rm -> new RenderHumanBender(rm, "airbender", 1));
	}

	@Override
	public void registerBlockModels() {
		setCustomModelResourceLocation(AvatarBlocks.itemBlockCloud, 0, "");
	}

	private void setCustomModelResourceLocation(ItemBlock itemBlock, int data, String suffix) {
		ModelLoader.setCustomModelResourceLocation(itemBlock, data, createItemBlockResourceLocation(itemBlock, suffix));
	}

	private ModelResourceLocation createItemBlockResourceLocation(ItemBlock itemBlock, String suffix) {
		return new ModelResourceLocation(itemBlock.getRegistryName() + suffix, "inventory");
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

		ParticleManager pm = mc.effectRenderer;

		if (CLIENT_CONFIG.useCustomParticles) {
			pm.registerParticle(AvatarParticles.getParticleFlames().getParticleID(),
					AvatarParticleFlames::new);
			pm.registerParticle(AvatarParticles.getParticleAir().getParticleID(), AvatarParticleAir::new);
		}

	}

	@Override
	public AvatarGui createClientGui(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (AvatarGuiHandler.isBendingGui(id)) {
			return new SkillsGui(AvatarGuiHandler.getBendingId(id));
		}
		if (id == AvatarGuiHandler.GUI_ID_BISON_CHEST) {
			// x-coordinate represents ID of sky bison
			int bisonId = x;
			EntitySkyBison bison = EntitySkyBison.findBison(world, bisonId);
			if (bison != null) {

				return new GuiBisonChest(player.inventory, bison);

			} else {
				AvatarLog.warn(WarningType.WEIRD_PACKET, player.getName()
						+ " tried to open skybison inventory, was not found. BisonId: " + bisonId);
			}
		}
		if (id == AvatarGuiHandler.GUI_ID_GET_BENDING) {
			return new GetBendingGui(player);
		}

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

	@SubscribeEvent
	public void onMainMenu(GuiOpenEvent e) {

		if (e.getGui() instanceof GuiMainMenu && ANALYTICS_CONFIG.displayAnalyticsWarning) {
			GuiScreen analyticsScreen = new AnalyticsWarningGui();
			mc.displayGuiScreen(analyticsScreen);
			e.setGui(analyticsScreen);
			return;
		}

		if (AvatarInfo.IS_DEVELOPMENT && e.getGui() instanceof GuiMainMenu && !displayedMainMenu) {
			GuiScreen screen = new DevelopmentWarningGui();
			mc.displayGuiScreen(screen);
			e.setGui(screen);
			displayedMainMenu = true;
		}
	}

	@Override
	public KeybindingWrapper createKeybindWrapper(String keybindName) {

		if (allKeybindings == null) {
			initAllKeybindings();
		}

		KeyBinding kb = null;
		for (KeyBinding candidate : allKeybindings) {
			if (candidate.getKeyDescription().equals(keybindName)) {
				kb = candidate;
				break;
			}
		}

		return kb == null ? new KeybindingWrapper() : new ClientKeybindWrapper(kb);

	}

	@Override
	public void registerItemModels() {
		AvatarItemRenderRegister.register();
	}

	@Override
	public boolean isOptifinePresent() {
		return FMLClientHandler.instance().hasOptifine();
	}

	/**
	 * Finds all keybindings list via reflection. Performance-wise this is ok
	 * since only supposed to be called once, after keybindings are registered
	 */
	private void initAllKeybindings() {
		try {

			Field field = KeyBinding.class.getDeclaredFields()[0];
			field.setAccessible(true);
			Map<String, KeyBinding> kbMap = (Map<String, KeyBinding>) field.get(null);
			this.allKeybindings = kbMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());

		} catch (Exception ex) {
			AvatarLog.error(
					"Could not load all keybindings list by using reflection. Will probably have serious problems",
					ex);
		}
	}

}
