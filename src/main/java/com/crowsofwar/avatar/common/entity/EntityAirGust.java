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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAirGust extends EntityOffensive {

	private boolean piercesEnemies = false, slowProjectiles = false, destroyProjectiles = false, pushStone, pushIronTrapDoor, pushIronDoor;

	public EntityAirGust(World world) {
		super(world);
		setSize(1f, 1f);
		putsOutFires = true;
		this.noClip = true;
		this.pushStoneButton = pushStone;
		this.pushDoor = pushIronDoor;
		this.pushTrapDoor = pushIronTrapDoor;
		setDamage(0);
	}


	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		slowProjectiles = nbt.getBoolean("SlowProjectiles");
		destroyProjectiles = nbt.getBoolean("DestroyProjectiles");
		piercesEnemies = nbt.getBoolean("PiercesEnemies");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("SlowProjectiles", slowProjectiles);
		nbt.setBoolean("DestroyProjectiles", destroyProjectiles);
		nbt.setBoolean("PiercesEnemies", piercesEnemies);
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
	}


	public void setSlowProjectiles(boolean slowProjectiles) {
		this.slowProjectiles = slowProjectiles;
	}

	public void setPiercesEnemies(boolean piercesEnemies) {
		this.piercesEnemies = piercesEnemies;
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
	public boolean isPiercing() {
		return piercesEnemies;
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
		return new Vec3d(1.5, 2, 1.5);
	}

	@Override
	public void spawnDissipateParticles(World world, Vec3d pos) {
		//We don't need to spawn any since particle collision handles it
	}

	@Override
	public void spawnPiercingParticles(World world, Vec3d pos) {
		//We don't need to spawn any since particle collision handles it
	}

	@Override
	public void applyElementalContact(AvatarEntity entity) {
		super.applyElementalContact(entity);
		entity.onAirContact();
		if (destroyProjectiles) {
			if (entity instanceof IOffensiveEntity && ((IOffensiveEntity) entity).getDamage() < 6 * getAvgSize() ||
					entity instanceof EntityOffensive && getAvgSize() < 1.25 * getAvgSize() || (entity.isProjectile() && entity.velocity().sqrMagnitude() <
					velocity().sqrMagnitude()) || entity.getTier() < getTier()) {
				((IOffensiveEntity) entity).Dissipate(entity);
			}
			if (entity.getTier() == getTier()) {
				Dissipate();
				if (entity instanceof IOffensiveEntity) {
					((IOffensiveEntity) entity).Dissipate(entity);
				}
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
		return super.getVolume() * 1.5F;
	}

	@Override
	public int getFireTime() {
		return 0;
	}


	@Override
	public void onCollideWithEntity(Entity entity) {
		super.onCollideWithEntity(entity);
		if (entity instanceof AvatarEntity && ((AvatarEntity) entity).isProjectile() || entity instanceof EntityArrow || entity instanceof EntityThrowable) {
			if (slowProjectiles) {
				entity.motionX *= 0.4;
				entity.motionY *= 0.4;
				entity.motionZ *= 0.4;
			}
		}
		if (entity instanceof EntityAirBubble && ((EntityAirBubble) entity).getTier() <= getTier()) {
			super.onCollideWithEntity(((EntityAirBubble) entity).getOwner());
			if (!isPiercing())
				Dissipate();
		}
		else if (entity instanceof EntityAirBubble)
			Dissipate();
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		if (entity == getOwner()) {
			return false;
		} else if (entity instanceof AvatarEntity && ((AvatarEntity) entity).getOwner() == getOwner()) {
			return false;
		} else if (entity instanceof EntityLivingBase && entity.getControllingPassenger() == getOwner()) {
			return false;
		} else if (getOwner() != null && getOwner().getTeam() != null && entity.getTeam() == getOwner().getTeam()) {
			return false;
		} else if (entity instanceof EntityEnderCrystal) {
			return true;
		} else
			return true;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public double getExpandedHitboxWidth() {
		return Math.max(0.3, Math.min(getAvgSize() / 3, 1));
	}

	@Override
	public double getExpandedHitboxHeight() {
		return Math.max(0.3, Math.min(getAvgSize() / 3, 1));
	}

	@Override
	public Vec3d getKnockback() {
		double x = Math.min(getKnockbackMult().x * motionX, motionX * 2);
		double y = Math.min(0.5, (motionY + 0.15) * getKnockbackMult().y);
		double z = Math.min(getKnockbackMult().z * motionZ, motionZ * 2);
		if (velocity().sqrMagnitude() > getAvgSize() * 15) {
			x = Math.min(x, motionX * 0.75F);
			z = Math.min(z, motionZ * 0.75F);
		}
		return new Vec3d(x, y, z);
	}



	@Override
	public boolean canDamageEntity(Entity entity) {
		return canCollideWith(entity);
	}
}
