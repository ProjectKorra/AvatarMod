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

import static com.crowsofwar.avatar.common.bending.StatusControl.THROW_WATER;

import java.util.Random;

import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.data.WaterArcBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityWaterArc extends EntityArc {
	
	private static final DataParameter<WaterArcBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityWaterArc.class, WaterArcBehavior.DATA_SERIALIZER);
	
	/**
	 * The amount of ticks since last played splash sound. -1 for splashable.
	 */
	private int lastPlayedSplash;
	
	private float damageMult;
	
	public EntityWaterArc(World world) {
		super(world);
		setSize(.5f, .5f);
		this.lastPlayedSplash = -1;
		this.damageMult = 1;
		this.putsOutFires = true;
	}
	
	public float getDamageMult() {
		return damageMult;
	}
	
	public void setDamageMult(float mult) {
		this.damageMult = mult;
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new WaterArcBehavior.Idle());
	}
	
	@Override
	public void onCollideWithSolid() {
		
		if (!worldObj.isRemote && getBehavior() instanceof WaterArcBehavior.Thrown) {
			if (tryDestroy()) {
				setDead();
			}
		}
		
		if (worldObj.isRemote) {
			Random random = new Random();
			
			double xVel = 0, yVel = 0, zVel = 0;
			double offX = 0, offY = 0, offZ = 0;
			
			if (isCollidedVertically) {
				
				xVel = 5;
				yVel = 3.5;
				zVel = 5;
				offX = 0;
				offY = 0.6;
				offZ = 0;
				
			} else {
				
				xVel = 7;
				yVel = 2;
				zVel = 7;
				offX = 0.6;
				offY = 0.2;
				offZ = 0.6;
				
			}
			
			xVel *= 0.0;
			yVel *= 0.0;
			zVel *= 0.0;
			
			int particles = random.nextInt(3) + 4;
			for (int i = 0; i < particles; i++) {
				
				worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, posX + random.nextGaussian() * offX,
						posY + random.nextGaussian() * offY + 0.2, posZ + random.nextGaussian() * offZ,
						random.nextGaussian() * xVel, random.nextGaussian() * yVel,
						random.nextGaussian() * zVel);
				
			}
			
		}
		
	}
	
	@Override
	protected Vector getGravityVector() {
		// Gravity is added in Behavior
		return Vector.ZERO;
	}
	
	@Override
	public void onUpdate() {
		
		super.onUpdate();
		if (lastPlayedSplash > -1) {
			lastPlayedSplash++;
			if (lastPlayedSplash > 20) lastPlayedSplash = -1;
		}
		getBehavior().onUpdate(this);
		
		if (inWater && getBehavior() instanceof WaterArcBehavior.PlayerControlled) {
			// try to go upwards
			for (double i = 0.1; i <= 3; i += 0.05) {
				BlockPos pos = new Vector(this).add(0, i, 0).toBlockPos();
				if (worldObj.getBlockState(pos).getBlock() == Blocks.AIR) {
					setPosition(posX, posY + i, posZ);
					inWater = false;
					break;
				}
			}
		}
		
	}
	
	public static EntityWaterArc findFromId(World world, int id) {
		for (Object obj : world.loadedEntityList) {
			if (obj instanceof EntityWaterArc && ((EntityWaterArc) obj).getId() == id)
				return (EntityWaterArc) obj;
		}
		return null;
	}
	
	@Override
	protected ControlPoint createControlPoint(float size) {
		return new WaterControlPoint(this, size, 0, 0, 0);
	}
	
	public boolean canPlaySplash() {
		return lastPlayedSplash == -1;
	}
	
	public void playSplash() {
		worldObj.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.PLAYERS, 0.3f,
				1.5f, false);
		lastPlayedSplash = 0;
	}
	
	public WaterArcBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}
	
	public void setBehavior(WaterArcBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}
	
	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof WaterArcBehavior.PlayerControlled ? getOwner() : null;
	}
	
	@Override
	protected double getControlPointTeleportDistanceSq() {
		return 9;
	}
	
	@Override
	public boolean tryDestroy() {
		if (getOwner() != null) {
			BendingData data = Bender.create(getOwner()).getData();
			data.removeStatusControl(THROW_WATER);
		}
		return true;
	}
	
	public static class WaterControlPoint extends ControlPoint {
		
		public WaterControlPoint(EntityArc arc, float size, double x, double y, double z) {
			super(arc, size, x, y, z);
		}
		
		@Override
		protected void onCollision(Entity entity) {}
		
	}
	
}
