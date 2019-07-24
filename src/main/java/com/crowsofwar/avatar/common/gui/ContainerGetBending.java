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

import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.scroll.ItemScroll;
import com.crowsofwar.avatar.common.item.scroll.Scrolls;
import com.crowsofwar.avatar.common.item.scroll.Scrolls.ScrollType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static net.minecraft.item.ItemStack.EMPTY;

/**
 * @author CrowsOfWar
 */
public class ContainerGetBending extends Container {

	private final GetBendingInventory inventory;

	private int invIndex, hotbarIndex;
	private float incompatibleMsgTicks;

	public ContainerGetBending(EntityPlayer player) {

		inventory = new GetBendingInventory();
		incompatibleMsgTicks = -1;

		addSlotToContainer(new ScrollSlot(inventory, 0, -18, -18));
		addSlotToContainer(new ScrollSlot(inventory, 1, -18, -18));
		addSlotToContainer(new ScrollSlot(inventory, 2, -18, -18));

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
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {

		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();

			if (index >= 0 && index <= 2) {
				if (!mergeItemStack(stack, 1, 37, true)) {
					return EMPTY;
				}
			} else {
				if (!mergeItemStack(stack, 0, 3, true)) {
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

		if (!player.world.isRemote) {
			clearContainer(player, player.world, inventory);
		}

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	public int getSize() {
		return inventory.getSizeInventory();
	}

	public int getInvIndex() {
		return invIndex;
	}

	public int getHotbarIndex() {
		return hotbarIndex;
	}

	/**
	 * Returns the ticks left to display incompatible scrolls message, or -1 if
	 * no display
	 */
	public float getIncompatibleMsgTicks() {
		return incompatibleMsgTicks;
	}

	public void decrementIncompatibleMsgTicks(float amount) {
		incompatibleMsgTicks -= amount;
	}

	/**
	 * Returns the ints that can be unlocked by the scrolls which are currently
	 * in the slots.
	 */
	public List<UUID> getEligibleBending() {

		UUID foundId = null;

		for (int i = 0; i <= 2; i++) {
			Slot slot = getSlot(i);

			// No possible unlocks if there aren't 3 scrolls
			if (!slot.getHasStack()) {
				return Collections.emptyList();
			}

			// If the scroll isn't universal, then we found the scroll type used
			// Possible since all scroll stacks in the inventory must all be
			// compatible (or they couldn't be added)
			// Don't return here b/c didn't check if all slots aren't empty
			UUID bendingId = Scrolls.getTypeForStack(slot.getStack()).getBendingId();
			if (bendingId != null) {
				foundId = bendingId;
			}

		}

		if (foundId == null) {
			// Didn't find scroll of a specific type
			// all universal scrolls
			return BendingStyles.allMainIds();
		} else {
			// Found scroll of specific type
			return Arrays.asList(foundId);
		}

	}

	private class ScrollSlot extends Slot {

		public ScrollSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean isItemValid(ItemStack stack) {
			if (stack.getItem() instanceof ItemScroll) {

				ScrollType type1 = Scrolls.getTypeForStack(stack);

				for (int i = 0; i <= 2; i++) {
					ItemStack stack2 = getSlot(i).getStack();
					if (!stack2.isEmpty()) {
						ScrollType type2 = Scrolls.getTypeForStack(stack);
						if (!type1.isCompatibleWith(type2)) {
							incompatibleMsgTicks = 100;
							return false;
						}
					}
				}

				return true;

			}

			return false;
		}

	}

}
