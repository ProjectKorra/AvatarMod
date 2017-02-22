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

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarCustomModelLoader implements ICustomModelLoader {
	
	private final ModelResourceLocation mrlRegular, mrlGlow;
	
	public AvatarCustomModelLoader(ModelResourceLocation mrlRegular, ModelResourceLocation mrlGlow) {
		this.mrlRegular = mrlRegular;
		this.mrlGlow = mrlGlow;
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {}
	
	@Override
	public boolean accepts(ResourceLocation location) {
		if (location.toString().contains("avatarmod")) {
			System.out.println("Approving: " + location);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		return new Model(mrlRegular, mrlGlow);
	}
	
}
