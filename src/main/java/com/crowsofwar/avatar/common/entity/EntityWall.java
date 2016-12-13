package com.crowsofwar.avatar.common.entity;

import static net.minecraft.util.EnumFacing.NORTH;

import com.crowsofwar.avatar.common.entity.data.SyncableEntityReference;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityWall extends AvatarEntity {
	
	private static final DataParameter<Integer> SYNC_DIRECTION = EntityDataManager.createKey(EntityWall.class,
			DataSerializers.VARINT);
	
	/**
	 * All the segments in this wall. MUST be fixed-length, as the data
	 * parameters must be same for both sides.
	 */
	private final SyncableEntityReference<EntityWallSegment>[] segments;
	
	private int nextSegment = 0;
	
	/**
	 * @param world
	 */
	public EntityWall(World world) {
		super(world);
		this.segments = new SyncableEntityReference[] { new SyncableEntityReference<>(this, EntityWall.class),
				new SyncableEntityReference<>(this, EntityWall.class),
				new SyncableEntityReference<>(this, EntityWall.class),
				new SyncableEntityReference<>(this, EntityWall.class),
				new SyncableEntityReference<>(this, EntityWall.class) };
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_DIRECTION, NORTH.ordinal());
	}
	
	public void addSegment(EntityWallSegment segment) {
		segments[nextSegment].setEntity(segment);
		nextSegment++;
	}
	
	public EntityWallSegment getSegment(int i) {
		return segments[i].getEntity();
	}
	
	public EnumFacing getDirection() {
		return EnumFacing.values()[dataManager.get(SYNC_DIRECTION)];
	}
	
	public void setDirection(EnumFacing direction) {
		if (direction.getAxis().isVertical())
			throw new IllegalArgumentException("Cannot face up/down: " + direction);
		this.dataManager.set(SYNC_DIRECTION, direction.ordinal());
	}
	
}
