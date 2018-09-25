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

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * BisonSaddle - Captn_Dubz & Mnesikos Created using Tabula 5.1.0
 */
public class ModelBisonSaddle extends ModelBase {

	private static final ResourceLocation texture = new ResourceLocation("avatarmod",
			"textures/mob/flyingbison_saddle.png");

	public ModelRenderer saddleBase;
	public ModelRenderer wall1;
	public ModelRenderer wall2;
	public ModelRenderer wall3;
	public ModelRenderer wall4;
	public ModelRenderer cargo;
	public ModelRenderer wallTop;
	public ModelRenderer wallSide1;
	public ModelRenderer wallSide2;

	public ModelBisonSaddle() {
		this.textureWidth = 112;
		this.textureHeight = 96;
		this.wall3 = new ModelRenderer(this, 58, 5);
		this.wall3.mirror = true;
		this.wall3.setRotationPoint(0.0F, -0.5F, 11.5F);
		this.wall3.addBox(-7.5F, -3.0F, -0.5F, 15, 3, 1, 0.0F);
		this.wallTop = new ModelRenderer(this, 0, 0);
		this.wallTop.setRotationPoint(0.0F, -3.5F, -11.1F);
		this.wallTop.addBox(-4.5F, -2.0F, -1.0F, 9, 3, 1, 0.0F);
		this.setRotateAngle(wallTop, 0.20943951023931953F, 0.0F, 0.0F);
		this.wallSide2 = new ModelRenderer(this, 0, 4);
		this.wallSide2.setRotationPoint(4.5F, -2.0F, -0.5F);
		this.wallSide2.addBox(0.0F, 0.0F, -0.5F, 3, 2, 1, 0.0F);
		this.setRotateAngle(wallSide2, 0.0F, 0.0F, 0.8726646259971648F);
		this.cargo = new ModelRenderer(this, 26, 25);
		this.cargo.setRotationPoint(0.0F, -2.02F, 11.75F);
		this.cargo.addBox(-5.5F, -2.5F, -2.0F, 11, 5, 4, 0.0F);
		this.saddleBase = new ModelRenderer(this, 0, 0);
		this.saddleBase.setRotationPoint(0.0F, -3.5F, 4.0F);
		this.saddleBase.addBox(-8.5F, -0.5F, -12.0F, 17, 1, 24, 0.0F);
		this.wallSide1 = new ModelRenderer(this, 0, 4);
		this.wallSide1.mirror = true;
		this.wallSide1.setRotationPoint(-4.5F, -2.0F, -0.5F);
		this.wallSide1.addBox(-3.0F, 0.0F, -0.5F, 3, 2, 1, 0.0F);
		this.setRotateAngle(wallSide1, 0.0F, 0.0F, -0.8726646259971648F);
		this.wall2 = new ModelRenderer(this, 0, 25);
		this.wall2.setRotationPoint(8.0F, -0.5F, 0.0F);
		this.wall2.addBox(-0.5F, -3.0F, -12.0F, 1, 3, 24, 0.0F);
		this.wall1 = new ModelRenderer(this, 0, 25);
		this.wall1.mirror = true;
		this.wall1.setRotationPoint(-8.0F, -0.5F, 0.0F);
		this.wall1.addBox(-0.5F, -3.0F, -12.0F, 1, 3, 24, 0.0F);
		this.wall4 = new ModelRenderer(this, 58, 0);
		this.wall4.setRotationPoint(0.0F, -0.5F, -11.5F);
		this.wall4.addBox(-7.5F, -3.0F, -0.5F, 15, 3, 2, 0.0F);
		this.saddleBase.addChild(this.wall3);
		this.saddleBase.addChild(this.wallTop);
		this.wallTop.addChild(this.wallSide2);
		this.saddleBase.addChild(this.cargo);
		this.wallTop.addChild(this.wallSide1);
		this.saddleBase.addChild(this.wall2);
		this.saddleBase.addChild(this.wall1);
		this.saddleBase.addChild(this.wall4);

		// CrowsOfWar: Slightly adjust position of saddle to make it more
		// on-center on the bison
		/*List<ModelRenderer> allBoxes = Arrays.asList(saddleBase, wall1, wall2, wall3, wall4, cargo, wallTop,
				wallSide1, wallSide2);
		for (ModelRenderer box : allBoxes) {
			box.rotationPointX += 40;
			if (box != saddleBase) {
				box.rotationPointX -= 40;
			}
		}*/

	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		this.saddleBase.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
