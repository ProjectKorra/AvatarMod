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

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.AvatarUiRenderer;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityLightningArc;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.network.packets.PacketCErrorMessage;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import static com.crowsofwar.avatar.common.config.ConfigChi.CHI_CONFIG;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class PlayerBender extends Bender {
	
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
		return BendingData.get(player);
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
			
			// Reduce water pouch level
			if (!isCreativeMode()) {
				int i = 0;
				while (amount > 0) {
					ItemStack stack = inv.getStackInSlot(i);
					if (stack.getItem() == AvatarItems.itemWaterPouch) {
						int oldMetadata = stack.getMetadata();
						int newMetadata = stack.getMetadata() - amount;
						if (newMetadata < 0) newMetadata = 0;
						amount -= oldMetadata - newMetadata;
						stack.setItemDamage(newMetadata);
					}
					i++;
				}
			}
			
			return true;
			
		} else {
			return false;
		}
		
	}

	@Override
	public boolean consumeChi(float amount) {

		// Avoid chi consumption if creative mode
		if (isCreativeMode() && CHI_CONFIG.infiniteInCreative) {
			return true;
		}

		// Otherwise just try normal chi consumption
		boolean result = getData().chi().consumeChi(amount);

		if (!result) {
			sendMessage("avatar.nochi");
		}

		return result;

	}

	@Override
	protected boolean canUseAbility(Ability ability) {
		return super.canUseAbility(ability) || player.isCreative();
	}

	@Override
	public void sendMessage(String message) {
		if (!getWorld().isRemote) {
			AvatarMod.network.sendTo(new PacketCErrorMessage(message), (EntityPlayerMP) player);
		} else {
			AvatarUiRenderer.displayErrorMessage(message);
		}
	}

	@Override
	public void executeAbility(Ability ability, Raytrace.Result raytrace) {
		if (getWorld().isRemote) {

			AvatarMod.network.sendToServer(new PacketSUseAbility(ability, raytrace));

		} else {
			super.executeAbility(ability, raytrace);
		}
	}

	public boolean redirectLightning(EntityLightningArc lightningArc) {

		Vector look = Vector.getLookRectangular(player);
		Vector currentVelocity = lightningArc.velocity();
		Vector newVelocity = look.times(currentVelocity.magnitude());

		lightningArc.setVelocity(newVelocity);

		return true;
	}

}
