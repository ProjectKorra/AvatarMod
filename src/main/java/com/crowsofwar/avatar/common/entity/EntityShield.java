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
package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

/**
 * An AvatarEntity that acts as a shield for further attacks. It has a certain amount of health
 * and absorbs damage until the health is removed. The shield remains attached to the player and
 * follows them wherever they go.
 *
 * @author CrowsOfWar
 */
public abstract class EntityShield extends AvatarEntity {

	public static final DataParameter<Float> SYNC_HEALTH = EntityDataManager.createKey(EntityShield.class,
			DataSerializers.FLOAT);
	public static final DataParameter<Float> SYNC_MAX_HEALTH = EntityDataManager
			.createKey(EntityShield.class, DataSerializers.FLOAT);

	public EntityShield(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_HEALTH, 20f);
		dataManager.register(SYNC_MAX_HEALTH, 20f);
	}

	@Override
	public EntityLivingBase getController() {
		return getOwner();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		EntityLivingBase owner = getOwner();
		if (owner == null) {
			setDead();
			return;
		}

		if (owner.isBurning()) {
			owner.extinguish();
		}

	}

	/**
	 * Handles hovering logic to make the owner hover. Preconditions (not in water, owner
	 * present, etc) are handled by the caller
	 */
	private void handleHovering() {

		if (getOwner() != null) {
			getOwner().fallDistance = 0;
		}

		// Min/max acceptable hovering distance
		// Hovering is allowed between these two values
		// Hover distance doesn't need to be EXACT
		final double minFloatHeight = 1.8;
		final double maxFloatHeight = 2.2;

		EntityLivingBase owner = getOwner();

		// Find whether there are blocks under the owner
		// Done by making a hitbox around the owner's feet and checking if there are blocks
		// colliding with that hitbox

		double x = owner.posX;
		double y = owner.posY;
		double z = owner.posZ;
		AxisAlignedBB hitbox = new AxisAlignedBB(x, y, z, x, y, z);
		hitbox = hitbox.grow(0.2, 0, 0.2);
		hitbox = hitbox.expand(0, -maxFloatHeight, 0);

		List<AxisAlignedBB> blockCollisions = world.getCollisionBoxes(null, hitbox);

		if (!blockCollisions.isEmpty()) {

			// Calculate the top-of-ground ground y position
			// Performed by finding the maximum ypos of each collided block
			double groundPosition = Double.MIN_VALUE;
			for (AxisAlignedBB blockHitbox : blockCollisions) {
				if (blockHitbox.maxY > groundPosition) {
					groundPosition = blockHitbox.maxY;
				}
			}
			// Now calculate the distance from ground
			// and use that to determine whether owner should float
			double distanceFromGround = owner.posY - groundPosition;

			// Tweak motion based on distance to ground, and target distance
			// Minecraft gravity is 0.08 blocks/tick

			if (distanceFromGround < minFloatHeight) {
				owner.motionY += 0.11;
			}
			if (distanceFromGround >= minFloatHeight && distanceFromGround < maxFloatHeight) {
				owner.motionY *= 0.7;
			}
			if (distanceFromGround >= maxFloatHeight) {
				owner.motionY += 0.07;

				// Avoid falling at over 3 m/s
				if (owner.motionY < -3.0 / 20) {
					owner.motionY = 0;
				}
			}

		}

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setHealth(nbt.getFloat("Health"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Health", getHealth());
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {

		EntityLivingBase owner = getOwner();
		if (owner != null) {

			if (!world.isRemote) {
				Entity sourceEntity = source.getTrueSource();
				if (sourceEntity != null) {
					if (!owner.isEntityInvulnerable(source)) {

						Bender bender = Bender.get(owner);
						BendingData data = bender.getData();
						if (bender.consumeChi(getChiDamageCost() * amount)) {

							AbilityData aData = data.getAbilityData(getAbilityName());
							aData.addXp(getProtectionXp());
							setHealth(getHealth() - amount);
							return true;

						} else {
							return true;
						}

					}
				}
			}

		} else {
			return true;
		}
		return false;

	}

	/**
	 * Returns the amount of chi to take per unit of damage taken (per half heart).
	 */
	protected abstract float getChiDamageCost();

	/**
	 * Returns the amount of XP to add when an attack was defended.
	 */
	protected abstract float getProtectionXp();

	/**
	 * Gets the name of the corresponding ability
	 */
	protected abstract String getAbilityName();

	/**
	 * Called when the health reaches zero.
	 */
	protected abstract void onDeath();

	@Override
	public boolean isShield() {
		return true;
	}

	public float getHealth() {
		return dataManager.get(SYNC_HEALTH);
	}

	public void setHealth(float health) {
		dataManager.set(SYNC_HEALTH, health);
		if (health <= 0) onDeath();
		if (health > getMaxHealth()) health = getMaxHealth();
	}

	public float getMaxHealth() {
		return dataManager.get(SYNC_MAX_HEALTH);
	}

	public void setMaxHealth(float health) {
		dataManager.set(SYNC_MAX_HEALTH, health);
	}

}
