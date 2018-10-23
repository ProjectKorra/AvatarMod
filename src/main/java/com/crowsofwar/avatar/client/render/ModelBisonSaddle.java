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
import net.minecraft.client.model.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * BisonSaddle - Captn_Dubz & Mnesikos Created using Tabula 5.1.0
 * Note: This model + texture were edited by Mnesikos to better fit the new Bison model altogether,
 * heavily based on original model + texture by Captn_Dubz.
 */
public class ModelBisonSaddle extends ModelBase {

	private static final ResourceLocation texture = new ResourceLocation("avatarmod", "textures/mob/flyingbison_saddle.png");

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
		textureWidth = 112;
		textureHeight = 96;
		wall3 = new ModelRenderer(this, 58, 5);
		wall3.mirror = true;
		wall3.setRotationPoint(0.0F, -0.5F, 11.5F);
		wall3.addBox(-7.5F, -3.0F, -0.5F, 15, 3, 1, 0.0F);
		wallTop = new ModelRenderer(this, 0, 0);
		wallTop.setRotationPoint(0.0F, -3.5F, -11.1F);
		wallTop.addBox(-4.5F, -2.0F, -1.0F, 9, 3, 1, 0.0F);
		setRotateAngle(wallTop, 0.20943951023931953F, 0.0F, 0.0F);
		wallSide2 = new ModelRenderer(this, 0, 4);
		wallSide2.setRotationPoint(4.5F, -2.0F, -0.5F);
		wallSide2.addBox(0.0F, 0.0F, -0.5F, 3, 2, 1, 0.0F);
		setRotateAngle(wallSide2, 0.0F, 0.0F, 0.8726646259971648F);
		cargo = new ModelRenderer(this, 26, 25);
		cargo.setRotationPoint(0.0F, -2.02F, 11.75F);
		cargo.addBox(-5.5F, -2.5F, -2.0F, 11, 5, 4, 0.0F);
		saddleBase = new ModelRenderer(this, 0, 0);
		saddleBase.setRotationPoint(0.0F, -3.5F, 4.0F);
		saddleBase.addBox(-8.5F, -0.5F, -12.0F, 17, 1, 24, 0.0F);
		wallSide1 = new ModelRenderer(this, 0, 4);
		wallSide1.mirror = true;
		wallSide1.setRotationPoint(-4.5F, -2.0F, -0.5F);
		wallSide1.addBox(-3.0F, 0.0F, -0.5F, 3, 2, 1, 0.0F);
		setRotateAngle(wallSide1, 0.0F, 0.0F, -0.8726646259971648F);
		wall2 = new ModelRenderer(this, 0, 25);
		wall2.setRotationPoint(8.0F, -0.5F, 0.0F);
		wall2.addBox(-0.5F, -3.0F, -12.0F, 1, 3, 24, 0.0F);
		wall1 = new ModelRenderer(this, 0, 25);
		wall1.mirror = true;
		wall1.setRotationPoint(-8.0F, -0.5F, 0.0F);
		wall1.addBox(-0.5F, -3.0F, -12.0F, 1, 3, 24, 0.0F);
		wall4 = new ModelRenderer(this, 58, 0);
		wall4.setRotationPoint(0.0F, -0.5F, -11.5F);
		wall4.addBox(-7.5F, -3.0F, -0.5F, 15, 3, 2, 0.0F);
		saddleBase.addChild(wall3);
		saddleBase.addChild(wallTop);
		wallTop.addChild(wallSide2);
		saddleBase.addChild(cargo);
		wallTop.addChild(wallSide1);
		saddleBase.addChild(wall2);
		saddleBase.addChild(wall1);
		saddleBase.addChild(wall4);

	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		saddleBase.render(f5);
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
