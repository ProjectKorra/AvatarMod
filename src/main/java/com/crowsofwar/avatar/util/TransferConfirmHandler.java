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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.crowsofwar.avatar.network.AvatarChatMessages.*;

/**
 * Manages information and handling of current bison transfers
 *
 * @author CrowsOfWar
 */
public class TransferConfirmHandler {

	private static final Map<EntityPlayer, TransferData> inProgressTransfers = new HashMap<>();

	/**
	 * Initiates the transfer process and intializes data about the transfer.
	 * Also sends messages to parties involved.
	 */
	public static void startTransfer(EntityPlayer from, EntityPlayer to, EntitySkyBison bison) {
		inProgressTransfers.put(from, new TransferData(from, to, bison));
		MSG_BISON_TRANSFER_OLD_START.send(from, bison.getName(), to.getName());
		MSG_BISON_TRANSFER_NEW_START.send(to, bison.getName(), from.getName());
	}

	/**
	 * Tries to transfer the player's bison to whoever was requested. Handles
	 * all transferring and messaging logic.
	 */
	public static void confirmTransfer(EntityPlayer oldOwner) {
		TransferData transfer = inProgressTransfers.get(oldOwner);
		if (transfer != null) {

			EntitySkyBison bison = transfer.bison;
			EntityPlayer newOwner = transfer.to;
			bison.setOwner(newOwner);

			MSG_BISON_TRANSFER_OLD.send(oldOwner, bison.getName(), newOwner.getName());
			MSG_BISON_TRANSFER_NEW.send(newOwner, bison.getName(), oldOwner.getName());

			inProgressTransfers.remove(oldOwner);

		} else {

			MSG_BISON_TRANSFER_NONE.send(oldOwner);

		}
	}

	private static class TransferData {

		private final EntityPlayer from, to;
		private final EntitySkyBison bison;
		private int ticksLeft;

		public TransferData(EntityPlayer from, EntityPlayer to, EntitySkyBison bison) {
			this.from = from;
			this.to = to;
			this.bison = bison;
			this.ticksLeft = 100;
		}

	}

	@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
	public static class TickHandler {

		@SubscribeEvent
		public static void onTick(TickEvent.ServerTickEvent e) {
			if (e.phase == Phase.START) {

				Set<Map.Entry<EntityPlayer, TransferData>> entries = inProgressTransfers.entrySet();
				Iterator<Map.Entry<EntityPlayer, TransferData>> iterator = entries.iterator();

				while (iterator.hasNext()) {
					Map.Entry<EntityPlayer, TransferData> entry = iterator.next();
					TransferData data = entry.getValue();
					data.ticksLeft--;

					if (data.ticksLeft <= 0 || data.bison.isDead || data.from.isDead || data.to.isDead) {
						MSG_BISON_TRANSFER_OLD_IGNORE.send(data.from, data.to.getName());
						MSG_BISON_TRANSFER_NEW_IGNORE.send(data.to, data.from.getName());
						iterator.remove();
					}

				}

			}
		}

	}

}
