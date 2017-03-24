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

import net.minecraft.item.Item;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ItemWaterPouch extends Item implements AvatarItem {
	
	public ItemWaterPouch() {
		setCreativeTab(AvatarItems.tabItems);
		setUnlocalizedName("water_pouch");
		setMaxStackSize(1);
		setMaxDamage(0);
		setHasSubtypes(false);
	}
	
	@Override
	public Item item() {
		return this;
	}
	
	@Override
	public String getModelName(int meta) {
		return "water_pouch_" + meta;
	}
	
}
