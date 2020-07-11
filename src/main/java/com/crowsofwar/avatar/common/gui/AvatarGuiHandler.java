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

package com.crowsofwar.avatar.common.gui;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingStyles;
import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.util.UUID;

public class AvatarGuiHandler implements IGuiHandler {

	public static final int GUI_ID_BISON_CHEST = 5;
	public static final int GUI_ID_GET_BENDING = 6;

	public static int getGuiId(UUID bendingId) {
		return 100 + BendingStyles.getNetworkId(bendingId);
	}

	public static UUID getBendingId(int guiId) {
		return BendingStyles.get((byte) (guiId - 100)).getId();
	}

	public static boolean isBendingGui(int guiId) {
		return guiId > 100 && BendingStyles.has(getBendingId(guiId));
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (isBendingGui(id)) {
			return new ContainerSkillsGui(player, getBendingId(id));
		}
		if (id == GUI_ID_BISON_CHEST) {
			// x-coordinate represents ID of sky bison
			int bisonId = x;
			EntitySkyBison bison = EntitySkyBison.findBison(world, bisonId);
			if (bison != null) {

				return new ContainerBisonChest(player.inventory, bison.getInventory(), bison, player);

			} else {
				AvatarLog.warn(WarningType.WEIRD_PACKET, player.getName()
						+ " tried to open skybison inventory, was not found. BisonId: " + bisonId);
			}
		}
		if (id == GUI_ID_GET_BENDING) {
			return new ContainerGetBending(player);
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return AvatarMod.proxy.createClientGui(id, player, world, x, y, z);
	}

}
