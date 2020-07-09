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

import com.crowsofwar.avatar.common.config.ConfigStats;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class EntityRavine extends EntityOffensive {

	private Vector initialPosition;

	private float damageMult;
	private double maxTravelDistanceSq;
	private boolean breakBlocks;
	private boolean dropEquipment;

	/**
	 * @param world
	 */
	public EntityRavine(World world) {
		super(world);
		setSize(0.125F, 0.125F);
		this.damageMult = 1;
		this.noClip = true;
	}

	@Override
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return true;
	}

	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return true;
	}

	@Override
	public double getExpandedHitboxWidth() {
		return 0.5;
	}

	@Override
	public double getExpandedHitboxHeight() {
		return 0.5;
	}

	public void setDamageMult(float mult) {
		this.damageMult = mult;
	}

	public void setDistance(double dist) {
		maxTravelDistanceSq = dist * dist;
	}

	public void setBreakBlocks(boolean breakBlocks) {
		this.breakBlocks = breakBlocks;
	}

	public void setDropEquipment(boolean dropEquipment) {
		this.dropEquipment = dropEquipment;
	}

	public double getSqrDistanceTravelled() {
		return position().sqrDist(initialPosition);
	}


	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public Vec3d getKnockback() {
		return super.getKnockback();
	}

	@Override
	public Vec3d getKnockbackMult() {
		return new Vec3d(STATS_CONFIG.ravineSettings.push, STATS_CONFIG.ravineSettings.push * 2,
				STATS_CONFIG.ravineSettings.push);
	}

	private void spawnEntity() {
		if (!world.isRemote) {
			BlockPos pos = new BlockPos(prevPosX, prevPosY, prevPosZ);

			if (world.getBlockState(pos.down()).getBlockHardness(world, pos.down()) != -1 && !world.isAirBlock(pos.down()) && world.isBlockNormalCube(pos.down(), false)
					// Checks that the block above is not solid, since this causes the falling sand to vanish.
					&& !world.isBlockNormalCube(pos, false)) {

				// Falling blocks do the setting block to air themselves.
				EntityFallingBlock fallingblock = new EntityFallingBlock(world, prevPosX, prevPosY - 1, prevPosZ,
						world.getBlockState(new BlockPos(prevPosX, prevPosY - 1, prevPosZ)));
				fallingblock.motionY = 0.3;
				world.spawnEntity(fallingblock);
			}
		}
	}
	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();

		if (initialPosition == null) {
			initialPosition = position();
		}

		if (ticksExisted % 2 == 0)
			spawnEntity();

		if (!world.isRemote && getSqrDistanceTravelled() > maxTravelDistanceSq) {
			Dissipate();
		}

		BlockPos below = getPosition().offset(EnumFacing.DOWN);
		Block belowBlock = world.getBlockState(below).getBlock();

		if (ticksExisted % 3 == 0) world.playSound(posX, posY, posZ,
				world.getBlockState(below).getBlock().getSoundType().getBreakSound(),
				SoundCategory.PLAYERS, 1, 1, false);

		//Lowers the ravine if there's a step below
		if (!ConfigStats.STATS_CONFIG.bendableBlocks.contains(belowBlock) && world.getEntitiesWithinAABB(EntityFallingBlock.class,
				getEntityBoundingBox().grow(1, 1, 1)).isEmpty()) {
			if (!STATS_CONFIG.bendableBlocks.contains(world.getBlockState(getPosition().down(2)).getBlock()))
				Dissipate();
			else {
				setPosition(position().minusY(1));
			}
		}

		// Destroy non-solid blocks in the ravine
		IBlockState inBlock = world.getBlockState(getPosition());
		if (inBlock.getBlock() != Blocks.AIR && !inBlock.isFullBlock()) {
			if (inBlock.getBlockHardness(world, getPosition()) == 0) {
				breakBlock(getPosition());
			} else {
				Dissipate();
			}

		}

		if (!world.isRemote && breakBlocks) {
			BlockPos last = new BlockPos(prevPosX, prevPosY, prevPosZ);
			if (!last.equals(getPosition()) && !last.equals(initialPosition.toBlockPos())) {

				world.destroyBlock(last.down(), true);

				double travel = Math.sqrt(getSqrDistanceTravelled() / maxTravelDistanceSq);
				double chance = -(travel - 0.5) * (travel - 0.5) + 0.25;
				chance *= 2;

				if (rand.nextDouble() <= chance) {
					world.destroyBlock(last.down(2), true);
				}

			}
		}

	}

	@Override
	public float getDamage() {
		return damageMult * STATS_CONFIG.ravineSettings.damage;
	}

	@Override
	public boolean isPiercing() {
		return true;
	}

	@Override
	public boolean shouldDissipate() {
		return true;
	}

	@Override
	public int getFireTime() {
		return 0;
	}

	@Override
	public void Dissipate() {
		if (world.isRemote && world.getBlockState(getPosition().down()) != null)
			world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, posX, posY, posZ, world.rand.nextGaussian() / 20,
					world.rand.nextDouble() / 20, world.rand.nextGaussian() / 20,
					Block.getStateId(world.getBlockState(getPosition())));
		setDead();
	}

	@Override
	public float getXpPerHit() {
		return SKILLS_CONFIG.ravineHit;
	}

	@Override
	public DamageSource getDamageSource(Entity target, EntityLivingBase owner) {
		return AvatarDamageSource.causeRavineDamage(target, owner);
	}

	@Override
	public void spawnExplosionParticles(World world, Vec3d pos) {

	}

	@Override
	public void spawnDissipateParticles(World world, Vec3d pos) {

	}

	@Override
	public void spawnPiercingParticles(World world, Vec3d pos) {

	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		super.onCollideWithEntity(entity);
		if (canCollideWith(entity)) {
			if (dropEquipment && entity instanceof EntityLivingBase) {

				EntityLivingBase living = (EntityLivingBase) entity;

				for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {

					ItemStack stack = living.getItemStackFromSlot(slot);
					if (!stack.isEmpty()) {
						double chance = slot.getSlotType() == Type.HAND ? 40 : 20;
						if (rand.nextDouble() * 100 <= chance) {
							living.entityDropItem(stack, 0);
							living.setItemStackToSlot(slot, ItemStack.EMPTY);
						}
					}

				}

			}
		}
	}

	@Override
	public boolean onCollideWithSolid() {
		// Destroy if in a block
		IBlockState inBlock = world.getBlockState(getPosition());
		if (inBlock.isFullBlock()) {
			inBlock = world.getBlockState(getPosition().up());
			if (inBlock.getBlock() == Blocks.AIR || !inBlock.isFullBlock() && inBlock.getBlockHardness(world, getPosition()) == 0) {
				setPosition(position().plusY(2));
				return false;
			}
			else return true;

		}
		return false;
	}
}
