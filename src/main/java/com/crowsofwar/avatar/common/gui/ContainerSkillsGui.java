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

import static net.minecraft.item.ItemStack.field_190927_a;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ContainerSkillsGui extends Container {
	
	private final EntityPlayer player;
	
	public ContainerSkillsGui(EntityPlayer player, int width, int height) {
		this.player = player;
		
		IInventory inv = new SkillsGuiInventory();
		
		addSlotToContainer(new Slot(inv, 0, (width - 18) / 2 + 20, (height - 18) / 2 + 22));
		inv.setInventorySlotContents(0, new ItemStack(Items.BONE));
		
		// Main inventory
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				int id = col + row * 9 + 9;
				addSlotToContainer(new Slot(player.inventory, id, width + 1 + (col - 9) * 18,
						height - 4 * 18 - 3 + row * 18));
			}
		}
		
		// Hotbar
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(player.inventory, i, width + 1 + (i - 9) * 18, height - 17));
		}
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		
		System.out.println("TransferStackInSlot" + index);
		
		Slot slot = inventorySlots.get(index);
		
		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			ItemStack copy = stack.copy();
			
			if (index == 0) {
				if (!mergeItemStack(stack, 1, 37, true)) {
					return field_190927_a;
				}
			} else {
				if (!mergeItemStack(stack, 0, 1, true)) {
					return field_190927_a;
				}
			}
			
			return stack;
			
		}
		
		return field_190927_a;
	}
	
}
