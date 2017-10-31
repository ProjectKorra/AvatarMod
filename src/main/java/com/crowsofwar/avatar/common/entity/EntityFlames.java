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
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityFlames extends AvatarEntity {
	
	/**
	 * The owner, null client side
	 */
	private EntityLivingBase owner;
	
	private boolean lightsFires;
	
	/**
	 * @param worldIn
	 */
	public EntityFlames(World worldIn) {
		super(worldIn);
		setSize(0.1f, 0.1f);
	}
	
	public EntityFlames(World world, EntityLivingBase owner) {
		this(world);
		this.owner = owner;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		// TODO Support saving/loading of EntityFlames
		super.readEntityFromNBT(nbt);
		setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		setDead();
	}

	@Override
	protected void onCollideWithEntity(Entity entity) {
		if (entity instanceof AvatarEntity) {
			((AvatarEntity) entity).onFireContact();
		}
	}

	@Override
	public void onUpdate() {
		
		super.onUpdate();

		setVelocity(velocity().times(0.94));

		if (velocity().sqrMagnitude() <= 0.5 * 0.5 || isCollided) setDead();
		
		Raytrace.Result raytrace = Raytrace.raytrace(world, position(), velocity().normalize(), 0.3,
				true);
		if (raytrace.hitSomething()) {
			EnumFacing sideHit = raytrace.getSide();
			setVelocity(velocity().reflect(new Vector(sideHit)).times(0.5));
			
			// Try to light firest
			if (lightsFires && sideHit != EnumFacing.DOWN && !world.isRemote) {
				
				BlockPos bouncingOff = getPosition().add(-sideHit.getFrontOffsetX(),
						-sideHit.getFrontOffsetY(), -sideHit.getFrontOffsetZ());
				
				if (sideHit == EnumFacing.UP || world.getBlockState(bouncingOff).getBlock()
						.isFlammable(world, bouncingOff, sideHit)) {
					
					world.setBlockState(getPosition(), Blocks.FIRE.getDefaultState());
					
				}
				
			}
			
		}
		
		if (!world.isRemote) {
			BendingData data = Bender.get(owner).getData();
			AbilityData abilityData = data.getAbilityData("flamethrower");
			
			List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox(),
					entity -> entity != owner && !(entity instanceof EntityFlames));
			
			for (Entity entity : collided) {
				
				entity.setFire((int) (3 * 1 + abilityData.getTotalXp() / 100f));
				
				// Add extra damage
				// Adding 0 since even though this doesn't affect health, will
				// cause mobs to aggro
				
				float additionalDamage = 0;
				if (abilityData.getTotalXp() >= 50) {
					additionalDamage = 2 + (abilityData.getTotalXp() - 50) / 25;
				}
				entity.attackEntityFrom(AvatarDamageSource.causeFlamethrowerDamage(entity, owner),
						additionalDamage);
				
			}
			
			abilityData.addXp(SKILLS_CONFIG.flamethrowerHit * collided.size());
			if (!collided.isEmpty()) setDead();
		}

	}

	@Override
	public boolean onMajorWaterContact() {
		setDead();
		spawnExtinguishIndicators();
		return true;
	}

	@Override
	public boolean onMinorWaterContact() {
		setDead();

		// Spawn less extinguish indicators in the rain to prevent spamming
		if (rand.nextDouble() < 0.3) {
			spawnExtinguishIndicators();
		}
		return true;
	}

	public boolean doesLightFires() {
		return lightsFires;
	}
	
	public void setLightsFires(boolean lightsFires) {
		this.lightsFires = lightsFires;
	}
	
}
