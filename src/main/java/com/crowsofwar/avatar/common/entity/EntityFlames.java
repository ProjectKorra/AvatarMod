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

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;

import java.util.List;
import java.util.Random;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entityproperty.EntityPropertyMotion;
import com.crowsofwar.avatar.common.entityproperty.IEntityProperty;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityFlames extends AvatarEntity {
	
	private final IEntityProperty<Vector> propVelocity;
	
	/**
	 * The owner, null client side
	 */
	private EntityPlayer owner;
	
	/**
	 * @param worldIn
	 */
	public EntityFlames(World worldIn) {
		super(worldIn);
		this.propVelocity = new EntityPropertyMotion(this);
		setSize(0.1f, 0.1f);
	}
	
	public EntityFlames(World world, EntityPlayer owner) {
		this(world);
		this.owner = owner;
	}
	
	@Override
	protected void entityInit() {
		
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// TODO Support saving/loading of EntityFlames
		setDead();
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		setDead();
	}
	
	@Override
	public void onUpdate() {
		
		Vector velocityPerTick = velocity().dividedBy(20);
		moveEntity(velocityPerTick.x(), velocityPerTick.y(), velocityPerTick.z());
		
		velocity().mul(0.94);
		
		if (velocity().sqrMagnitude() <= 0.5 * 0.5 || isCollided) setDead();
		
		Raytrace.Result raytrace = Raytrace.raytrace(worldObj, position(), velocity().copy().normalize(), 0.3,
				true);
		if (raytrace.hitSomething()) {
			EnumFacing sideHit = raytrace.getSide();
			velocity().set(velocity().reflect(new Vector(sideHit)).times(0.5));
		}
		
		if (!worldObj.isRemote) {
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(owner);
			AbilityData abilityData = data.getAbilityData(BendingAbility.ABILITY_FLAMETHROWER);
			
			List<Entity> collided = worldObj.getEntitiesInAABBexcluding(this, getEntityBoundingBox(),
					entity -> entity != owner && !(entity instanceof EntityFlames));
			
			for (Entity entity : collided) {
				if (abilityData.getXp() >= 50) {
					entity.attackEntityFrom(DamageSource.inFire, 2 + (abilityData.getXp() - 50) / 25);
				}
				entity.setFire((int) (3 * 1 + abilityData.getXp() / 100f));
			}
			
			abilityData.addXp(SKILLS_CONFIG.flamethrowerHit * collided.size());
			if (!collided.isEmpty()) setDead();
		}
		
		handleWaterMovement();
		if (inWater) {
			setDead();
			Random random = new Random();
			if (worldObj.isRemote) {
				worldObj.spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ,
						(random.nextGaussian() - 0.5) * 0.05 + motionX / 10, random.nextGaussian() * 0.08,
						(random.nextGaussian() - 0.5) * 0.05 + motionZ / 10);
			}
			worldObj.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
					SoundCategory.PLAYERS, 0.3f, random.nextFloat() * 0.3f + 1.1f, false);
		}
		
	}
	
	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {}
	
}
