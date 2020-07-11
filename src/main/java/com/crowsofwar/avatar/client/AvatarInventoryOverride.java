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

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.entity.mob.EntitySkyBison;
import com.crowsofwar.avatar.network.packets.PacketSBisonInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

/**
 * Causes the player to open the sky bison inventory instead of their own while
 * riding a bison.
 *
 * @author CrowsOfWar
 */
public class AvatarInventoryOverride {

	private AvatarInventoryOverride() {
	}

	public static void register() {
		MinecraftForge.EVENT_BUS.register(new AvatarInventoryOverride());
	}

	@SubscribeEvent
	public void onInventoryOpen(KeyInputEvent e) {

		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		KeyBinding keybind = mc.gameSettings.keyBindInventory;

		// don't use isPressed() as that marks it as "not pressed" if it was
		if (keybind.isKeyDown()) {

			if (player.getRidingEntity() instanceof EntitySkyBison) {
				EntitySkyBison bison = (EntitySkyBison) player.getRidingEntity();
				if (bison.canPlayerViewInventory(player)) {

					AvatarMod.network.sendToServer(new PacketSBisonInventory());
					// mark key as not pressed to avoid vanilla behavior
					keybind.isPressed();

				}
			}

		}
	}

}
