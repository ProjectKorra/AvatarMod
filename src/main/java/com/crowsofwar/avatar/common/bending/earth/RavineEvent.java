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

import com.crowsofwar.avatar.common.entity.EntityRavine;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

/**
 * Describes events for the ravine ability.
 *
 * @author CrowsOfWar
 */
public abstract class RavineEvent {
	
	private final EntityRavine ravine;
	
	public RavineEvent(EntityRavine ravine) {
		this.ravine = ravine;
	}
	
	public EntityRavine getRavine() {
		return ravine;
	}
	
	/**
	 * The ravine was created by the player. Only called server side.
	 *
	 * @author CrowsOfWar
	 */
	public static class Created extends RavineEvent {
		
		private final EntityPlayer creator;
		
		public Created(EntityRavine ravine, EntityPlayer creator) {
			super(ravine);
			this.creator = creator;
		}
		
		public EntityPlayer getCreator() {
			return creator;
		}
		
	}
	
	/**
	 * The ravine ended because it was too far.
	 *
	 * @author CrowsOfWar
	 */
	public static class End extends RavineEvent {
		
		public End(EntityRavine ravine) {
			super(ravine);
		}
		
	}
	
	/**
	 * The ravine stopped, possibly due to a barrier, or the lack of ground.
	 *
	 * @author CrowsOfWar
	 */
	public static class Stop extends RavineEvent {
		
		public Stop(EntityRavine ravine) {
			super(ravine);
		}
		
	}
	
	/**
	 * The ravine hit an entity.
	 *
	 * @author CrowsOfWar
	 */
	public static class HitEntity extends RavineEvent {
		
		private final Entity hit;
		
		public HitEntity(EntityRavine ravine, Entity hit) {
			super(ravine);
			this.hit = hit;
		}
		
		public Entity getHit() {
			return hit;
		}
		
	}
	
	/**
	 * The ravine destroyed a block.
	 *
	 * @author CrowsOfWar
	 */
	public static class DestroyBlock extends RavineEvent {
		
		private final IBlockState block;
		private final BlockPos destroyedAt;
		
		public DestroyBlock(EntityRavine ravine, IBlockState block, BlockPos destroyedAt) {
			super(ravine);
			this.block = block;
			this.destroyedAt = destroyedAt;
		}
		
		public IBlockState getBlockState() {
			return block;
		}
		
		public BlockPos getDestroyedAt() {
			return destroyedAt;
		}
		
	}
	
}
