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
package com.crowsofwar.avatar.common.data.ctx;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.item.AvatarItems;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class PlayerBender implements Bender {
	
	private final EntityPlayer player;
	
	public PlayerBender(EntityPlayer player) {
		this.player = player;
	}
	
	@Override
	public EntityLivingBase getEntity() {
		return player;
	}
	
	@Override
	public BendingData getData() {
		return AvatarPlayerData.fetcher().fetch(player);
	}
	
	@Override
	public boolean isCreativeMode() {
		return player.capabilities.isCreativeMode;
	}
	
	@Override
	public boolean isFlying() {
		return player.capabilities.isFlying;
	}
	
	@Override
	public boolean consumeWaterLevel(int amount) {
		
		int total = 0;
		InventoryPlayer inv = player.inventory;
		
		int inventorySlots = 36;
		for (int i = 0; i < inventorySlots; i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == AvatarItems.itemWaterPouch) {
				total += stack.getMetadata();
			}
		}
		
		if (total >= amount) {
			
			int i = 0;
			while (amount > 0) {
				ItemStack stack = inv.getStackInSlot(i);
				if (stack.getItem() == AvatarItems.itemWaterPouch) {
					int oldMetadata = stack.getMetadata();
					int newMetadata = stack.getMetadata() - amount;
					if (newMetadata < 0) newMetadata = 0;
					amount -= oldMetadata - newMetadata;
					System.out.println("Consuming " + (oldMetadata - newMetadata) + " water in slot " + i);
				}
				i++;
			}
			
			return true;
			
		} else {
			return false;
		}
		
	}
	
}
