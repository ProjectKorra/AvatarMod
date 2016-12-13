package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.entity.data.SyncableEntityReference;

import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityWallSegment extends AvatarEntity {
	
	private static final DataParameter<Integer> SYNC_WALL = EntityDataManager
			.createKey(EntityWallSegment.class, DataSerializers.VARINT);
	
	private final SyncableEntityReference<EntityWall> wallReference;
	
	public EntityWallSegment(World world) {
		super(world);
		this.wallReference = new SyncableEntityReference<>(this, SYNC_WALL);
	}
	
	public EntityWall getWall() {
		return wallReference.getEntity();
	}
	
	/**
	 * Allows this segment to reference the wall, and allows the wall to
	 * reference this segment.
	 */
	public void attachToWall(EntityWall wall) {
		wallReference.setEntity(wall);
		wall.addSegment(this);
	}
	
}
