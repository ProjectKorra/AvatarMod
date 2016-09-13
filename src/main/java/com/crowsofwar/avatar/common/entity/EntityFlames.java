package com.crowsofwar.avatar.common.entity;

import java.util.List;

import com.crowsofwar.avatar.common.entityproperty.EntityPropertyMotion;
import com.crowsofwar.avatar.common.entityproperty.IEntityProperty;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityFlames extends Entity implements IPhysics {
	
	private final IEntityProperty<Vector> propVelocity;
	
	/**
	 * The owner, null client side
	 */
	private EntityPlayer owner;
	
	/**
	 * @param worldIn
	 */
	public EntityFlames(World worldIn) {
		super(worldIn);
		this.propVelocity = new EntityPropertyMotion(this);
		setSize(0.1f, 0.1f);
	}
	
	public EntityFlames(World world, EntityPlayer owner) {
		this(world);
		this.owner = owner;
	}
	
	@Override
	protected void entityInit() {
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// TODO Support saving/loading of EntityFlames
		setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		setDead();
	}
	
	@Override
	public void onUpdate() {
		Vector velocityPerTick = getVelocity().dividedBy(20);
		moveEntity(velocityPerTick.x(), velocityPerTick.y(), velocityPerTick.z());
		
		setVelocity(getVelocity().times(0.94));
		
		if (getVelocity().sqrMagnitude() <= 0.5 * 0.5 || isCollided) setDead();
		
		if (worldObj.getBlockState(getPosition()).getBlock() == Blocks.AIR) {
			
			Raytrace.Result raytrace = Raytrace.raytrace(worldObj, getVecPosition(), getVelocity(), 0.06,
					true);
			if (raytrace.hitSomething()) {
				EnumFacing sideHit = raytrace.getSide();
				setVelocity(getVelocity().reflect(new Vector(sideHit)));
				System.out.println("Reflect across " + sideHit + ", hit " + raytrace.getPos().precision());
			}
			
			// Vector blockPosition = new Vector(getPosition());
			// Raytrace.getTargetBlock(player, range, raycastLiquids)
			// for (Vector direction : Vector.DIRECTION_VECTORS) {
			// IBlockState blockThere = worldObj
			// .getBlockState(getPosition().add(direction.toMinecraftInteger()));
			// if (blockThere.getBlock() != Blocks.AIR) {
			// System.out.println("Reflecting in " + direction);
			// setVelocity(getVelocity().reflect(direction).mul(0.3));
			// setPosition(posX + direction.x() * -1, posY + direction.y() * -1,
			// posZ + direction.z() * -1);
			// break;
			// }
			// }
		}
		
		if (!worldObj.isRemote) {
			List<Entity> collided = worldObj.getEntitiesInAABBexcluding(this, getEntityBoundingBox(),
					entity -> entity != owner && !(entity instanceof EntityFlames));
			
			for (Entity entity : collided) {
				entity.setFire(3);
			}
		}
		
	}
	
	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {}
	
	@Override
	public Vector getVecPosition() {
		return Vector.getEntityPos(this);
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
		propVelocity.getValue().add(vel);
	}
	
}
