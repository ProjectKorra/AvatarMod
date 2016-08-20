package com.crowsofwar.avatar.common.entity;

import java.util.List;

import com.crowsofwar.avatar.common.entityproperty.EntityPropertyMotion;
import com.crowsofwar.avatar.common.entityproperty.IEntityProperty;
import com.crowsofwar.gorecore.util.VectorD;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * A control point in an arc.
 * <p>
 * An arc is made up of multiple control points. This allows the arc to twist and turn. Segments are
 * drawn in-between control points, which creates a blocky arc.
 * <p>
 * CPs exist on both client and server. However, they exist using a different method than normal.
 * The client and server BOTH create arcs and spawn them into the world. (this is required in order
 * to have important CP field - like reference to the arc - not null)
 * <p>
 * This would create duplicate CPs on the client-side. (CPs would be instantiated both from the arc
 * constructor and vanilla spawn packets sent from server). So, CPs will actually ignore the
 * server's spawn packets!
 * <p>
 * They do so by implementing IEntityAdditionalSpawnData. The readSpawnData method, provided by this
 * interface, is called whenever the spawn packet is received. The CP kills itself (via
 * {@link #setDead()}) which effectively ignores the spawn packet.
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityControlPoint extends Entity implements IPhysics, IEntityAdditionalSpawnData {
	
	private static final int DATAWATCHER_VELOCITY = 3; // 3,4,5
	private static final int DATAWATCHER_ID = 6;
	
	private static int nextId = 1;
	
	protected EntityArc arc;
	protected EntityPlayer owner;
	
	private VectorD internalPosition;
	private IEntityProperty<VectorD> internalVelocity;
	
	public EntityControlPoint(World world) {
		super(world);
		internalPosition = new VectorD();
		internalVelocity = new EntityPropertyMotion(this);
		if (!worldObj.isRemote) setId(nextId++);
	}
	
	public EntityControlPoint(EntityArc arc, float size, double x, double y, double z) {
		super(arc.worldObj);
		setPosition(x, y, z);
		this.arc = arc;
		setSize(size, size);
		internalPosition = new VectorD(x, y, z);
		internalVelocity = new EntityPropertyMotion(this);
		if (!worldObj.isRemote) setId(nextId++);
	}
	
	@Override
	protected void entityInit() {
		dataWatcher.addObject(DATAWATCHER_ID, 0);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setDead();
	}
	
	@Override
	public void setDead() {
		super.setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		setDead();
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		setVecPosition(getVecPosition().plus(getVelocity().times(0.05)));
		setVelocity(getVelocity().times(0.4));
		
		List<Entity> collisions = worldObj.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox());
		if (!collisions.isEmpty()) {
			Entity collidedWith = collisions.get(0);
			if (!(collidedWith instanceof EntityControlPoint) && collidedWith != this.arc) {
				onCollision(collisions.get(0));
			}
		}
		
	}
	
	/**
	 * Called whenever the control point is in contact with this entity.
	 */
	protected void onCollision(Entity entity) {}
	
	public void setVecPosition(VectorD pos) {
		setPosition(pos.x(), pos.y(), pos.z());
	}
	
	public void move(double x, double y, double z) {
		posX += x;
		posY += y;
		posZ += z;
	}
	
	public void move(VectorD offset) {
		move(offset.xCoord, offset.yCoord, offset.zCoord);
	}
	
	public double getXPos() {
		return getVecPosition().x();
	}
	
	public double getYPos() {
		return getVecPosition().y();
	}
	
	public double getZPos() {
		return getVecPosition().z();
	}
	
	public double getDistance(EntityControlPoint point) {
		return getVecPosition().dist(point.getVecPosition());
	}
	
	@Override
	public VectorD getVecPosition() {
		internalPosition.setX(posX);
		internalPosition.setY(posY);
		internalPosition.setZ(posZ);
		return internalPosition;
	}
	
	@Override
	public VectorD getVelocity() {
		return internalVelocity.getValue();
	}
	
	@Override
	public void setVelocity(VectorD vel) {
		internalVelocity.setValue(vel);
	}
	
	@Override
	public void addVelocity(VectorD vel) {
		setVelocity(getVelocity().plus(vel));
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
	 * Get the Id of this control point. It is unique until the ControlPoint despawns. The Id is
	 * synced between server and client.
	 */
	public int getId() {
		return dataWatcher.getWatchableObjectInt(DATAWATCHER_ID);
	}
	
	/**
	 * Synchronize the Id between server and client.
	 */
	public void setId(int id) {
		dataWatcher.updateObject(DATAWATCHER_ID, id);
	}
	
	/**
	 * "Attach" the arc to this control point, meaning that the control point now has a reference to
	 * the given arc.
	 */
	public void setArc(EntityArc arc) {
		this.arc = arc;
	}
	
	public static EntityControlPoint findFromId(World world, int id) {
		for (Object obj : world.loadedEntityList) {
			if (obj instanceof EntityControlPoint && ((EntityControlPoint) obj).getId() == id)
				return (EntityControlPoint) obj;
		}
		return null;
	}
	
	@Override
	public void writeSpawnData(ByteBuf buf) {
		// Spawn data is used to allow server-spawned CPs to kill themselves.
		// This is so that client-side CPs are only from client-side
		// instantiation.
	}
	
	@Override
	public void readSpawnData(ByteBuf buf) {
		// Ignore server spawn packet
		setDead();
	}
	
}
