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

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import com.crowsofwar.avatar.common.entity.EntityWallSegment;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

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
			entity.velocity().add(0, -7.0 / 20, 0);
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
			// not 0 since client missed 0th tick
			if (ticks == 1)
				entity.velocity().set(0, STATS_CONFIG.wallMomentum, 0);
			else
				entity.velocity().setY(entity.velocity().y() * 0.9);
			
			// For some reason, the same entity instance is on server/client,
			// but has different world reference when this is called...?
			if (!entity.worldObj.isRemote) ticks++;
			
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
			entity.velocity().set(0, 0, 0);
			ticks++;
			return ticks < (STATS_CONFIG.wallWaitTime * 20) ? this : new Drop();
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
