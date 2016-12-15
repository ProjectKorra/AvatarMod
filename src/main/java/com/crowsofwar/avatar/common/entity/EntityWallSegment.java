package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.entity.data.SyncableEntityReference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
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
		this.setSize(1, 5);
	}
	
	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_WALL, -1);
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
	
	@Override
	public void setDead() {
		super.setDead();
		if (getWall() != null) getWall().setDead();
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		this.noClip = false;
		// System.out.println(this.getWall());
	}
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand stack) {
		System.out.println("Attacked");// EntityItemFrame Minecraft
		if (!this.isDead && !worldObj.isRemote) {
			setDead();
			setBeenAttacked();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
	
	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return getEntityBoundingBox();
	}
	
	@Override
	public boolean canBePushed() {
		return true;
	}
	
	@Override
	public void applyEntityCollision(Entity entity) {
		
		double amt = 0.4;
		
		// entity.motionZ = velocity;
		if (entity.posZ > this.posZ) {
			entity.posZ = this.posZ + 1.1;
			entity.motionZ = amt;
		} else {
			entity.posZ = this.posZ - 1.1;
			entity.motionZ = -amt;
		}
		entity.motionY = .25;
		
		entity.isAirBorne = true;
		if (entity instanceof EntityPlayerMP) {
			((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));
		}
		if (entity instanceof AvatarEntity) {
			// ((AvatarEntity) entity).velocity().setZ(velocity);
		}
	}
	
}
