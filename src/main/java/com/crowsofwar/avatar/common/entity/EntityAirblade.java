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

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import java.util.List;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.data.ctx.BenderInfo;
import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityAirblade extends AvatarEntity {
	
	public static final DataParameter<BenderInfo> SYNC_OWNER = EntityDataManager
			.createKey(EntityAirblade.class, AvatarDataSerializers.SERIALIZER_BENDER);
	
	private final OwnerAttribute ownerAttr;
	private float damage;
	
	/**
	 * Hardness threshold to chop blocks. For example, setting to 1.5 will allow
	 * the airblade to chop stone.
	 * <p>
	 * Note: Threshold of 0 means that the airblade can chop grass and similar
	 * blocks. Set to > 0 to avoid chopping blocks at all.
	 */
	private float chopBlocksThreshold;
	private boolean chainAttack;
	private boolean pierceArmor;
	
	public EntityAirblade(World world) {
		super(world);
		setSize(1.5f, .2f);
		this.ownerAttr = new OwnerAttribute(this, SYNC_OWNER);
		this.chopBlocksThreshold = -1;
	}
	
	@Override
	public void onUpdate() {
		
		super.onUpdate();
		
		// Don't bounce off a block if piercing
		// bouncing would prevent destroying the block
		// if (!worldObj.isRemote && piercing) {
		// moveAsNormal = false;
		//
		// // Check if the block is too strong
		// // If so, don't go through it since it can't be destroyed
		// // just bounce off as normal
		// IBlockState inBlockState = worldObj.getBlockState(getPosition());
		// Block inBlock = inBlockState.getBlock();
		// if (inBlock != Blocks.AIR && inBlockState.getBlockHardness(worldObj,
		// getPosition()) > 2f) {
		// position().add(v.times(-1));
		// moveAsNormal = true;
		// }
		//
		// }
		
		if (!worldObj.isRemote && velocity().sqrMagnitude() <= .9) {
			setDead();
		}
		
		IBlockState inBlockState = worldObj.getBlockState(getPosition());
		Block inBlock = inBlockState.getBlock();
		if (inBlock == Blocks.WATER) setDead();
		
		if (!worldObj.isRemote && chopBlocksThreshold >= 0 && inBlock != Blocks.AIR) {
			float hardness = inBlockState.getBlockHardness(worldObj, getPosition());
			if (hardness <= chopBlocksThreshold) {
				breakBlock(getPosition());
				velocity().mul(0.1);
			} else {
				// get out of block
				// moveEntity(MoverType.SELF, p_70091_2_, p_70091_4_,
				// p_70091_6_);
				noClip = true;
			}
		}
		
		if (!isDead && !worldObj.isRemote) {
			List<EntityLivingBase> collidedList = worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
					getEntityBoundingBox());
			
			if (!collidedList.isEmpty()) {
				
				EntityLivingBase collided = collidedList.get(0);
				
				DamageSource source = AvatarDamageSource.causeAirbladeDamage(collided, getOwner());
				if (pierceArmor) {
					source.setDamageBypassesArmor();
				}
				collided.attackEntityFrom(source, STATS_CONFIG.airbladeSettings.damage);
				
				Vector motion = velocity().copy();
				motion.mul(STATS_CONFIG.airbladeSettings.push);
				motion.setY(0.08);
				collided.addVelocity(motion.x(), motion.y(), motion.z());
				
				if (getOwner() != null) {
					BendingData data = getOwnerBender().getData();
					data.getAbilityData(BendingAbility.ABILITY_AIRBLADE).addXp(SKILLS_CONFIG.airbladeHit);
				}
				
				setDead();
				
			}
		}
		
	}
	
	@Override
	public void setDead() {
		super.setDead();
	}
	
	@Override
	public EntityLivingBase getOwner() {
		return ownerAttr.getOwner();
	}
	
	public void setOwner(EntityLivingBase owner) {
		ownerAttr.setOwner(owner);
	}
	
	public Bender getOwnerBender() {
		return ownerAttr.getOwnerBender();
	}
	
	public void setDamage(float damage) {
		this.damage = damage;
	}
	
	public float getChopBlocksThreshold() {
		return chopBlocksThreshold;
	}
	
	public void setChopBlocksThreshold(float chopBlocksThreshold) {
		this.chopBlocksThreshold = chopBlocksThreshold;
		this.noClip = chopBlocksThreshold >= 0;
	}
	
	public boolean getPierceArmor() {
		return pierceArmor;
	}
	
	public void setPierceArmor(boolean piercing) {
		this.pierceArmor = piercing;
	}
	
	public boolean isChainAttack() {
		return chainAttack;
	}
	
	public void setChainAttack(boolean chainAttack) {
		this.chainAttack = chainAttack;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		ownerAttr.load(nbt);
		damage = nbt.getFloat("Damage");
		chopBlocksThreshold = nbt.getFloat("ChopBlocksThreshold");
		pierceArmor = nbt.getBoolean("Piercing");
		chainAttack = nbt.getBoolean("ChainAttack");
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		ownerAttr.save(nbt);
		nbt.setFloat("Damage", damage);
		nbt.setFloat("ChopBlocksThreshold", chopBlocksThreshold);
		nbt.setBoolean("Piercing", pierceArmor);
		nbt.setBoolean("ChainAttack", chainAttack);
	}
	
}
