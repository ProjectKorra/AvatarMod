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
package com.crowsofwar.avatar.util.data.ctx;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.AvatarUiRenderer;
import com.crowsofwar.avatar.util.analytics.*;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.lightning.Lightningbending;
import com.crowsofwar.avatar.util.data.*;
import com.crowsofwar.avatar.entity.EntityLightningArc;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.registry.AvatarItems;
import com.crowsofwar.avatar.network.packets.*;
import com.crowsofwar.avatar.util.Raytrace;

import static com.crowsofwar.avatar.network.AvatarChatMessages.MSG_LIGHTNING_REDIRECT_SUCCESS;
import static com.crowsofwar.avatar.config.ConfigChi.CHI_CONFIG;
import static com.crowsofwar.avatar.util.data.TickHandlerController.LIGHTNING_REDIRECT;

/**
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
		return BendingData.getFromEntity(player);
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

		//Avoid chi consumption if client side.
		if (getEntity().world.isRemote)
			return true;
		else {
			// Avoid chi consumption if creative mode
			if (isCreativeMode() && CHI_CONFIG.infiniteInCreative) {
				return true;
			}

			// Otherwise just try normal chi consumption
			boolean result = getData().chi().consumeChi(amount);

			if (!result) {
				sendMessage("avatar.nochi");

				// Send out of chi analytic
				AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onOutOfChi());

			}

			return result;
		}
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
	public void executeAbility(Ability ability, Raytrace.Result raytrace, boolean switchPath) {
		if (getWorld().isRemote) {
			super.executeAbility(ability, raytrace, switchPath);
			AvatarMod.network.sendToServer(new PacketSUseAbility(ability, raytrace, switchPath));
		} else {
			super.executeAbility(ability, raytrace, switchPath);
		}
	}

	@Override
	public BenderInfo getInfo() {
		return new BenderInfoPlayer(player.getName());
	}

	@Override
	public boolean redirectLightning(EntityLightningArc lightningArc) {

		if (lightningArc.isCreatedByRedirection()) {
			return false;
		}

		EntityLivingBase owner = lightningArc.getOwner();
		BendingData data = getData();
		AbilityData abilityData = data.getAbilityData("lightning_redirect");

		if ((owner instanceof EntityPlayer && !((EntityPlayer) owner).isCreative()) && abilityData.getLevel() == -1) {
			return false;
		} else if ((abilityData.getLevel() == -1 && (owner instanceof EntityBender))) {
			return false;
		} else if (!data.hasBendingId(Lightningbending.ID)) {
			return false;
		}

		// Percent chance to redirect lightning; 0..100
		double chance = abilityData.getLevel() * 10 + 60;

		if (Math.random() * 100 < chance) {

			BenderInfo originalShooterInfo = BenderInfo.get(lightningArc.getOwner());
			if (lightningArc.getOwner() != null) {

				data.getMiscData().setRedirectionSource(originalShooterInfo);
				data.addTickHandler(LIGHTNING_REDIRECT);

			}

			String lightningArcOwner = lightningArc.getOwner() == null ? "lightningbender" : lightningArc.getOwner().getName();
			MSG_LIGHTNING_REDIRECT_SUCCESS.send(player, lightningArcOwner);

			return true;

		}

		return false;

	}

}
