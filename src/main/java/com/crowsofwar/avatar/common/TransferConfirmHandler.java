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

import java.util.Map;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Manages information about bison transfer confirmation
 * 
 * @author CrowsOfWar
 */
public class TransferConfirmHandler {
	
	private static final Map<EntityPlayer, TransferTarget> inProcessTransfers;
	
	public static void registerTransfer(EntityPlayer from, EntityPlayer to, Runnable onConfirm) {
		
	}
	
	public static void confirmTransfer(EntityPlayer from) {
		
	}
	
	private static class TransferTarget {
		
		private final EntityPlayer transferTo;
		private final EntitySkyBison bison;
		
		public TransferTarget(EntityPlayer transferTo, EntitySkyBison bison) {
			this.transferTo = transferTo;
			this.bison = bison;
		}
		
	}
	
}
