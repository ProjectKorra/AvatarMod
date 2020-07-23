package com.crowsofwar.avatar.common.entity.mob;

import com.crowsofwar.avatar.api.capabilities.IPlayerShoulders;
import com.crowsofwar.avatar.client.sounds.SoundsHandler;
import com.crowsofwar.avatar.common.capabilities.CapabilityPlayerShoulders;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

/**
 * EntityWolf EntityParrot EntityOcelot EntityCreeper EntityZombie EntityHorse EntityTameable
 *
 * @author Korog3a
 */

public class EntityFlyingLemur extends EntityTameable implements EntityFlying
{

	private static final DataParameter<Integer> VARIANT = EntityDataManager.<Integer>createKey(EntityFlyingLemur.class, DataSerializers.VARINT);
	private static final Set<Item> TAME_ITEMS = Sets.newHashSet(Items.CARROT, Items.APPLE, Items.GOLDEN_APPLE);
	protected static final DataParameter<Byte> RIGHTSHOULDER = EntityDataManager.<Byte>createKey(EntityFlyingLemur.class, DataSerializers.BYTE);
	protected static final DataParameter<Byte> LEFTSHOULDER = EntityDataManager.<Byte>createKey(EntityFlyingLemur.class, DataSerializers.BYTE);

	public double speed;
	private boolean previusRidingPos;
	private boolean partyLemur;
	private BlockPos jukeboxPosition;

	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
	{
		this.setVariant(this.rand.nextInt(2));
		return super.onInitialSpawn(difficulty, livingdata);
	}

	public EntityFlyingLemur(World worldIn)
	{
		super(worldIn);
		this.setSize(0.4F, 1F);
		this.moveHelper = new EntityFlyHelper(this);
		experienceValue = 200;
	}

