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

import com.crowsofwar.avatar.common.item.AvatarItem;
import com.crowsofwar.avatar.common.item.AvatarItems;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarItemRenderRegister {
	
	public static void register() {
		
		ModelResourceLocation mrlRegular = new ModelResourceLocation("avatarmod:scroll_air", "inventory");
		ModelResourceLocation mrlGlow = new ModelResourceLocation("avatarmod:scroll_air_glow", "inventory");
		
		System.out.println("=================== MRLs: " + mrlRegular + " // " + mrlGlow);
		System.out.println("=================== Regname: " + AvatarItems.itemScroll.getRegistryName());
		
		ModelLoaderRegistry.registerLoader(new AvatarCustomModelLoader(mrlRegular, mrlGlow));
		// register(AvatarItems.itemScroll, 0, 1, 2, 3, 4);
		
		ModelLoader.setCustomModelResourceLocation(AvatarItems.itemScroll, 0, mrlRegular);
		
	}
	
	/**
	 * Registers the specified item with the given metadata(s). Maps it to
	 * {unlocalizedName}.json. Note that if no metadata is specified, the item
	 * will not be registered.
	 */
	private static void register(AvatarItem item, int... metadata) {
		
		for (int meta : metadata) {
			ModelResourceLocation mrl = new ModelResourceLocation("avatarmod:" + item.getModelName(meta),
					"inventory");
			
			ModelLoader.setCustomModelResourceLocation(item.item(), meta, mrl);
			
		}
		
	}
	
}
