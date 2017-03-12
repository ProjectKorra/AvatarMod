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

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityFireArc extends EntityArc {
	
	private static final Vector GRAVITY = new Vector(0, -9.81 / 60, 0);
	
	private static final DataParameter<FireArcBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityFireArc.class, FireArcBehavior.DATA_SERIALIZER);
	
	private float damageMult;
	
	public EntityFireArc(World world) {
		super(world);
		this.damageMult = 1;
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new FireArcBehavior.Idle());
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (inWater || worldObj.isRainingAt(getPosition())) {
			setDead();
			Random random = new Random();
			if (worldObj.isRemote) {
				int particles = random.nextInt(3) + 4;
				for (int i = 0; i < particles; i++) {
					worldObj.spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ,
							(random.nextGaussian() - 0.5) * 0.05 + motionX / 10, random.nextGaussian() * 0.08,
							(random.nextGaussian() - 0.5) * 0.05 + motionZ / 10);
				}
			}
			worldObj.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
					SoundCategory.PLAYERS, 1, random.nextFloat() * 0.3f + 1.1f, false);
		}
		FireArcBehavior newBehavior = (FireArcBehavior) getBehavior().onUpdate(this);
		if (getBehavior() != newBehavior) setBehavior(newBehavior);
	}
	
	public static EntityFireArc findFromId(World world, int id) {
		return (EntityFireArc) EntityArc.findFromId(world, id);
	}
	
	@Override
	public void setDead() {
		super.setDead();
		if (getOwner() != null) {
			BendingData data = Bender.create(getOwner()).getData();
			data.removeStatusControl(StatusControl.THROW_FIRE);
			if (!worldObj.isRemote) {
				data.removeStatusControl(StatusControl.THROW_FIRE);
			}
		}
	}
	
	@Override
	protected void onCollideWithBlock() {
		if (!worldObj.isRemote) {
			int x = (int) Math.floor(posX);
			int y = (int) Math.floor(posY);
			int z = (int) Math.floor(posZ);
			worldObj.setBlockState(new BlockPos(x, y, z), Blocks.FIRE.getDefaultState());
		}
	}
	
	@Override
	protected Vector getGravityVector() {
		return GRAVITY;
	}
	
	@Override
	public ControlPoint createControlPoint(float size) {
		return new FireControlPoint(this, size, 0, 0, 0);
	}
	
	public FireArcBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}
	
	public void setBehavior(FireArcBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}
	
	public float getDamageMult() {
		return damageMult;
	}
	
	public void setDamageMult(float damageMult) {
		this.damageMult = damageMult;
	}
	
	public static class FireControlPoint extends ControlPoint {
		
		public FireControlPoint(EntityArc arc, float size, double x, double y, double z) {
			super(arc, size, x, y, z);
		}
		
		@Override
		protected void onCollision(Entity entity) {
			entity.setFire(3);
			arc.setDead();
		}
		
	}
	
}
