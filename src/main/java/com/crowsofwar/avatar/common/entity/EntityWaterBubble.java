package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.avatar.common.entity.data.WaterBubbleBehavior;

import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
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
	private static final DataParameter<String> SYNC_OWNER = EntityDataManager
			.createKey(EntityWaterBubble.class, DataSerializers.STRING);
	
	private final OwnerAttribute ownerAttrib;
	
	/**
	 * @param world
	 */
	public EntityWaterBubble(World world) {
		super(world);
		this.ownerAttrib = new OwnerAttribute(this, SYNC_OWNER);
	}
	
	@Override
	protected void entityInit() {
		dataManager.register(SYNC_BEHAVIOR, new WaterBubbleBehavior.Drop());
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		velocity().set(0, 1, 0);
		moveEntity(MoverType.SELF, velocity().x() / 20, velocity().y() / 20, velocity().z() / 20);
		WaterBubbleBehavior currentBehavior = getBehavior();
		WaterBubbleBehavior nextBehavior = (WaterBubbleBehavior) currentBehavior.onUpdate();
		if (currentBehavior != nextBehavior) setBehavior(nextBehavior);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		ownerAttrib.load(compound);
		setBehavior((WaterBubbleBehavior) Behavior.lookup(compound.getInteger("Behavior"), this));
		getBehavior().load(compound);
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		ownerAttrib.save(compound);
		compound.setInteger("Behavior", getBehavior().getId());
		getBehavior().save(compound);
	}
	
	public WaterBubbleBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}
	
	public void setBehavior(WaterBubbleBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}
	
	public EntityPlayer getOwner() {
		return ownerAttrib.getOwner();
	}
	
	public void setOwner(EntityPlayer player) {
		ownerAttrib.setOwner(player);
	}
	
}
