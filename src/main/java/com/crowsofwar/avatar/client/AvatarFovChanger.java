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
package com.crowsofwar.avatar.client;

import com.crowsofwar.avatar.registry.AvatarItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CrowsOfWar
 */
public class AvatarFovChanger {

	private AvatarFovChanger() {
	}

	public static void register() {
		MinecraftForge.EVENT_BUS.register(new AvatarFovChanger());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onUpdateFOV(FOVUpdateEvent event) {

		EntityPlayer entity = event.getEntity();

		ItemStack activeStack = entity.getActiveItemStack();

		if (entity.isHandActive() && activeStack.getItem() == AvatarItems.itemBisonWhistle) {

			float progress = activeStack.getItem().getMaxItemUseDuration(activeStack)
					- entity.getItemInUseCount();
			float neededProgress = 20;

			float percent = progress / neededProgress;
			if (percent > 1) percent = 1;
			float inversePercent = 1 - percent;

			float fov = event.getFov();
			fov *= 1 - (1 - inversePercent * inversePercent) * 0.3F;
			event.setNewfov(fov);

		}

	}

}
