package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.entityproperty.EntityPropertyDataManager;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityArc extends Entity implements IPhysics {
	
	private static final int DATAWATCHER_ID = 3, DATAWATCHER_VELOCITY = 4, // 4,5,6
			DATAWATCHER_GRAVITY = 7;
	
	private static final DataParameter<Integer> SYNC_ID = EntityDataManager.createKey(EntityArc.class,
			DataSerializers.VARINT);
	private static final DataParameter<Vector> SYNC_VELOCITY = EntityDataManager.createKey(EntityArc.class,
			AvatarDataSerializers.SERIALIZER_VECTOR);
	private static final DataParameter<Boolean> SYNC_GRAVITY = EntityDataManager.createKey(EntityArc.class,
			DataSerializers.BOOLEAN);
	
	private static int nextId = 1;
	private EntityControlPoint[] points;
	
	private Vector internalPos;
	private EntityPropertyDataManager<Vector> velocity;
	
	protected EntityPlayer owner;
	
	public EntityArc(World world) {
		super(world);
		float size = .2f;
		setSize(size, size);
		
		this.points = new EntityControlPoint[getAmountOfControlPoints()];
		for (int i = 0; i < points.length; i++) {
			points[i] = createControlPoint(size);
		}
		
		this.internalPos = new Vector(0, 0, 0);
		this.velocity = new EntityPropertyDataManager<Vector>(this, EntityArc.class,
				AvatarDataSerializers.SERIALIZER_VECTOR, new Vector());
		if (!worldObj.isRemote) {
			setId(nextId++);
		}
	}
	
	/**
	 * Called from the EntityArc constructor to create a new control point entity.
	 * 
	 * @param size
	 * @return
	 */
	protected EntityControlPoint createControlPoint(float size) {
		return new EntityControlPoint(this, size, 0, 0, 0);
	}
	
	@Override
	protected void entityInit() {
		dataManager.register(SYNC_ID, 0);
		dataManager.register(SYNC_GRAVITY, false);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if (this.ticksExisted == 1) {
			for (int i = 0; i < points.length; i++) {
				points[i].setVecPosition(getVecPosition());
				worldObj.spawnEntityInWorld(points[i]);
			}
		}
		
		ignoreFrustumCheck = true;
		
		Vector vel = getVelocity();
		
		if (isGravityEnabled()) {
			addVelocity(getGravityVector());
		}
		
		moveEntity(vel.x() / 20, vel.y() / 20, vel.z() / 20);
		getLeader().setPosition(posX, posY, posZ);
		getLeader().setVelocity(getVelocity());
		
		if (isCollided) {
			setDead();
			onCollideWithBlock();
		}
		
		for (int i = 1; i < points.length; i++) {
			
			EntityControlPoint leader = points[i - 1];
			EntityControlPoint p = points[i];
			Vector leadPos = i == 0 ? getVecPosition() : getLeader(i).getVecPosition();
			double sqrDist = p.getVecPosition().sqrDist(leadPos);
			
			if (sqrDist > getControlPointTeleportDistanceSq()) {
				
				p.setPosition(leader.getXPos(), leader.getYPos(), leader.getZPos());
				
			} else if (sqrDist > getControlPointMaxDistanceSq()) {
				
				Vector diff = leader.getVecPosition().minus(p.getVecPosition());
				diff.normalize();
				diff.mul(3);
				p.addVelocity(diff);
				
			}
			
		}
		
	}
	
	protected abstract void onCollideWithBlock();
	
	protected abstract Vector getGravityVector();
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		setDead();// IEntityMultiPart
	}
	
	@Override
	public void setPosition(double x, double y, double z) {
		super.setPosition(x, y, z);
		// Set position - called from entity constructor, so points might be null
		if (points != null) {
			points[0].setPosition(x, y, z);
		}
	}
	
	@Override
	public void setDead() {
		super.setDead();
		for (EntityControlPoint point : points)
			point.setDead();
	}
	
	public EntityControlPoint[] getControlPoints() {
		return points;
	}
	
	public EntityControlPoint getControlPoint(int index) {
		return points[index];
	}
	
	/**
	 * Get the first control point in this arc.
	 */
	public EntityControlPoint getLeader() {
		return points[0];// EntityDragon
	}
	
	/**
	 * Get the leader of the specified control point.
	 */
	public EntityControlPoint getLeader(int index) {
		return points[index == 0 ? index : index - 1];
	}
	
	public int getId() {
		return dataManager.get(SYNC_ID);
	}
	
	public void setId(int id) {
		dataManager.set(SYNC_ID, id);
	}
	
	public static EntityArc findFromId(World world, int id) {
		for (Object obj : world.loadedEntityList) {
			if (obj instanceof EntityArc && ((EntityArc) obj).getId() == id) return (EntityArc) obj;
		}
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double d) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float p_70070_1_) {
		return 15728880;
	}
	
	@Override
	public Vector getVecPosition() {
		internalPos.setX(posX);
		internalPos.setY(posY);
		internalPos.setZ(posZ);
		return internalPos;
	}
	
	@Override
	public Vector getVelocity() {
		return velocity.getValue();
	}
	
	@Override
	public void setVelocity(Vector vel) {
		velocity.setValue(vel);
	}
	
	@Override
	public void addVelocity(Vector vel) {
		setVelocity(getVelocity().plus(vel));
	}
	
	public boolean isGravityEnabled() {
		return dataManager.get(SYNC_GRAVITY);
	}
	
	public void setGravityEnabled(boolean enabled) {
		dataManager.set(SYNC_GRAVITY, enabled);
	}
	
	public EntityPlayer getOwner() {
		return owner;
	}
	
	public void setOwner(EntityPlayer owner) {
		this.owner = owner;
		for (EntityControlPoint cp : points)
			cp.setOwner(owner);
	}
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
	
	/**
	 * Returns the amount of control points which will be created.
	 */
	public int getAmountOfControlPoints() {
		return 5;
	}
	
	/**
	 * Returns the maximum distance between control points, squared. Any control points beyond this
	 * distance will follow their leader to get closer.
	 */
	protected double getControlPointMaxDistanceSq() {
		return 1;
	}
	
	/**
	 * Returns the distance between control points to be teleported to their leader, squared. If any
	 * control point is more than this distance from its leader, then it is teleported to the
	 * leader.
	 */
	protected double getControlPointTeleportDistanceSq() {
		return 36;
	}
	
}
