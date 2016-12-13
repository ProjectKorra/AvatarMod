package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.data.CachedEntity;

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
	
	private static final DataParameter<Integer> SYNC_WALL_ID = EntityDataManager
			.createKey(EntityWallSegment.class, DataSerializers.VARINT);
	
	private CachedEntity<EntityWall> wall;
	
	public EntityWallSegment(World world) {
		super(world);
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_WALL_ID, -1);
	}
	
	public EntityWall getWall() {
		return wall.getEntity(worldObj);
	}
	
	public void setWall(EntityWall wall) {
		this.wall.setEntity(wall);
	}
	
}
