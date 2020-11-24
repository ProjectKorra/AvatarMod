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
import com.crowsofwar.avatar.entity.mob.EntitySkyBison;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class BisonLeftClickHandler {

	@SubscribeEvent
	public static void onLeftClickBison(LivingAttackEvent e) {
		Entity interacted = e.getEntity();
		DamageSource damage = e.getSource();
		if (damage.getTrueSource() instanceof EntityPlayer && interacted instanceof EntitySkyBison) {

			EntitySkyBison bison = (EntitySkyBison) interacted;
			EntityPlayer player = (EntityPlayer) damage.getTrueSource();

			// ignore damage other than melee
			if (damage.getDamageType().equals("player")) {
				if (bison.onLeftClick(player)) {
					e.setCanceled(true);
				}
			} else {

				if (bison.getOwner() == player) {
					e.setCanceled(true);
				}

			}

		}
	}

}
