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

import static com.crowsofwar.avatar.client.uitools.ScreenInfo.scaleFactor;
import static net.minecraft.item.ItemStack.field_190927_a;

import com.crowsofwar.avatar.common.item.AvatarItems;

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
	
	public ContainerSkillsGui(EntityPlayer player, int width, int height) {
		this.player = player;
		
		inventory = new SkillsGuiInventory();
		
		int scrollX = (width - 18) / 2;
		int scrollY = (height - 18) / 2;
		// System.out.println(width + " x " + height);
		
		addSlotToContainer(new Slot(inventory, 0, scrollX / scaleFactor(), scrollY / scaleFactor()) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == AvatarItems.itemScroll;
			}
		});
		
		int w = width / scaleFactor(), h = height / scaleFactor();
		
		// Main inventory
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 9; c++) {
				int id = c + r * 9 + 9;
				addSlotToContainer(
						new Slot(player.inventory, id, w + 1 + (c - 9) * 18, h - 4 * 18 - 3 + r * 18));
			}
		}
		
		// Hotbar
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(player.inventory, i, w + 1 + (i - 9) * 18, h - 17));
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
	
}
