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

import com.crowsofwar.avatar.common.item.AvatarItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarItemRenderRegister {
	
	public static void register() {
		register(AvatarItems.itemScroll, 0);
	}
	
	private static void register(Item item, int meta) {
		ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
		ModelResourceLocation mrl = new ModelResourceLocation(item.getUnlocalizedName().substring(5),
				"inventory");
		System.out.println(mrl);
		mesher.register(item, meta, mrl);
	}
	
}
