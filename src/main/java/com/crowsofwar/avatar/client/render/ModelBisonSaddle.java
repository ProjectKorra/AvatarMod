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

import java.util.Arrays;
import java.util.List;

/**
 * BisonSaddle - Captn_Dubz Created using Tabula 5.1.0
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
		this.textureWidth = 192;
		this.textureHeight = 128;
		this.wall2 = new ModelRenderer(this, 2, 48);
		this.wall2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.wall2.addBox(18.0F, -6.0F, -20.0F, 1, 6, 42, 0.0F);
		this.wall1 = new ModelRenderer(this, 2, 48);
		this.wall1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.wall1.addBox(-19.0F, -6.0F, -20.0F, 1, 6, 42, 0.0F);
		this.wallSide2 = new ModelRenderer(this, 2, 2);
		this.wallSide2.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.wallSide2.addBox(14.0F, -1.5F, -18.0F, 5, 6, 1, 0.0F);
		this.setRotateAngle(wallSide2, 0.0F, 0.0F, -0.8203047484373349F);
		this.wallSide1 = new ModelRenderer(this, 2, 2);
		this.wallSide1.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.wallSide1.addBox(-19.0F, -1.5F, -18.0F, 5, 6, 1, 0.0F);
		this.setRotateAngle(wallSide1, 0.0F, 0.0F, 0.8203047484373349F);
		this.wall3 = new ModelRenderer(this, 90, 66);
		this.wall3.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.wall3.addBox(-18.0F, -6.0F, 21.0F, 36, 6, 1, 0.0F);
		this.cargo = new ModelRenderer(this, 2, 100);
		this.cargo.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.cargo.addBox(-14.0F, -8.0F, 18.0F, 28, 10, 8, 0.0F);
		this.wallTop = new ModelRenderer(this, 96, 88);
		this.wallTop.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.wallTop.addBox(-12.0F, -15.0F, -18.0F, 24, 5, 1, 0.0F);
		this.setRotateAngle(wallTop, 0.2617993877991494F, 0.0F, 0.0F);
		this.saddleBase = new ModelRenderer(this, 2, 2);
		this.saddleBase.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.saddleBase.addBox(-19.0F, 0.0F, -20.0F, 38, 2, 42, 0.0F);
		this.wall4 = new ModelRenderer(this, 54, 54);
		this.wall4.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.wall4.addBox(-18.0F, -6.0F, -20.0F, 36, 6, 1, 0.0F);
		this.saddleBase.addChild(this.wall2);
		this.saddleBase.addChild(this.wall1);
		this.wallTop.addChild(this.wallSide2);
		this.wallTop.addChild(this.wallSide1);
		this.saddleBase.addChild(this.wall3);
		this.saddleBase.addChild(this.cargo);
		this.saddleBase.addChild(this.wallTop);
		this.saddleBase.addChild(this.wall4);

		// CrowsOfWar: Slightly adjust position of saddle to make it more
		// on-center on the bison
		List<ModelRenderer> allBoxes = Arrays.asList(saddleBase, wall1, wall2, wall3, wall4, cargo, wallTop,
				wallSide1, wallSide2);
		for (ModelRenderer box : allBoxes) {
			box.rotationPointX += 2;
			if (box != saddleBase) {
				box.rotationPointX -= 2;
			}
		}

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
