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