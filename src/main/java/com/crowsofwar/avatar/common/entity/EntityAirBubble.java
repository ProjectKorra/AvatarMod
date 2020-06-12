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

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.StatusControlController;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;

/**
 * @author CrowsOfWar
 */
public class EntityAirBubble extends EntityShield {

	public static final DataParameter<Integer> SYNC_DISSIPATE = EntityDataManager
			.createKey(EntityAirBubble.class, DataSerializers.VARINT);
	public static final DataParameter<Boolean> SYNC_HOVERING = EntityDataManager
			.createKey(EntityAirBubble.class, DataSerializers.BOOLEAN);
	public static final UUID SLOW_ATTR_ID = UUID.fromString("40354c68-6e88-4415-8a6b-e3ddc56d6f50");
	public static final AttributeModifier SLOW_ATTR = new AttributeModifier(SLOW_ATTR_ID,
			"airbubble_slowness", -.3, 2);

	private int airLeft;

	public EntityAirBubble(World world) {
		super(world);
		setSize(2.5f, 2.5f);

		this.noClip = true;
		this.airLeft = 600;
		this.putsOutFires = true;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_DISSIPATE, 0);
		dataManager.register(SYNC_HOVERING, false);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		assert getOwner() != null;
		setPosition(getEntityPos(getOwner()));
	}


	@Override
	public BendingStyle getElement() {
		return new Airbending();
	}

	@Override
	public EntityLivingBase getController() {
		return !isDissipating() ? getOwner() : null;
	}

	public boolean doesAllowHovering() {
		return dataManager.get(SYNC_HOVERING);
	}

	public void setAllowHovering(boolean floating) {
		dataManager.set(SYNC_HOVERING, floating);
	}


	@Override
	public boolean canBeCollidedWith() {
		return true;
	}


	@Override
	public boolean isPushedByWater() {
		return false;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		EntityLivingBase owner = getOwner();
		if (owner == null && !isDissipating()) {
			this.setDead();
			removeStatCtrl();
		}

		if (owner == null) {
			return;
		}

		if (owner.isDead) {
			dissipateSmall();
			removeStatCtrl();
			return;
		}

		setPosition(AvatarEntityUtils.getBottomMiddleOfEntity(getOwner()));
		this.motionX = this.motionY = this.motionZ = 0;

		//	System.out.println("Pitch: " + getOwner().rotationPitch);
		//	System.out.println("Yaw: " + getOwner().rotationYaw);
		//Particles go spin!
		if (world.isRemote && getOwner() != null) {
			for (int i = 0; i < 10; i++) {
				double x1, y1, z1, xVel, yVel, zVel;
				double theta = (ticksExisted % 180) * 10 + i * 36;
				double dphi = 30 / Math.sin(Math.toRadians(theta));
				double phi = ((ticksExisted % 360) * dphi) + i * 36;
				double rphi = Math.toRadians(phi);
				double rtheta = Math.toRadians(theta);

				x1 = getSize() * Math.cos(rphi) * Math.sin(rtheta);
				y1 = getSize() * Math.sin(rphi) * Math.sin(rtheta);
				z1 = getSize() * Math.cos(rtheta);
				xVel = x1 * world.rand.nextGaussian() / 200;
				yVel = y1 * world.rand.nextGaussian() / 200;
				zVel = z1 * world.rand.nextGaussian() / 200;

				Vec3d centre = AvatarEntityUtils.getMiddleOfEntity(this);
				double x = centre.x;
				double y = centre.y;
				double z = centre.z;

				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1 + x, y1 + y, z1 +z).vel(xVel, yVel, zVel)
						.clr(0.95F, 0.95F, 0.95F, 0.1F).time(15 + AvatarUtils.getRandomNumberInRange(0, 10)).spawnEntity(getOwner())
						.scale(0.75F * getSize() * (1 / getSize())).element(getElement()).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(x1 + x, y1 + y, z1 + z).vel(xVel, yVel, zVel)
						.clr(0.95F, 0.95F, 0.95F, 0.1F).time(15 + AvatarUtils.getRandomNumberInRange(0, 10)).spawnEntity(getOwner())
						.scale(0.75F * getSize() * (1 / getSize())).element(getElement()).spawn(world);
			}


		}
		if (getOwner() != null) {
			EntityAirBubble bubble = AvatarEntity.lookupControlledEntity(world, EntityAirBubble.class, getOwner());
			BendingData bD = BendingData.get(getOwner());
			if (bubble == null && (bD.hasStatusControl(StatusControlController.BUBBLE_CONTRACT) || bD.hasStatusControl(StatusControlController.BUBBLE_EXPAND))) {
				bD.removeStatusControl(StatusControlController.BUBBLE_CONTRACT);
				bD.removeStatusControl(StatusControlController.BUBBLE_EXPAND);
			}
			if (bubble != null && !(bD.hasStatusControl(StatusControlController.BUBBLE_CONTRACT) || bD.hasStatusControl(StatusControlController.BUBBLE_EXPAND))) {
				bD.addStatusControl(StatusControlController.BUBBLE_CONTRACT);
				bD.addStatusControl(StatusControlController.BUBBLE_EXPAND);
			}
		}

		AxisAlignedBB box = new AxisAlignedBB(getEntityBoundingBox().minX - (getSize() / 4), getEntityBoundingBox().minY - getSize() / 4, getEntityBoundingBox().minZ - (getSize() / 4),
				getEntityBoundingBox().maxX + (getSize() / 4), getEntityBoundingBox().maxY + (getSize() / 4), getEntityBoundingBox().maxZ + (getSize() / 4));
		List<Entity> nearby = world.getEntitiesWithinAABB(Entity.class, box);
		if (!nearby.isEmpty()) {
			for (Entity collided : nearby) {
				if (collided != this && collided != getOwner() && collided.canBePushed()) {
					onCollideWithEntity(collided);
				}
			}
		}

		if (!world.isRemote && owner.isInsideOfMaterial(Material.WATER)) {
			owner.setAir(Math.min(airLeft, 300));
			airLeft--;
		}

		Bender ownerBender = Bender.get(getOwner());
		if (ownerBender != null) {
			if (!world.isRemote && !ownerBender.consumeChi(STATS_CONFIG.chiAirBubbleOneSecond / 20f)) {
				dissipateSmall();

			}

			ItemStack chest = owner.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			boolean elytraOk = (STATS_CONFIG.allowAirBubbleElytra || chest.getItem() != Items.ELYTRA);
			if (!elytraOk) {
				ownerBender.sendMessage("avatar.airBubbleElytra");
				dissipateSmall();
			}

			AvatarEntity a = AvatarEntity.lookupControlledEntity(world, EntityIceShield.class, owner);
			if (!isDissipating() && a == null) {
				IAttributeInstance attribute = owner.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
				if (attribute.getModifier(SLOW_ATTR_ID) == null) {
					attribute.applyModifier(SLOW_ATTR);
				}

				if (!ownerBender.isFlying() && !getOwner().hasNoGravity()) {
					owner.motionY += 0.03;

					if (doesAllowHovering()) {
						if (doesAllowHovering() && !owner.isSneaking()) {
							handleHovering();
						} else if (!owner.isSneaking()) {
							owner.motionY += 0.03;
						}
					}

				}

			}
		}

		float size = getSize();

		if (isDissipatingLarge()) {
			setDissipateTime(getDissipateTime() + 1);
			float mult = 1 + getDissipateTime() / 10f;
			setSize(size * mult, size * mult);
			if (getDissipateTime() >= 10) {
				setDead();
			}
		} else if (isDissipatingSmall()) {
			setDissipateTime(getDissipateTime() - 1);
			float mult = 1 + getDissipateTime() / 40f;
			setSize(size * mult, size * mult);
			if (getDissipateTime() <= -10) {
				setDead();
			}
		} else {
			setSize(size, size);
		}

	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	/**
	 * Handles hovering logic to make the owner hover. Preconditions (not in water, owner
	 * present, etc) are handled by the caller
	 */
	private void handleHovering() {

		if (getOwner() == null) {
			return;
		}
		if (getOwner() != null) {
			getOwner().fallDistance = 0;
		}

		// Min/max acceptable hovering distance
		// Hovering is allowed between these two values
		// Hover distance doesn't need to be EXACT
		final double minFloatHeight = 1.2;
		final double maxFloatHeight = 3;


		EntityLivingBase owner = getOwner();

		// Find whether there are blocks under the owner
		// Done by making a hitbox around the owner's feet and checking if there are blocks
		// colliding with that hitbox
		assert owner != null;
		double x = owner.posX;
		double y = owner.posY;
		double z = owner.posZ;
		//Don't use setPosition; that makes it super duper ultra glitchy
		AxisAlignedBB hitbox = new AxisAlignedBB(x, y, z, x, y, z);
		hitbox = hitbox.grow(0.2, 0, 0.2);
		hitbox = hitbox.expand(0, -maxFloatHeight, 0);

		List<AxisAlignedBB> blockCollisions = world.getCollisionBoxes(null, hitbox);

		BlockPos pos = new BlockPos(getPosition().getX(), getPosition().getY() - maxFloatHeight, getPosition().getZ());
		BlockPos below = pos.offset(EnumFacing.DOWN);
		Block belowBlock = world.getBlockState(below).getBlock();

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
				owner.motionY *= 0.8;
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
	public void setDead() {
		super.setDead();
		removeStatCtrl();
	}

	@Override
	public boolean isShield() {
		return true;
	}


	@Override
	public boolean canPush() {
		return false;
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (entity instanceof AvatarEntity) {
			if (((AvatarEntity) entity).getOwner() != getOwner()) {
				((AvatarEntity) entity).onAirContact();
			}
		}

		if (canCollideWith(entity) && entity != getOwner() && getOwner() != null) {


			Vector velocity = getEntityPos(entity).minus(getEntityPos(getOwner()));
			//Vector that comes from the owner of the air bubble towards the entity being collided with
			double dist = getOwner().getDistance(entity);
			double sizeMult = isDissipatingLarge() ? 4 * (getSize() * 2 / 3) : 2 * (getSize() * 2 / 3);
			double mult = (dist - getSize()) * sizeMult > 1 ? (dist - getSize()) * sizeMult : 1 * sizeMult;
			velocity = velocity.normalize().times(mult).withY(getSize() / 4);

			//The velocity is 20 times the motion of the entity, so you wanna divide by 20, unless you wanna make the entities
			//that have been collided with fly super far way
			entity.addVelocity(velocity.x() / 2, velocity.y(), velocity.z() / 2);

			if (entity instanceof AvatarEntity) {
				AvatarEntity avent = (AvatarEntity) entity;
				avent.setVelocity(velocity);
			}
			entity.isAirBorne = true;
			AvatarUtils.afterVelocityAdded(entity);
		}
	}


	@Override
	public boolean canCollideWith(Entity entity) {
		if (entity instanceof AvatarEntity && ((AvatarEntity) entity).getOwner() == getOwner()) {
			return false;
		} else if (entity.getPassengers().contains(getOwner())) {
			return false;
		} else return entity != getOwner() && !(entity instanceof EntityXPOrb) && !(entity instanceof EntityItem);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDissipateTime(nbt.getInteger("Dissipate"));
		setAllowHovering(nbt.getBoolean("AllowHovering"));
		setSize(nbt.getFloat("Size"));
		airLeft = nbt.getInteger("AirLeft");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("Dissipate", getDissipateTime());
		nbt.setBoolean("AllowHovering", doesAllowHovering());
		nbt.setFloat("Size", getSize());
		nbt.setInteger("AirLeft", airLeft);
	}


	@Override
	protected float getChiDamageCost() {
		return STATS_CONFIG.chiAirBubbleTakeDamage;
	}

	@Override
	protected float getProtectionXp() {
		return SKILLS_CONFIG.airbubbleProtect;
	}

	@Override
	protected String getAbilityName() {
		return "air_bubble";
	}

	@Override
	protected void onDeath() {
		dissipateLarge();
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return true;
	}

	public int getDissipateTime() {
		return dataManager.get(SYNC_DISSIPATE);
	}

	public void setDissipateTime(int dissipate) {
		dataManager.set(SYNC_DISSIPATE, dissipate);
	}

	public void dissipateLarge() {
		if (!isDissipating()) setDissipateTime(1);
		removeStatCtrl();
	}

	public void dissipateSmall() {
		if (!isDissipating()) setDissipateTime(-1);
		removeStatCtrl();
	}

	private boolean isDissipating() {
		return getDissipateTime() != 0;
	}

	public boolean isDissipatingLarge() {
		return getDissipateTime() > 0;
	}

	public boolean isDissipatingSmall() {
		return getDissipateTime() < 0;
	}

	private void removeStatCtrl() {
		if (getOwner() != null) {
			BendingData data = Bender.get(getOwner()).getData();
			data.removeStatusControl(StatusControlController.BUBBLE_EXPAND);
			data.removeStatusControl(StatusControlController.BUBBLE_CONTRACT);

			IAttributeInstance attribute = getOwner()
					.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			if (attribute.getModifier(SLOW_ATTR_ID) != null) {
				attribute.removeModifier(SLOW_ATTR);
			}
		}
	}
}
