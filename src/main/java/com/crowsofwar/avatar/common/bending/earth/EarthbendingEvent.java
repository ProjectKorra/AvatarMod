package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.util.event.IEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class EarthbendingEvent implements IEvent {
	
	public static class BlockPickedUp extends EarthbendingEvent {
		
		private final EntityFloatingBlock floating;
		
		public BlockPickedUp(EntityFloatingBlock floating) {
			this.floating = floating;
		}
		
		public EntityFloatingBlock getFloatingBlock() {
			return this.floating;
		}
		
	}
	
	public static class BlockPlaced extends EarthbendingEvent {
		
		private final EntityFloatingBlock floating;
		
		public BlockPlaced(EntityFloatingBlock floating) {
			this.floating = floating;
		}
		
		public EntityFloatingBlock getFloatingBlock() {
			return this.floating;
		}
		
	}
	
	public static class BlockPlacedReached extends EarthbendingEvent {
		
		private final EntityFloatingBlock floating;
		
		public BlockPlacedReached(EntityFloatingBlock floating) {
			this.floating = floating;
		}
		
		public EntityFloatingBlock getFloatingBlock() {
			return this.floating;
		}
		
	}
	
	public static class BlockThrown extends EarthbendingEvent {
		
		private final EntityFloatingBlock floating;
		
		public BlockThrown(EntityFloatingBlock floating) {
			this.floating = floating;
		}
		
		public EntityFloatingBlock getFloatingBlock() {
			return this.floating;
		}
		
	}
	
	public static class BlockThrownReached extends EarthbendingEvent {
		
		private final EntityFloatingBlock floating;
		
		public BlockThrownReached(EntityFloatingBlock floating) {
			this.floating = floating;
		}
		
		public EntityFloatingBlock getFloatingBlock() {
			return this.floating;
		}
		
	}
	
}
