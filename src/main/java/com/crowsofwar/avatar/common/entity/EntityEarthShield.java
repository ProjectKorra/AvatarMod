package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityEarthShield extends AvatarEntity {
	private float Damage;
	private Vector startingPosition;
	private float i;
	private float Radius;
	private float ticksAlive;
	private float Health;
	//How far away the entity is from the player.
	/**
	 * @param world
	 */
	public EntityEarthShield(World world) {
		super(world);
		float size = 1F;
		this.Damage = 0.5F;
		i = 0;
		this.Radius = 2;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		setDead();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		float Angle = i % 360;
		this.posX = Math.cos(Math.toRadians(Angle)) * Radius;
		this.posZ = Math.sin(Math.toRadians(Angle)) * Radius;
		i++;
		if (Health <= 0) {
			this.setDead();
		}

		// amount of entities which were successfully attacked
		int attacked = 0;

		// Push collided entities back
		if (!world.isRemote) {
			List<Entity> collided = world.getEntitiesInAABBexcluding(this, getEntityBoundingBox(),
					entity -> entity != getOwner());
			if (!collided.isEmpty()) {
				for (Entity entity : collided) {
					if (attackEntity(entity)) {
						attacked++;
					}
				}
			}
		}

		if (!world.isRemote && getOwner() != null) {
			BendingData data = BendingData.get(getOwner());
			if (data != null) {
				data.getAbilityData("earthspike").addXp(SKILLS_CONFIG.earthspikeHit * attacked);
			}
		}
	}
	@Override
	protected void onCollideWithEntity(Entity entity) {
		if (!world.isRemote) {
			pushEntity(entity);
			if (attackEntity(entity)) {

				if (getOwner() != null) {
					BendingData data = BendingData.get(getOwner());
					data.getAbilityData("earth_shield").addXp(3 - data.getAbilityData("earth_shield").getLevel()/3);
					BattlePerformanceScore.addMediumScore(getOwner());
				}

			}
		}
	}

	private boolean attackEntity(Entity entity) {
		if (!(entity instanceof EntityItem && entity.ticksExisted <= 10)) {
			DamageSource ds = AvatarDamageSource.causeFloatingBlockDamage(entity, getOwner());
			float damage = Damage;
			return entity.attackEntityFrom(ds, damage);
		}

		return false;
	}

	private void pushEntity(Entity entity) {
		Vector entityPos = Vector.getEntityPos(entity);
		Vector direction = entityPos.minus(this.position());
		Vector velocity = direction.times(0.01);
		entity.addVelocity(velocity.x(), velocity.y(), velocity.z());
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

}
