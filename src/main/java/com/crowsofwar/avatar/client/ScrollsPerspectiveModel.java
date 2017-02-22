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

import static net.minecraft.client.Minecraft.getMinecraft;

import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ScrollsPerspectiveModel implements IBakedModel, IPerspectiveAwareModel {
	
	private final ModelResourceLocation mrlRegular, mrlGlow;
	private final ItemCameraTransforms cameraTransforms;
	private final ItemOverrideList overrideList;
	
	public ScrollsPerspectiveModel(ModelResourceLocation mrlRegular, ModelResourceLocation mrlGlow) {
		this.mrlRegular = mrlRegular;
		this.mrlGlow = mrlGlow;
		this.cameraTransforms = ItemCameraTransforms.DEFAULT;
		this.overrideList = ItemOverrideList.NONE;
	}
	
	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType transform) {
		ModelManager mm = getMinecraft().getRenderItem().getItemModelMesher().getModelManager();
		ModelResourceLocation mrl = transform == TransformType.GUI ? mrlGlow : mrlRegular;
		
		// System.out.println("Model1: " + mm.getModel(mrl));
		
		return Pair.of(mm.getModel(mrl), null);
		
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		IBakedModel model = getMinecraft().getRenderItem().getItemModelMesher().getModelManager()
				.getModel(new ModelResourceLocation("bread", "inventory"));
		return model.getQuads(state, side, rand);
	}
	
	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}
	
	@Override
	public boolean isGui3d() {
		return true;
	}
	
	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}
	
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}
	
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return cameraTransforms;
	}
	
	@Override
	public ItemOverrideList getOverrides() {
		return overrideList;
	}
	
}
