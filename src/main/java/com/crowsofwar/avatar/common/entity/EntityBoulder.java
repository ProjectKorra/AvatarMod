package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.air.CloudburstPowerModifier;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.BoulderBehavior;
import com.crowsofwar.avatar.common.entity.data.CloudburstBehavior;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.nestedCompound;
import static net.minecraft.network.datasync.EntityDataManager.createKey;

public class EntityBoulder extends AvatarEntity {




	public static final DataParameter<Integer> SYNC_BOULDERS_LEFT= EntityDataManager.createKey(
			EntityBoulder.class, DataSerializers.VARINT);

	public static final DataParameter<BoulderBehavior> SYNC_BEHAVIOR = EntityDataManager
			.createKey(EntityBoulder.class, BoulderBehavior.DATA_SERIALIZER);

	public static final DataParameter<Integer> SYNC_SIZE = EntityDataManager.createKey(EntityBoulder.class,
			DataSerializers.VARINT);

	private AxisAlignedBB expandedHitbox;

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

	public int getBouldersLeft(){
		return dataManager.get(SYNC_BOULDERS_LEFT);
	}

	public float getSpeed(){
		return this.speed;
	}

	public float getRadius(){
		return this.Radius;
	}

	public float getDamage() {
		return Damage;
	}

	public int getSize() {
		return dataManager.get(SYNC_SIZE);
	}

	public void setSize(int size) {
		dataManager.set(SYNC_SIZE, size);
	}


	public BoulderBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}


	public void setBehavior(BoulderBehavior behavior) {
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}


	/**
	 * @param world
	 */
	public EntityBoulder (World world) {
		super(world);
		setSize(0.8f, 0.8f);

	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_BEHAVIOR, new BoulderBehavior.Idle());
		dataManager.register(SYNC_SIZE, (int) size);
	}



	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof BoulderBehavior.PlayerControlled ? getOwner() : null;
	}




	@Override
	public void onUpdate() {

		super.onUpdate();


		setBehavior((BoulderBehavior) getBehavior().onUpdate(this));

		if (Health <= 0) {
			this.setDead();
		}
		if (this.ticksAlive <= this.ticksExisted){
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
	/**
	 * Prevents the cloudburst from colliding with arrows and other projectiles and deflecting them,
	 * which messes up the absorption mechanic.
	 */
	@Override
	public boolean canBeCollidedWith() {
		return false;
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
		return this.expandedHitbox;
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
			BendingData data = Bender.get(getOwner()).getData();
			data.removeStatusControl(StatusControl.THROW_CLOUDBURST);
		}
	}




	@Override
	public boolean onCollideWithSolid() {

		BoulderBehavior behavior = getBehavior();
		// Spawn particles
		Random random = new Random();

		AbilityData data = BendingData.get(getOwner()).getAbilityData("boulder_ring");
		if (data.isMasterPath(AbilityData.AbilityTreePath.SECOND) && rand.nextBoolean()) {

			Explosion explosion = new Explosion(world, this, posX, posY, posZ, 2, false, false);
			if (!ForgeEventFactory.onExplosionStart(world, explosion)) {
				explosion.doExplosionA();
				explosion.doExplosionB(true);
			}

		}

		setDead();
		return true;

	}





	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double d) {
		return true;
	}


	@Override
	public void setDead() {
		super.setDead();
		if (!world.isRemote && this.isDead) {
			Thread.dumpStack();
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
		else if (entity instanceof EntityBoulder && ((EntityBoulder) entity).getOwner() == this.getOwner()){
			return false;
		}
		else return entity instanceof EntityLivingBase || super.canCollideWith(entity);
	}

	public boolean attackEntity(Entity entity) {
		if (!(entity instanceof EntityItem && entity.ticksExisted <= 10)) {
			DamageSource ds = AvatarDamageSource.causeFloatingBlockDamage(entity, getOwner());
			float damage = Damage;
			this.Health -= 0.1;
			return entity.attackEntityFrom(ds, damage);
		}

		return false;
	}

	public void pushEntity(Entity entity) {
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
