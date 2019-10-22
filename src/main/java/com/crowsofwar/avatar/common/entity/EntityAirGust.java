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
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAirGust extends EntityOffensive {

	private boolean destroyProjectiles, pushStone, pushIronTrapDoor, pushIronDoor;

	public EntityAirGust(World world) {
		super(world);
		setSize(1f, 1f);
		putsOutFires = true;
		this.noClip = true;
		this.pushStoneButton = pushStone;
		this.pushDoor = pushIronDoor;
		this.pushTrapDoor = pushIronTrapDoor;
	}


	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		//	airGrab = nbt.getBoolean("AirGrab");
		destroyProjectiles = nbt.getBoolean("DestroyProjectiles");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		//nbt.setBoolean("AirGrab", airGrab);
		nbt.setBoolean("DestroyProjectiles", destroyProjectiles);

	}

	@Override
	public BendingStyle getElement() {
		return new Airbending();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		//Not sure why I have this here, but I'm too lazy to test it right now.
		if (ticksExisted <= 2) {
			this.pushStoneButton = pushStone;
			this.pushDoor = pushIronDoor;
			this.pushTrapDoor = pushIronTrapDoor;
		}

		setVelocity(velocity().times(0.95));
		if (velocity().sqrMagnitude() < 0.5 * 0.5)
			Dissipate();

		float expansionRate = 1f / 80;
		setEntitySize(getAvgSize() + expansionRate);
		//Rendering.
		if (world.isRemote) {
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(AvatarEntityUtils.getMiddleOfEntity(this)).vel(motionX + world.rand.nextGaussian() / 50, motionY + world.rand.nextGaussian() / 50,
					motionZ + world.rand.nextGaussian() / 50).time(14 + AvatarUtils.getRandomNumberInRange(0, 12)).clr(0.85F, 0.85F, 0.85F)
					.scale(getAvgSize() * 4F).collide(true).spawn(world);
		}
	}


	public boolean doesDestroyProjectiles() {
		return destroyProjectiles;
	}

	public void setDestroyProjectiles(boolean destroyProjectiles) {
		this.destroyProjectiles = destroyProjectiles;
	}

	public void setPushStone(boolean pushStone) {
		this.pushStone = pushStone;
	}

	public void setPushIronDoor(boolean pushIronDoor) {
		this.pushIronDoor = pushIronDoor;
	}

	public void setPushIronTrapDoor(boolean pushIronTrapDoor) {
		this.pushIronTrapDoor = pushIronTrapDoor;
	}

	@Override
	public boolean pushButton(boolean pushStone) {
		return true;
	}

	@Override
	public boolean pushLever() {
		return true;
	}

	@Override
	public boolean pushDoor(boolean pushIron) {
		return true;
	}

	@Override
	public boolean pushTrapdoor(boolean pushIron) {
		return true;
	}

	@Override
	public boolean pushGate() {
		return true;
	}

	@Override
	public float getDamage() {
		return 0;
	}

	@Override
	public boolean isPiercing() {
		return false;
	}

	@Override
	public boolean shouldExplode() {
		return false;
	}

	@Override
	public boolean shouldDissipate() {
		return true;
	}

	@Override
	public Vec3d getKnockbackMult() {
		return new Vec3d(2, 2, 2);
	}

	@Override
	public void spawnDissipateParticles(World world, Vec3d pos) {

	}


	@Override
	public void applyElementalContact(AvatarEntity entity) {
		super.applyElementalContact(entity);
		entity.onAirContact();
		if (destroyProjectiles) {
			if (entity instanceof IOffensiveEntity && ((IOffensiveEntity) entity).getDamage() < 3 * getAvgSize() ||
					entity instanceof EntityOffensive && getAvgSize() < 1.25 * getAvgSize()) {
				((IOffensiveEntity) entity).Dissipate(entity);
				Dissipate();
			}
		}
	}

	@Override
	public SoundEvent[] getSounds() {
		SoundEvent[] events = new SoundEvent[1];
		events[0] = SoundEvents.BLOCK_FIRE_EXTINGUISH;
		return events;
	}

	@Override
	public float getVolume() {
		return super.getVolume() * 0.5F;
	}

	@Override
	public int getFireTime() {
		return 0;
	}

	@Override
	public Vec3d getKnockback() {
		return new Vec3d(motionX, motionY + 0.3, motionZ);
	}
}
