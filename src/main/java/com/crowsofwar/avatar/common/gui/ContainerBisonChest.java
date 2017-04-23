package com.crowsofwar.avatar.common.gui;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBisonChest extends Container {
	
	private final IInventory bisonInventory;
	private final EntitySkyBison bison;
	
	public ContainerBisonChest(IInventory playerInventory, IInventory bisonInventory, EntitySkyBison bison,
			EntityPlayer player) {
		
		this.bisonInventory = bisonInventory;
		this.bison = bison;
		
		bisonInventory.openInventory(player);
		
		// Saddle stack
		this.addSlotToContainer(new Slot(bisonInventory, 0, 8, 18) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem() == Items.SADDLE && !this.getHasStack();
			}
		});
		// Armor slot
		this.addSlotToContainer(new Slot(bisonInventory, 1, 8, 36) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return false;
			}
			
			@Override
			public int getSlotStackLimit() {
				return 1;
			}
		});
		
		// Bison inventory
		for (int r = 0; r < 3; ++r) {
			for (int c = 0; c < 9; ++c) {
				int index = 2 + r * 9 + c;
				int x = 79 + (c % 9) * 18;
				int y = 17 + r * 18;
				addSlotToContainer(new Slot(bisonInventory, index, x, y));
			}
		}
		
		// for (int r = 0; r < 2; r++) {
		// for (int c = 0; c < 9; c++) {
		// int index = 2 + r * 9 + c;
		// int x = (c % 9) * 18;
		// int y = r * 18;
		// addSlotToContainer(new Slot(bisonInventory, index, x, y));
		// }
		// }
		
		// Player inventory slots
		for (int i1 = 0; i1 < 3; ++i1) {
			for (int k1 = 0; k1 < 9; ++k1) {
				this.addSlotToContainer(
						new Slot(playerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
			}
		}
		
		for (int j1 = 0; j1 < 9; ++j1) {
			this.addSlotToContainer(new Slot(playerInventory, j1, 8 + j1 * 18, 142));
		}
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return bisonInventory.isUseableByPlayer(playerIn) && bison.isEntityAlive()
				&& bison.getDistanceToEntity(playerIn) < 8.0F;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.field_190927_a;
		Slot slot = this.inventorySlots.get(index);
		
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			if (index < this.bisonInventory.getSizeInventory()) {
				if (!this.mergeItemStack(itemstack1, this.bisonInventory.getSizeInventory(),
						this.inventorySlots.size(), true)) {
					return ItemStack.field_190927_a;
				}
			} else if (this.getSlot(1).isItemValid(itemstack1) && !this.getSlot(1).getHasStack()) {
				if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
					return ItemStack.field_190927_a;
				}
			} else if (this.getSlot(0).isItemValid(itemstack1)) {
				if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
					return ItemStack.field_190927_a;
				}
			} else if (this.bisonInventory.getSizeInventory() <= 2
					|| !this.mergeItemStack(itemstack1, 2, this.bisonInventory.getSizeInventory(), false)) {
				return ItemStack.field_190927_a;
			}
			
			if (itemstack1.func_190926_b()) {
				slot.putStack(ItemStack.field_190927_a);
			} else {
				slot.onSlotChanged();
			}
		}
		
		return itemstack;
	}
	
	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		this.bisonInventory.closeInventory(playerIn);
	}
}