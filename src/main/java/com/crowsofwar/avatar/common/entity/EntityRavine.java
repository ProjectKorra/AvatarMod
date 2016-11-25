/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.entity;

import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_RAVINE;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.earth.RavineEvent;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityRavine extends AvatarEntity {
	
	private final IEntityProperty<Vector> propVelocity;
	private Vector initialPosition;
	private EntityPlayer owner;
	
	private float damageMult;
	
	/**
	 * @param world
	 */
	public EntityRavine(World world) {
		super(world);
		
		this.propVelocity = new EntityPropertyMotion(this);
		setSize(1, 1);
		
		this.damageMult = 1;
		
	}
	
	public void setDamageMult(float mult) {
		this.damageMult = mult;
	}
	
	public void setOwner(EntityPlayer owner) {
		this.owner = owner;
	}
	
	public double getSqrDistanceTravelled() {
		return position().sqrDist(initialPosition);
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
			initialPosition = position().copy();
		}
		
		Vector position = position();
		Vector velocity = velocity();
		
		Vector nowPos = position.add(velocity.times(0.05));
		setPosition(nowPos.x(), nowPos.y(), nowPos.z());
		
		if (getSqrDistanceTravelled() > 100) {
			BendingManager.getBending(BendingType.EARTHBENDING).post(new RavineEvent.End(this));
			setDead();
		}
		
		BlockPos above = getPosition().offset(EnumFacing.UP);
		BlockPos below = getPosition().offset(EnumFacing.DOWN);
		
		if (ticksExisted % 3 == 0) worldObj.playSound(posX, posY, posZ,
				worldObj.getBlockState(below).getBlock().getSoundType().getBreakSound(),
				SoundCategory.PLAYERS, 1, 1, false);
		
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
			
			if (inBlock.getBlockHardness(worldObj, getPosition()) == 0) {
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
				
			} else {
				
				BendingManager.getBending(BendingType.EARTHBENDING).post(new RavineEvent.Stop(this));
				setDead();
				
			}
			
		}
		
		// amount of entities which were successfully attacked
		int attacked = 0;
		
		// Push collided entities back
		if (!worldObj.isRemote) {
			List<Entity> collided = worldObj.getEntitiesInAABBexcluding(this, getEntityBoundingBox(),
					entity -> entity != owner);
			if (!collided.isEmpty()) {
				for (Entity entity : collided) {
					if (!(entity instanceof EntityItem && entity.ticksExisted <= 10)) {
						BendingManager.getBending(BendingType.EARTHBENDING)
								.post(new RavineEvent.HitEntity(this, entity));
						
						Vector push = velocity.copy().setY(1).mul(STATS_CONFIG.ravineSettings.push);
						entity.addVelocity(push.x(), push.y(), push.z());
						if (entity.attackEntityFrom(AvatarDamageSource.causeRavineDamage(entity, owner),
								STATS_CONFIG.ravineSettings.damage * damageMult))
							attacked++;
					}
				}
			}
		}
		
		if (!worldObj.isRemote && owner != null) {
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(owner);
			if (data != null) {
				data.getAbilityData(ABILITY_RAVINE).addXp(SKILLS_CONFIG.ravineHit * attacked);
			}
		}
		
	}
	
}
