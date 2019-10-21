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
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import elucent.albedo.event.GatherLightsEvent;
import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo")
public class EntityFlames extends EntityOffensive implements ILightProvider {

	private boolean reflect;
	private boolean lightTrailingFire;

	public EntityFlames(World worldIn) {
		super(worldIn);
		setSize(0.1f, 0.1f);
		this.setsFires = true;
		this.lightTrailingFire = false;
		this.reflect = false;
		this.ignoreFrustumCheck = true;
	}

	@Override
	public BendingStyle getElement() {
		return new Firebending();
	}


	@Override
	public void onUpdate() {
		super.onUpdate();

		motionX *= 0.95;
		motionY *= 0.95;
		motionZ *= 0.95;

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
	@Optional.Method(modid = "albedo")
	public Light provideLight() {
		return Light.builder().pos(this).color(2F, 1F, 0F).radius(8).build();
	}

	@Override
	@Optional.Method(modid = "albedo")
	public void gatherLights(GatherLightsEvent event, Entity entity) {

	}

	@Override
	public Vec3d getKnockbackMult() {
		return new Vec3d(STATS_CONFIG.fireShotSetttings.push * 2, STATS_CONFIG.fireShotSetttings.push * 2, STATS_CONFIG.fireShotSetttings.push * 2);
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

}
