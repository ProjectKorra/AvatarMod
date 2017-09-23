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
package com.crowsofwar.avatar.common;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.EntityIcePrison;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Cancels any actions done when a player is in an ice prison or ice shield
 * 
 * @author CrowsOfWar
 */
public class IceActionCanceller {
	
	private IceActionCanceller() {}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new IceActionCanceller());
	}

	private boolean isTrapped(EntityLivingBase entity) {
		if (EntityIcePrison.isImprisoned(entity)) {
			return true;
		}

		if (Bender.isBenderSupported(entity)) {
			return BendingData.get(entity).hasStatusControl(StatusControl.SHIELD_SHATTER);
		}

		return false;
	}

	@SubscribeEvent
	public void onJump(LivingJumpEvent e) {
		EntityLivingBase entity = e.getEntityLiving();
		if (isTrapped(entity)) {
			entity.motionY = 0;
		}
	}
	
	@SubscribeEvent
	public void onInteract(PlayerInteractEvent e) {
		EntityPlayer player = e.getEntityPlayer();
		if (isTrapped(player)) {
			if (e.isCancelable()) {
				e.setCanceled(true);
			}
		}
	}
	
}
