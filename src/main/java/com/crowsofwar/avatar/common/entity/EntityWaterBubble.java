package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.entity.data.WaterBubbleBehavior;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityWaterBubble extends AvatarEntity {
	
	private static final DataParameter<WaterBubbleBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityWaterBubble.class, WaterBubbleBehavior.DATA_SERIALIZER);
	
	/**
	 * @param world
	 */
	public EntityWaterBubble(World world) {
		super(world);
	}
	
	@Override
	protected void entityInit() {
		dataManager.register(SYNC_BEHAVIOR, new WaterBubbleBehavior.Drop());
	}
	
	@Override
	public void onUpdate() {
		WaterBubbleBehavior currentBehavior = getBehavior();
		WaterBubbleBehavior nextBehavior = (WaterBubbleBehavior) currentBehavior.onUpdate();
		if (currentBehavior != nextBehavior) setBehavior(nextBehavior);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		
	}
	
	public WaterBubbleBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}
	
	public void setBehavior(WaterBubbleBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}
	
}
