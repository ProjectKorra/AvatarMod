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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityFireArc extends EntityArc<EntityFireArc.FireControlPoint> {

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
	public boolean onMajorWaterContact() {
		spawnExtinguishIndicators();
		setDead();
		return true;
	}

	@Override
	public boolean onMinorWaterContact() {
		spawnExtinguishIndicators();
		setDead();
		return true;
	}

	private void cleanup() {
		if (getOwner() != null) {
			BendingData data = Bender.get(getOwner()).getData();
			data.removeStatusControl(StatusControl.THROW_FIRE);
			if (!world.isRemote) {
				data.removeStatusControl(StatusControl.THROW_FIRE);
			}
		}
	}

	@Override
	public void setDead() {
		super.setDead();
		cleanup();
	}

	@Override
	protected void onCollideWithEntity(Entity entity) {
		if (entity instanceof AvatarEntity && getBehavior() instanceof FireArcBehavior.Thrown) {
			((AvatarEntity) entity).onFireContact();
		}
	}

	@Override
	public boolean onCollideWithSolid() {

		if (!(getBehavior() instanceof FireArcBehavior.Thrown)) {
			return false;
		}

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

			setDead();

		}
		return true;
	}

	@Override
	public FireControlPoint createControlPoint(float size, int index) {
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

	public boolean getCreateBigFire() {
		return createBigFire;
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
