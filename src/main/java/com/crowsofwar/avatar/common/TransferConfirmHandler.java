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

import static com.crowsofwar.avatar.common.AvatarChatMessages.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

/**
 * Manages information and handling of current bison transfers
 * 
 * @author CrowsOfWar
 */
public class TransferConfirmHandler {
	
	private static final Map<EntityPlayer, TransferData> inProgressTransfers = new HashMap<>();
	
	private TransferConfirmHandler() {}
	
	public static void registerEventHandler() {
		MinecraftForge.EVENT_BUS.register(new TransferConfirmHandler.TickHandler());
	}
	
	public static void registerTransfer(EntityPlayer from, EntityPlayer to, EntitySkyBison bison) {
		inProgressTransfers.put(from, new TransferData(from, to, bison));
	}
	
	public static void confirmTransfer(EntityPlayer newOwner) {
		TransferData transfer = inProgressTransfers.get(newOwner);
		if (transfer != null) {
			
			EntitySkyBison bison = transfer.bison;
			EntityPlayer oldOwner = transfer.to;
			bison.setOwner(newOwner);
			
			MSG_BISON_TRANSFER_OLD.send(oldOwner, bison.getName(), newOwner.getName());
			MSG_BISON_TRANSFER_NEW.send(newOwner, bison.getName(), oldOwner.getName());
			
		} else {
			
			MSG_BISON_TRANSFER_NONE.send(newOwner);
			
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
	
	private static class TickHandler {
		
		@SubscribeEvent
		public void onTick(TickEvent.ServerTickEvent e) {
			if (e.phase == Phase.START) {
				
				Set<Map.Entry<EntityPlayer, TransferData>> entries = inProgressTransfers.entrySet();
				Iterator<Map.Entry<EntityPlayer, TransferData>> iterator = entries.iterator();
				
				while (iterator.hasNext()) {
					Map.Entry<EntityPlayer, TransferData> entry = iterator.next();
					TransferData data = entry.getValue();
					data.ticksLeft--;
					
					if (data.ticksLeft <= 0 || data.bison.isDead || data.from.isDead || data.to.isDead) {
						iterator.remove();
					}
					
				}
				
			}
		}
		
	}
	
}
