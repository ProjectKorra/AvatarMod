/*package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.air.Airbending;
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

public class EntityAirSlash extends AvatarEntity {

		private float damage;

		/**
		 * Hardness threshold to chop blocks. For example, setting to 1.5 will allow
		 * the airblade to chop stone.
		 * <p>
		 * Note: Threshold of 0 means that the airblade can chop grass and similar
		 * blocks. Set to > 0 to avoid chopping blocks at all.
		 */
		/*private float chopBlocksThreshold;
		private boolean chainAttack;
		private boolean pierceArmor;

		public EntityAirSlash(World world) {
			super(world);
			setSize(1.5f, .2f);
			this.chopBlocksThreshold = -1;
		}

		@Override
		public BendingStyle getElement() {
			return new Airbending();
		}

		@Override
		public boolean canCollideWith(Entity entity) {
			return super.canCollideWith(entity) && entity != getOwner();
		}

		@Override
		public void onUpdate() {

			super.onUpdate();

			this.motionX = this.motionX * 0.95;
			this.motionY = this.motionY * 0.95;
			this.motionZ = this.motionZ * 0.95;

			if (!world.isRemote && velocity().sqrMagnitude() <= .7) {
				setDead();
			}

			if (this.ticksExisted > 200) {
				this.setDead();
			}
			if (!world.isRemote && inWater) {
				setDead();
			}

			if (!world.isRemote && chopBlocksThreshold >= 0 && this.collidedHorizontally) {
				breakCollidingBlocks();

			}

			if (!isDead && !world.isRemote) {
				List<Entity> collidedList = world.getEntitiesWithinAABB(Entity.class,
						getEntityBoundingBox());

				if (!collidedList.isEmpty()) {
					for (Entity collided : collidedList) {
						if (collided instanceof AvatarEntity) {
							((AvatarEntity) collided).onAirContact();
						} else if (canCollideWith(collided)) {
							handleCollision((EntityLivingBase) collided);
						}

					}
				}
			}

		}

		private void handleCollision(EntityLivingBase collided) {
			Vector motion = velocity();
			motion = motion.times(STATS_CONFIG.airbladeSettings.push).withY(0.08);
			collided.addVelocity(motion.x(), motion.y(), motion.z());

			if (canDamageEntity(collided)) {


				DamageSource source = AvatarDamageSource.causeAirbladeDamage(collided, getOwner());
				if (pierceArmor) {
					source.setDamageBypassesArmor();
				}

				boolean successfulHit = collided.attackEntityFrom(source, damage);

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
		}

		/**
		 * When the airblade can break blocks, checks any blocks that the airblade
		 * collides with and tries to break them
		 */
	/*	private void breakCollidingBlocks() {
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
	/*	private void tryBreakBlock(IBlockState state, BlockPos pos) {
			if (state.getBlock() == Blocks.AIR || !STATS_CONFIG.airBladeBreakableBlocks.contains(state.getBlock())) {
				return;
			}
			if (!this.collidedHorizontally) {
				return;
			}

			float hardness = state.getBlockHardness(world, pos);
			if (hardness <= chopBlocksThreshold) {
				breakBlock(pos);
				setVelocity(velocity().times(0.5));
			}
		}


		public Bender getOwnerBender() {
			return Bender.get(getOwner());
		}

		public void setDamage(float damage) {
			this.damage = damage;
		}


		@Override
		protected void readEntityFromNBT(NBTTagCompound nbt) {
			super.readEntityFromNBT(nbt);
			damage = nbt.getFloat("Damage");

		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound nbt) {
			super.writeEntityToNBT(nbt);
			nbt.setFloat("Damage", damage);
		}

		@Override
		public boolean isProjectile() {
			return true;
		}

}
**/