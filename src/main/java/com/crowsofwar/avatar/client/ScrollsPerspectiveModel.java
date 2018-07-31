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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.List;

import static net.minecraft.client.Minecraft.getMinecraft;

/**
 * @author CrowsOfWar
 */
public class ScrollsPerspectiveModel implements IBakedModel {

	private final ModelResourceLocation mrlRegular, mrlGlow;
	private final ItemCameraTransforms cameraTransforms;
	private final ItemOverrideList overrideList;
	private final IBakedModel baseModel, baseModelGlow;

	public ScrollsPerspectiveModel(ModelResourceLocation mrlRegular, ModelResourceLocation mrlGlow, IBakedModel baseModel,
					IBakedModel baseModelGlow) {
		this.mrlRegular = mrlRegular;
		this.mrlGlow = mrlGlow;
		cameraTransforms = ItemCameraTransforms.DEFAULT;
		overrideList = ItemOverrideList.NONE;
		this.baseModel = baseModel;
		this.baseModelGlow = baseModelGlow;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType transform) {

		ModelManager mm = getMinecraft().getRenderItem().getItemModelMesher().getModelManager();
		ModelResourceLocation mrl = transform == TransformType.GUI ? mrlGlow : mrlRegular;

		Matrix4f mat = baseModel.handlePerspective(transform).getRight();

		return Pair.of(mm.getModel(mrl), mat);
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return baseModel.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return baseModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return baseModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return baseModel.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return overrideList;
	}

}
