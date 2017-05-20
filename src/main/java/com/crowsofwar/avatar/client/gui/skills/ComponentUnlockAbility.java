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
package com.crowsofwar.avatar.client.gui.skills;

import com.crowsofwar.avatar.client.gui.AvatarUiTextures;
import com.crowsofwar.avatar.client.uitools.ComponentImage;

import net.minecraft.util.ResourceLocation;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ComponentUnlockAbility extends ComponentImage {
	
	/**
	 * @param texture
	 * @param u
	 * @param v
	 * @param textureWidth
	 * @param textureHeight
	 */
	public ComponentUnlockAbility(ResourceLocation texture, int u, int v, int textureWidth,
			int textureHeight) {
		super(AvatarUiTextures.skillsGui, u, v, textureWidth, textureHeight);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void componentDraw(float partialTicks, boolean mouseHover) {
		
	}
	
}
