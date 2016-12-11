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

import java.util.List;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.water.WaterbendingState;
import com.crowsofwar.avatar.common.config.ConfigSkills;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;

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
	
	public WaterArcBehavior(EntityWaterArc water) {
		super(water);
	}
	
	protected void applyGravity() {
		entity.velocity().add(0, -9.81 / 50, 0);
	}
	
	public static class PlayerControlled extends WaterArcBehavior {
		
		public PlayerControlled() {}
		
		public PlayerControlled(EntityWaterArc arc, EntityPlayer player) {
			super(arc);
		}
		
		private EntityPlayer getPlayer() {
			return entity.getOwner();
		}
		
		@Override
		public WaterArcBehavior onUpdate() {
			
			EntityPlayer player = getPlayer();
			if (player == null) {
				return this;
			}
			World world = player.worldObj;
			
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
			
			if (data != null) {
				WaterbendingState bendingState = (WaterbendingState) data
						.getBendingState(BendingManager.getBending(BendingType.WATERBENDING));
				
				if (bendingState != null) {
					
					EntityWaterArc water = bendingState.getWaterArc(world);
					if (water != null) {
						
						Raytrace.Result res = Raytrace.getTargetBlock(player, 3, false);
						
						Vector lookPos;
						if (res.hitSomething()) {
							lookPos = res.getPosPrecise();
						} else {
							Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
									Math.toRadians(player.rotationPitch));
							lookPos = Vector.getEyePos(player).plus(look.times(3));
						}
						
						Vector motion = lookPos.minus(new Vector(water));
						motion.mul(.3);
						water.moveEntity(MoverType.SELF, motion.x(), motion.y(), motion.z());
						
						if (water.worldObj.isRemote && water.canPlaySplash()) {
							if (motion.sqrMagnitude() >= 0.004) water.playSplash();
						}
					} else {
						if (!world.isRemote) bendingState.setWaterArc(null);
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
	
	public static class Thrown extends WaterArcBehavior {
		
		public Thrown() {}
		
		public Thrown(EntityWaterArc arc) {
			super(arc);
		}
		
		@Override
		public WaterArcBehavior onUpdate() {
			applyGravity();
			
			List<EntityLivingBase> collidedList = entity.getEntityWorld().getEntitiesWithinAABB(
					EntityLivingBase.class, entity.getEntityBoundingBox().expandXyz(0.9),
					collided -> collided != entity.getOwner());
			
			for (EntityLivingBase collided : collidedList) {
				if (collided != entity.getOwner()) return this;
				collided.addVelocity(entity.motionX, 0.4, entity.motionZ);
				collided.attackEntityFrom(AvatarDamageSource.causeWaterDamage(collided, entity.getOwner()),
						6 * entity.getDamageMult());
				
				if (!entity.worldObj.isRemote) {
					AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(entity.getOwner());
					if (data != null) {
						data.getAbilityData(BendingAbility.ABILITY_WATER_ARC)
								.addXp(ConfigSkills.SKILLS_CONFIG.waterHit);
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
		
		public Idle() {}
		
		public Idle(EntityWaterArc arc) {
			super(arc);
		}
		
		@Override
		public WaterArcBehavior onUpdate() {
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
