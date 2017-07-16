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

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.water.AbilityCreateWave;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.data.ctx.BenderInfo;
import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.gorecore.util.BackedVector;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityWave extends Entity {
	
	private static final DataParameter<BenderInfo> SYNC_OWNER = EntityDataManager.createKey(EntityWave.class,
			AvatarDataSerializers.SERIALIZER_BENDER);
	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityWave.class,
			DataSerializers.FLOAT);
	
	private final Vector internalVelocity;
	private final Vector internalPosition;
	private final OwnerAttribute ownerAttr;
	
	private float damageMult;
	private int timeOnLand;
	
	public EntityWave(World world) {
		super(world);
		//@formatter:off
		this.internalVelocity = new BackedVector(x -> this.motionX = x / 20, y -> this.motionY = y / 20, z -> this.motionZ = z / 20,
				() -> this.motionX * 20, () -> this.motionY * 20, () -> this.motionZ * 20);
		this.internalPosition = new Vector();
		
		setSize(2, 2);
		
		damageMult = 1;
		
		ownerAttr = new OwnerAttribute(this, SYNC_OWNER);
		
	}
	
	@Override
	protected void entityInit() {
		dataManager.register(SYNC_SIZE, 2f);
	}
	
	public void setDamageMultiplier(float damageMult) {
		this.damageMult = damageMult;
	}
	
	public float getWaveSize() {
		return dataManager.get(SYNC_SIZE);
	}
	
	public void setWaveSize(float size) {
		dataManager.set(SYNC_SIZE, size);
	}
	
	@Override
	public void onUpdate() {
		
		setSize(getWaveSize() * 0.75f, 2);
		
		EntityLivingBase owner = getOwner();
		
		Vector move = velocity().dividedBy(20);
		Vector newPos = getVecPosition().add(move);
		setPosition(newPos.x(), newPos.y(), newPos.z());
		
		if (!world.isRemote) {
			List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox(), entity -> entity != owner);
			for (Entity entity : collided) {
				Vector motion = velocity().dividedBy(20).times(STATS_CONFIG.waveSettings.push);
				motion.setY(0.4);
				entity.addVelocity(motion.x(), motion.y(), motion.z());
				entity.attackEntityFrom(AvatarDamageSource.causeWaveDamage(entity, owner), STATS_CONFIG.waveSettings.damage * damageMult);
			}
			if (!collided.isEmpty()) {
				BendingData data = Bender.get(owner).getData();
				if (data != null) {
					data.getAbilityData(AbilityCreateWave.ID).addXp(SKILLS_CONFIG.waveHit);
				}
			}
		}
		
		if (ticksExisted > 7000) {
			setDead();
		}
		if (!world.isRemote && world.getBlockState(getPosition()).getBlock() != Blocks.WATER) {
			timeOnLand++;
			if (timeOnLand >= maxTimeOnLand()) {
				setDead();
			}
		}
		
	}
	
	private int maxTimeOnLand() {
		if (getOwner() != null) {
			AbilityData data = Bender.getData(getOwner()).getAbilityData(AbilityCreateWave.ID);
			if (data.isMasterPath(AbilityTreePath.FIRST)) {
				return 30;
			}
		}
		return 0;
	}
	
	public Vector getVecPosition() {
		return internalPosition.set(posX, posY, posZ);
	}
	
	/**
	 * Get velocity in m/s. Any modifications to this vector will modify the entity motion fields.
	 */
	public Vector velocity() {
		return internalVelocity;
	}
	
	public EntityLivingBase getOwner() {
		return ownerAttr.getOwner();
	}
	
	public void setOwner(EntityLivingBase owner) {
		ownerAttr.setOwner(owner);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		// TODO Save/load waves??
		setDead();
	}
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}
	
}
