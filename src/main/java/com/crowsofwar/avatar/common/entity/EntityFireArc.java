package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.util.VectorUtils;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityFireArc extends Entity {
	
	private static final int DATAWATCHER_ID = 3;
	
	private static int nextId = 1;
	private ControlPoint[] points;
	
	public EntityFireArc(World world) {
		super(world);
		setSize(0.2f, 0.2f);
		this.points = new ControlPoint[] {
			new ControlPoint(0, 0, 0),
			new ControlPoint(0, 0, 0),
			new ControlPoint(0, 0, 0)
		};
		if (!worldObj.isRemote) setId(nextId++);
	}
	
	@Override
	protected void entityInit() {
		dataWatcher.addObject(DATAWATCHER_ID, 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
//		setDead();
		for (int i = 1; i < points.length; i++) {
			ControlPoint leader = points[i - 1];
			ControlPoint p = points[i];
			double dist = p.getDistance(leader);
			if (dist > 20) {
				p.setPosition(leader.getXPos(), leader.getYPos(), leader.getZPos());
			} else if (dist > 2) {
				Vec3 diff = VectorUtils.minus(leader.getPos(), p.getPos());
				diff.normalize();
				VectorUtils.mult(diff, 0.05*3);
				p.move(diff);
			}
		}
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setDead();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void setPosition(double x, double y, double z) {
		super.setPosition(x, y, z);
		// Set position called in entity constructor
		if (points != null) points[0].setPosition(x, y, z);
	}
	
	public ControlPoint[] getControlPoints() {
		return points;
	}
	
	public ControlPoint getControlPoint(int index) {
		return points[index];
	}
	
	public int getId() {
		return dataWatcher.getWatchableObjectInt(DATAWATCHER_ID);
	}
	
	public void setId(int id) {
		dataWatcher.updateObject(DATAWATCHER_ID, id);
	}
	
	public static EntityFireArc findFromId(World world, int id) {
		for (Object obj : world.loadedEntityList) {
			if (obj instanceof EntityFireArc && ((EntityFireArc) obj).getId() == id) return (EntityFireArc) obj;
		}
		return null;
	}
	
	public class ControlPoint {
		
		private Vec3 position;
		
		public ControlPoint(double x, double y, double z) {
			position = Vec3.createVectorHelper(x, y, z);
		}
		
		public void setPosition(double x, double y, double z) {
			position.xCoord = x;
			position.yCoord = y;
			position.zCoord = z;
		}
		
		public void setPosition(Vec3 pos) {
			setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
		}
		
		public void move(double x, double y, double z) {
			position.xCoord += x;
			position.yCoord += y;
			position.zCoord += z;
		}
		
		public void move(Vec3 offset) {
			move(offset.xCoord, offset.yCoord, offset.zCoord);
		}
		
		public double getXPos() {
			return position.xCoord;
		}
		
		public double getYPos() {
			return position.yCoord;
		}
		
		public double getZPos() {
			return position.zCoord;
		}
		
		public Vec3 getPos() {
			return position;
		}
		
		public double getDistance(ControlPoint point) {
			return position.distanceTo(point.getPos());
		}
		
	}
	
}
