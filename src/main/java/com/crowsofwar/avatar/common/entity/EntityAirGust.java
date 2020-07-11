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
import com.crowsofwar.avatar.common.bending.air.AbilityAirGust;
import com.crowsofwar.avatar.common.bending.air.AbilityAirblade;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.bending.air.AbilityAirGust.PIERCES_ENEMIES;

public class EntityAirGust extends EntityOffensive {

	private boolean piercesEnemies = false, slowProjectiles = false, destroyProjectiles = false,
			pushStone, pushIronTrapDoor, pushIronDoor;
	private float exWidth, exHeight;

	public EntityAirGust(World world) {
		super(world);
		setSize(1f, 1f);
		putsOutFires = true;
		this.noClip = true;
		this.pushStoneButton = pushStone;
		this.pushDoor = pushIronDoor;
		this.pushTrapDoor = pushIronTrapDoor;
		this.exWidth = 0.5F;
		this.exHeight = 0.5F;
		setDamage(0);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		slowProjectiles = nbt.getBoolean("SlowProjectiles");
		destroyProjectiles = nbt.getBoolean("DestroyProjectiles");
		piercesEnemies = nbt.getBoolean("PiercesEnemies");
		exWidth = nbt.getFloat("Expanded Width");
		exHeight = nbt.getFloat("Expanded Height");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("SlowProjectiles", slowProjectiles);
		nbt.setBoolean("DestroyProjectiles", destroyProjectiles);
		nbt.setBoolean("PiercesEnemies", piercesEnemies);
		nbt.setFloat("Expanded Width", exWidth);
		nbt.setFloat("Expanded Height", exHeight);
	}

	@Override
	public BendingStyle getElement() {
		return new Airbending();
	}

	@Override
	public DamageSource getDamageSource(Entity target, EntityLivingBase owner) {
		return AvatarDamageSource.causeAirDamage(target, owner);
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

	@Override
	public boolean pushLevers(BlockPos pos) {
		if (super.pushLevers(pos))
			if (getElement() instanceof Airbending)
				if (getOwner() != null && getAbility() != null)
					AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit() / 2);
		return super.pushLevers(pos);
	}

	@Override
	public boolean pushButtons(BlockPos pos) {
		if (super.pushButtons(pos))
			if (getElement() instanceof Airbending)
				if (getOwner() != null && getAbility() != null)
					AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit() / 2);
		return super.pushButtons(pos);

	}

	@Override
	public boolean pushTrapDoors(BlockPos pos) {
		if (super.pushTrapDoors(pos))
			if (getElement() instanceof Airbending)
				if (getOwner() != null && getAbility() != null)
					AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit() / 2);
		return super.pushTrapDoors(pos);

	}

	@Override
	public boolean pushDoors(BlockPos pos) {
		if (super.pushGates(pos))
			if (getElement() instanceof Airbending)
				if (getOwner() != null && getAbility() != null)
					AbilityData.get(getOwner(), getAbility().getName()).addXp(getXpPerHit() / 2);
		return super.pushGates(pos);

	}

	public void setExpandedWidth(float width) {
		this.exWidth = width;
	}

	public float getExpandedWidth() {
		return this.exWidth;
	}

	public void setExpandedHeight(float height) {
		this.height = height;
	}

	public float getExpandedHeight() {
		return this.exHeight;
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
	public boolean isPiercing() {
		if (!piercesEnemies) {
			if (getAbility() instanceof AbilityAirGust && getOwner() != null) {
				piercesEnemies = getAbility().getBooleanProperty(PIERCES_ENEMIES, AbilityData.get(getOwner(), getAbility().getName()));
			}
		}
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
		return new Vec3d(2, 3, 2);
	}


	@Override
	public void setDead() {
		super.setDead();
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
	public boolean multiHit() {
		return getAbility() instanceof AbilityAirblade && getOwner() != null && AbilityData.get(getOwner(), getAbility().getName()).isDynamicMasterLevel(AbilityData.AbilityTreePath.SECOND);
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
	public boolean onCollideWithSolid() {
			if (super.onCollideWithSolid()) {
				setVelocity(Vector.ZERO);
				setLifeTime(1);
			}
			return super.onCollideWithSolid();
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
		return exWidth;
	}

	@Override
	public double getExpandedHitboxHeight() {
		return exHeight;
	}

	@Override
	public Vec3d getKnockback() {
		double x = getKnockbackMult().x * motionX;
		double y = Math.max(0.25, Math.min((motionY + 0.15) * getKnockbackMult().y, 0.25));
		double z = getKnockbackMult().z * motionZ;
		double scale = 0.5F + getTier() / 4F;
		return new Vec3d(x * scale, y * scale, z * scale);
	}


	@Override
	public boolean canDamageEntity(Entity entity) {
		return canCollideWith(entity);
	}
}
