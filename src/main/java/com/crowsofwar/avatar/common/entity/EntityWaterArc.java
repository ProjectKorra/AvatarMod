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

import java.util.Random;
import java.util.function.Consumer;

import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.water.WaterbendingState;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.data.WaterArcBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
	protected void onCollideWithBlock() {
		
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
		getBehavior().setEntity(this);
		getBehavior().onUpdate();
		
		for (int x = 0; x <= 1; x++) {
			for (int z = 0; z <= 1; z++) {
				BlockPos pos = new BlockPos(posX + x * width, posY, posZ + z * width);
				if (worldObj.getBlockState(pos).getBlock() == Blocks.FIRE) {
					worldObj.setBlockToAir(pos);
					worldObj.playSound(posX, posY, posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH,
							SoundCategory.PLAYERS, 1, 1, false);
				}
			}
		}
		
		// if (worldObj.getBlockState(getPosition()).getBlock() == Blocks.FIRE)
		// {
		// worldObj.setBlockToAir(getPosition());
		// worldObj.playSound(posX, posY, posZ,
		// SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1,
		// 1, false);
		// }
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
	protected double getControlPointTeleportDistanceSq() {
		return 9;
	}
	
	public static class WaterControlPoint extends ControlPoint {
		
		public WaterControlPoint(EntityArc arc, float size, double x, double y, double z) {
			super(arc, size, x, y, z);
		}
		
		@Override
		protected void onCollision(Entity entity) {}
		
	}
	
	@Override
	protected Consumer<EntityPlayer> getNewOwnerCallback() {
		return newOwner -> {
			WaterbendingState state = (WaterbendingState) AvatarPlayerData.fetcher()
					.fetchPerformance(newOwner).getBendingState(BendingType.WATERBENDING.id());
			state.setWaterArc(this);
		};
	}
	
}
