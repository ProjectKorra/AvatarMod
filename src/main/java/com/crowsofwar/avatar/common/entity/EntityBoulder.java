package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityBoulder extends AvatarEntity {
	public static final DataParameter<Integer> SYNC_BOULDERS_LEFT= EntityDataManager.createKey(
			EntityBoulder.class, DataSerializers.VARINT);

	private float Damage;
	private float speed;
	//Just a test to see if I need to add this to the circle code
	private float Radius;
	private float ticksAlive;
	private float Health;
	private float knockBack;
	public float size;
	private int bouldersLeft;
	//How far away the entity is from the player.

	public void setHealth (float health) {
		this.Health = health;
	}

	public void setTicksAlive (float ticks) {
		this.ticksAlive = ticks;
	}

	public void setRadius (float radius) {
		this.Radius = radius;
	}

	public void setDamage (float damage) {
		this.Damage = damage;
	}

	public void setSpeed (float speed) {
		this.speed = speed;
	}

	public void setKnockBack (float knockBack){
		this.knockBack = knockBack;
	}

	public void setSize (float size) {
		this.size = size;
	}

	public void setBouldersLeft (int boulders) {
		dataManager.set(SYNC_BOULDERS_LEFT, boulders);
	}

	public float getSize () {
		return this.size;
	}

	public int getBouldersLeft(){
		return dataManager.get(SYNC_BOULDERS_LEFT);
	}

	public EntityBoulder(World world) {
		super(world);
		this.Damage = 0.5F;
		this.Radius = 2;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BOULDERS_LEFT, bouldersLeft);
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
		float Angle = speed % 360;
		this.posX = Math.cos(Math.toRadians(Angle)) * Radius;
		this.posZ = Math.sin(Math.toRadians(Angle)) * Radius;
		speed ++;
     	//Need to make speed increase by whatever I set it too originally
		if (Health <= 0) {
			this.setDead();
		}
		if (this.ticksAlive % this.ticksExisted == 0){
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
				data.getAbilityData("boulder_ring").addXp(
						(data.getAbilityData("boulder_ring").getLevel()/3) * attacked);
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
					data.getAbilityData("boulder_ring").addXp(3 - data.getAbilityData("boulder_ring").getLevel()/3);
					BattlePerformanceScore.addSmallScore(getOwner());
				}

			}
			if (entity instanceof EntityArrow){
				this.Health -= 1;
			}
			if (entity instanceof AvatarEntity && ((AvatarEntity) entity).isProjectile()){
				entity.setDead();
				this.Health -= 1;
			}
		}
	}

	@Override
	protected boolean canCollideWith(Entity entity) {
		if ((entity instanceof EntityBender || entity instanceof EntityPlayer) && this.getOwner() == entity) {
			return false;
		}
		return entity instanceof EntityLivingBase || super.canCollideWith(entity);
	}

	private boolean attackEntity(Entity entity) {
		if (!(entity instanceof EntityItem && entity.ticksExisted <= 10)) {
			DamageSource ds = AvatarDamageSource.causeFloatingBlockDamage(entity, getOwner());
			float damage = Damage;
			this.Health -= 0.1;
			return entity.attackEntityFrom(ds, damage);
		}

		return false;
	}

	private void pushEntity(Entity entity) {
		Vector entityPos = Vector.getEntityPos(entity);
		Vector direction = entityPos.minus(this.position());
		Vector velocity = direction.times(knockBack);
		entity.addVelocity(velocity.x(), velocity.y(), velocity.z());
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

}
