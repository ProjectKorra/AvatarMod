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
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.MiscData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class FallAbsorptionHandler {

	@SubscribeEvent
	public static void onFall(LivingFallEvent e) {
		Entity entity = e.getEntity();
		if (entity instanceof EntityPlayer && !entity.world.isRemote && !(entity instanceof FakePlayer)) {
			EntityPlayer player = (EntityPlayer) entity;
			BendingData data = BendingData.get(player);
			MiscData miscData = data.getMiscData();
			if (miscData.getFallAbsorption() != 0) {
				e.setDistance(e.getDistance() - miscData.getFallAbsorption());
				if (e.getDistance() < 0) e.setDistance(0);
				miscData.setFallAbsorption(0);
			}
		}
	}

}
