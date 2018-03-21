package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.TransferConfirmHandler;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.entity.EntityWaterCannon;
import com.crowsofwar.avatar.common.particle.ClientParticleSpawner;
import com.crowsofwar.avatar.common.particle.ParticleSpawner;
import com.sun.prism.paint.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;


public class RenderWaterCannon extends RenderArc {
	/*private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/water-ribbon.png");**/

	private final ParticleSpawner particleSpawner;

	public RenderWaterCannon(RenderManager renderManager) {
		super(renderManager, true);
		enableFullBrightness();
		particleSpawner = new ClientParticleSpawner();
	}
/*
	@Override
	public void doRender(Entity entity, double xx, double yy, double zz, float p_76986_8_,
						 float partialTicks) {

		EntityWaterCannon cannon = (EntityWaterCannon) entity;
		renderArc(cannon, partialTicks, 3f, 3f * cannon.getSizeMultiplier());

	}
**/

	public static ResourceLocation textLaser = new ResourceLocation("avatarmod",
			"textures/entity/water-ribbon.png");

	public static boolean drawingLasers = false;


	public static void startRenderingLasers() {
		drawingLasers = true;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bb = tessellator.getBuffer();
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
	}

	public static void finishRenderingLasers() {
		drawingLasers = false;
		Tessellator.getInstance().draw();
	}

	public static void renderLaser(Color color, Vec3d start, Vec3d end) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(textLaser);

		float alpha = Math.max(50, color.getAlpha());
		float addColorMin = 30 * alpha / 255;
		color = new Color(Math.max(addColorMin, color.getRed()), Math.max(addColorMin, color.getGreen()), Math.max(addColorMin, color.getBlue()), alpha);

		GlStateManager.disableCull();

		Vec3d playerEyes = Minecraft.getMinecraft().player.getPositionEyes(100);
		Vec3d normal = (end.subtract(start)).crossProduct(playerEyes.subtract(start)).normalize(); //(b.subtract(a)).crossProduct(c.subtract(a));
		if (normal.y < 0)
			normal = normal.scale(-1);

		//Vec3d d = normal.scale((0.25 * color.getAlpha() / 255f) / 2.);
		Vec3d d = new Vec3d(0, (0.25 * color.getAlpha() / 255f) / 2.0, 0);
		Vec3d d2 = new Vec3d((0.25 * color.getAlpha() / 255f) / 2.0, 0, 0);
		Vec3d d3 = new Vec3d(0, 0, (0.25 * color.getAlpha() / 255f) / 2.0);

		double vMin = 0, vMax = 1;
		double uMin = 0, uMax = 1;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bb = tessellator.getBuffer();

		GlStateManager.depthMask(false);

		if (!drawingLasers) bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		pos(bb, start.add(d)).tex(uMin, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
		pos(bb, start.subtract(d)).tex(uMin, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
		pos(bb, end.subtract(d)).tex(uMax, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
		pos(bb, end.add(d)).tex(uMax, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();

		if (!drawingLasers)
			tessellator.draw();

		if (!drawingLasers) bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		pos(bb, start.add(d2)).tex(uMin, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
		pos(bb, start.subtract(d2)).tex(uMin, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
		pos(bb, end.subtract(d2)).tex(uMax, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
		pos(bb, end.add(d2)).tex(uMax, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();

		if (!drawingLasers)
			tessellator.draw();

		if (!drawingLasers) bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		pos(bb, start.add(d3)).tex(uMin, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
		pos(bb, start.subtract(d3)).tex(uMin, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
		pos(bb, end.subtract(d3)).tex(uMax, vMax).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();
		pos(bb, end.add(d3)).tex(uMax, vMin).color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(128, color.getAlpha())).endVertex();

		if (!drawingLasers)
			tessellator.draw();
	}

	private static BufferBuilder pos(BufferBuilder bb, Vec3d pos) {
		return bb.pos(pos.x, pos.y, pos.z);
	}
	@Override
	protected ResourceLocation getTexture() {
		return textLaser;
	}
}



