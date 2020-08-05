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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * A control point in an arc.
 * <p>
 * An arc is made up of multiple control points. This allows the arc to twist
 * and turn. Segments are drawn in-between control points, which creates a
 * blocky arc.
 *
 * @author CrowsOfWar
 */
public class ControlPoint {

	private final World world;
	protected EntityArc arc;
	protected EntityLivingBase owner;
	protected float size;
	private Vector internalVelocity;
	private Vector internalPosition;
	private Vector lastPos;
	private AxisAlignedBB hitbox;

	public ControlPoint(EntityArc arc, float size, double x, double y, double z) {
		internalPosition = new Vector();
		internalVelocity = new Vector();
		this.arc = arc;
		this.world = arc.world;
		this.size = size;

		double sizeHalfed = size / 2;
		hitbox = new AxisAlignedBB(position().x() - sizeHalfed, position().y() - sizeHalfed,
				position().z() - sizeHalfed, position().x() + sizeHalfed, position().y() + sizeHalfed,
				position().z() + sizeHalfed);

		lastPos = new Vector();

	}

	/**
	 * Get the velocity of this entity in m/s. Changes to this vector will be
	 * reflected in the entity's actual velocity.
	 */
	public Vector velocity() {
		return internalVelocity;
	}

	public void setVelocity(Vector velocity) {
		internalVelocity = velocity;
	}

	/**
	 * Get the position of this entity. Changes to this vector will be reflected
	 * in the entity's actual position.
	 */
	public Vector position() {
		return internalPosition;
	}

	public Vector lastPosition() {
		return lastPos;
	}

	public double x() {
		return position().x();
	}

	public double y() {
		return position().y();
	}

	public double z() {
		return position().z();
	}

	/**
	 * Remove the control point's arc.
	 */
	public void setDead() {
		arc.setDead();
	}

	public AxisAlignedBB getBoundingBox() {
		return hitbox;
	}

	public float size() {
		return size;
	}

	public void onUpdate() {

		double sizeHalfed = size / 2;
		hitbox = new AxisAlignedBB(position().x() - sizeHalfed, position().y() - sizeHalfed,
				position().z() - sizeHalfed, position().x() + sizeHalfed, position().y() + sizeHalfed,
				position().z() + sizeHalfed);

		lastPos = position();
		move(velocity().times(0.05));
		setVelocity(velocity().times(0.4));

	}

	public void setPosition(Vector pos) {
		internalPosition = pos;
	}

	/**
	 * Move this control point by the designated offset, not checking for
	 * collisions.
	 * .
	 */
	public void move(double x, double y, double z) {
		setPosition(position().plus(x, y, z));
	}

	/**
	 * Move this control point by the designated offset, not checking for
	 * collisions.
	 * .
	 */
	public void move(Vector offset) {
		move(offset.x(), offset.y(), offset.z());
	}

	public double getDistance(ControlPoint point) {
		return position().dist(point.position());
	}

	/**
	 * Get the arc that this control point belongs to.
	 *
	 * @return
	 */
	public EntityArc getArc() {
		return arc;
	}

	/**
	 * "Attach" the arc to this control point, meaning that the control point
	 * now has a reference to the given arc.
	 */
	public void setArc(EntityArc arc) {
		this.arc = arc;
	}

	public EntityLivingBase getOwner() {
		return owner;
	}

	public void setOwner(EntityLivingBase owner) {
		this.owner = owner;
	}

	/**
	 * Get the interpolated position between two ticks.
	 *
	 * @param partialTicks The percentage through this tick, from 0..1
	 */
	public Vector getInterpolatedPosition(float partialTicks) {
		//@formatter:on
		return lastPosition().plus((position().minus(lastPosition())).times(partialTicks));
		//@formatter:off
	}

}
