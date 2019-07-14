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

import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.fire.AbilityFireShot;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Objects;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

/**
 * @author CrowsOfWar
 */
@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo")
public class EntityFlames extends AvatarEntity implements ILightProvider {

	private boolean reflect;
	private boolean lightTrailingFire;
	private double damageMult;

	public EntityFlames(World worldIn) {
		super(worldIn);
		setSize(0.1f, 0.1f);
		this.setsFires = true;
		this.lightTrailingFire = false;
	}

	@Override
	public BendingStyle getElement() {
		return new Firebending();
	}


	@Override
	public void onCollideWithEntity(Entity entity) {
		if (entity instanceof AvatarEntity) {
			((AvatarEntity) entity).onFireContact();
		}
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		ignoreFrustumCheck = true;
		setVelocity(velocity().times(0.99999));

		if (velocity().sqrMagnitude() <= 0.5 * 0.5) setDead();

		Raytrace.Result raytrace = Raytrace.raytrace(world, position(), velocity().normalize(), 0.5,
				true);
		if (raytrace.hitSomething()) {
			EnumFacing sideHit = raytrace.getSide();
			if (reflect) {
				setVelocity(velocity().reflect(new Vector(Objects.requireNonNull(sideHit))).times(0.5));

				// Try to light fires
				/*if (sideHit != EnumFacing.DOWN && !world.isRemote) {

					BlockPos bouncingOff = getPosition().add(-sideHit.getXOffset(),
							-sideHit.getYOffset(),
							-sideHit.getZOffset());

					if (sideHit == EnumFacing.UP || world.getBlockState(bouncingOff).getBlock()
							.isFlammable(world, bouncingOff, sideHit)) {

						world.setBlockState(getPosition(), Blocks.FIRE.getDefaultState());

					}

				}**/

			}
		}
		if (lightTrailingFire) {
			if (AvatarUtils.getRandomNumberInRange(1, 10) <= 5) {
				BlockPos pos = getPosition();
				if (Blocks.FIRE.canPlaceBlockAt(world, pos) && world.getBlockState(pos).getBlock() == Blocks.AIR) {
					world.setBlockState(pos, Blocks.FIRE.getDefaultState());
				}
				BlockPos pos2 = getPosition().down();
				if (Blocks.FIRE.canPlaceBlockAt(world, pos2) && world.getBlockState(pos2).getBlock() == Blocks.AIR) {
					world.setBlockState(pos2, Blocks.FIRE.getDefaultState());
				}
			}
		}

		if (!world.isRemote) {
			if (getOwner() != null) {
				AbilityData abilityData = AbilityData.get(getOwner(), getAbility().getName());

				List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox(),
						entity -> entity != getOwner()
								&& !(entity instanceof EntityFlames));

				for (Entity entity : collided) {

					entity.setFire((int) (3F * 1 + abilityData.getTotalXp() / 100f));

					// Add extra damage
					// Adding 0 since even though this doesn't affect health, will
					// cause mobs to aggro

					float additionalDamage = 0;
					if (abilityData.getTotalXp() >= 50) {
						additionalDamage = 2 + (abilityData.getTotalXp() - 50) / 25;
					}
					additionalDamage *= damageMult;
					if (entity.attackEntityFrom(
							AvatarDamageSource.causeFireShotDamage(entity, getOwner()),
							additionalDamage)) {
						BattlePerformanceScore.addSmallScore(getOwner());
					}

				}

				abilityData.addXp(SKILLS_CONFIG.fireShotHit * collided.size());
				if (getAbility() instanceof AbilityFireShot) {
					if (!collided.isEmpty() && !abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) setDead();
				}
				else if (!collided.isEmpty()) setDead();
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
			}
			else return false;
		}
		else {
			setDead();
			spawnExtinguishIndicators();
			return true;
		}
	}

	@Override
	public boolean canBePushed() {
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

	public void setDamageMult(double damageMult) {
		this.damageMult = damageMult;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public boolean onCollideWithSolid() {
		if (getAbility() instanceof AbilityFireShot && getOwner() != null) {
			AbilityData data = AbilityData.get(getOwner(), getAbility().getName());
			if (!data.isMasterPath(AbilityData.AbilityTreePath.FIRST))
				setDead();
			return !data.isMasterPath(AbilityData.AbilityTreePath.FIRST);
		}
		setDead();
		return true;

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
}
