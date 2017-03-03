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

import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ContainerSkillsGui extends Container {
	
	private final EntityPlayer player;
	private final SkillsGuiInventory inventory;
	
	private int invIndex, hotbarIndex;
	
	public ContainerSkillsGui(EntityPlayer player, BendingType type) {
		this.player = player;
		
		inventory = new SkillsGuiInventory();
		
		addSlotToContainer(new Slot(inventory, 0, 100, 100) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == AvatarItems.itemScroll
						&& ScrollType.fromId(stack.getMetadata()).accepts(type);
			}
		});
		
		// Main inventory
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 9; c++) {
				int id = c + r * 9 + 9;
				Slot slot = new Slot(player.inventory, id, 100, 100);
				addSlotToContainer(slot);
				if (r == 0 && c == 0) {
					invIndex = slot.slotNumber;
				}
			}
		}
		
		// Hotbar
		for (int i = 0; i < 9; i++) {
			Slot slot = new Slot(player.inventory, i, 100, 100);
			addSlotToContainer(slot);
			if (i == 0) {
				hotbarIndex = slot.slotNumber;
			}
		}
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		
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
	
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		ItemStack scroll = inventory.getStackInSlot(0);
		if (scroll != field_190927_a) {
			player.dropItem(scroll, false);
			inventory.setInventorySlotContents(0, field_190927_a);
		}
	}
	
	public int getInvIndex() {
		return invIndex;
	}
	
	public int getHotbarIndex() {
		return hotbarIndex;
	}
	
}
