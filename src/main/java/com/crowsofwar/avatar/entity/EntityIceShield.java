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
package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class EntityIceShield extends EntityShield {

	private double normalBaseValue;

	private double damageMult;
	private boolean targetMobs;
	/**
	 * When shattering the ice shield, this represents the pitch angles which shards are thrown
	 * from. Only set on the server thread.
	 */
	private float[] pitchAngles;

	public EntityIceShield(World world) {
		super(world);
	}

	public void shatter() {

		world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1,
				1);
		setDead();

		EntityLivingBase owner = getOwner();

		// Shoot shards at mobs
		int shardsLeft = 12;

		if (targetMobs) {

			double range = 40;
			AxisAlignedBB aabb = new AxisAlignedBB(//
					owner.posX - range / 2, owner.posY - range / 2, owner.posZ - range / 2, //
					owner.posX + range / 2, owner.posY + range / 2, owner.posZ + range / 2);
			List<EntityMob> targets = world.getEntitiesWithinAABB(EntityMob.class, aabb);

			int shardsAtMobs = Math.min(targets.size(), 5);
			for (int i = 0; i < shardsAtMobs; i++) {
				shootShardAt(targets.get(i));
				shardsLeft--;
			}

		}

		shootShardsAround(owner, 4, shardsLeft);

	}

	@Override
	public EntityLivingBase getController() {
		return getOwner();
	}

	@Override
	public boolean isShield() {
		return true;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		EntityLivingBase owner = getOwner();
		if (owner != null) {
			/*IAttributeInstance speed = owner.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			if (speed.getBaseValue() != 0) {
				normalBaseValue = speed.getBaseValue();
				speed.setBaseValue(0);
			}**/
			owner.setPositionAndUpdate(posX, posY, posZ);
			owner.motionX *= 0;
			owner.motionZ *= 0;
			this.motionX *= 0;
			if (onGround)
				this.motionY *= 0;
			this.motionZ *= 0;
		}
	}

	@Override
	public void setDead() {
		super.setDead();
		EntityLivingBase owner = getOwner();
		/*if (owner != null) {
			IAttributeInstance speed = owner.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
			if (speed.getBaseValue() == 0) {
				speed.setBaseValue(normalBaseValue);
			}
		}**/
	}

	@Override
	public boolean onFireContact() {
		setHealth(getHealth() - 0.2f);
		return getHealth() <= 0;
	}

	@Override
	public boolean canPush() {
		return false;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		normalBaseValue = nbt.getDouble("NormalBaseValue");
		damageMult = nbt.getDouble("DamageMult");
		setTargetMobs(nbt.getBoolean("TargetMobs"));

		NBTTagList pitchAngleList = nbt.getTagList("PitchAngles", 5);
		pitchAngles = new float[pitchAngleList.tagCount()];
		for (int i = 0; i < pitchAngleList.tagCount(); i++) {
			pitchAngles[i] = pitchAngleList.getFloatAt(i);
		}

	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setDouble("NormalBaseValue", normalBaseValue);
		nbt.setDouble("DamageMult", damageMult);
		nbt.setBoolean("TargetMobs", isTargetMobs());

		NBTTagList pitchAngleList = new NBTTagList();
		for (float pitchAngle : pitchAngles) {
			pitchAngleList.appendTag(new NBTTagFloat(pitchAngle));
		}
		nbt.setTag("PitchAngles", pitchAngleList);

	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	protected float getChiDamageCost() {
		return STATS_CONFIG.chiIceShieldProtect;
	}

	@Override
	protected float getProtectionXp() {
		return SKILLS_CONFIG.iceShieldProtected;
	}

	@Override
	protected String getAbilityName() {
		return "ice_burst";
	}

	@Override
	protected void onDeath() {
		shatter();

		if (getOwner() != null) {
			BendingData.get(getOwner()).removeStatusControl(StatusControlController.SHIELD_SHATTER);
		}

	}

	/**
	 * Shoots a single ice shard at the given target, using physics equations to properly aim.
	 */
	private void shootShardAt(Entity target) {

		EntityLivingBase owner = getOwner();
		Vector targetPos = Vector.getEyePos(target);
		Vector ownerPos = Vector.getEyePos(owner);

		Vector direction = Vector.getRotationTo(ownerPos, targetPos);
		float yaw = (float) Math.toDegrees(direction.y());

		double horizDist = targetPos.withY(0).dist(ownerPos.withY(0));
		double vertDist = targetPos.y() - ownerPos.y();
		float pitch = (float) Math.toDegrees(Vector.getProjectileAngle(20, 20, horizDist,
				vertDist));

		EntityIceShard shard = new EntityIceShard(world);
		shard.setLocationAndAngles(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ, yaw, pitch);
		shard.aim(yaw, pitch, 20);
		shard.setDamageMult(damageMult);
		world.spawnEntity(shard);

	}

	/**
	 * Shoot ice shards around the entity.
	 *
	 * @param yawAngles   Spacing for yaw angles
	 * @param shardsLimit Limit the number of ice shards to shoot. Note that the actual shards shot is
	 *                    also limited by the number of possible angles to shoot at (<code>yawAngles
	 *                    * pitchAngles.length</code>), so this acts as a limiter rather than the actual
	 *                    amount of shards to shoot.
	 */
	private void shootShardsAround(EntityLivingBase shooter, int yawAngles, int shardsLimit) {

		// pitchAngles is not set on the client-side, so if this code is executed on the client, it
		// would cause an NPE
		if (world.isRemote) {
			return;
		}

		for (int i = 0; i < yawAngles; i++) {
			float yaw = 360f / yawAngles * i;
			for (int j = 0; j < pitchAngles.length; j++) {

				if (shardsLimit == 0) {
					break;
				}

				float pitch = pitchAngles[j];

				EntityIceShard shard = new EntityIceShard(world);
				shard.setLocationAndAngles(shooter.posX, shooter.posY + shooter.getEyeHeight(), shooter.posZ,
						0, 0);
				shard.aim(yaw + shooter.rotationYaw, pitch + shooter.rotationPitch, 53);
				shard.setDamageMult(damageMult);
				world.spawnEntity(shard);

				shardsLimit--;

			}
		}
	}

	public double getDamageMult() {
		return damageMult;
	}

	public void setDamageMult(double damageMult) {
		this.damageMult = damageMult;
	}

	public boolean isTargetMobs() {
		return targetMobs;
	}

	public void setTargetMobs(boolean targetMobs) {
		this.targetMobs = targetMobs;
	}

	public float[] getPitchAngles() {
		return pitchAngles;
	}

	public void setPitchAngles(float[] pitchAngles) {
		this.pitchAngles = pitchAngles;
	}
}
