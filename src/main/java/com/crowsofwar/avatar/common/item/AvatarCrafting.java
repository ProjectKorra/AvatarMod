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
package com.crowsofwar.avatar.common.item;

import static com.crowsofwar.avatar.common.item.AvatarItems.itemBisonSaddle;
import static net.minecraft.init.Items.*;
import static net.minecraftforge.fml.common.registry.GameRegistry.addRecipe;

import net.minecraft.item.ItemStack;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarCrafting {
	
	public static void register() {
		
		// @formatter:off
		
		addRecipe(new ItemStack(itemBisonSaddle, 1, 0), new Object[] {
			"gle",
			"lsl",
			" s ",
			'l', LEATHER,
			'g', GOLD_NUGGET,
			's', STRING,
			'e', LEAD
		});
		addRecipe(new ItemStack(itemBisonSaddle, 1, 1), new Object[] {
			"dle",
			"lsl",
			"dsd",
			'l', LEATHER,
			'd', DIAMOND,
			's', STRING,
			'e', LEAD
		});
		
		// @formatter:on
		
	}
	
}
