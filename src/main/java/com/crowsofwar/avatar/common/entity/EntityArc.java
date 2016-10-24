package com.crowsofwar.avatar.common.entity;

import java.util.function.Consumer;

import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityArc extends AvatarEntity {
	
	private static final int DATAWATCHER_ID = 3, DATAWATCHER_VELOCITY = 4, // 4,5,6
			DATAWATCHER_GRAVITY = 7;
	
	private static final DataParameter<Integer> SYNC_ID = EntityDataManager.createKey(EntityArc.class,
			DataSerializers.VARINT);
	private static final DataParameter<Boolean> SYNC_GRAVITY = EntityDataManager.createKey(EntityArc.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<String> SYNC_OWNER_NAME = EntityDataManager.createKey(EntityArc.class,
			DataSerializers.STRING);
	
	private static int nextId = 1;
	private ControlPoint[] points;
	
	private final OwnerAttribute ownerAttrib;
	
	public EntityArc(World world) {
		super(world);
		float size = .2f;
		setSize(size, size);
		
		this.points = new ControlPoint[getAmountOfControlPoints()];
		for (int i = 0; i < points.length; i++) {
			points[i] = createControlPoint(size);
		}
		
		if (!worldObj.isRemote) {
			setId(nextId++);
		}
		
		ownerAttrib = new OwnerAttribute(this, SYNC_OWNER_NAME, getNewOwnerCallback());
		
	}
	
	/**
	 * Called from the EntityArc constructor to create a new control point
	 * entity.
	 * 
	 * @param size
	 * @return
	 */
	protected ControlPoint createControlPoint(float size) {
		return new ControlPoint(this, size, 0, 0, 0);
	}
	
	/**
	 * Get a callback which is called when the owner is changed.
	 */
	protected Consumer<EntityPlayer> getNewOwnerCallback() {
		return newOwner -> {
		};
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
				points[i].position().set(position());
			}
		}
		
		ignoreFrustumCheck = true;
		
		if (isGravityEnabled()) {
			velocity().add(getGravityVector());
		}
		
		Vector velPerTick = velocity().dividedBy(20);
		moveEntity(velPerTick.x(), velPerTick.y(), velPerTick.z());
		getLeader().position().set(posX, posY, posZ);
		getLeader().velocity().set(velocity());
		
		for (ControlPoint cp : points)
			cp.onUpdate();
		
		if (isCollided) {
			setDead();
			onCollideWithBlock();
		}
		
		for (int i = 1; i < points.length; i++) {
			
			ControlPoint leader = points[i - 1];
			ControlPoint p = points[i];
			Vector leadPos = i == 0 ? velocity() : getLeader(i).position();
			double sqrDist = p.position().sqrDist(leadPos);
			
			if (sqrDist > getControlPointTeleportDistanceSq()) {
				
				Vector toFollowerDir = p.position().minus(leader.position()).normalize();
				
				double idealDist = Math.sqrt(getControlPointTeleportDistanceSq());
				if (idealDist > 1) idealDist -= 1; // Make sure there is some
													// room
				
				Vector revisedOffset = leader.position().add(toFollowerDir.times(idealDist));
				p.position().set(revisedOffset);
				p.velocity().set(Vector.ZERO);
				
				// Vector diff = leader.position().minus(p.position());
				// double speed = leader.velocity().magnitude();
				//
				// p.velocity().set(diff.normalize().times(speed));
				
			} else if (sqrDist > getControlPointMaxDistanceSq()) {
				
				Vector diff = leader.position().minus(p.position());
				diff.normalize();
				diff.mul(3);
				p.velocity().add(diff);
				
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
	protected void writeEntityToNBT(NBTTagCompound nbt) {}
	
	@Override
	public void setPosition(double x, double y, double z) {
		super.setPosition(x, y, z);
		// Set position - called from entity constructor, so points might be
		// null
		if (points != null) {
			points[0].position().set(x, y, z);
		}
	}
	
	public ControlPoint[] getControlPoints() {
		return points;
	}
	
	public ControlPoint getControlPoint(int index) {
		return points[index];
	}
	
	/**
	 * Get the first control point in this arc.
	 */
	public ControlPoint getLeader() {
		return points[0];// EntityDragon
	}
	
	/**
	 * Get the leader of the specified control point.
	 */
	public ControlPoint getLeader(int index) {
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
	
	public boolean isGravityEnabled() {
		return dataManager.get(SYNC_GRAVITY);
	}
	
	public void setGravityEnabled(boolean enabled) {
		dataManager.set(SYNC_GRAVITY, enabled);
	}
	
	public EntityPlayer getOwner() {
		return ownerAttrib.getOwner();
	}
	
	public void setOwner(EntityPlayer owner) {
		this.ownerAttrib.setOwner(owner);
		for (ControlPoint cp : points)
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
