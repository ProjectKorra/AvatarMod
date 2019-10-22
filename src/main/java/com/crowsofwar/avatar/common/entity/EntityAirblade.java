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

import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.air.AbilityAirblade;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class EntityAirblade extends EntityOffensive {

	private static final DataParameter<Float> SYNC_SIZE_MULT = EntityDataManager.createKey(EntityAirblade.class, DataSerializers.FLOAT);
	/**
	 * Hardness threshold to chop blocks. For example, setting to 1.5 will allow
	 * the airblade to chop stone.
	 * <p>
	 * Note: Threshold of 0 means that the airblade can chop grass and similar
	 * blocks. Set to < 0 to avoid chopping blocks at all.
	 */
	private float chopBlocksThreshold;
	private boolean pierceArmor;

	public EntityAirblade(World world) {
		super(world);
		setSize(0.2f, 1.5f);
		this.chopBlocksThreshold = -1;
		this.noClip = true;
	}

	public float getSizeMult() {
		return dataManager.get(SYNC_SIZE_MULT);
	}

	public void setSizeMult(float sizeMult) {
		dataManager.set(SYNC_SIZE_MULT, sizeMult);
	}


	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_SIZE_MULT, 1.0F);
	}

	@Override
	public BendingStyle getElement() {
		return new Airbending();
	}

	@Override
	public void applyElementalContact(AvatarEntity entity) {
		super.applyElementalContact(entity);
		entity.onAirContact();
	}

	@Override
	public void onUpdate() {

		super.onUpdate();
		setEntitySize(getSizeMult() * 1.5F, getSizeMult() * 0.2F);

		this.motionX *= 0.98;
		this.motionY *= 0.98;
		this.motionZ *= 0.98;

		if (!world.isRemote && getOwner() != null && getAbility() instanceof AbilityAirblade) {
			AbilityData data = AbilityData.get(getOwner(), getAbility().getName());
			if (data.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				if (velocity().sqrMagnitude() <= 0.7 * 0.7) {
					setDead();
				}

			} else if (velocity().sqrMagnitude() <= 0.9 * 0.9) {
				setDead();
			}
		} else if (!world.isRemote && velocity().sqrMagnitude() <= 0.9 * 0.9) {
			setDead();
		}

		if (!world.isRemote && chopBlocksThreshold >= 0) {
			breakCollidingBlocks();
		}


		if (world.isRemote) {
			for (double i = 0; i < 0.5; i += 1 / getHeight()) {
				AxisAlignedBB boundingBox = getEntityBoundingBox();
				double spawnX = boundingBox.minX + world.rand.nextDouble() * (boundingBox.maxX - boundingBox.minX);
				double spawnY = boundingBox.minY + world.rand.nextDouble() * (boundingBox.maxY - boundingBox.minY);
				double spawnZ = boundingBox.minZ + world.rand.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
						world.rand.nextGaussian() / 60).collide(true).time(8).clr(0.8F, 0.8F, 0.8F)
						.scale(getWidth() * 4).spawn(world);
			}
		}

	}

	@Override
	public DamageSource getDamageSource(Entity target) {
		DamageSource ds = AvatarDamageSource.causeAirbladeDamage(target, getOwner());
		return pierceArmor ? ds.setDamageBypassesArmor() : ds;
	}

	/**
	 * When the airblade can break blocks, checks any blocks that the airblade
	 * collides with and tries to break them
	 */
	private void breakCollidingBlocks() {
		// Hitbox expansion (in each direction) to destroy blocks before the
		// airblade collides with them
		double expansion = getSizeMult() / 20;
		AxisAlignedBB hitbox = getEntityBoundingBox().grow(expansion, expansion, expansion);
		for (int iy = 0; iy <= getSizeMult() * 1.5F; iy++) {
			for (int ix = 0; ix <= 1; ix++) {
				for (int iz = 0; iz <= 1; iz++) {

					double x = ix == 0 ? hitbox.minX : hitbox.maxX;
					double y = iy == 0 ? hitbox.minY : hitbox.maxY;
					double z = iz == 0 ? hitbox.minZ : hitbox.maxZ;
					BlockPos pos = new BlockPos(x, y, z);

					tryBreakBlock(world.getBlockState(pos), pos);

				}
			}
		}
	}


	/**
	 * Assuming the airblade can break blocks, tries to break the block.
	 */
	private void tryBreakBlock(IBlockState state, BlockPos pos) {
		if (state.getBlock() == Blocks.AIR || !STATS_CONFIG.airBladeBreakableBlocks.contains(state.getBlock())) {
			return;
		}

		float hardness = state.getBlockHardness(world, pos);
		if (hardness <= chopBlocksThreshold) {
			breakBlock(pos);
			setVelocity(velocity().times(0.95));
		}
	}


	public Bender getOwnerBender() {
		return Bender.get(getOwner());
	}


	public float getChopBlocksThreshold() {
		return chopBlocksThreshold;
	}

	public void setChopBlocksThreshold(float chopBlocksThreshold) {
		this.chopBlocksThreshold = chopBlocksThreshold;
	}

	public boolean getPierceArmor() {
		return pierceArmor;
	}

	public void setPierceArmor(boolean piercing) {
		this.pierceArmor = piercing;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		chopBlocksThreshold = nbt.getFloat("ChopBlocksThreshold");
		pierceArmor = nbt.getBoolean("Piercing");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("ChopBlocksThreshold", chopBlocksThreshold);
		nbt.setBoolean("Piercing", pierceArmor);
	}

	@Override
	public boolean onCollideWithSolid() {
		if (chopBlocksThreshold > 0)
			return super.onCollideWithSolid();
		else return false;
	}

	@Override
	public void spawnExplosionParticles(World world, Vec3d pos) {

	}

	@Override
	public void spawnDissipateParticles(World world, Vec3d pos) {
		if (world.isRemote)
			for (int i = 0; i < 1; i += 1 / getWidth())
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(AvatarEntityUtils.getMiddleOfEntity(this)).vel(world.rand.nextGaussian() / 20,
						world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20).time(6).clr(0.8F, 0.8F, 0.8F)
						.scale(getWidth() * 5F).collide(true).spawn(world);
	}

	@Override
	public void spawnPiercingParticles(World world, Vec3d pos) {

	}

	@Override
	public boolean shouldDissipate() {
		return true;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public boolean isPiercing() {
		return getAbility() instanceof AbilityAirblade && AbilityData.get(getOwner(), getAbility().getName()).getLevel() == 3;
	}

	@Override
	public Vec3d getKnockbackMult() {
		return new Vec3d(STATS_CONFIG.airbladeSettings.push, STATS_CONFIG.airbladeSettings.push * 2, STATS_CONFIG.airbladeSettings.push);
	}

	@Override
	public int getFireTime() {
		return 0;
	}

	@Override
	public Vec3d getKnockback() {
		return super.getKnockback();
	}

	@Override
	public double getExpandedHitboxWidth() {
		return getAvgSize() / 3;
	}

	@Override
	public double getExpandedHitboxHeight() {
		return getAvgSize() / 3;
	}

	@Override
	public SoundEvent[] getSounds() {
		SoundEvent[] events = new SoundEvent[1];
		events[0] = SoundEvents.BLOCK_FIRE_EXTINGUISH;
		return events;
	}

	@Override
	public float getVolume() {
		return super.getVolume() * 6;
	}


}
