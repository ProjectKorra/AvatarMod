package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BattlePerformanceScore;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.BoulderBehavior;
import com.crowsofwar.avatar.common.entity.data.FloatingBlockBehavior;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
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

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.nestedCompound;
import static net.minecraft.network.datasync.EntityDataManager.createKey;

public class EntityBoulder extends AvatarEntity {

	public static final Block DEFAULT_BLOCK = Blocks.STONE;

	private static final DataParameter<Integer> SYNC_ENTITY_ID = createKey(EntityBoulder.class,
			DataSerializers.VARINT);

	public static final DataParameter<Integer> SYNC_BOULDERS_LEFT= EntityDataManager.createKey(
			EntityBoulder.class, DataSerializers.VARINT);

	private static final DataParameter<Vector> SYNC_VELOCITY = createKey(EntityBoulder.class,
			AvatarDataSerializers.SERIALIZER_VECTOR);
	private static final DataParameter<Float> SYNC_FRICTION = createKey(EntityBoulder.class,
			DataSerializers.FLOAT);
	private static final DataParameter<Optional<IBlockState>> SYNC_BLOCK = createKey(
			EntityFloatingBlock.class, DataSerializers.OPTIONAL_BLOCK_STATE);

	private static final DataParameter<BoulderBehavior> SYNC_BEHAVIOR = createKey(
			EntityBoulder.class, BoulderBehavior.DATA_SERIALIZER);

	private static int nextBlockID = 0;

	/**
	 * Cached owner of this floating block. May not be accurate- use
	 * {@link #getOwner()} to use updated version.
	 */
	private EntityPlayer ownerCached;

	/**
	 * Whether or not to drop an ItemBlock when the floating block has been
	 * destroyed. Does not matter on client.
	 */
	private boolean enableItemDrops;

	/**
	 * The hitbox for this floating block, but slightly expanded to give more
	 * room for killing things with.
	 */
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

	public float getSize () {
		return this.size;
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

	public EntityBoulder(World world) {
		super(world);
		float size = this.size;
		setSize(size, size);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			setID(nextBlockID++);
		}


	}

	public EntityBoulder(World world, IBlockState blockState) {
		this(world);
		setBlockState(blockState);
	}

	public EntityBoulder(World world, IBlockState blockState, EntityPlayer owner) {
		this(world, blockState);
		setOwner(owner);
	}

	public static EntityBoulder getFromID(World world, int id) {
		for (int i = 0; i < world.loadedEntityList.size(); i++) {
			Entity e = world.loadedEntityList.get(i);
			if (e instanceof EntityBoulder && ((EntityBoulder) e).getID() == id)
				return (EntityBoulder) e;
		}
		return null;
	}

