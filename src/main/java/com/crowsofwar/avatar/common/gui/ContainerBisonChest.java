package com.crowsofwar.avatar.common.gui;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import com.crowsofwar.avatar.common.item.AvatarItems;

import net.minecraft.entity.player.EntityPlayer;
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
				return stack.getItem() == AvatarItems.itemBisonSaddle;
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
		int slotsAdded = 0;
		outer: for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 9; c++) {
				
				if (slotsAdded >= bison.getChestSlots()) {
					break outer;
				}
				slotsAdded++;
				
				int index = 2 + r * 9 + c;
				int x = 80 + (c % 9) * 18;
				int y = 18 + r * 18;
				addSlotToContainer(new Slot(bisonInventory, index, x, y));
				
			}
		}
		
		// Player inventory slots
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 9; c++) {
				int index = c + r * 9 + 9;
				int x = 44 + c * 18;
				int y = 84 + r * 18;
				this.addSlotToContainer(new Slot(playerInventory, index, x, y));
			}
		}
		
		for (int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(playerInventory, i, 44 + i * 18, 142));
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