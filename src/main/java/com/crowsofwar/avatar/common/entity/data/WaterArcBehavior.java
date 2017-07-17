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

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.water.AbilityWaterArc;
import com.crowsofwar.avatar.common.config.ConfigSkills;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;

import java.util.List;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class WaterArcBehavior extends Behavior<EntityWaterArc> {
	
	public static final DataSerializer<WaterArcBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();
	
	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		
		registerBehavior(PlayerControlled.class);
		registerBehavior(Thrown.class);
		registerBehavior(Idle.class);
		
	}
	
	public WaterArcBehavior() {}
	
	public static class PlayerControlled extends WaterArcBehavior {
		
		@Override
		public WaterArcBehavior onUpdate(EntityWaterArc water) {
			
			EntityLivingBase owner = water.getOwner();
			World world = owner.world;
			
			if (owner == null) return this;
			
			Raytrace.Result res = Raytrace.getTargetBlock(owner, 3, false);
			
			Vector target;
			if (res.hitSomething()) {
				target = res.getPosPrecise();
			} else {
				Vector look = Vector.toRectangular(Math.toRadians(owner.rotationYaw),
						Math.toRadians(owner.rotationPitch));
				target = Vector.getEyePos(owner).plus(look.times(3));
			}
			
			Vector motion = target.minus(water.position());
			motion.mul(.3 * 20);
			water.velocity().set(motion);
			
			if (water.world.isRemote && water.canPlaySplash()) {
				if (motion.sqrMagnitude() >= 0.004) water.playSplash();
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
	
	public static class Thrown extends WaterArcBehavior {
		
		@Override
		public WaterArcBehavior onUpdate(EntityWaterArc entity) {
			
			BendingData data = Bender.get(entity.getOwner()).getData();
			AbilityData abilityData = data.getAbilityData(AbilityWaterArc.ID);
			
			if (!abilityData.isMasterPath(AbilityTreePath.SECOND) || entity.ticksExisted >= 40) {
				entity.velocity().add(0, -9.81 / 60, 0);
			}
			
			List<EntityLivingBase> collidedList = entity.getEntityWorld().getEntitiesWithinAABB(
					EntityLivingBase.class, entity.getEntityBoundingBox().expand(0.9, 0.9, 0.9),
					collided -> collided != entity.getOwner());
			
			for (EntityLivingBase collided : collidedList) {
				if (collided == entity.getOwner()) return this;
				collided.addVelocity(entity.motionX, 0.4, entity.motionZ);
				collided.attackEntityFrom(AvatarDamageSource.causeWaterDamage(collided, entity.getOwner()),
						6 * entity.getDamageMult());
				
				if (!entity.world.isRemote) {
					
					abilityData.addXp(ConfigSkills.SKILLS_CONFIG.waterHit);
					
					if (abilityData.isMasterPath(AbilityTreePath.FIRST)) {
						entity.setBehavior(new PlayerControlled());
						data.addStatusControl(StatusControl.THROW_WATER);
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
	
	public static class Idle extends WaterArcBehavior {
		
		@Override
		public WaterArcBehavior onUpdate(EntityWaterArc entity) {
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
