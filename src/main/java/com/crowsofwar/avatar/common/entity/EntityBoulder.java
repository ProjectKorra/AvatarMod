package com.crowsofwar.avatar.common.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.data.*;
import com.crowsofwar.avatar.common.entity.data.*;
import com.crowsofwar.gorecore.util.Vector;

import java.util.Objects;

public class EntityBoulder extends AvatarEntity {

	public static final DataParameter<Integer> SYNC_BOULDERS_LEFT = EntityDataManager.createKey(EntityBoulder.class, DataSerializers.VARINT);

	public static final DataParameter<BoulderBehavior> SYNC_BEHAVIOR = EntityDataManager
					.createKey(EntityBoulder.class, BoulderBehavior.DATA_SERIALIZER);

	public static final DataParameter<Integer> SYNC_SIZE = EntityDataManager.createKey(EntityBoulder.class, DataSerializers.VARINT);

	public static final DataParameter<Float> SYNC_KNOCKBACK = EntityDataManager.createKey(EntityBoulder.class, DataSerializers.FLOAT);

	public static final DataParameter<Float> SYNC_DAMAGE = EntityDataManager.createKey(EntityBoulder.class, DataSerializers.FLOAT);
	public float size;
	private AxisAlignedBB expandedHitbox;
	private float Damage;
	private float speed;
	//Just a test to see if I need to add this to the circle code
	private float Radius;
	private float ticksAlive;
	private float Health;
	private float knockBack;
	private int bouldersLeft;
	//How far away the entity is from the player.

	public EntityBoulder(World world) {
		super(world);
		setSize(0.8f, 0.8f);
		Damage = 0.1F;

	}

	public void setHealth(float health) {
		Health = health;
	}

	/*public void setRadius (float radius) {
		this.Radius = radius;
	}**/

	public void setTicksAlive(float ticks) {
		ticksAlive = ticks;
	}

	public int getBouldersLeft() {
		return dataManager.get(SYNC_BOULDERS_LEFT);
	}

	public void setBouldersLeft(int boulders) {
		dataManager.set(SYNC_BOULDERS_LEFT, boulders);
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getDamage() {
		return dataManager.get(SYNC_DAMAGE);
	}

	public void setDamage(float damage) {
		dataManager.set(SYNC_DAMAGE, damage);
	}

	//public float getRadius(){
	//	return this.Radius;
	//}

	public int getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(float size) {
		dataManager.set(SYNC_SIZE, (int) size);
	}

	public float getKnockBack() {
		return dataManager.get(SYNC_KNOCKBACK);
	}

	public void setKnockBack(float knockBack) {
		dataManager.set(SYNC_KNOCKBACK, knockBack);
	}

	public BoulderBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehavior(BoulderBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new BoulderBehavior.Idle());
		dataManager.register(SYNC_SIZE, 30);
		dataManager.register(SYNC_BOULDERS_LEFT, bouldersLeft);
		dataManager.register(SYNC_KNOCKBACK, 0.1F);
		dataManager.register(SYNC_DAMAGE, 0.1F);
	}

	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof BoulderBehavior.PlayerControlled ? getOwner() : null;
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		if (getBehavior() == null) {
			setDead();
		}
		setBehavior((BoulderBehavior) getBehavior().onUpdate(this));

		if (Health <= 0) {
			setDead();
		}

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setDamage(nbt.getFloat("Damage"));
		setBehavior((BoulderBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Damage", getDamage());
		nbt.setInteger("Behavior", getBehavior().getId());
	}

	public AxisAlignedBB getExpandedHitbox() {
		return expandedHitbox;
	}

	@Override
	public void setEntityBoundingBox(AxisAlignedBB bb) {
		super.setEntityBoundingBox(bb);
		expandedHitbox = bb.grow(0.35, 0.35, 0.35);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0 || pass == 1;
	}

	private void removeStatCtrl() {
		if (getOwner() != null) {
			BendingData data = Objects.requireNonNull(Bender.get(getOwner())).getData();
			data.removeStatusControl(StatusControl.THROW_CLOUDBURST);
		}
	}

	@Override
	public boolean onCollideWithSolid() {
		Health--;
		return true;

	}

	@Override
	public void setDead() {
		super.setDead();
		if (!world.isRemote && isDead) {
			Thread.dumpStack();
		}
	}

	@Override
	public void onCollideWithEntity(Entity entity) {
		if (!world.isRemote) {
			pushEntity(entity);
			if (attackEntity(entity)) {
				if (getOwner() != null) {
					BendingData data = BendingData.get(getOwner());
					data.getAbilityData("boulder_ring").addXp(3 - (float) data.getAbilityData("boulder_ring").getLevel() / 3);
					BattlePerformanceScore.addSmallScore(getOwner());
				}

			}
			if (entity instanceof EntityArrow) {
				Health -= 1;
			}
			if (entity instanceof AvatarEntity && ((AvatarEntity) entity).isProjectile()) {
				entity.setDead();
				Health -= 1;
			}
		}
	}

	public boolean attackEntity(Entity entity) {
		if (!(entity instanceof EntityItem && entity.ticksExisted <= 10)) {
			DamageSource ds = AvatarDamageSource.causeFloatingBlockDamage(entity, getOwner());
			float damage = Damage;
			Health -= 0.1;
			return entity.attackEntityFrom(ds, damage);
		}

		return false;
	}

	public void pushEntity(Entity entity) {
		Vector entityPos = Vector.getEntityPos(entity);
		Vector direction = entityPos.minus(position());
		Vector velocity = direction.times(knockBack);
		entity.addVelocity(velocity.x(), velocity.y(), velocity.z());
	}

	@Override
	public boolean isProjectile() {
		return true;
	}

}
