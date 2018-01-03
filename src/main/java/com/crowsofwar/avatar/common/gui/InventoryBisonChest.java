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
package com.crowsofwar.avatar.common.gui;

import net.minecraft.inventory.InventoryBasic;

/**
 * @author CrowsOfWar
 */
public class InventoryBisonChest extends InventoryBasic {

	/**
	 * Creates a flying bison inventory.
	 *
	 * @param chestSlots The amount of slots to be used in the chest (not including
	 *                   armor/saddle slots)
	 */
	public InventoryBisonChest(int chestSlots) {
		super("Flying Bison", false, chestSlots + 2);
	}

}
