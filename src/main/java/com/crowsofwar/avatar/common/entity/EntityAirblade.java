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
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class EntityAirblade extends AvatarEntity {

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
		this.chopBlocksThreshold = -1;
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		setVelocity(velocity().times(0.96));
		if (!world.isRemote && velocity().sqrMagnitude() <= .9) {
			setDead();
		}
		if (!world.isRemote && inWater) {
			setDead();
		}

		if (!world.isRemote && chopBlocksThreshold >= 0) {
			breakCollidingBlocks();
		}

		if (!isDead && !world.isRemote) {
			List<Entity> collidedList = world.getEntitiesWithinAABB(Entity.class,
					getEntityBoundingBox());

			if (!collidedList.isEmpty()) {

				Entity collided = collidedList.get(0);

				if (collided instanceof AvatarEntity) {
					((AvatarEntity) collided).onAirContact();
				} else if (collided instanceof EntityLivingBase) {
					handleCollision((EntityLivingBase) collided);
				}

			}
		}

	}

	private void handleCollision(EntityLivingBase collided) {

		DamageSource source = AvatarDamageSource.causeAirbladeDamage(collided, getOwner());
		if (pierceArmor) {
			source.setDamageBypassesArmor();
		}
		boolean successfulHit = collided.attackEntityFrom(source, damage);

		Vector motion = velocity();
		motion = motion.times(STATS_CONFIG.airbladeSettings.push).withY(0.08);
		collided.addVelocity(motion.x(), motion.y(), motion.z());

		if (getOwner() != null) {
			BendingData data = getOwnerBender().getData();
			data.getAbilityData("airblade").addXp(SKILLS_CONFIG.airbladeHit);
		}

		if (successfulHit) {
			BattlePerformanceScore.addMediumScore(getOwner());
		}

		if (chainAttack) {
			if (successfulHit) {

				AxisAlignedBB aabb = getEntityBoundingBox().grow(10);
				Predicate<EntityLivingBase> notFriendly =//
						entity -> entity != collided && entity != getOwner();

				List<EntityLivingBase> nextTargets = world.getEntitiesWithinAABB
						(EntityLivingBase.class, aabb, notFriendly);

				nextTargets.sort(AvatarUtils.getSortByDistanceComparator
						(this::getDistance));

				if (!nextTargets.isEmpty()) {
					EntityLivingBase nextTarget = nextTargets.get(0);
					Vector direction = Vector.getEntityPos(nextTarget).minus(this.position());
					setVelocity(direction.normalize().times(velocity().magnitude() *
							0.5));
				}

			}
		} else if (!world.isRemote) {
			setDead();
		}

	}

	/**
	 * When the airblade can break blocks, checks any blocks that the airblade
	 * collides with and tries to break them
	 */
	private void breakCollidingBlocks() {
		// Hitbox expansion (in each direction) to destroy blocks before the
		// airblade collides with them
		double expansion = 0.1;
		AxisAlignedBB hitbox = getEntityBoundingBox().grow(expansion, expansion, expansion);

		for (int ix = 0; ix <= 1; ix++) {
			for (int iz = 0; iz <= 1; iz++) {

				double x = ix == 0 ? hitbox.minX : hitbox.maxX;
				double y = hitbox.minY;
				double z = iz == 0 ? hitbox.minZ : hitbox.maxZ;
				BlockPos pos = new BlockPos(x, y, z);

				tryBreakBlock(world.getBlockState(pos), pos);

			}
		}
	}

	/**
	 * Assuming the airblade can break blocks, tries to break the block.
	 */
	private void tryBreakBlock(IBlockState state, BlockPos pos) {
		if (state.getBlock() == Blocks.AIR) {
			return;
		}

		float hardness = state.getBlockHardness(world, pos);
		if (hardness <= chopBlocksThreshold) {
			breakBlock(pos);
			setVelocity(velocity().times(0.5));
		}
	}

	@Override
	public void setDead() {
		super.setDead();
	}

	public Bender getOwnerBender() {
		return Bender.get(getOwner());
	}

	public void setDamage(float damage) {
		this.damage = damage;
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

	public boolean isChainAttack() {
		return chainAttack;
	}

	public void setChainAttack(boolean chainAttack) {
		this.chainAttack = chainAttack;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		damage = nbt.getFloat("Damage");
		chopBlocksThreshold = nbt.getFloat("ChopBlocksThreshold");
		pierceArmor = nbt.getBoolean("Piercing");
		chainAttack = nbt.getBoolean("ChainAttack");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Damage", damage);
		nbt.setFloat("ChopBlocksThreshold", chopBlocksThreshold);
		nbt.setBoolean("Piercing", pierceArmor);
		nbt.setBoolean("ChainAttack", chainAttack);
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

}
