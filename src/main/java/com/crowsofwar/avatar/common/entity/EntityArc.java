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

import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class EntityArc<T extends ControlPoint> extends AvatarEntity {

	private List<T> points;

	public EntityArc(World world) {
		super(world);
		float size = .2f;
		setSize(size, size);

		this.points = new ArrayList<>();
		for (int i = 0; i < getAmountOfControlPoints(); i++) {
			points.add(createControlPoint(size, i));
		}

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

		ignoreFrustumCheck = true;

		updateControlPoints();

	}

	private void updateControlPoints() {

		updateCpBehavior();

		// Update velocity
		for (T cp : points) {
			cp.onUpdate();
		}

	}

	protected void updateCpBehavior() {

		getLeader().setPosition(position());
		getLeader().setVelocity(velocity());

		// Move control points to follow leader

		for (int i = 1; i < points.size(); i++) {

			ControlPoint leader = points.get(i - 1);
			ControlPoint p = points.get(i);
			Vector leadPos = leader.position();
			double sqrDist = p.position().sqrDist(leadPos);

			if (sqrDist > getControlPointTeleportDistanceSq()) {

				Vector toFollowerDir = p.position().minus(leader.position()).normalize();

				double idealDist = Math.sqrt(getControlPointTeleportDistanceSq());
				if (idealDist > 1) idealDist -= 1; // Make sure there is some
				// room

				Vector revisedOffset = leader.position().plus(toFollowerDir.times(idealDist));
				p.setPosition(revisedOffset);
				leader.setPosition(revisedOffset);
				p.setVelocity(Vector.ZERO);

			} else if (sqrDist > getControlPointMaxDistanceSq()) {

				Vector diff = leader.position().minus(p.position());
				diff = diff.normalize().times(3);
				p.setVelocity(p.velocity().plus(diff));

			}

		}

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
	}

	@Override
	public void setPosition(double x, double y, double z) {
		super.setPosition(x, y, z);
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
		return 15728880;
	}

	@Override
	public void setOwner(EntityLivingBase owner) {
		super.setOwner(owner);
		for (T cp : points) {
			cp.setOwner(owner);
		}
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	/**
	 * Returns the amount of control points which will be created.
	 */
	public int getAmountOfControlPoints() {
		return 5;
	}

	/**
	 * Returns the maximum distance between control points, squared. Any control
	 * points beyond this distance will follow their leader to get closer.
	 */
	protected double getControlPointMaxDistanceSq() {
		return 1;
	}

	/**
	 * Returns the distance between control points to be teleported to their
	 * leader, squared. If any control point is more than this distance from its
	 * leader, then it is teleported to the leader.
	 */
	protected double getControlPointTeleportDistanceSq() {
		return 16;
	}

}
