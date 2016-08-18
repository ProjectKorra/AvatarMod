package com.crowsofwar.gorecore.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * The entity renderer, with a few adjustments. Thanks CoolAlias!
 * 
 * @author CrowsOfWar
 */
public class GoreCoreEntityRenderer extends EntityRenderer {
	
	private static GoreCoreEntityRenderer INSTANCE;
	private final Minecraft mc;
	private final List<GoreCorePlayerViewpointAdjuster> viewpointAdjusters;
	
	public GoreCoreEntityRenderer(Minecraft mc, IResourceManager manager) {
		super(mc, manager);
		this.mc = mc;
		this.viewpointAdjusters = new ArrayList<GoreCorePlayerViewpointAdjuster>();
		INSTANCE = this;
		FMLCommonHandler.instance().bus().post(new GoreCoreEntityRendererReadyEvent());
	}
	
	@Override
	public void updateCameraAndRender(float partialTicks) {
		EntityPlayer player = mc.thePlayer;
		if (player != null && !player.isPlayerSleeping()) {
			for (GoreCorePlayerViewpointAdjuster adjuster : viewpointAdjusters)
				player.yOffset -= adjuster.getAdjustedEyeHeight(player);
		}
		super.updateCameraAndRender(partialTicks);
		if (player != null) player.yOffset = 1.62f;
	}
	
	/**
	 * Replace Minecraft's vanilla entity renderer with this one.
	 */
	public static void replace() {
		Minecraft mc = Minecraft.getMinecraft();
		mc.entityRenderer = new GoreCoreEntityRenderer(mc, mc.getResourceManager());
	}
	
	/**
	 * Hook a viewpoint adjuster to be called each tick. This should be done in the postInit method.
	 */
	public static void hook(GoreCorePlayerViewpointAdjuster adjuster) {
		if (INSTANCE == null) throw new NullPointerException("You must register a hook after preInit!");
		INSTANCE.viewpointAdjusters.add(adjuster);
	}
	
}
