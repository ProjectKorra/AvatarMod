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
import com.crowsofwar.avatar.common.bending.fire.AbilityFireShot;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.blocks.BlockTemp;
import com.crowsofwar.avatar.common.blocks.BlockUtils;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */

// todo:Colored Flux
@Optional.Interface(iface = "com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity", modid = "hammercore")
public class EntityFlames extends EntityOffensive implements IGlowingEntity, ICustomHitbox {

	private boolean reflect;
	private boolean lightTrailingFire;

	public EntityFlames(World worldIn) {
		super(worldIn);
		setSize(0.1f, 0.1f);
		this.lightTrailingFire = false;
		this.reflect = false;
		this.ignoreFrustumCheck = true;
		this.lightTnt = true;
		this.setsFires = true;
	}

	@Override
	public BendingStyle getElement() {
		return new Firebending();
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (entity instanceof EntityItem)
			AvatarEntityUtils.smeltItemEntity((EntityItem) entity, getTier());
		super.onCollideWithEntity(entity);
	}

	@Override
	public boolean onCollideWithSolid() {
		if (collided && setsFires)
			setFires();
		return super.onCollideWithSolid();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		motionX *= 0.975;
		motionY *= 0.975;
		motionZ *= 0.975;


		if (velocity().sqrMagnitude() <= 0.5 * 0.5) Dissipate();

		Raytrace.Result raytrace = Raytrace.raytrace(world, position(), velocity().normalize(), 0.5,
				true);
		if (raytrace.hitSomething()) {
			EnumFacing sideHit = raytrace.getSide();
			if (reflect) {
				setVelocity(velocity().reflect(new Vector(Objects.requireNonNull(sideHit))).times(0.5));

				// Try to light fires
				if (sideHit != EnumFacing.DOWN && !world.isRemote) {

					BlockPos bouncingOff = getPosition().add(-sideHit.getXOffset(),
							-sideHit.getYOffset(),
							-sideHit.getZOffset());

					if (sideHit == EnumFacing.UP || world.getBlockState(bouncingOff).getBlock()
							.isFlammable(world, bouncingOff, sideHit)) {

						world.setBlockState(getPosition(), Blocks.FIRE.getDefaultState());

					}

				}

			}
		}

		if (world.isRemote) {
			for (double i = 0; i < width; i += 0.05) {
				Random random = new Random();
				AxisAlignedBB boundingBox = getEntityBoundingBox();
				double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
				double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
				double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
						world.rand.nextGaussian() / 60).time(12 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(255, 10, 5)
						.scale(getAvgSize() * 6).element(getElement()).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
						world.rand.nextGaussian() / 60).time(12 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
						20 + AvatarUtils.getRandomNumberInRange(0, 60), 10)
						.scale(getAvgSize() * 6).element(getElement()).spawn(world);
			}
		}

		if (lightTrailingFire && !world.isRemote) {
			if (AvatarUtils.getRandomNumberInRange(1, 10) <= 5) {
				BlockPos pos = getPosition();
				if (BlockUtils.canPlaceFireAt(world, pos)) {
					BlockTemp.createTempBlock(world, pos, 20, Blocks.FIRE.getDefaultState());
				}
				BlockPos pos2 = getPosition().down();
				if (BlockUtils.canPlaceFireAt(world, pos2)) {
					BlockTemp.createTempBlock(world, pos2, 20, Blocks.FIRE.getDefaultState());
				}
			}
		}
	}

	@Override
	public DamageSource getDamageSource(Entity target, EntityLivingBase owner) {
		return AvatarDamageSource.causeFireShotDamage(target, owner);
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return super.canCollideWith(entity) || entity instanceof EntityItem;
	}

	@Override
	public double getExpandedHitboxWidth() {
		return 0.35;
	}

	@Override
	public double getExpandedHitboxHeight() {
		return 0.35;
	}

	@Override
	public boolean onAirContact() {
		if (getAbility() instanceof AbilityFireShot && getOwner() != null) {
			AbilityData data = AbilityData.get(getOwner(), getAbility().getName());
			if (!data.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				setDead();
				spawnExtinguishIndicators();
				return true;
			} else return false;
		} else {
			setDead();
			spawnExtinguishIndicators();
			return true;
		}
	}

	@Override
	public boolean shouldDissipate() {
		return true;
	}

	@Override
	public boolean shouldExplode() {
		return false;
	}

	@Override
	public boolean onMajorWaterContact() {
		setDead();
		spawnExtinguishIndicators();
		return true;
	}

	@Override
	public boolean onMinorWaterContact() {
		if (getAbility() instanceof AbilityFireShot && getOwner() != null) {
			AbilityData data = AbilityData.get(getOwner(), getAbility().getName());
			if (!data.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				setDead();
				// Spawn less extinguish indicators in the rain to prevent spamming
				if (rand.nextDouble() < 0.4) {
					spawnExtinguishIndicators();
				}
				return true;

			} else return false;
		} else {
			setDead();
			// Spawn less extinguish indicators in the rain to prevent spamming
			if (rand.nextDouble() < 0.4) {
				spawnExtinguishIndicators();
			}
			return true;
		}
	}

	public void setReflect(boolean reflect) {
		this.reflect = reflect;
	}

	public void setTrailingFire(boolean fire) {
		this.lightTrailingFire = fire;
	}

	@Override
	public void applyElementalContact(AvatarEntity entity) {
		entity.onFireContact();
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public int getNumberofParticles() {
		return 15;
	}

	@Override
	public double getParticleSpeed() {
		return 0.04;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}


	@Override
	public Vec3d getKnockbackMult() {
		return new Vec3d(STATS_CONFIG.fireShotSetttings.push * 2, STATS_CONFIG.fireShotSetttings.push / 2, STATS_CONFIG.fireShotSetttings.push * 2);
	}

	@Override
	public boolean isPiercing() {
		if (getOwner() != null && getAbility() instanceof AbilityFireShot) {
			AbilityData data = AbilityData.get(getOwner(), getAbility().getName());
			if (data != null)
				return data.isMasterPath(AbilityData.AbilityTreePath.FIRST);
		}
		return false;
	}


	@Override
	@Optional.Method(modid = "hammercore")
	public ColoredLight produceColoredLight(float partialTicks) {
		return ColoredLight.builder().pos(this).color(1f, 0f, 0f, 1f).radius(10f).build();
	}

	@Override
	public Vec3d calculateIntercept(Vec3d origin, Vec3d endpoint, float fuzziness) {
		return null;
	}

	@Override
	public boolean contains(Vec3d point) {
		return false;
	}
}
