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
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.client.particle.ParticleExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityWave extends AvatarEntity {

	private static final DataParameter<Float> SYNC_SIZE = EntityDataManager.createKey(EntityWave.class,
			DataSerializers.FLOAT);

	private float damageMult;
	private boolean createExplosion;
	private float Size;
	private float Shrink;
	private int collided;

	public EntityWave(World world) {
		super(world);
		this.Size = 2;
		setSize(Size, Size);
		damageMult = 1;
		Shrink = 0.05F;
		this.collided = 0;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_SIZE, Size);
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
	public boolean canBeCollidedWith() {
		return collided >= 1;
	}

	@Override
	public void onUpdate() {

		super.onUpdate();
		//this.noClip = true;
		BendingData data = BendingData.get(getOwner());
		AbilityData lvl = data.getAbilityData("wave");

		if (lvl.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			setSize(Size * 2, Size * 0.75F * 2);
		}
		else {
			setSize(Size, Size * 0.75F);
		}

		BlockPos below = getPosition().offset(EnumFacing.DOWN);
		Block belowBlock = world.getBlockState(below).getBlock();

		if (belowBlock != Blocks.FLOWING_WATER && belowBlock != Blocks.WATER) {
			this.setVelocity(velocity().dividedBy(40));
			this.posY -= Shrink;
		}
		else{
			this.posY += Shrink;
		}

		EntityLivingBase owner = getOwner();

		Vector move = velocity().dividedBy(20);
		Vector newPos = position().plus(move);
		setPosition(newPos.x(), newPos.y(), newPos.z());



		if (!world.isRemote) {
			WorldServer World = (WorldServer) world;
			World.spawnParticle(EnumParticleTypes.WATER_WAKE, posX, posY, posZ, 300, getWaveSize() / 2.5, getWaveSize() / 5, getWaveSize() / 2.5, 0);
			World.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, posX, posY + (Size * 0.75F), posZ, 1, getWaveSize() / 5, getWaveSize() / 20, getWaveSize() / 5, 0);
			World.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX, posY + (Size * 0.75F), posZ, 30, getWaveSize() / 5, 0, getWaveSize() / 5, 0);

			List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox(), entity -> entity != owner);
			for (Entity entity : collided) {
				Vector motion = velocity().dividedBy(20).times(STATS_CONFIG.waveSettings.push);
				motion = motion.withY(0.4);
				entity.addVelocity(motion.x(), motion.y(), motion.z());

				if (entity.attackEntityFrom(AvatarDamageSource.causeWaveDamage(entity, owner),
						STATS_CONFIG.waveSettings.damage * damageMult)) {

					BattlePerformanceScore.addLargeScore(getOwner());

				}

				if (createExplosion) {
					world.createExplosion(null, posX, posY, posZ, 2, false);
				}

			}
			if (!collided.isEmpty() && owner != null) {
				AbilityData.get(owner, "wave").addXp(SKILLS_CONFIG.waveHit);
			}
		}

		BlockPos below1 = getPosition();
		Block belowBlock1 = world.getBlockState(below1).getBlock();
		if (ticksExisted > 200 && belowBlock1 != Blocks.WATER) {
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
	public boolean onCollideWithSolid() {
		onMajorWaterContact();

		collided++;
		BlockPos below = getPosition().offset(EnumFacing.DOWN);
		Block belowBlock = world.getBlockState(below).getBlock();

		if (!this.isCollidedVertically && (belowBlock != Blocks.FLOWING_WATER && belowBlock != Blocks.WATER)) {
			Shrink = 0.005F;
			return false;
		}
		return collided >= 1;

		//help
		//TODO: Make wave go onto land

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
