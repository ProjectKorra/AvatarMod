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
package com.crowsofwar.avatar.common.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityOstrichHorse extends EntityAnimal {
	
	private static final DataParameter<Float> SYNC_RIDE_SPEED = EntityDataManager
			.createKey(EntityOstrichHorse.class, DataSerializers.FLOAT);
	
	/**
	 * @param world
	 */
	public EntityOstrichHorse(World world) {
		super(world);
		setSize(1, 2);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_RIDE_SPEED, 0f);
	}
	
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return new EntityOstrichHorse(world);
	}
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 0.3));
		this.tasks.addTask(2, new EntityAIMate(this, 0.1));
		this.tasks.addTask(3, new EntityAITempt(this, 1, Items.WHEAT, false));
		this.tasks.addTask(4, new EntityAIFollowParent(this, 0.25));
		this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 10));
		this.tasks.addTask(7, new EntityAILookIdle(this));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (!super.processInteract(player, hand)) {
			player.startRiding(this);
			return true;
		}
		return false;
	}
	
	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return !getPassengers().isEmpty() ? getPassengers().get(0) : null;
	}
	
	@Override
	public boolean canBeSteered() {
		return getControllingPassenger() instanceof  EntityLivingBase;
	}
	
	@Override
	public void travel(float strafe, float jump, float forward) {
		EntityLivingBase driver = (EntityLivingBase) getControllingPassenger();

		if (isBeingRidden() && canBeSteered()) {
			this.rotationYaw = driver.rotationYaw;
			this.prevRotationYaw = this.rotationYaw;
			this.rotationPitch = driver.rotationPitch * 0.5F;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.renderYawOffset = this.rotationYaw;
			this.rotationYawHead = this.rotationYaw;
			this.stepHeight = 1.0F;
			this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
			
			if (this.canPassengerSteer()) {

				updateRideSpeed(driver.moveForward);
				setAIMoveSpeed(getRideSpeed());

//				forward = getRideSpeed() > 0 ? 0.98f : 0;
//				strafe = driver.moveStrafing * 0.3f;

				super.travel(strafe, jump, forward);
				
			} else {
				this.motionX = 0.0D;
				this.motionY = 0.0D;
				this.motionZ = 0.0D;
			}
			
			this.prevLimbSwingAmount = this.limbSwingAmount;
			double d1 = this.posX - this.prevPosX;
			double d0 = this.posZ - this.prevPosZ;
			float f1 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
			
			if (f1 > 1.0F) {
				f1 = 1.0F;
			}
			
			this.limbSwingAmount += (f1 - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		} else {
			this.stepHeight = 0.5F;
			this.jumpMovementFactor = 0.02F;

			// Slow down ride speed
			float rideSpeed = getRideSpeed();
			if (rideSpeed > 0) {
//				unknown = rideSpeed;
				rideSpeed -= 0.006f;
				setRideSpeed(Math.max(rideSpeed, 0));
				setAIMoveSpeed(getRideSpeed());
				forward = getRideSpeed() * 10;
				System.out.println(forward + "");
			}

			super.travel(strafe, jump, forward);
		}
	}
	
	/**
	 * Get ride speed.
	 * <p>
	 * Ride speed represents the current speed of the ostrich when being ridden.
	 * When the ostrich starts running, it's slow but speeds up as it continues
	 * running. Then when the driver tells it to stop, the ostrich doesn't stop
	 * immediately, and instead slows down to a stop. Ride speed represents the
	 * ostrich's current speed, which is influenced by the driver's directions.
	 */
	private float getRideSpeed() {
		return dataManager.get(SYNC_RIDE_SPEED);
	}
	
	private void setRideSpeed(float rideSpeed) {
		dataManager.set(SYNC_RIDE_SPEED, rideSpeed);
	}
	
	/**
	 * Assuming that the ostrich is currently ridden, update ride speed to the
	 * driver's specifications
	 * 
	 * @see #getRideSpeed()
	 * @param instructions
	 *            Positive if the ostrich should move faster, negative if the
	 *            ostrich should move slower, zero to stay the same
	 */
	private void updateRideSpeed(float instructions) {
		
		float moveSpeedAttr = (float) getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
				.getAttributeValue();
		float current = getRideSpeed();
		float target = moveSpeedAttr * 1.5f;
		float next = current;
		
		if (instructions > 0) {
			
			// Move faster!
			if (current < target) {
				next += moveSpeedAttr * 0.02f;
			}
			
		}
		if (instructions < 0) {
			
			// Move slower!
			if (current > 0) {
				next -= moveSpeedAttr * 0.03f;
			}
			
		}
		if (instructions == 0) {
			
			// Don't move very fast
			target = moveSpeedAttr * 0.5f;
			
		}
		
		// Update to dataManager
		if (next < 0) {
			next = 0;
		}
		if (next > target) {
			next += (target - next) * 0.05f;
		}
		setRideSpeed(next);
		
	}
	
}
