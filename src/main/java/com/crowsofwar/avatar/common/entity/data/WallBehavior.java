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

package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityWallSegment;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class WallBehavior extends Behavior<EntityWallSegment> {
	
	public static DataSerializer<WallBehavior> SERIALIZER = new Behavior.BehaviorSerializer<>();
	
	public static void register() {
		DataSerializers.registerSerializer(SERIALIZER);
		registerBehavior(Drop.class);
		registerBehavior(Rising.class);
		registerBehavior(Waiting.class);
	}
	
	public static class Drop extends WallBehavior {
		
		@Override
		public Behavior onUpdate(EntityWallSegment entity) {
			entity.addVelocity(0, -7.0 / 20, 0);
			if (entity.onGround) {
				entity.dropBlocks();
				entity.setDead();
			}
			return this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
		@Override
		public void load(NBTTagCompound nbt) {}
		
		@Override
		public void save(NBTTagCompound nbt) {}
		
	}
	
	public static class Rising extends WallBehavior {
		
		private int ticks = 0;
		
		@Override
		public Behavior onUpdate(EntityWallSegment entity) {
			
			if (entity.getWall() == null) {
				return this;
			}
			
			// not 0 since client missed 0th tick
			if (ticks == 1) {
				
				int maxHeight = 0;
				for (int i = 0; i < 5; i++) {
					EntityWallSegment seg = entity.getWall().getSegment(i);
					if (seg.height > maxHeight) maxHeight = (int) seg.height;
				}
				
				entity.motionY = STATS_CONFIG.wallMomentum / 5 * maxHeight;
				
			} else {
				entity.motionY *= 0.9;
			}
			
			// For some reason, the same entity instance is on server/client,
			// but has different world reference when this is called...?
			if (!entity.world.isRemote) ticks++;
			
			return ticks > 5 && entity.velocity().y() <= 0.2 ? new Waiting() : this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
		@Override
		public void load(NBTTagCompound nbt) {}
		
		@Override
		public void save(NBTTagCompound nbt) {}
		
	}
	
	public static class Waiting extends WallBehavior {
		
		private int ticks = 0;
		
		@Override
		public Behavior onUpdate(EntityWallSegment entity) {
			entity.setVelocity(Vector.ZERO);
			ticks++;
			
			boolean drop = ticks >= STATS_CONFIG.wallWaitTime * 20;
			
			BendingData data = BendingData.get(entity.getOwner());
			AbilityData abilityData = data.getAbilityData("wall");
			if (abilityData.isMasterPath(AbilityTreePath.SECOND)) {

				drop = entity.getOwner().isDead || ticks >= STATS_CONFIG.wallWaitTime2 * 20;
				
				BendingContext ctx = new BendingContext(data, entity.getOwner(),
						Bender.get(entity.getOwner()), new Raytrace.Result());
				
				if (!entity.world.isRemote && !ctx.consumeChi(STATS_CONFIG.chiWallOneSecond / 20)) {
					drop = true;
				}
				
			}
			
			return drop ? new Drop() : this;
		}
		
		@Override
		public void fromBytes(PacketBuffer buf) {}
		
		@Override
		public void toBytes(PacketBuffer buf) {}
		
		@Override
		public void load(NBTTagCompound nbt) {}
		
		@Override
		public void save(NBTTagCompound nbt) {}
		
	}
	
}
