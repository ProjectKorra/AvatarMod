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

import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class EntityArc<T extends ControlPoint> extends EntityOffensive {

	private static final DataParameter<Integer> SYNC_POINTS = EntityDataManager.createKey(EntityArc.class,
			DataSerializers.VARINT);

	public List<T> points;
	private int brightness = 15728880;

	public EntityArc(World world) {
		super(world);
		float size = .2f;
		setSize(size, size);

		this.points = new ArrayList<>();
		for (int i = 0; i < getAmountOfControlPoints(); i++) {
			points.add(createControlPoint(size, i));
		}

	}

	public void setNumberofPoints(int points) {
		dataManager.set(SYNC_POINTS, points);
	}

	public int getNumberofPoints() {
		return dataManager.get(SYNC_POINTS);
	}

	/**
	 * Called from the EntityArc constructor to get a new control point
	 * entity.
	 */
	protected T createControlPoint(float size, int index) {
		return (T) new ControlPoint(this, size, 0, 0, 0);
	}

	/**
	 * Get a callback which is called when the owner is changed.
	 */
	protected Consumer<EntityLivingBase> getNewOwnerCallback() {
		return newOwner -> {
		};
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.ticksExisted == 1) {
			for (T point : points) {
				point.setPosition(position());
			}
		}
		if (!this.world.isDaytime()) {
			brightness = 500;
		} else brightness = 15728880;

		ignoreFrustumCheck = true;

		updateControlPoints();

	}

	private void updateControlPoints() {
	/*	if (points.size() < getAmountOfControlPoints()) {
			int i = points.size();
			for (; i < getAmountOfControlPoints(); i++) {
				points.add(createControlPoint(getAvgSize(), i));
			}
		}
		else if (points.size() > getAmountOfControlPoints()) {
			if (points.size() > getAmountOfControlPoints()) {
				points.subList(getAmountOfControlPoints(), points.size()).clear();
			}
		}**/

		updateCpBehavior();

		// Update velocity
		for (T cp : points) {
			cp.onUpdate();
		}



	}

	protected void updateCpBehavior() {

		getLeader().setPosition(position().plusY(height / 2));
		getLeader().setVelocity(velocity());

		// Move control points to follow leader

		for (int i = 1; i < points.size(); i++) {

			ControlPoint leader = points.get(i - 1);
			ControlPoint p = points.get(i);
			Vector leadPos = leader.position();
			double sqrDist = p.position().sqrDist(leadPos);

			if (sqrDist > getControlPointTeleportDistanceSq() && getControlPointTeleportDistanceSq() != -1) {

				Vector toFollowerDir = p.position().minus(leader.position()).normalize();

				double idealDist = Math.sqrt(getControlPointTeleportDistanceSq());
				if (idealDist > 1) idealDist -= 1; // Make sure there is some room

				Vector revisedOffset = leader.position().plus(toFollowerDir.times(idealDist));
				p.setPosition(revisedOffset);
				leader.setPosition(revisedOffset);
				p.setVelocity(Vector.ZERO);

			} else if (sqrDist > getControlPointMaxDistanceSq() && getControlPointMaxDistanceSq() != -1) {

				Vector diff = leader.position().minus(p.position());
				diff = diff.normalize().times(getVelocityMultiplier());
				p.setVelocity(p.velocity().plus(diff));

			}

		}

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		//setNumberofPoints(nbt.getInteger("Points"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		//nbt.setInteger("Points", getNumberofPoints());
	}

	@Override
	public void setPositionAndUpdate(double x, double y, double z) {
		super.setPositionAndUpdate(x, y, z);
		// Set position - called from entity constructor, so points might be
		// null
		if (points != null) {
			getLeader().setPosition(new Vector(x, y, z));
		}
	}

	public List<T> getControlPoints() {
		return points;
	}

	public T getControlPoint(int index) {
		return points.get(index);
	}

	public T getLeader() {
		return getControlPoint(0);
	}

	/**
	 * Get the leader of the specified control point.
	 */
	public T getLeader(int index) {
		return getControlPoint(index == 0 ? index : index - 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double d) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender() {
		return brightness;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_POINTS, 6);
	}

	@Override
	public void setOwner(EntityLivingBase owner) {
		super.setOwner(owner);
		for (T cp : points) {
			cp.setOwner(owner);
		}
	}

	@Override
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return true;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return true;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	/**
	 * Returns the amount of control points which will be created.
	 */
	public int getAmountOfControlPoints() {
		return getNumberofPoints();
	}

	/**
	 * Returns the maximum distance between control points, squared. Any control
	 * points beyond this distance will follow their leader to get closer.
	 * Set to -1 for it to be infinite.
	 */
	protected double getControlPointMaxDistanceSq() {
		return 1;
	}

	/**
	 * Returns the distance between control points to be teleported to their
	 * leader, squared. If any control point is more than this distance from its
	 * leader, then it is teleported to the leader.
	 * Set to -1 for it never to teleport.
	 */
	protected double getControlPointTeleportDistanceSq() {
		return 16;
	}

	protected double getVelocityMultiplier() {
		return 4;
	}

}
