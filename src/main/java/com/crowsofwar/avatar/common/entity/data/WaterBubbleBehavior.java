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

import com.crowsofwar.avatar.common.data.AvatarWorldData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.EntityWaterBubble;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class WaterBubbleBehavior extends Behavior<EntityWaterBubble> {
	
	public static final DataSerializer<WaterBubbleBehavior> DATA_SERIALIZER = new BehaviorSerializer<>();
	
	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		registerBehavior(Drop.class);
		registerBehavior(PlayerControlled.class);
		registerBehavior(Thrown.class);
	}
	
	protected WaterBubbleBehavior() {}
	
	public static class Drop extends WaterBubbleBehavior {
		
		@Override
		public Behavior onUpdate(EntityWaterBubble entity) {
			entity.velocity().add(0, -9.81 / 20, 0);
			if (entity.isCollided) {
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
	
	public static class PlayerControlled extends WaterBubbleBehavior {
		
		@Override
		public Behavior onUpdate(EntityWaterBubble entity) {
			EntityLivingBase owner = entity.getOwner();
			
			if (owner == null) return this;
			
			BendingData data = Bender.get(owner).getData();
			
			Vector target;
			Raytrace.Result raytrace = Raytrace.getTargetBlock(owner, 3, false);
			if (raytrace.hitSomething()) {
				target = raytrace.getPosPrecise().plus(0, .2, 0);
			} else {
				double yaw = Math.toRadians(owner.rotationYaw);
				double pitch = Math.toRadians(owner.rotationPitch);
				Vector forward = Vector.toRectangular(yaw, pitch);
				Vector eye = Vector.getEyePos(owner);
				target = forward.times(3).plus(eye);
			}
			
			Vector motion = target.minus(new Vector(entity));
			motion.mul(3);
			
			entity.velocity().set(motion);
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
	
	public static class Thrown extends WaterBubbleBehavior {
		
		@Override
		public Behavior onUpdate(EntityWaterBubble entity) {
			entity.velocity().add(0, -9.81 / 10, 0);
			if (entity.isCollided) {
				
				IBlockState state = Blocks.FLOWING_WATER.getDefaultState();
				
				if (!entity.world.isRemote) {
					
					entity.world.setBlockState(entity.getPosition(), state, 3);
					entity.setDead();
					
					if (!entity.isSourceBlock()) {
						AvatarWorldData wd = AvatarWorldData.getDataFromWorld(entity.world);
						wd.addTemporaryWaterLocation(entity.getPosition());
					}
					
				}
				
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
	
}
