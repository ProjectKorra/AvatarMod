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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ContainerSkillsGui extends Container {
	
	private final EntityPlayer player;
	
	public ContainerSkillsGui(EntityPlayer player, int width) {
		this.player = player;
		
		addSlotToContainer(new Slot(new SkillsGuiInventory(), 0, 0, 0));
		
		// Main inventory
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				int id = col + row * 9 + 9;
				addSlotToContainer(
						new Slot(player.inventory, row + col * 9 + 9, 8 + col * 18, 120 + row * 18));
			}
		}
		
		// Hotbar
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 178));
		}
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
}
