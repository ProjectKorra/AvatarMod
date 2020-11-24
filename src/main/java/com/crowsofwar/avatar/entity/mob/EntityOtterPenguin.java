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
package com.crowsofwar.avatar.entity.mob;

import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Set;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

/**
 * @author CrowsOfWar
 */
public class EntityOtterPenguin extends EntityAnimal {

	public static final ResourceLocation LOOT_TABLE = LootTableList
			.register(new ResourceLocation("avatarmod", "otterpenguin"));

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

		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanic(this, 1.3D));
		this.tasks.addTask(3, new EntityAIMate(this, 1.25D));
		this.tasks.addTask(4, new EntityAITempt(this, 1.0D, false, temptItems));
		this.tasks.addTask(5, new EntityAIFollowParent(this, 1.25D));
		this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
	}

	/**
	 * Checking speed + setting up this.isSprinting() to use for animation purposes
	 * @author Mnesikos
	 */
	@Override
	public void updateAITasks() {
		if (this.getMoveHelper().isUpdating()) {
			double d0 = this.getMoveHelper().getSpeed();

			if (d0 >= 1.25D) {
				this.setSprinting(true);
			} else {
				this.setSprinting(false);
			}
		} else {
			this.setSprinting(false);
		}
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
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
			if (!isBreedingItem(player.getHeldItemMainhand()) && !isBreedingItem(player.getHeldItemOffhand())
					&& !player.isSneaking() && !this.isChild()) {
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
	 * @author Mnesikos
	 */
	@Override
	public void updatePassenger(Entity passenger) {
		if (this.isPassenger(passenger)) {
			double offset = -0.5;
			double angle = -toRadians(rotationYaw);
			passenger.setPosition(this.posX + sin(angle) * offset, this.posY + passenger.getYOffset() + 0.2, this.posZ + cos(angle) * offset);
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

		if (this.isBeingRidden() && this.canBeSteered()) {
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
				strafe = driver.moveStrafing;

				setAIMoveSpeed((float) getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
						.getAttributeValue());
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
			super.travel(strafe, jump, forward);
		}
	}

}
