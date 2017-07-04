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

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityOstrichHorse extends EntityAnimal {
	
	/**
	 * @param world
	 */
	public EntityOstrichHorse(World world) {
		super(world);
		setSize(1, 2);
	}
	
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return new EntityOstrichHorse(worldObj);
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
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2);
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
		return getControllingPassenger() != null;
	}
	
	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
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
				
				forward = driver.moveForward;
				strafe = driver.moveStrafing * 0.3f;
				
				setAIMoveSpeed(
						(float) getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()
								* 1.5f);
				super.moveEntityWithHeading(strafe, forward);
				
			} else {
				this.motionX = 0.0D;
				this.motionY = 0.0D;
				this.motionZ = 0.0D;
			}
			
			this.prevLimbSwingAmount = this.limbSwingAmount;
			double d1 = this.posX - this.prevPosX;
			double d0 = this.posZ - this.prevPosZ;
			float f1 = MathHelper.sqrt_double(d1 * d1 + d0 * d0) * 4.0F;
			
			if (f1 > 1.0F) {
				f1 = 1.0F;
			}
			
			this.limbSwingAmount += (f1 - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		} else {
			this.stepHeight = 0.5F;
			this.jumpMovementFactor = 0.02F;
			super.moveEntityWithHeading(strafe, forward);
		}
	}
	
}
