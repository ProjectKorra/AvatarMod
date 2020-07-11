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

import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.scroll.ItemScroll;
import com.crowsofwar.avatar.common.item.scroll.Scrolls;
import com.crowsofwar.avatar.common.item.scroll.Scrolls.ScrollType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.UUID;

import static net.minecraft.item.ItemStack.EMPTY;

/**
 * @author CrowsOfWar
 */
public class ContainerSkillsGui extends Container {

	private final EntityPlayer player;
	private final SkillsGuiInventory inventory;

	private int invIndex, hotbarIndex;

	public ContainerSkillsGui(EntityPlayer player, UUID bendingId) {
		this.player = player;

		inventory = new SkillsGuiInventory();

		addSlotToContainer(new Slot(inventory, 0, 100, 100) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				ScrollType scrollType = Scrolls.getTypeForStack(stack);
				Item item = stack.getItem();
				Slot other = getSlot(1);
				return item instanceof ItemScroll && scrollType.accepts(bendingId) && !other.getHasStack();
			}
		});
		// Second scroll slot
		addSlotToContainer(new Slot(inventory, 1, 100, 100) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				ScrollType scrollType = Scrolls.getTypeForStack(stack);
				Item item = stack.getItem();
				Slot other = getSlot(0);
				return item instanceof ItemScroll && scrollType.accepts(bendingId) && !other.getHasStack();
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
					return EMPTY;
				}
			} else {
				if (!mergeItemStack(stack, 0, 1, true)) {
					return EMPTY;
				}
			}

			return stack;

		}

		return EMPTY;
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		ItemStack scroll = inventory.getStackInSlot(0);
		if (scroll != EMPTY) {
			player.dropItem(scroll, false);
			inventory.setInventorySlotContents(0, EMPTY);
		}

		ItemStack scroll2 = inventory.getStackInSlot(1);
		if (scroll2 != EMPTY) {
			player.dropItem(scroll2, false);
			inventory.setInventorySlotContents(1, EMPTY);
		}

	}

	public int getInvIndex() {
		return invIndex;
	}

	public int getHotbarIndex() {
		return hotbarIndex;
	}

}
