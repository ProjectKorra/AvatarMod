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

import com.crowsofwar.avatar.common.entity.EntityWaterBubble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import static com.crowsofwar.avatar.client.render.RenderUtils.drawQuad;
import static net.minecraft.util.math.MathHelper.cos;
import static net.minecraft.util.math.MathHelper.sin;

/**
 * @author CrowsOfWar
 */
public class RenderWaterBubble extends Render<EntityWaterBubble> {

	private static final ResourceLocation water = new ResourceLocation("minecraft",
			"textures/blocks/water_still.png");

	public RenderWaterBubble(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityWaterBubble bubble, double x, double y, double z, float entityYaw,
						 float partialTicks) {

		float ticks = bubble.ticksExisted + partialTicks;
		float colorEnhancement = 1.2f;

		Minecraft mc = Minecraft.getMinecraft();
		mc.renderEngine.bindTexture(water);
		GlStateManager.enableBlend();
		GlStateManager.color(colorEnhancement, colorEnhancement, colorEnhancement, 0.6f);

		Matrix4f mat = new Matrix4f();
		mat.translate((float) x - 0.5f, (float) y * bubble.getSize(), (float) z - 0.5f);

		// (0 Left)/(1 Right), (0 Bottom)/(1 Top), (0 Front)/(1 Back)
		//Vector4f mid = new Vector4f((float) x, (float) y + .5f, (float) z, 1);

		//mat.rotate(ticks / 25f * bubble.getDegreesPerSecond(), 1, 0, 0);
		//mat.rotate(ticks / 25f * bubble.getDegreesPerSecond(), 0, 1, 0);
		//mat.rotate(ticks / 25f * bubble.getDegreesPerSecond(), 0, 0, 1);

		// @formatter:off
		Vector4f
		lbf = new Vector4f(0, 0, 0, 1).mul(mat),
		rbf = new Vector4f(1, 0, 0, 1).mul(mat),
		ltf = new Vector4f(0, 1, 0, 1).mul(mat),
		rtf = new Vector4f(1, 1, 0, 1).mul(mat),
		lbb = new Vector4f(0, 0, 1, 1).mul(mat),
		rbb = new Vector4f(1, 0, 1, 1).mul(mat),
		ltb = new Vector4f(0, 1, 1, 1).mul(mat),
		rtb = new Vector4f(1, 1, 1, 1).mul(mat);
		
		float t1 = ticks * (float) Math.PI / 10f;
		float t2 = t1 + (float) Math.PI / 2f;
		float amt = .05f;
		
		lbf.add(cos(t1)*amt, sin(t2)*amt, cos(t2)*amt, 0);
		rbf.add(sin(t1)*amt, cos(t2)*amt, sin(t2)*amt, 0);
		lbb.add(sin(t2)*amt, cos(t2)*amt, cos(t2)*amt, 0);
		rbb.add(cos(t2)*amt, cos(t1)*amt, cos(t1)*amt, 0);
		
		ltf.add(cos(t2)*amt, cos(t1)*amt, sin(t1)*amt, 0);
		rtf.add(sin(t2)*amt, sin(t1)*amt, cos(t1)*amt, 0);
		ltb.add(sin(t1)*amt, sin(t2)*amt, cos(t1)*amt, 0);
		rtb.add(cos(t1)*amt, cos(t2)*amt, sin(t1)*amt, 0);
		
		// @formatter:on

		float existed = ticks / 4f;
		int anim = ((int) existed % 16);
		float v1 = anim / 16f, v2 = v1 + 1f / 16;

		drawQuad(2, ltb, lbb, lbf, ltf, 0, v1, 1, v2); // -x
		drawQuad(2, rtb, rbb, rbf, rtf, 0, v1, 1, v2); // +x
		drawQuad(2, rbb, rbf, lbf, lbb, 0, v1, 1, v2); // -y
		drawQuad(2, rtb, rtf, ltf, ltb, 0, v1, 1, v2); // +y
		drawQuad(2, rtf, rbf, lbf, ltf, 0, v1, 1, v2); // -z
		drawQuad(2, rtb, rbb, lbb, ltb, 0, v1, 1, v2); // +z

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableBlend();

	}
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
	protected ResourceLocation getEntityTexture(EntityWaterBubble entity) {
		return null;
	}

}