	// Called from constructor of Entity class
	@Override
	protected void entityInit() {

		super.entityInit();
		dataManager.register(SYNC_ENTITY_ID, 0);
		dataManager.register(SYNC_VELOCITY, Vector.ZERO);
		dataManager.register(SYNC_FRICTION, 1f);
		dataManager.register(SYNC_BLOCK, Optional.of(DEFAULT_BLOCK.getDefaultState()));
		dataManager.register(SYNC_BEHAVIOR, new BoulderBehavior.DoNothing());
		dataManager.register(SYNC_BOULDERS_LEFT, bouldersLeft);

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setBlockState(
				Block.getBlockById(nbt.getInteger("BlockId")).getStateFromMeta(nbt.getInteger("Metadata")));
		setVelocity(new Vector(nbt.getDouble("VelocityX"), nbt.getDouble("VelocityY"), nbt.getDouble
				("VelocityZ")));
		setFriction(nbt.getFloat("Friction"));
		setItemDropsEnabled(nbt.getBoolean("DropItems"));
		setBehavior((BoulderBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
		getBehavior().load(nbt.getCompoundTag("BehaviorData"));
		Damage = nbt.getFloat("Damage");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("BlockId", Block.getIdFromBlock(getBlock()));
		nbt.setInteger("Metadata", getBlock().getMetaFromState(getBlockState()));
		nbt.setDouble("VelocityX", velocity().x());
		nbt.setDouble("VelocityY", velocity().y());
		nbt.setDouble("VelocityZ", velocity().z());
		nbt.setFloat("Friction", getFriction());
		nbt.setBoolean("DropItems", areItemDropsEnabled());
		nbt.setInteger("Behavior", getBehavior().getId());
		getBehavior().save(nestedCompound(nbt, "BehaviorData"));
		nbt.setFloat("Damage", Damage);
	}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	public Block getBlock() {
		return getBlockState().getBlock();
	}

	public void setBlock(Block block) {
		setBlockState(block.getDefaultState());
	}

	public IBlockState getBlockState() {
		Optional<IBlockState> obs = dataManager.get(SYNC_BLOCK);
		return obs.get();
	}

	public void setBlockState(IBlockState state) {
		dataManager.set(SYNC_BLOCK, Optional.of(state));
	}

	/**
	 * Get the ID of this floating block. Each instance has its own unique ID.
	 * Synced between client and server.
	 */
	public int getID() {
		return dataManager.get(SYNC_ENTITY_ID);
	}

	public void setID(int id) {
		if (!world.isRemote) dataManager.set(SYNC_ENTITY_ID, id);
	}

	/**
	 * Returns whether the floating block drops the block as an item when it is
	 * destroyed. Only used on server-side. By default, is true.
	 */
	public boolean areItemDropsEnabled() {
		return enableItemDrops;
	}

	/**
	 * Set whether the block should be dropped when it is destroyed.
	 */
	public void setItemDropsEnabled(boolean enable) {
		this.enableItemDrops = enable;
	}



	private void spawnCrackParticle(double x, double y, double z, double mx, double my, double mz) {
		world.spawnParticle(EnumParticleTypes.BLOCK_CRACK, x, y, z, mx, my, mz,
				Block.getStateId(getBlockState()));
	}

	@Override
	public void onUpdate() {

		super.onUpdate();

		extinguish();

		if (ticksExisted == 1) {

			for (int i = 0; i < 10; i++) {
				double spawnX = posX + (rand.nextDouble() - 0.5);
				double spawnY = posY - 0;
				double spawnZ = posZ + (rand.nextDouble() - 0.5);
				spawnCrackParticle(spawnX, spawnY, spawnZ, 0, -0.1, 0);
			}

		}

		setVelocity(velocity().times(getFriction()));

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		BoulderBehavior nextBehavior = (BoulderBehavior) getBehavior().onUpdate(this);
		if (nextBehavior != getBehavior()) setBehavior(nextBehavior);

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
	public boolean onCollideWithSolid() {

		BoulderBehavior behavior = getBehavior();
		if (!(behavior instanceof BoulderBehavior.Fall|| behavior instanceof
				BoulderBehavior.Thrown)) {

			return false;

		}

		// Spawn particles
		Random random = new Random();
		for (int i = 0; i < 7; i++) {
			spawnCrackParticle(posX, posY + 0.3, posZ, random.nextGaussian() * 0.1,
					random.nextGaussian() * 0.1, random.nextGaussian() * 0.1);
		}

		if (!world.isRemote && areItemDropsEnabled()) {
			List<ItemStack> drops = getBlock().getDrops(world, new BlockPos(this), getBlockState(), 0);
			for (ItemStack is : drops) {
				EntityItem ei = new EntityItem(world, posX, posY, posZ, is);
				world.spawnEntity(ei);
			}
		}

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

	public float getFriction() {
		return dataManager.get(SYNC_FRICTION);
	}

	public void setFriction(float friction) {
		if (!world.isRemote) dataManager.set(SYNC_FRICTION, friction);
	}

	public void drop() {
		setBehavior(new BoulderBehavior.Fall());
	}

	public BoulderBehavior getBehavior() {
		return dataManager.get(SYNC_BEHAVIOR);
	}

	public void setBehavior(BoulderBehavior behavior) {
		// FIXME research: why doesn't sync_Behavior cause an update to client?
		if (behavior == null) throw new IllegalArgumentException("Cannot have null behavior");
		dataManager.set(SYNC_BEHAVIOR, behavior);
	}

	@Override
	public EntityLivingBase getController() {
		return getBehavior() instanceof BoulderBehavior.PlayerControlled ? getOwner() : null;
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
		return entity instanceof EntityLivingBase || super.canCollideWith(entity);
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
