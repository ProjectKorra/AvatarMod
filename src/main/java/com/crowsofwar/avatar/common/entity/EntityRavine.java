package com.crowsofwar.avatar.common.entity;

import java.util.List;

import com.crowsofwar.avatar.common.entityproperty.EntityPropertyMotion;
import com.crowsofwar.avatar.common.entityproperty.IEntityProperty;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityRavine extends Entity implements IPhysics {
	
	private final IEntityProperty<Vector> propVelocity;
	private Vector initialPosition;
	private EntityPlayer owner;
	
	/**
	 * @param world
	 */
	public EntityRavine(World world) {
		super(world);
		
		this.propVelocity = new EntityPropertyMotion(this);
		setSize(1, 1);
		
	}
	
	public void setOwner(EntityPlayer owner) {
		this.owner = owner;
	}
	
	public double getSqrDistanceTravelled() {
		return getVecPosition().sqrDist(initialPosition);
	}
	
	@Override
	protected void entityInit() {
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		setDead();
	}
	
	@Override
	public void onEntityUpdate() {
		
		if (initialPosition == null) {
			initialPosition = getVecPosition();
		}
		
		Vector position = getVecPosition();
		Vector velocity = getVelocity();
		
		Vector nowPos = position.add(velocity.times(0.05));
		setPosition(nowPos.x(), nowPos.y(), nowPos.z());
		
		if (getSqrDistanceTravelled() > 100) setDead();
		
		BlockPos above = getPosition().offset(EnumFacing.UP);
		BlockPos below = getPosition().offset(EnumFacing.DOWN);
		
		if (!worldObj.getBlockState(below).isNormalCube()) setDead();
		IBlockState inBlock = worldObj.getBlockState(getPosition());
		if (inBlock.getBlock() != Blocks.AIR && !inBlock.isFullBlock()) {
			for (int i = 0; i < 7; i++) {
				worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX, posY, posZ, -velocity.x(), 0.3,
						-velocity.z(), Block.getStateId(inBlock));
			}
			worldObj.setBlockToAir(getPosition());
		}
		
		if (!worldObj.isRemote) {
			List<Entity> collided = worldObj.getEntitiesInAABBexcluding(this, getEntityBoundingBox(),
					entity -> entity != owner);
			if (!collided.isEmpty()) {
				for (Entity entity : collided) {
					entity.addVelocity(velocity.x() / 4, 1, velocity.z() / 4);
				}
			}
		}
		
	}
	
	@Override
	public Vector getVecPosition() {
		return new Vector(this);
	}
	
	@Override
	public Vector getVelocity() {
		return propVelocity.getValue();
	}
	
	@Override
	public void setVelocity(Vector vel) {
		propVelocity.setValue(vel);
	}
	
	@Override
	public void addVelocity(Vector vel) {
		setVelocity(getVelocity().plus(vel));
	}
	
}
