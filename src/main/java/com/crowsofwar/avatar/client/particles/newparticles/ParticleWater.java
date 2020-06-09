package com.crowsofwar.avatar.client.particles.newparticles;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import static net.minecraft.util.math.MathHelper.cos;
import static net.minecraft.util.math.MathHelper.sin;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class ParticleWater extends ParticleAvatar {

	private static final ResourceLocation WATER = new ResourceLocation("minecraft",
							 "textures/blocks/water_still.png");

	/**
	 * Creates a new particle in the given world at the given position. All other parameters are set via the various
	 * setter methods ({@link ParticleBuilder ParticleBuilder} deals with all of that anyway).
	 *
	 * @param world The world in which to create the particle.
	 * @param x     The x-coordinate at which to create the particle.
	 * @param y     The y-coordinate at which to create the particle.
	 * @param z     The z-coordinate at which to create the particle.
	 */
	public ParticleWater(World world, double x, double y, double z) {
		super(world, x, y, z);//, WATER);
		this.setRBGColorF(1, 1, 1);
		this.particleAlpha = 1F;
		this.particleMaxAge = 12 + rand.nextInt(4);
		this.shaded = false;
		this.canCollide = true;
	}

	public static void drawQuad(int normal, Vector4f pos1, Vector4f pos2, Vector4f pos3, Vector4f
			pos4, double u1, double v1, double u2, double v2, float r, float g, float b, float a) {

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		if (normal == 0 || normal == 2) {
			//Clears the previous builder
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			buffer.pos(pos1.x, pos1.y, pos1.z).tex(u2, v1).color(r, g, b, a).endVertex();
			buffer.pos(pos2.x, pos2.y, pos2.z).tex(u2, v2).color(r, g, b, a).endVertex();
			buffer.pos(pos3.x, pos3.y, pos3.z).tex(u1, v2).color(r, g, b, a).endVertex();
			buffer.pos(pos4.x, pos4.y, pos4.z).tex(u1, v1).color(r, g, b, a).endVertex();
			tessellator.draw();
		}
		if (normal == 1 || normal == 2) {
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			buffer.pos(pos1.x, pos1.y, pos1.z).tex(u2, v1).color(r, g, b, a).endVertex();
			buffer.pos(pos4.x, pos4.y, pos4.z).tex(u1, v1).color(r, g, b, a).endVertex();
			buffer.pos(pos3.x, pos3.y, pos3.z).tex(u1, v2).color(r, g, b, a).endVertex();
			buffer.pos(pos2.x, pos2.y, pos2.z).tex(u2, v2).color(r, g, b, a).endVertex();
			tessellator.draw();
		}
	}

	@SubscribeEvent
	public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
		event.getMap().registerSprite(WATER);
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity viewer, float partialTicks, float lookZ, float lookY, float lookX, float lookXY, float lookYZ) {

		updateEntityLinking(partialTicks);

		Minecraft mc = Minecraft.getMinecraft();

		float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
		float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
		float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);

		GlStateManager.pushMatrix();
		mc.renderEngine.bindTexture(WATER);
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.translate(x, y, z);


		float ticks = this.particleAge + partialTicks;
		float colorEnhancement = 1.5f;
		float size = 1;
		float scale = particleScale / 10;


		GlStateManager.scale(scale, scale, scale);

		Matrix4f mat = new Matrix4f();
		mat = mat.translate(x, y + 0.4F, z);

		//4 = degrees per second
		mat = mat.rotate(ticks / 20 * 0.2F * 40, 1, 0, 0);
		mat = mat.rotate(ticks / 20 * 40, 0, 1, 0);
		mat = mat.rotate(ticks / 20 * -0.4F * 40, 0, 0, 1);


		// @formatter:off
		Vector4f
				//You can't mul using the size because that would mul the w component, which would still make the bubble with a size of 1.
				lbf = new Vector4f(-.5f * size, -.5f * size, -.5f * size, 1).mul(mat),
				rbf = new Vector4f(0.5f * size, -.5f * size, -.5f * size, 1).mul(mat),
				ltf = new Vector4f(-.5f * size, 0.5f * size, -.5f * size, 1).mul(mat),
				rtf = new Vector4f(0.5f * size, 0.5f * size, -.5f * size, 1).mul(mat),
				lbb = new Vector4f(-.5f * size, -.5f * size, 0.5f * size, 1).mul(mat),
				rbb = new Vector4f(0.5f * size, -.5f * size, 0.5f * size, 1).mul(mat),
				ltb = new Vector4f(-.5f * size, 0.5f * size, 0.5f * size, 1).mul(mat),
				rtb = new Vector4f(0.5f * size, 0.5f * size, 0.5f * size, 1).mul(mat);


		float t1 = ticks * (float) Math.PI / 10f;
		float t2 = t1 + (float) Math.PI / 2f;
		float amt = 0.05f;

		mc.renderEngine.bindTexture(WATER);
		lbf.add(cos(t1) * amt, sin(t2) * amt, cos(t2) * amt, 0);
		rbf.add(sin(t1) * amt, cos(t2) * amt, sin(t2) * amt, 0);
		lbb.add(sin(t2) * amt, cos(t2) * amt, cos(t2) * amt, 0);
		rbb.add(cos(t2) * amt, cos(t1) * amt, cos(t1) * amt, 0);

		ltf.add(cos(t2) * amt, cos(t1) * amt, sin(t1) * amt, 0);
		rtf.add(sin(t2) * amt, sin(t1) * amt, cos(t1) * amt, 0);
		ltb.add(sin(t1) * amt, sin(t2) * amt, cos(t1) * amt, 0);
		rtb.add(cos(t1) * amt, cos(t2) * amt, sin(t1) * amt, 0);

		// @formatter:on


		float existed = ticks;
		int anim = ((int) existed % 16);
		float v1 = anim / 16f, v2 = v1 + 1f / 16;

		drawQuad(2, ltb, lbb, lbf, ltf, 0, v1, 1, v2,
				particleRed * colorEnhancement, particleGreen * colorEnhancement, particleBlue * colorEnhancement, particleAlpha * 0.35F); // -x
		drawQuad(2, rtb, rbb, rbf, rtf, 0, v1, 1, v2,
				particleRed * colorEnhancement, particleGreen * colorEnhancement, particleBlue * colorEnhancement, particleAlpha * 0.35F); // +x
		drawQuad(2, rbb, rbf, lbf, lbb, 0, v1, 1, v2,
				particleRed * colorEnhancement, particleGreen * colorEnhancement, particleBlue * colorEnhancement, particleAlpha * 0.35F); // -y
		drawQuad(2, rtb, rtf, ltf, ltb, 0, v1, 1, v2,
				particleRed * colorEnhancement, particleGreen * colorEnhancement, particleBlue * colorEnhancement, particleAlpha * 0.35F); // +y
		drawQuad(2, rtf, rbf, lbf, ltf, 0, v1, 1, v2,
				particleRed * colorEnhancement, particleGreen * colorEnhancement, particleBlue * colorEnhancement, particleAlpha * 0.35F); // -z
		drawQuad(2, rtb, rbb, lbb, ltb, 0, v1, 1, v2,
				particleRed * colorEnhancement, particleGreen * colorEnhancement, particleBlue * colorEnhancement, particleAlpha * 0.35F); // +z

		GlStateManager.color(1F, 1F, 1F, 1F);
		mc.renderEngine.bindTexture(WATER);
		GlStateManager.disableBlend();
	//	GlStateManager.enableLighting();
		GlStateManager.popMatrix();

	}


	@Override
	public int getFXLayer() {
		return 3;
	}

}
