package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityBoulder;
import com.crowsofwar.avatar.common.entity.EntityCloudBall;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.Random;

import static net.minecraft.client.renderer.GlStateManager.*;
import static net.minecraft.client.renderer.GlStateManager.disableBlend;
import static net.minecraft.client.renderer.GlStateManager.enableLighting;
import static net.minecraft.util.math.MathHelper.cos;

public class RenderBoulder extends Render<EntityBoulder> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
		"textures/entity/cloudburst.png");
	private static final Random random = new Random();

	public RenderBoulder(RenderManager renderManager) {
		super(renderManager);
	}
	Random rand = new Random();

	// @formatter:off
	@Override
	public void doRender(EntityBoulder entity, double xx, double yy, double zz, float entityYaw,
						 float partialTicks) {

		float x = (float) xx, y = (float) yy, z = (float) zz;

		float particlePosition = rand.nextInt(1);

		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

		float ticks = entity.ticksExisted + partialTicks;

		float rotation = ticks / 5f;
		float size = .8f + cos(ticks / 5f) * .05f;
		size *= Math.sqrt(entity.getSize() / 30f);

		enableBlend();
		if (entity.ticksExisted % 3 == 0) {
			World world = entity.world;
			AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
			double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
			double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
			double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
			//world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, spawnX + particlePosition, spawnY + particlePosition, spawnZ + particlePosition, 0, 0, 0);
		}

		if (MinecraftForgeClient.getRenderPass() == 0) {

			renderCube(x, y, z, //
					0, 8 / 256.0, 0, 8 / 256.0, //
					.5f, //
					0, ticks / 25f, 0);

			int i = 15728880;
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);

		} else {

			disableLighting();
			pushMatrix();
			renderCube(x, y, z, //
					8 / 256.0, 16 / 256.0, 0 / 256.0, 8 / 256.0, //
					size, //
					rotation * .2f, rotation, rotation * -.4f);
			popMatrix();
			enableLighting();

		}

		disableBlend();

	}
	// @formatter:on

	private void renderCube(float x, float y, float z, double u1, double u2, double v1, double v2, float size,
							float rotateX, float rotateY, float rotateZ) {
		org.joml.Matrix4f mat = new Matrix4f();
		mat.translate(x, y + .4f, z);

		mat.rotate(rotateX, 1, 0, 0);
		mat.rotate(rotateY, 0, 1, 0);
		mat.rotate(rotateZ, 0, 0, 1);

		// @formatter:off
		// Can't use .mul(size) here because it would mul the w component
		Vector4f
				lbf = new Vector4f(-.5f*size, -.5f*size, -.5f*size, 1).mul(mat),
				rbf = new Vector4f(0.5f*size, -.5f*size, -.5f*size, 1).mul(mat),
				ltf = new Vector4f(-.5f*size, 0.5f*size, -.5f*size, 1).mul(mat),
				rtf = new Vector4f(0.5f*size, 0.5f*size, -.5f*size, 1).mul(mat),
				lbb = new Vector4f(-.5f*size, -.5f*size, 0.5f*size, 1).mul(mat),
				rbb = new Vector4f(0.5f*size, -.5f*size, 0.5f*size, 1).mul(mat),
				ltb = new Vector4f(-.5f*size, 0.5f*size, 0.5f*size, 1).mul(mat),
				rtb = new Vector4f(0.5f*size, 0.5f*size, 0.5f*size, 1).mul(mat);

		// @formatter:on

		drawQuad(2, ltb, lbb, lbf, ltf, u1, v1, u2, v2); // -x
		drawQuad(2, rtb, rbb, rbf, rtf, u1, v1, u2, v2); // +x
		drawQuad(2, rbb, rbf, lbf, lbb, u1, v1, u2, v2); // -y
		drawQuad(2, rtb, rtf, ltf, ltb, u1, v1, u2, v2); // +y
		drawQuad(2, rtf, rbf, lbf, ltf, u1, v1, u2, v2); // -z
		drawQuad(2, rtb, rbb, lbb, ltb, u1, v1, u2, v2); // +z
	}

	private void drawQuad(int normal, Vector4f pos1, Vector4f pos2, Vector4f pos3, Vector4f pos4, double u1,
						  double v1, double u2, double v2) {

		Tessellator t = Tessellator.getInstance();
		BufferBuilder vb = t.getBuffer();

		if (normal == 0 || normal == 2) {
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(pos1.x, pos1.y, pos1.z).tex(u2, v1).endVertex();
			vb.pos(pos2.x, pos2.y, pos2.z).tex(u2, v2).endVertex();
			vb.pos(pos3.x, pos3.y, pos3.z).tex(u1, v2).endVertex();
			vb.pos(pos4.x, pos4.y, pos4.z).tex(u1, v1).endVertex();
			t.draw();
		}
		if (normal == 1 || normal == 2) {
			vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			vb.pos(pos1.x, pos1.y, pos1.z).tex(u2, v1).endVertex();
			vb.pos(pos4.x, pos4.y, pos4.z).tex(u1, v1).endVertex();
			vb.pos(pos3.x, pos3.y, pos3.z).tex(u1, v2).endVertex();
			vb.pos(pos2.x, pos2.y, pos2.z).tex(u2, v2).endVertex();
			t.draw();

		}

	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBoulder entity) {
		return null;
	}

}