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
package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

/**
 * An AvatarEntity that acts as a shield for further attacks. It has a certain amount of health
 * and absorbs damage until the health is removed. The shield remains attached to the player and
 * follows them wherever they go.
 *
 * @author CrowsOfWar
 */
public abstract class EntityShield extends AvatarEntity implements ICustomHitbox {

	public static final DataParameter<Float> SYNC_HEALTH = EntityDataManager.createKey(EntityShield.class,
			DataSerializers.FLOAT);
	public static final DataParameter<Float> SYNC_MAX_HEALTH = EntityDataManager
			.createKey(EntityShield.class, DataSerializers.FLOAT);
	public static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityShield.class,
			DataSerializers.FLOAT);

	/**
	 * Shields do not protect against some types of damage, such as falling.
	 */
	public static final List<String> UNPROTECTED_DAMAGE = Arrays.asList("fall", "magic", "poison",
			"wither", "indirectMagic");

	public EntityShield(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_HEALTH, 20f);
		dataManager.register(SYNC_MAX_HEALTH, 20f);
		dataManager.register(SYNC_SIZE, 2.0F);
		this.putsOutFires = true;
	}


	/**
	 * Returns true if the given bounding box is completely inside this forcefield (the surface counts as outside).
	 */
	public boolean contains(AxisAlignedBB box) {
		return Arrays.stream(AvatarEntityUtils.getVertices(box)).allMatch(this::contains);
	}

	/**
	 * Returns true if the given entity is completely inside this forcefield (the surface counts as outside).
	 */
	public boolean contains(Entity entity) {
		return contains(entity.getEntityBoundingBox());
	}

	@Override
	public Vec3d calculateIntercept(Vec3d origin, Vec3d endpoint, float fuzziness) {

		// We want the intercept between the line and a sphere
		// First we need to find the point where the line is closest to the centre
		// Then we can use a bit of geometry to find the intercept

		// Find the closest point to the centre
		// http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
		Vec3d line = endpoint.subtract(origin);
		double t = -origin.subtract(this.getPositionVector()).dotProduct(line) / line.lengthSquared();
		Vec3d closestPoint = origin.add(line.scale(t));
		// Now calculate the distance from that point to the centre (squared because that's all we need)
		double dsquared = closestPoint.squareDistanceTo(this.getPositionVector());
		double rsquared = Math.pow(getSize() + fuzziness, 2);
		// If the minimum distance is outside the radius (plus fuzziness) then there is no intercept
		if (dsquared > rsquared) return null;
		// Now do pythagoras to find the other side of the triangle, which is the distance along the line from
		// the closest point to the edge of the sphere, and go that far back towards the origin - and that's it!
		return closestPoint.subtract(line.normalize().scale(MathHelper.sqrt(rsquared - dsquared)));
	}

	@Override
	public boolean contains(Vec3d point) {
		return point.distanceTo(AvatarEntityUtils.getMiddleOfEntity(this)) <= getSize();
	}

	@Override
	public EntityLivingBase getController() {
		return getOwner();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (getOwner() != null)
			setPosition(AvatarEntityUtils.getBottomMiddleOfEntity(getOwner()));
		this.motionX = this.motionY = this.motionZ = 0;

		EntityLivingBase owner = getOwner();
		if (owner == null) {
			setDead();
			return;
		}

		if (owner.isBurning()) {
			owner.extinguish();
		}

		setSize(getSize(), getSize());

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setHealth(nbt.getFloat("Health"));
		setMaxHealth(nbt.getFloat("MaxHealth"));
		assert getOwner() != null;
		setPosition(Vector.getEntityPos(getOwner()));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Health", getHealth());
		nbt.setFloat("MaxHealth", getMaxHealth());
	}

	@Override
	public void setPositionAndUpdate(double x, double y, double z) {
		if (getOwner() != null) {
			Vec3d pos = AvatarEntityUtils.getBottomMiddleOfEntity(getOwner());
			x = pos.x;
			y = pos.y;
			z = pos.z;
			super.setPositionAndUpdate(x, y, z);
		} else
			super.setPositionAndUpdate(x, y, z);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {

		EntityLivingBase owner = getOwner();
		if (owner != null) {

			if (!world.isRemote) {

				if (!UNPROTECTED_DAMAGE.contains(source.getDamageType())) {
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

		} else return true;

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

	public float getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(float size) {
		dataManager.set(SYNC_SIZE, size);
	}

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
