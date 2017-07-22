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

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.FireArcBehavior;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class EntityFireArc extends EntityArc {
	
	private static final Vector GRAVITY = new Vector(0, -9.81 / 60, 0);
	
	private static final DataParameter<FireArcBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityFireArc.class, FireArcBehavior.DATA_SERIALIZER);
	
	private float damageMult;
	private boolean createBigFire;
	
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
		FireArcBehavior newBehavior = (FireArcBehavior) getBehavior().onUpdate(this);
		if (getBehavior() != newBehavior) setBehavior(newBehavior);
	}

	@Override
	public void onWaterContact() {
		setDead();
		Random random = new Random();
		if (world.isRemote) {
			int particles = random.nextInt(3) + 4;
			for (int i = 0; i < particles; i++) {
				world.spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ,
						(random.nextGaussian() - 0.5) * 0.05 + motionX / 10, random.nextGaussian() * 0.08,
						(random.nextGaussian() - 0.5) * 0.05 + motionZ / 10);
			}
		}
		world.playSound(posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
				SoundCategory.PLAYERS, 1, random.nextFloat() * 0.3f + 1.1f, false);
	}

	@Override
	public void onRaining() {
		onWaterContact();
	}

	public static EntityFireArc findFromId(World world, int id) {
		return (EntityFireArc) EntityArc.findFromId(world, id);
	}
	
	@Override
	public void setDead() {
		super.setDead();
		if (getOwner() != null) {
			BendingData data = Bender.get(getOwner()).getData();
			data.removeStatusControl(StatusControl.THROW_FIRE);
			if (!world.isRemote) {
				data.removeStatusControl(StatusControl.THROW_FIRE);
			}
		}
	}
	
	@Override
	public void onCollideWithSolid() {
		if (!world.isRemote) {
			int x = (int) Math.floor(posX);
			int y = (int) Math.floor(posY);
			int z = (int) Math.floor(posZ);
			BlockPos pos = new BlockPos(x, y, z);
			world.setBlockState(pos, Blocks.FIRE.getDefaultState());
			
			if (createBigFire) {
				for (EnumFacing dir : EnumFacing.HORIZONTALS) {
					BlockPos offsetPos = pos.offset(dir);
					if (world.isAirBlock(offsetPos)) {
						world.setBlockState(offsetPos, Blocks.FIRE.getDefaultState());
					}
				}
			}
			
			if (tryDestroy()) {
				setDead();
			}
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
	
	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof FireArcBehavior.PlayerControlled ? getOwner() : null;
	}
	
	public float getDamageMult() {
		return damageMult;
	}
	
	public void setDamageMult(float damageMult) {
		this.damageMult = damageMult;
	}
	
	public void setCreateBigFire(boolean createBigFire) {
		this.createBigFire = createBigFire;
	}
	
	public static class FireControlPoint extends ControlPoint {
		
		public FireControlPoint(EntityArc arc, float size, double x, double y, double z) {
			super(arc, size, x, y, z);
		}
		
	}
	
}
