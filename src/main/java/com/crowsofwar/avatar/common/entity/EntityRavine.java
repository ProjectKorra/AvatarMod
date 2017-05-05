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
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BenderInfo;
import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.avatar.common.entityproperty.EntityPropertyMotion;
import com.crowsofwar.avatar.common.entityproperty.IEntityProperty;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityRavine extends AvatarEntity {
	
	private static final DataParameter<BenderInfo> SYNC_OWNER = EntityDataManager
			.createKey(EntityRavine.class, AvatarDataSerializers.SERIALIZER_BENDER);
	
	private final IEntityProperty<Vector> propVelocity;
	private Vector initialPosition;
	private final OwnerAttribute ownerAttr;
	
	private float damageMult;
	
	/**
	 * @param world
	 */
	public EntityRavine(World world) {
		super(world);
		
		this.propVelocity = new EntityPropertyMotion(this);
		setSize(1, 1);
		
		this.damageMult = 1;
		this.ownerAttr = new OwnerAttribute(this, SYNC_OWNER);
		
	}
	
	public void setDamageMult(float mult) {
		this.damageMult = mult;
	}
	
	@Override
	public EntityLivingBase getOwner() {
		return ownerAttr.getOwner();
	}
	
	public void setOwner(EntityLivingBase owner) {
		ownerAttr.setOwner(owner);
	}
	
	public double getSqrDistanceTravelled() {
		return position().sqrDist(initialPosition);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
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
			setDead();
		}
		
		BlockPos above = getPosition().offset(EnumFacing.UP);
		BlockPos below = getPosition().offset(EnumFacing.DOWN);
		
		if (ticksExisted % 3 == 0) worldObj.playSound(posX, posY, posZ,
				worldObj.getBlockState(below).getBlock().getSoundType().getBreakSound(),
				SoundCategory.PLAYERS, 1, 1, false);
		
		if (!worldObj.getBlockState(below).isNormalCube()) {
			setDead();
		}
		
		// Destroy if in a block
		IBlockState inBlock = worldObj.getBlockState(getPosition());
		if (inBlock.isFullBlock()) {
			setDead();
		}
		
		// Destroy non-solid blocks in the ravine
		BlockPos inPos = getPosition();
		if (inBlock.getBlock() != Blocks.AIR && !inBlock.isFullBlock()) {
			
			if (inBlock.getBlockHardness(worldObj, getPosition()) == 0) {
				
				breakBlock(getPosition());
				
			} else {
				
				setDead();
				
			}
			
		}
		
		// amount of entities which were successfully attacked
		int attacked = 0;
		
		// Push collided entities back
		if (!worldObj.isRemote) {
			List<Entity> collided = worldObj.getEntitiesInAABBexcluding(this, getEntityBoundingBox(),
					entity -> entity != getOwner());
			if (!collided.isEmpty()) {
				for (Entity entity : collided) {
					if (!(entity instanceof EntityItem && entity.ticksExisted <= 10)) {
						Vector push = velocity.copy().setY(1).mul(STATS_CONFIG.ravineSettings.push);
						entity.addVelocity(push.x(), push.y(), push.z());
						if (entity.attackEntityFrom(AvatarDamageSource.causeRavineDamage(entity, getOwner()),
								STATS_CONFIG.ravineSettings.damage * damageMult))
							attacked++;
						AvatarUtils.afterVelocityAdded(entity);
					}
				}
			}
		}
		
		if (!worldObj.isRemote && getOwner() != null) {
			BendingData data = ownerAttr.getOwnerBender().getData();
			if (data != null) {
				data.getAbilityData(ABILITY_RAVINE).addXp(SKILLS_CONFIG.ravineHit * attacked);
			}
		}
		
	}
	
}
