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

import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import com.google.common.collect.Sets;

import java.util.Set;

import static java.lang.Math.*;

/**
 * @author CrowsOfWar
 */
public class EntityOtterPenguin extends EntityAnimal {

	public static final ResourceLocation LOOT_TABLE = LootTableList.register(new ResourceLocation("avatarmod", "otterpenguin"));

	/**
	 * @param world
	 */
	public EntityOtterPenguin(World world) {
		super(world);
		setSize(0.7f, 1.6f);
	}

	@Override
	protected void initEntityAI() {
		Set<Item> temptItems = Sets.newHashSet(Items.FISH);

		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIPanic(this, 1.3D));
		tasks.addTask(3, new EntityAIMate(this, 1.25D));
		tasks.addTask(4, new EntityAITempt(this, 1.0D, false, temptItems));
		tasks.addTask(5, new EntityAIFollowParent(this, 1.25D));
		tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
		tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
	}

	/**
	 * Checking speed + setting up this.isSprinting() to use for animation purposes
	 *
	 * @author Mnesikos
	 */
	@Override
	public void updateAITasks() {
		if (getMoveHelper().isUpdating()) {
			double d0 = getMoveHelper().getSpeed();

			if (d0 >= 1.25D) {
				setSprinting(true);
			} else {
				setSprinting(false);
			}
		} else {
			setSprinting(false);
		}
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return new EntityOtterPenguin(world);
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (!super.processInteract(player, hand) && !world.isRemote) {
			if (!isBreedingItem(player.getHeldItemMainhand()) && !isBreedingItem(player.getHeldItemOffhand()) && !player.isSneaking() && !isChild()) {
				player.startRiding(this);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem() == Items.FISH;
	}

	@Override
	public Entity getControllingPassenger() {
		return getPassengers().isEmpty() ? null : getPassengers().get(0);
	}

	/**
	 * Adjusts the rider's position, math borrowed from EntitySkyBison#updatePassenger
	 *
	 * @author Mnesikos
	 */
	@Override
	public void updatePassenger(Entity passenger) {
		if (isPassenger(passenger)) {
			double offset = -0.5;
			double angle = -toRadians(rotationYaw);
			passenger.setPosition(posX + sin(angle) * offset, posY + passenger.getYOffset() + 0.2, posZ + cos(angle) * offset);
		}
	}

	@Override
	public boolean canBeSteered() {
		return getControllingPassenger() instanceof EntityLivingBase;
	}

	@Override
	public boolean canPassengerSteer() {
		return super.canPassengerSteer();
	}

	// moveWithHeading
	@Override
	public void travel(float strafe, float jump, float forward) {
		EntityLivingBase driver = (EntityLivingBase) getControllingPassenger();

		if (isBeingRidden() && canBeSteered()) {
			rotationYaw = driver.rotationYaw;
			prevRotationYaw = rotationYaw;
			rotationPitch = driver.rotationPitch * 0.5F;
			setRotation(rotationYaw, rotationPitch);
			renderYawOffset = rotationYaw;
			rotationYawHead = rotationYaw;
			stepHeight = 1.0F;
			jumpMovementFactor = getAIMoveSpeed() * 0.1F;

			if (canPassengerSteer()) {

				forward = driver.moveForward;
				strafe = driver.moveStrafing;

				setAIMoveSpeed((float) getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				super.travel(strafe, jump, forward);

			} else {
				motionX = 0.0D;
				motionY = 0.0D;
				motionZ = 0.0D;
			}

			prevLimbSwingAmount = limbSwingAmount;
			double d1 = posX - prevPosX;
			double d0 = posZ - prevPosZ;
			float f1 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

			if (f1 > 1.0F) {
				f1 = 1.0F;
			}

			limbSwingAmount += (f1 - limbSwingAmount) * 0.4F;
			limbSwing += limbSwingAmount;
		} else {
			stepHeight = 0.5F;
			jumpMovementFactor = 0.02F;
			super.travel(strafe, jump, forward);
		}
	}

}
