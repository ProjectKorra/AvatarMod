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
package com.crowsofwar.avatar.util;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.entity.EntityIcePrison;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

import static com.crowsofwar.avatar.util.data.StatusControlController.SHIELD_SHATTER;

/**
 * Cancels any actions done when a player is in an ice prison or ice shield
 *
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class IceActionCanceller {

	private static boolean isTrapped(EntityLivingBase entity) {
		if (EntityIcePrison.isImprisoned(entity)) {
			return true;
		}

		//noinspection SimplifiableIfStatement
		if (Bender.isBenderSupported(entity)) {
			if (BendingData.getFromEntity(entity) != null) {
				return Objects.requireNonNull(BendingData.getFromEntity(entity)).hasStatusControl(SHIELD_SHATTER);
			}
		}

		return false;
	}

	@SubscribeEvent
	public static void onJump(LivingJumpEvent e) {
		EntityLivingBase entity = e.getEntityLiving();
		if (isTrapped(entity) && entity.onGround) {
			entity.motionY *= 0;
		}
		else if (isTrapped(entity)) {
			entity.motionX *= 0;
			entity.motionZ *= 0;
		}
	}

	@SubscribeEvent
	public static void onInteract(PlayerInteractEvent e) {
		EntityPlayer player = e.getEntityPlayer();
		if (isTrapped(player)) {
			if (e.isCancelable()) {
				e.setCanceled(true);
			}
		}
	}

}
