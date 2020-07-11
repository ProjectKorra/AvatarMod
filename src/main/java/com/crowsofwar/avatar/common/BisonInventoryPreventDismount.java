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

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import com.crowsofwar.avatar.common.gui.ContainerBisonChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class BisonInventoryPreventDismount {

	@SubscribeEvent
	public static void onDismount(EntityMountEvent e) {
		Entity mounting = e.getEntityMounting();
		Entity mount = e.getEntityBeingMounted();
		if (e.isDismounting() && mounting instanceof EntityPlayer && mount instanceof EntitySkyBison) {
			EntityPlayer player = (EntityPlayer) mounting;
			EntitySkyBison bison = (EntitySkyBison) mount;
			if (!player.world.isRemote) {
				if (player.openContainer instanceof ContainerBisonChest) {
					e.setCanceled(true);
				}
			}
		}
	}

}
