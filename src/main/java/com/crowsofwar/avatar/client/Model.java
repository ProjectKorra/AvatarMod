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

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class Model implements IModel {
	
	private final ModelResourceLocation mrlRegular, mrlGlow;
	
	public Model(ModelResourceLocation mrlRegular, ModelResourceLocation mrlGlow) {
		this.mrlRegular = mrlRegular;
		this.mrlGlow = mrlGlow;
	}
	
	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableList.of();
	}
	
	@Override
	public Collection<ResourceLocation> getTextures() {
		return ImmutableList.of();
	}
	
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		
		return new ScrollsPerspectiveModel(mrlRegular, mrlGlow);
		
	}
	
	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}
	
}
