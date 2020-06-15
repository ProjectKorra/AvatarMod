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
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAirGust extends EntityOffensive {

	private boolean piercesEnemies = false, slowProjectiles = false, destroyProjectiles = false,
			pushStone, pushIronTrapDoor, pushIronDoor;

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

		if (world.isRemote && getOwner() != null) {
			for (int i = 0; i < 4; i++) {
				Vec3d mid = AvatarEntityUtils.getMiddleOfEntity(this);
				double spawnX = mid.x + world.rand.nextGaussian() / 20;
				double spawnY = mid.y + world.rand.nextGaussian() / 20;
				double spawnZ = mid.z + world.rand.nextGaussian() / 20;
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45,
						world.rand.nextGaussian() / 45).time(4 + AvatarUtils.getRandomNumberInRange(0, 6)).clr(0.95F, 0.95F, 0.95F, 0.075F).spawnEntity(getOwner())
						.scale(getAvgSize() * (1 / getAvgSize() + 1)).element(getElement()).collide(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 45 + motionX, world.rand.nextGaussian() / 45 + motionY,
						world.rand.nextGaussian() / 45 + motionZ).time(14 + AvatarUtils.getRandomNumberInRange(0, 10)).clr(0.95F, 0.95F, 0.95F, 0.075F).spawnEntity(getOwner())
						.scale(getAvgSize() * (1 / getAvgSize() + 0.5F)).element(getElement()).collide(true).spawn(world);
			}
			for (int i = 0; i < 2; i++) {
				Vec3d pos = Vector.getOrthogonalVector(getLookVec(), i * 180 + (ticksExisted % 360) * 20 *
						(1 / getAvgSize()), getAvgSize() / 1.5F).toMinecraft();
				Vec3d velocity;
				Vec3d entityPos = AvatarEntityUtils.getMiddleOfEntity(this);

				pos = pos.add(entityPos);
				velocity = pos.subtract(entityPos).normalize();
				velocity = velocity.scale(AvatarUtils.getSqrMagnitude(getVelocity()) / 400000);
				double spawnX = pos.x;
				double spawnY = pos.y;
				double spawnZ = pos.z;
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x,
						world.rand.nextGaussian() / 80 + velocity.y, world.rand.nextGaussian() / 80 + velocity.z)
						.time(6 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(0.95F, 0.95F, 0.95F, 0.1F).spawnEntity(getOwner())
						.scale(0.75F * getAvgSize() * (1 / getAvgSize())).element(new Airbending()).collide(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 80 + velocity.x,
						world.rand.nextGaussian() / 80 + velocity.y, world.rand.nextGaussian() / 80 + velocity.z)
						.time(10 + AvatarUtils.getRandomNumberInRange(0, 6)).clr(0.95F, 0.95F, 0.95F, 0.1F).spawnEntity(getOwner())
						.scale(0.75F * getAvgSize() * (1 / getAvgSize())).element(new Airbending()).collide(true).spawn(world);

			}
		}

		//Not sure why I have this here, but I'm too lazy to test it right now.
		if (ticksExisted <= 2) {
			this.pushStoneButton = pushStone;
			this.pushDoor = pushIronDoor;
			this.pushTrapDoor = pushIronTrapDoor;
		}
	}

	@Override
	public void pushLevers(BlockPos pos) {
		super.pushLevers(pos);
		if (getOwner() != null && getAbility() != null)
			AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit());
	}

	@Override
	public void pushButtons(BlockPos pos) {
		super.pushButtons(pos);
		if (getOwner() != null && getAbility() != null)
			AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit());
	}

	@Override
	public void pushTrapDoors(BlockPos pos) {
		super.pushTrapDoors(pos);
		if (getOwner() != null && getAbility() != null)
			AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit());
	}

	@Override
	public void pushDoors(BlockPos pos) {
		super.pushDoors(pos);
		if (getOwner() != null && getAbility() != null)
			AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit());
	}

	@Override
	public void pushGates(BlockPos pos) {
		super.pushGates(pos);
		if (getOwner() != null && getAbility() != null)
			AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit());
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
		return new Vec3d(0.75, 3, 0.75);
	}

	@Override
	public void applyPiercingCollision() {
		if (!world.isRemote)
			super.applyPiercingCollision();
	}

	@Override
	public void spawnDissipateParticles(World world, Vec3d pos) {
		if (world.isRemote && getOwner() != null) {
			for (int i = 0; i < 8; i++) {
				Vec3d mid = AvatarEntityUtils.getMiddleOfEntity(this);
				double spawnX = mid.x + world.rand.nextGaussian() / 10;
				double spawnY = mid.y + world.rand.nextGaussian() / 10;
				double spawnZ = mid.z + world.rand.nextGaussian() / 10;
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20,
						world.rand.nextGaussian() / 20).time(4).clr(0.95F, 0.95F, 0.95F, 0.1F).spawnEntity(getOwner())
						.scale(getAvgSize() * 1.25F).element(getElement()).collide(true).spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20,
						world.rand.nextGaussian() / 20).time(12).clr(0.95F, 0.95F, 0.95F, 0.1F).spawnEntity(getOwner())
						.scale(getAvgSize() * 1.25F).element(getElement()).collide(true).spawn(world);
			}
		}
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
	public boolean setVelocity() {
		return true;
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
		} else if (entity instanceof EntityAirBubble)
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
		} else if (getOwner() != null && getOwner().getTeam() != null && entity.getTeam() != null && entity.getTeam() == getOwner().getTeam()) {
			return false;
		} else
			return true;
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

	@Override
	public double getExpandedHitboxWidth() {
		return Math.max(0.35, Math.min(getAvgSize() / 2, 1));
	}

	@Override
	public double getExpandedHitboxHeight() {
		return Math.max(0.35, Math.min(getAvgSize() / 2, 1));
	}

	@Override
	public Vec3d getKnockback() {
		double x = getKnockbackMult().x * motionX;
		double y = Math.max(0.25, Math.min((motionY + 0.15) * getKnockbackMult().y, 0.25));
		double z = getKnockbackMult().z * motionZ;

		return new Vec3d(x, y, z);
	}


	@Override
	public boolean canDamageEntity(Entity entity) {
		return canCollideWith(entity);
	}
}
