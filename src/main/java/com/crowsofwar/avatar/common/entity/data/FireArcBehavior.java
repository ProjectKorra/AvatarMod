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
import com.crowsofwar.avatar.common.bending.fire.AbilityFireArc;
import com.crowsofwar.avatar.common.config.ConfigSkills;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class FireArcBehavior extends Behavior<EntityFireArc> {
	
	public static final DataSerializer<FireArcBehavior> DATA_SERIALIZER = new Behavior.BehaviorSerializer<>();
	
	public static void register() {
		DataSerializers.registerSerializer(DATA_SERIALIZER);
		
		registerBehavior(PlayerControlled.class);
		registerBehavior(Thrown.class);
		registerBehavior(Idle.class);
		
	}
	
	public static class PlayerControlled extends FireArcBehavior {
		
		public PlayerControlled() {}
		
		@Override
		public FireArcBehavior onUpdate(EntityFireArc entity) {
			
			EntityLivingBase owner = entity.getOwner();
			if (owner == null) {
				return this;
			}
			World world = owner.world;
			
			Vector look = Vector.toRectangular(Math.toRadians(owner.rotationYaw),
					Math.toRadians(owner.rotationPitch));
			Vector lookPos = Vector.getEyePos(owner).plus(look.times(3));
			Vector motion = lookPos.minus(new Vector(entity));
			motion.mul(.3);
			entity.move(MoverType.SELF, motion.x(), motion.y(), motion.z());
			
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
	
	public static class Thrown extends FireArcBehavior {
		
		@Override
		public FireArcBehavior onUpdate(EntityFireArc entity) {
			entity.velocity().add(0, -9.81 / 60, 0);
			
			List<EntityLivingBase> collidedList = entity.getEntityWorld().getEntitiesWithinAABB(
					EntityLivingBase.class, entity.getEntityBoundingBox().expand(0.9, 0.9, 0.9),
					collided -> collided != entity.getOwner());
			
			for (EntityLivingBase collided : collidedList) {
				
				double push = STATS_CONFIG.fireballSettings.push;
				collided.addVelocity(entity.motionX * push, 0.4 * push, entity.motionZ * push);
				collided.attackEntityFrom(AvatarDamageSource.causeFireDamage(collided, entity.getOwner()),
						STATS_CONFIG.fireballSettings.damage * entity.getDamageMult());
				collided.setFire(3);
				
				if (!entity.world.isRemote) {
					BendingData data = Bender.get(entity.getOwner()).getData();
					if (data != null) {
						data.getAbilityData(AbilityFireArc.ID)
								.addXp(ConfigSkills.SKILLS_CONFIG.fireHit);
					}
				}
				
			}
			
			if (!collidedList.isEmpty() && entity.getOwner() != null) {
				BendingData data = BendingData.get(entity.getOwner());
				AbilityData abilityData = data.getAbilityData(AbilityFireArc.ID);
				if (abilityData.isMasterPath(AbilityTreePath.SECOND)) {
					data.addStatusControl(StatusControl.THROW_FIRE);
					return new FireArcBehavior.PlayerControlled();
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
	
	public static class Idle extends FireArcBehavior {
		
		@Override
		public FireArcBehavior onUpdate(EntityFireArc entity) {
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
