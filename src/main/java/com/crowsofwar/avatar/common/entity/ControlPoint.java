package com.crowsofwar.avatar.common.entity;

import java.util.List;

import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * A control point in an arc.
 * <p>
 * An arc is made up of multiple control points. This allows the arc to twist and turn. Segments are
 * drawn in-between control points, which creates a blocky arc.
 * 
 * @author CrowsOfWar
 */
public class ControlPoint {
	
	private static int nextId = 1;
	
	protected EntityArc arc;
	protected EntityPlayer owner;
	
	private final Vector internalVelocity;
	private final Vector internalPosition;
	
	private final World world;
	private AxisAlignedBB hitbox;
	
	private final float size;
	
	public ControlPoint(EntityArc arc, float size, double x, double y, double z) {
		internalPosition = new Vector();
		internalVelocity = new Vector();
		this.arc = arc;
		this.world = arc.worldObj;
		this.size = size;
	}
	
	/**
	 * Get the velocity of this entity in m/s. Changes to this vector will be reflected in the
	 * entity's actual velocity.
	 */
	public Vector velocity() {
		return internalVelocity;
	}
	
	/**
	 * Get the position of this entity. Changes to this vector will be reflected in the entity's
	 * actual position.
	 */
	public Vector position() {
		return internalPosition;
	}
	
	public void update() {
		
		double sizeHalfed = size / 2;
		hitbox = new AxisAlignedBB(position().x() - sizeHalfed, position().y() - sizeHalfed,
				position().z() - sizeHalfed, position().x() + sizeHalfed, position().y() + sizeHalfed,
				position().z() + sizeHalfed);
		
		position().add(velocity().times(0.05));
		velocity().mul(0.4);
		
		List<Entity> collisions = world.getEntitiesWithinAABBExcludingEntity(arc, hitbox);
		if (!collisions.isEmpty()) {
			Entity collidedWith = collisions.get(0);
			onCollision(collisions.get(0));
		}
		
	}
	
	/**
	 * Called whenever the control point is in contact with this entity.
	 */
	protected void onCollision(Entity entity) {}
	
	/**
	 * @deprecated Use {@link #position()}.{@link Vector#set(Vector) set(pos)}
	 */
	@Deprecated
	public void setVecPosition(Vector pos) {
		position().set(pos);
	}
	
	/**
	 * Move this control point by the designated offset, not checking for collisions.
	 * <p>
	 * Not to be confused with {@link Entity#moveEntity(double, double, double)}.
	 */
	public void move(double x, double y, double z) {
		position().add(x, y, z);
	}
	
	/**
	 * Move this control point by the designated offset, not checking for collisions.
	 * <p>
	 * Not to be confused with {@link Entity#moveEntity(double, double, double)}.
	 */
	public void move(Vector offset) {
		move(offset.x(), offset.y(), offset.z());
	}
	
	public double getXPos() {
		return position().x();
	}
	
	public double getYPos() {
		return position().y();
	}
	
	public double getZPos() {
		return position().z();
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
	
	public EntityPlayer getOwner() {
		return owner;
	}
	
	public void setOwner(EntityPlayer owner) {
		this.owner = owner;
	}
	
	/**
	 * "Attach" the arc to this control point, meaning that the control point now has a reference to
	 * the given arc.
	 */
	public void setArc(EntityArc arc) {
		this.arc = arc;
	}
	
}
