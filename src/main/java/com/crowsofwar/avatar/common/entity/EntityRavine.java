package com.crowsofwar.avatar.common.entity;

import static com.crowsofwar.avatar.common.config.AvatarConfig.ravineDamage;
import static com.crowsofwar.avatar.common.config.AvatarConfig.ravinePush;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.earth.RavineEvent;
import com.crowsofwar.avatar.common.entityproperty.EntityPropertyMotion;
import com.crowsofwar.avatar.common.entityproperty.IEntityProperty;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
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
		
		if (getSqrDistanceTravelled() > 100) {
			BendingManager.getBending(BendingType.EARTHBENDING).post(new RavineEvent.End(this));
			setDead();
		}
		
		BlockPos above = getPosition().offset(EnumFacing.UP);
		BlockPos below = getPosition().offset(EnumFacing.DOWN);
		
		if (!worldObj.getBlockState(below).isNormalCube()) {
			BendingManager.getBending(BendingType.EARTHBENDING).post(new RavineEvent.Stop(this));
			setDead();
		}
		
		// Destroy if in a block
		IBlockState inBlock = worldObj.getBlockState(getPosition());
		if (inBlock.isFullBlock()) {
			BendingManager.getBending(BendingType.EARTHBENDING).post(new RavineEvent.Stop(this));
			setDead();
		}
		
		// Destroy non-solid blocks in the ravine
		BlockPos inPos = getPosition();
		if (inBlock.getBlock() != Blocks.AIR && !inBlock.isFullBlock()) {
			BendingManager.getBending(BendingType.EARTHBENDING)
					.post(new RavineEvent.DestroyBlock(this, inBlock, inPos));
			
			for (int i = 0; i < 7; i++) {
				worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX, posY, posZ,
						3 * (rand.nextGaussian() - 0.5), rand.nextGaussian() * 2 + 1,
						3 * (rand.nextGaussian() - 0.5), Block.getStateId(inBlock));
			}
			worldObj.setBlockToAir(getPosition());
			
			if (!worldObj.isRemote) {
				List<ItemStack> drops = inBlock.getBlock().getDrops(worldObj, inPos, inBlock, 0);
				for (ItemStack stack : drops) {
					EntityItem item = new EntityItem(worldObj, posX, posY, posZ, stack);
					item.setDefaultPickupDelay();
					item.motionX *= 2;
					item.motionY *= 1.2;
					item.motionZ *= 2;
					worldObj.spawnEntityInWorld(item);
				}
			}
		}
		
		// Push collided entities back
		if (!worldObj.isRemote) {
			List<Entity> collided = worldObj.getEntitiesInAABBexcluding(this, getEntityBoundingBox(),
					entity -> entity != owner);
			if (!collided.isEmpty()) {
				for (Entity entity : collided) {
					if (!(entity instanceof EntityItem && entity.ticksExisted <= 10)) {
						BendingManager.getBending(BendingType.EARTHBENDING)
								.post(new RavineEvent.HitEntity(this, entity));
						
						Vector push = velocity.copy().setY(1).mul(ravinePush.currentValue());
						entity.addVelocity(push.x(), push.y(), push.z());
						entity.attackEntityFrom(AvatarDamageSource.causeRavineDamage(this, owner),
								ravineDamage.currentValue());
					}
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
