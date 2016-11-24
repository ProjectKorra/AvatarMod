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

package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;

import net.minecraft.entity.player.EntityPlayer;

/**
 * An event caused by a floating block.
 *
 * @author CrowsOfWar
 */
public class FloatingBlockEvent {
	
	private final EntityFloatingBlock floating;
	
	public FloatingBlockEvent(EntityFloatingBlock floating) {
		this.floating = floating;
	}
	
	public EntityFloatingBlock getFloatingBlock() {
		return this.floating;
	}
	
	/**
	 * The floating block was picked up. Only created from server.
	 *
	 * @author CrowsOfWar
	 */
	public static class BlockPickedUp extends FloatingBlockEvent {
		
		private final EntityPlayer player;
		
		public BlockPickedUp(EntityFloatingBlock floating, EntityPlayer player) {
			super(floating);
			this.player = player;
		}
		
		public EntityPlayer getPlayer() {
			return player;
		}
		
	}
	
	/**
	 * The floating block was placed on the ground. Only created from server.
	 *
	 * @author CrowsOfWar
	 */
	public static class BlockPlaced extends FloatingBlockEvent {
		
		private final EntityPlayer player;
		
		public BlockPlaced(EntityFloatingBlock floating, EntityPlayer player) {
			super(floating);
			this.player = player;
		}
		
		public EntityPlayer getPlayer() {
			return player;
		}
		
	}
	
	/**
	 * The floating block was placed, and reached its destination. Called from both sides.
	 *
	 * @author CrowsOfWar
	 */
	public static class BlockPlacedReached extends FloatingBlockEvent {
		
		public BlockPlacedReached(EntityFloatingBlock floating) {
			super(floating);
		}
		
	}
	
	/**
	 * The floating block was thrown. Only called from server.
	 *
	 *
	 * @author CrowsOfWar
	 */
	public static class BlockThrown extends FloatingBlockEvent {
		
		private final EntityPlayer player;
		
		public BlockThrown(EntityFloatingBlock floating, EntityPlayer player) {
			super(floating);
			this.player = player;
		}
		
		public EntityPlayer getPlayer() {
			return player;
		}
		
	}
	
	public static class BlockThrownReached extends FloatingBlockEvent {
		
		public BlockThrownReached(EntityFloatingBlock floating) {
			super(floating);
		}
		
	}
	
}