	@Override
	protected void initEntityAI()
	{
		this.aiSit = new EntityAISit(this);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, this.aiSit);
		this.tasks.addTask(3, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(3, new EntityAIWanderAvoidWaterFlying(this, 1.0D));
		this.tasks.addTask(2, new EntityAIFollowOwner(this, 1.0F, 10.0F, 2.0F));
		this.tasks.addTask(2, new EntityAIFollowOwnerFlying(this, 1.0D, 5.0F, 1.0F));
		this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
		this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.2D, true));
		this.tasks.addTask(6, new EntityAIMate(this, 1.5D));
		this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
	}


	protected PathNavigate createNavigator(World worldIn)
	{
		PathNavigateFlying pathnavigateflying = new PathNavigateFlying(this, worldIn);
		pathnavigateflying.setCanOpenDoors(false);
		pathnavigateflying.setCanFloat(true);
		pathnavigateflying.setCanEnterDoors(true);

		PathNavigateGround pathnavigateground = new PathNavigateGround(this, worldIn);


		if(this.getMoveHelper() instanceof EntityFlyHelper)
		{
			return pathnavigateflying;
		}

		if(this.getMoveHelper() instanceof EntityMoveHelper)
		{
			return pathnavigateground;
		}

		return null;
	}

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand)
	{
		if(player.isSneaking() &&  this.getOwner() != null && this.getOwner() == player && !this.getLeashed())
		{
			IPlayerShoulders playerShoulders = player.getCapability(CapabilityPlayerShoulders.TEST_HANDLER, null);
			this.setSitting(false);
			if(playerShoulders.getRiders().size() == 0)
			{
				if(!previusRidingPos)
				{
					this.setRightShoulder(true);
					this.setNoAI(true);
					playerShoulders.setRightShoulder(true);
					previusRidingPos = true;
				}
				else
				{
					this.setLeftShoulder(true);
					this.setNoAI(true);
					playerShoulders.setLeftShoulder(true);
					previusRidingPos = false;
				}

				playerShoulders.addRiders(this);
			}
			else {
				if(playerShoulders.getRiders().size() < 2)
				{
					if(playerShoulders.getRiders().size() == 1 && !playerShoulders.getRiders().contains(this))
					{
						if(playerShoulders.getRiders().get(0) instanceof EntityAscendedFlyingLemur) {
							EntityAscendedFlyingLemur lemur = (EntityAscendedFlyingLemur) playerShoulders.getRiders().get(0);
							if(lemur.getRightShoulder()) {
								this.setLeftShoulder(true);
								this.setNoAI(true);
								playerShoulders.setLeftShoulder(true);
								previusRidingPos = false;

								playerShoulders.addRiders(this);
							}
							else if (lemur.getLeftShoulder()) {
								this.setRightShoulder(true);
								this.setNoAI(true);
								playerShoulders.setRightShoulder(true);
								previusRidingPos = true;

								playerShoulders.addRiders(this);
							}
						}
						else if (playerShoulders.getRiders().get(0) instanceof EntityFlyingLemur) {
							EntityFlyingLemur lemur = (EntityFlyingLemur) playerShoulders.getRiders().get(0);
							if(lemur.getRightShoulder()) {
								this.setLeftShoulder(true);
								this.setNoAI(true);
								playerShoulders.setLeftShoulder(true);
								previusRidingPos = false;

								playerShoulders.addRiders(this);
							}
							else if (lemur.getLeftShoulder()) {
								this.setRightShoulder(true);
								this.setNoAI(true);
								playerShoulders.setRightShoulder(true);
								previusRidingPos = true;

								playerShoulders.addRiders(this);

							}
						}

					}
				}
			}




		}

		return super.applyPlayerInteraction(player, vec, hand);
	}

	public boolean isLemurRiding() {
		if(!this.getLeftShoulder() && !this.getRightShoulder() && !this.isChild() && this.height != 1 && this.width != 0.4F) {
			this.height = 1f;
			this.width = 0.4f;
		}
		return this.getLeftShoulder() || this.getRightShoulder();
	}

	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		updatespeed(!this.isLemurRiding());

		if(!this.isLemurRiding()  && this.isAIDisabled()) this.setNoAI(false);

		if (this.jukeboxPosition == null || this.jukeboxPosition.distanceSq(this.posX, this.posY, this.posZ) > 12.0D || this.world.getBlockState(this.jukeboxPosition).getBlock() != Blocks.JUKEBOX)
		{
			this.partyLemur = false;
			this.jukeboxPosition = null;
		}

		if(this.ticksExisted % 100 == 0 && this.speed == 0 && this.getAIMoveSpeed() == 0) {
			if(this.getMoveHelper() instanceof EntityFlyHelper) {
				this.moveHelper = new EntityMoveHelper(this);
			}
			else {
				if(!this.isInWater() && !this.isInLove() && !this.isAngry()) {
					this.moveHelper = new EntityFlyHelper(this);
				}
			}

		}

		if(this.isLemurRiding()) {
			if(this.getOwner() != null && this.getOwner().dimension == this.dimension) {
				this.setRotation(this.getOwner().renderYawOffset, 0);
				if(this.isFlying()){
					this.setRotationYawHead(this.getOwner().rotationYaw);
				}else{
					this.setRotationYawHead(this.getOwner().renderYawOffset);
				}

				this.setRenderYawOffset(this.getOwner().renderYawOffset);
				if(this.isChild()) {
					this.setPosition(this.getOwner().posX, this.getOwner().posY + this.getOwner().getMountedYOffset() + this.getYOffset() + 0.30F, this.getOwner().posZ);
				}else {
					this.setPosition(this.getOwner().posX, this.getOwner().posY + this.getOwner().getMountedYOffset() + this.getYOffset() + 0.30F, this.getOwner().posZ);
				}


			}
		}

		if (!this.world.isRemote && this.getAttackTarget() == null && this.isAngry())
		{
			this.setAngry(false);
		}
	}

	@SideOnly(Side.CLIENT)
	public void setPartying(BlockPos pos, boolean p_191987_2_)
	{
		this.jukeboxPosition = pos;
		this.partyLemur = p_191987_2_;
	}

	@SideOnly(Side.CLIENT)
	public boolean isPartying()
	{
		return this.partyLemur;
	}


	@Override
	public boolean canBeCollidedWith() {
		if(this.isLemurRiding()) return false;
		return super.canBeCollidedWith();
	}

	public boolean getLeftShoulder() {
		return (((Byte)this.dataManager.get(LEFTSHOULDER)).byteValue() & 1) != 0;
	}
	public boolean getRightShoulder() {

		return (((Byte)this.dataManager.get(RIGHTSHOULDER)).byteValue() & 1) != 0;
	}

	public void setLeftShoulder(boolean ride) {
		byte b0 = ((Byte)this.dataManager.get(LEFTSHOULDER)).byteValue();

		if (ride)
		{
			this.height = 0.0f;
			this.width = 0.1f;
			this.setSitting(false);
			this.dataManager.set(LEFTSHOULDER, Byte.valueOf((byte)(b0 | 1)));
		}
		else
		{
			this.dataManager.set(LEFTSHOULDER, Byte.valueOf((byte)(b0 & -2)));
		}
	}
	public void setRightShoulder(boolean ride) {

		byte b0 = ((Byte)this.dataManager.get(RIGHTSHOULDER)).byteValue();

		if (ride)
		{
			this.height = 0.0f;
			this.width = 0.1f;
			this.setSitting(false);
			this.dataManager.set(RIGHTSHOULDER, Byte.valueOf((byte)(b0 | 1)));
		}
		else
		{
			this.dataManager.set(RIGHTSHOULDER, Byte.valueOf((byte)(b0 & -2)));
		}
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}


	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(RIGHTSHOULDER, Byte.valueOf((byte)0));
		this.dataManager.register(LEFTSHOULDER, Byte.valueOf((byte)0));
		this.dataManager.register(VARIANT, Integer.valueOf(0));
	}
		/*
	    public boolean isFlying()
	    {
	    	if(this.onGround || this.isLemurRiding() || this.isInWater()) return false;
	    	return this.isActualyFlying;
	    }
	    */

	public boolean isFlying()
	{
		return !this.onGround;
	}

	public void updatespeed(boolean canUpdate) {
		if(canUpdate) {
			double motionX = this.posX - this.prevPosX;
			double motionY = this.posY - this.prevPosY;
			double motionZ = this.posZ - this.prevPosZ;
			this.speed = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
		}
	}

	public float getEyeHeight()
	{
		if(this.isFlying() || this.isLemurRiding() ) {
			return  0.1F;
		}
		else {
			if(this.isSitting()) {
				return 0.65F;
			}

			if(this.isChild()) {
				return 0.55F;
			}

			return 0.85F;

		}

	}
	@Override
	public double getYOffset() {
		if(this.getOwner() != null && this.getOwner().isSneaking()) {
			if(this.isChild()) {
				return 0.15F;
			}else {
				return 0.50F;
			}
		}
		else {
			if(this.isChild()) {
				return 0.3F;
			}else {
				return 0.45F;
			}
		}

	}
	public void fall(float distance, float damageMultiplier)
	{

	}
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
	{

	}
	public boolean getCanSpawnHere()
	{
		int i = MathHelper.floor(this.posX);
		int j = MathHelper.floor(this.getEntityBoundingBox().minY);
		int k = MathHelper.floor(this.posZ);
		BlockPos blockpos = new BlockPos(i, j, k);
		Block block = this.world.getBlockState(blockpos.down()).getBlock();
		return block instanceof BlockLeaves || block == Blocks.GRASS || block instanceof BlockLog || block == Blocks.AIR && this.world.getLight(blockpos) > 8 && super.getCanSpawnHere();
	}
	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.33D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
	}
	public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn)
	{
		super.setAttackTarget(entitylivingbaseIn);

		if (entitylivingbaseIn == null)
		{
			this.setAngry(false);
		}
		else if (!this.isTamed())
		{
			this.setAngry(true);
		}
	}
	/*
	 * Config - End
	 */

	public int getVerticalFaceSpeed()
	{
		return this.isSitting() ? 20 : super.getVerticalFaceSpeed();
	}
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.isEntityInvulnerable(source))
		{
			return false;
		}
		else
		{
			Entity entity = source.getTrueSource();

			if (this.aiSit != null)
			{
				this.aiSit.setSitting(false);
			}

			return super.attackEntityFrom(source, amount);
		}
	}
	public boolean attackEntityAsMob(Entity entityIn)
	{
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

		if (flag)
		{
			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}
	public void setTamed(boolean tamed)
	{
		super.setTamed(tamed);
	}
	public boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		ItemStack itemstack = player.getHeldItem(hand);

		if (this.isTamed())
		{
			if (!itemstack.isEmpty())
			{
				if (TAME_ITEMS.contains(itemstack.getItem()) && this.getHealth() < 30.0F)
				{
					if (!player.capabilities.isCreativeMode)
					{
						itemstack.shrink(1);
					}

					this.heal(10);
					return true;
				}
			}

			if (this.isOwner(player) && !this.world.isRemote && !this.isBreedingItem(itemstack))
			{
				this.aiSit.setSitting(!this.isSitting());
				this.isJumping = false;
				this.navigator.clearPath();
				this.setAttackTarget((EntityLivingBase)null);
			}
		}
		else if (TAME_ITEMS.contains(itemstack.getItem()) && !this.isAngry())
		{
			if (!player.capabilities.isCreativeMode)
			{
				itemstack.shrink(1);
			}

			if (!this.world.isRemote)
			{
				if (this.rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player))
				{
					this.setTamedBy(player);
					this.navigator.clearPath();
					this.setAttackTarget((EntityLivingBase)null);
					this.aiSit.setSitting(true);
					this.setHealth(30.0F);
					this.playTameEffect(true);
					this.world.setEntityState(this, (byte)7);
				}
				else
				{
					this.playTameEffect(false);
					this.world.setEntityState(this, (byte)6);
				}
			}

			return true;
		}

		return super.processInteract(player, hand);
	}
	public boolean canBeLeashedTo(EntityPlayer player)
	{
		return !this.isAngry() && super.canBeLeashedTo(player);
	}
	public int getMaxSpawnedInChunk()
	{
		return 8;
	}

	public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner)
	{
		if (!(target instanceof EntityCreeper) && !(target instanceof EntityGhast) && !(target instanceof EntityAscendedFlyingLemur))
		{
			if (target instanceof EntityFlyingLemur)
			{
				EntityFlyingLemur entitylemur = (EntityFlyingLemur)target;

				if (entitylemur.isTamed() && entitylemur.getOwner() == owner)
				{
					return false;
				}
			}

			if (target instanceof EntityPlayer && owner instanceof EntityPlayer && !((EntityPlayer)owner).canAttackPlayer((EntityPlayer)target))
			{
				return false;
			}
			else
			{
				return !(target instanceof AbstractHorse) || !((AbstractHorse)target).isTame();

			}
		}
		else
		{
			return false;
		}
	}

	/*
	 * Emotions - Start
	 */
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		compound.setBoolean("Angry", this.isAngry());
		compound.setInteger("Variant", this.getVariant());
	}
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		this.setAngry(compound.getBoolean("Angry"));
		this.setVariant(compound.getInteger("Variant"));
	}
	public boolean isAngry()
	{
		return (((Byte)this.dataManager.get(TAMED)).byteValue() & 2) != 0;
	}
	public void setAngry(boolean angry)
	{
		byte b0 = ((Byte)this.dataManager.get(TAMED)).byteValue();

		if (angry)
		{
			this.dataManager.set(TAMED, Byte.valueOf((byte)(b0 | 2)));
		}
		else
		{
			this.dataManager.set(TAMED, Byte.valueOf((byte)(b0 & -3)));
		}
	}
	/*
	 * Emotions - End
	 */

	public void setVariant(int variantIn)
	{
		this.dataManager.set(VARIANT, Integer.valueOf(variantIn));
	}
	public int getVariant()
	{
		return MathHelper.clamp(((Integer)this.dataManager.get(VARIANT)).intValue(), 0, 1);
	}

	/*
	 * Mating - Start
	 */
	public boolean isBreedingItem(ItemStack stack)
	{
		return stack.getItem() == Items.GOLDEN_APPLE;
	}
	@Override
	public EntityAgeable createChild(EntityAgeable ageable)
	{
		EntityFlyingLemur entitymonkey = new EntityFlyingLemur(this.world);
		UUID uuid = this.getOwnerId();

		if (uuid != null)
		{
			entitymonkey.setOwnerId(uuid);
			entitymonkey.setTamed(true);
		}

		return entitymonkey;
	}
	public boolean canMateWith(EntityAnimal otherAnimal)
	{
		if (otherAnimal == this)
		{
			return false;
		}
		else if (!this.isTamed())
		{
			return false;
		}
		else if (!(otherAnimal instanceof EntityFlyingLemur))
		{
			return false;
		}
		else
		{
			EntityFlyingLemur lemur = (EntityFlyingLemur)otherAnimal;

			if (!lemur.isTamed())
			{
				return false;
			}
			else if (lemur.isSitting())
			{
				return false;
			}
			else
			{
				return this.isInLove() && lemur.isInLove();
			}
		}
	}
	/*
	 * Mating - End
	 */

	/*
	 * Sounds - Start
	 */
	protected SoundEvent getAmbientSound()
	{
		return SoundsHandler.ENTITY_FLYINGLEMUR_AMBIENT;
	}
	protected SoundEvent getHurtSound(DamageSource damageSourceIn)
	{
		return SoundsHandler.ENTITY_FLYINGLEMUR_HURT;
	}
	protected SoundEvent getDeathSound()
	{
		return SoundsHandler.ENTITY_FLYINGLEMUR_DEATH;
	}
	protected float getSoundVolume()
	{
		return 1.75F;
	}
	/*
	 * Sounds - End
	 */

	/*
	 * Particle Effects - Start
	 */
	public void playRideEffect(boolean play) {
		EnumParticleTypes enumparticletypes = EnumParticleTypes.EXPLOSION_NORMAL;
		for (int i = 0; i < 4; ++i)
		{
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			this.world.spawnParticle(enumparticletypes, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
		}
	}
	/*
	 * Particle Effects - End
	 */
}