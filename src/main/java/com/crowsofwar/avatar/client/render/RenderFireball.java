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
package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.entity.EntityFireball;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import static com.crowsofwar.avatar.client.render.RenderUtils.drawQuad;
import static net.minecraft.client.renderer.GlStateManager.*;
import static net.minecraft.util.math.MathHelper.cos;

/**
 * @author CrowsOfWar
 */
public class RenderFireball extends Render<EntityFireball> {

	private static ResourceLocation TEXTURE = new ResourceLocation("avatarmod", "textures/entity/fireball_together.png");

	public RenderFireball(RenderManager renderManager) {
		super(renderManager);
	}

	// @formatter:off
	@Override
	public void doRender(EntityFireball entity, double xx, double yy, double zz, float entityYaw,
						 float partialTicks) {


		float x = (float) xx, y = (float) yy, z = (float) zz;


		float ticks = entity.ticksExisted + partialTicks;
		float rotation = ticks / 3f;
		float size = .8f + cos(ticks / 5f) * .05f;
		size *= Math.sqrt(entity.getSize() / 30f);

		pushMatrix();
		enableBlend();
		disableLighting();

		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, DestFactor.ONE);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

		GlStateManager.color(2F, 2F, 2F, 1f);


		renderCube(x, y, z, //
				0, 8.0 / 16.0, 0, 1,//0, 8 / 256.0, 0, 8 / 256.0, //
				.5f * size, //
				ticks / 15F, ticks / 15F, ticks / 15F);


		//pushMatrix();
		//Where did crows get this number from?
		//Maybe try using 3932220 instead?
		int i = 15728880;
		int j = i % 65536;
		int k = i / 65536;
	//	OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
		GlStateManager.color(0.75F, 0.75F, 0.75F, 0.75f);
			renderCube(x, y, z, //
				8.0 / 16.0, 1, 0, 1,//8 / 256.0, 16 / 256.0, 0 / 256.0, 8 / 256.0, //
				size, //
				rotation * .2f, rotation, rotation * -.4f);

		enableLighting();
		disableBlend();
		popMatrix();

	}
	// @formatter:on

	private void renderCube(float x, float y, float z, double u1, double u2, double v1, double v2, float size,
							float rotateX, float rotateY, float rotateZ) {
		Matrix4f mat = new Matrix4f();
		mat.translate(x, y + .4f, z);

		mat.rotate(rotateX, 1, 0, 0);
		mat.rotate(rotateY, 0, 1, 0);
		mat.rotate(rotateZ, 0, 0, 1);

		// @formatter:off
		// Can't use .mul(size) here because it would mul the w component
		Vector4f
				lbf = new Vector4f(-.5f * size, -.5f * size, -.5f * size, 1).mul(mat),
				rbf = new Vector4f(0.5f * size, -.5f * size, -.5f * size, 1).mul(mat),
				ltf = new Vector4f(-.5f * size, 0.5f * size, -.5f * size, 1).mul(mat),
				rtf = new Vector4f(0.5f * size, 0.5f * size, -.5f * size, 1).mul(mat),
				lbb = new Vector4f(-.5f * size, -.5f * size, 0.5f * size, 1).mul(mat),
				rbb = new Vector4f(0.5f * size, -.5f * size, 0.5f * size, 1).mul(mat),
				ltb = new Vector4f(-.5f * size, 0.5f * size, 0.5f * size, 1).mul(mat),
				rtb = new Vector4f(0.5f * size, 0.5f * size, 0.5f * size, 1).mul(mat);

		// @formatter:on

		drawQuad(2, ltb, lbb, lbf, ltf, u1, v1, u2, v2); // -x
		drawQuad(2, rtb, rbb, rbf, rtf, u1, v1, u2, v2); // +x
		drawQuad(2, rbb, rbf, lbf, lbb, u1, v1, u2, v2); // -y
		drawQuad(2, rtb, rtf, ltf, ltb, u1, v1, u2, v2); // +y
		drawQuad(2, rtf, rbf, lbf, ltf, u1, v1, u2, v2); // -z
		drawQuad(2, rtb, rbb, lbb, ltb, u1, v1, u2, v2); // +z
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFireball entity) {
		return TEXTURE;
	}

}
