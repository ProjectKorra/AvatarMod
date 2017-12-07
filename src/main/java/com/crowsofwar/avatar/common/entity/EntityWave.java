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
import com.crowsofwar.avatar.common.data.AbilityData;
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

public class EntityWave extends AvatarEntity {
	
	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityWave.class,
			DataSerializers.FLOAT);
	
	private float damageMult;
	private boolean createExplosion;
	
	public EntityWave(World world) {
		super(world);
		setSize(2, 2);
		damageMult = 1;
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
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

		super.onUpdate();

		setSize(getWaveSize() * 0.75f, 2);
		
		EntityLivingBase owner = getOwner();
		
		Vector move = velocity().dividedBy(20);
		Vector newPos = position().plus(move);
		setPosition(newPos.x(), newPos.y(), newPos.z());
		
		if (!world.isRemote) {
			List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox(), entity -> entity != owner);
			for (Entity entity : collided) {
				Vector motion = velocity().dividedBy(20).times(STATS_CONFIG.waveSettings.push);
				motion = motion.withY(0.4);
				entity.addVelocity(motion.x(), motion.y(), motion.z());
				entity.attackEntityFrom(AvatarDamageSource.causeWaveDamage(entity, owner), STATS_CONFIG.waveSettings.damage * damageMult);

				if (createExplosion) {
					world.createExplosion(null, posX, posY, posZ, 2, false);
				}

			}
			if (!collided.isEmpty() && owner != null) {
				AbilityData.get(owner, "wave").addXp(SKILLS_CONFIG.waveHit);
			}
		}

		if (ticksExisted > 7000 || world.getBlockState(getPosition()).getBlock() != Blocks.WATER) {
			setDead();
		}

	}

	@Override
	protected void onCollideWithEntity(Entity entity) {
		if (entity instanceof AvatarEntity) {
			((AvatarEntity) entity).onMajorWaterContact();
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		setDead();
	}
	
	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	public void setCreateExplosion(boolean createExplosion) {
		this.createExplosion = createExplosion;
	}

}
