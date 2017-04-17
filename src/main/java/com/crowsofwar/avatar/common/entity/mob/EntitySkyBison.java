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
package com.crowsofwar.avatar.common.entity.mob;

import static com.crowsofwar.avatar.common.AvatarChatMessages.*;
import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_AIR_JUMP;
import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;
import static com.crowsofwar.avatar.common.util.AvatarUtils.normalizeAngle;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.toRectangular;
import static java.lang.Math.*;
import static net.minecraft.init.Blocks.STONE;
import static net.minecraft.item.ItemStack.field_190927_a;
import static net.minecraft.util.SoundCategory.NEUTRAL;

import java.util.UUID;

import javax.annotation.Nullable;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.BenderInfo;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.data.ctx.NoBenderInfo;
import com.crowsofwar.avatar.common.entity.ai.EntityAiBisonBreeding;
import com.crowsofwar.avatar.common.entity.ai.EntityAiBisonEatGrass;
import com.crowsofwar.avatar.common.entity.ai.EntityAiBisonFollowOwner;
import com.crowsofwar.avatar.common.entity.ai.EntityAiBisonSit;
import com.crowsofwar.avatar.common.entity.ai.EntityAiBisonTempt;
import com.crowsofwar.avatar.common.entity.ai.EntityAiBisonWander;
import com.crowsofwar.avatar.common.entity.data.AnimalCondition;
import com.crowsofwar.avatar.common.entity.data.OwnerAttribute;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.ItemBisonWhistle;
import com.crowsofwar.avatar.common.util.AvatarDataSerializers;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.AccountUUIDs;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Type;

/**
 * EntityGhast EntityTameable
 * 
 * @author CrowsOfWar
 */
public class EntitySkyBison extends EntityBender implements IEntityOwnable {
	
	private static final DataParameter<BenderInfo> SYNC_OWNER = EntityDataManager
			.createKey(EntitySkyBison.class, AvatarDataSerializers.SERIALIZER_BENDER);
	
	private static final DataParameter<Boolean> SYNC_SITTING = EntityDataManager
			.createKey(EntitySkyBison.class, DataSerializers.BOOLEAN);
	
	private static final DataParameter<Float> SYNC_FOOD = EntityDataManager.createKey(EntitySkyBison.class,
			DataSerializers.FLOAT);
	
	private static final DataParameter<Integer> SYNC_DOMESTICATION = EntityDataManager
			.createKey(EntitySkyBison.class, DataSerializers.VARINT);
	
	private static final DataParameter<Integer> SYNC_EAT_GRASS = EntityDataManager
			.createKey(EntitySkyBison.class, DataSerializers.VARINT);
	
	private static final DataParameter<Integer> SYNC_AGE = EntityDataManager.createKey(EntitySkyBison.class,
			DataSerializers.VARINT);
	
	private static final DataParameter<Boolean> SYNC_IN_LOVE = EntityDataManager
			.createKey(EntitySkyBison.class, DataSerializers.BOOLEAN);
	
	private final OwnerAttribute ownerAttr;
	private Vector originalPos;
	private final AnimalCondition condition;
	/**
	 * Note: Is null clientside.
	 */
	private EntityAiBisonEatGrass aiEatGrass;
	private int riderTicks;
	
	private ForgeChunkManager.Ticket ticket;
	
	private boolean wasTouchingGround;
	
	/**
	 * @param world
	 */
	public EntitySkyBison(World world) {
		super(world);
		
		moveHelper = new SkyBisonMoveHelper(this);
		ownerAttr = new OwnerAttribute(this, SYNC_OWNER);
		condition = new AnimalCondition(this, 30, 20, SYNC_FOOD, SYNC_DOMESTICATION, SYNC_AGE);
		setSize(2.5f, 2);
		
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		
		int domestication = MOBS_CONFIG.bisonMinDomestication
				+ rand.nextInt(MOBS_CONFIG.bisonMaxDomestication - MOBS_CONFIG.bisonMinDomestication);
		
		dataManager.register(SYNC_SITTING, false);
		dataManager.register(SYNC_FOOD, 20f);
		dataManager.register(SYNC_DOMESTICATION, domestication);
		dataManager.register(SYNC_EAT_GRASS, -1);
		dataManager.register(SYNC_AGE, 0);
		dataManager.register(SYNC_IN_LOVE, false);
		
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(2);
	}
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
		
		this.tasks.addTask(1, BendingAbility.ABILITY_AIR_BUBBLE.getAi(this, this));
		this.tasks.addTask(2, BendingAbility.ABILITY_AIR_GUST.getAi(this, this));
		this.tasks.addTask(3, BendingAbility.ABILITY_AIRBLADE.getAi(this, this));
		
		this.tasks.addTask(3, new EntityAiBisonSit(this));
		this.tasks.addTask(4, new EntityAiBisonBreeding(this));
		this.tasks.addTask(5, aiEatGrass = new EntityAiBisonEatGrass(this));
		this.tasks.addTask(6, new EntityAiBisonFollowOwner(this));
		this.tasks.addTask(7, new EntityAiBisonTempt(this, 10));
		this.tasks.addTask(8, new EntityAiBisonWander(this));
		System.out.println("Set aiEatGrass to " + aiEatGrass);
		
	}
	
	@Override
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty,
			@Nullable IEntityLivingData livingdata) {
		
		originalPos = Vector.getEntityPos(this);
		return super.onInitialSpawn(difficulty, livingdata);
		
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		originalPos = Vector.readFromNbt(nbt);
		ownerAttr.load(nbt);
		setSitting(nbt.getBoolean("Sitting"));
		condition.readFromNbt(nbt);
		riderTicks = nbt.getInteger("RiderTicks");
		setInLove(nbt.getBoolean("InLove"));
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		originalPos.writeToNbt(nbt);
		ownerAttr.save(nbt);
		nbt.setBoolean("Sitting", isSitting());
		condition.writeToNbt(nbt);
		nbt.setInteger("RiderTicks", riderTicks);
		nbt.setBoolean("InLove", isInLove());
	}
	
	// ================================================================================
	// DATA ACCESS / DATAMANAGER
	// ================================================================================
	
	public Vector getOriginalPos() {
		return originalPos;
	}
	
	@Override
	public UUID getOwnerId() {
		return ownerAttr.getId();
	}
	
	public void setOwnerId(UUID id) {
		ownerAttr.setOwnerInfo(id == null ? new NoBenderInfo() : new BenderInfo(true, id));
	}
	
	public boolean hasOwner() {
		return getOwnerId() != null;
	}
	
	@Override
	public EntityPlayer getOwner() {
		return (EntityPlayer) ownerAttr.getOwner();
	}
	
	public void setOwner(EntityPlayer owner) {
		ownerAttr.setOwner(owner);
	}
	
	public boolean isSitting() {
		return dataManager.get(SYNC_SITTING);
	}
	
	public void setSitting(boolean sitting) {
		dataManager.set(SYNC_SITTING, sitting);
	}
	
	public double getFlySpeedMultiplier() {
		return condition.getSpeedMultiplier();
	}
	
	public AnimalCondition getCondition() {
		return condition;
	}
	
	public boolean wantsGrass() {
		return condition.getFoodPoints() < 15;
	}
	
	public boolean isEatingGrass() {
		return getEatGrassTime() > -1;
	}
	
	public int getEatGrassTime() {
		return dataManager.get(SYNC_EAT_GRASS);
	}
	
	public void setEatGrassTime(int time) {
		dataManager.set(SYNC_EAT_GRASS, time);
	}
	
	public int getRiderTicks() {
		return riderTicks;
	}
	
	public void setRiderTicks(int riderTicks) {
		this.riderTicks = riderTicks;
	}
	
	public boolean isInLove() {
		return dataManager.get(SYNC_IN_LOVE);
	}
	
	public void setInLove(boolean inLove) {
		dataManager.set(SYNC_IN_LOVE, inLove);
	}
	
	// ================================================================================
	// CHUNK LOADING
	// ================================================================================
	
	public boolean isForceLoadingChunks() {
		return ticket != null;
	}
	
	public void beginForceLoadingChunks() {
		if (!isForceLoadingChunks()) {
			ticket = ForgeChunkManager.requestTicket(AvatarMod.instance, worldObj, Type.ENTITY);
			ticket.bindEntity(this);
			ticket.getModData().setUniqueId("BisonId", getUniqueID());
		}
	}
	
	public void stopForceLoadingChunks() {
		if (isForceLoadingChunks()) {
			ForgeChunkManager.releaseTicket(ticket);
			ticket = null;
		}
	}
	
	// ================================================================================
	// PASSENGER LOGIC
	// ================================================================================
	
	@Override
	public void updatePassenger(Entity passenger) {
		
		double index = getPassengers().indexOf(passenger);
		riderTicks++;
		
		if (index > -1) {
			
			double offset = 0.75;
			double angle = (index + 0.5) * Math.PI - toRadians(rotationYaw);
			double yOffset = passenger.getYOffset() + 1.75;
			
			if (passenger == getControllingPassenger()) {
				angle = -toRadians(passenger.rotationYaw);
				offset = 1;
				yOffset = passenger.getYOffset() + 2 - Math.sin(toRadians(rotationPitch));
			}
			
			passenger.setPosition(posX + sin(angle) * offset, posY + yOffset, posZ + cos(angle) * offset);
			
			if (passenger != getControllingPassenger()) {
				if (motionX != 0 || motionY != 0 || motionZ != 0) {
					passenger.rotationYaw = this.rotationYaw;
					passenger.rotationPitch = this.rotationPitch;
				}
			}
			
		}
		
	}
	
	@Override
	protected boolean canFitPassenger(Entity passenger) {
		return getPassengers().size() < condition.getMaxRiders();
	}
	
	@Override
	public Entity getControllingPassenger() {
		if (getPassengers().contains(getOwner())) {
			return getOwner();
		} else {
			return null;
		}
	}
	
	@Override
	public boolean canBeSteered() {
		return getControllingPassenger() != null;
	}
	
	@Override
	protected boolean canDespawn() {
		return false;
	}
	
	// ================================================================================
	// PLAYER INTERACTION HOOKS
	// ================================================================================
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		
		boolean willBeOwned = condition.canHaveOwner() && stack.getItem() == Items.APPLE && !hasOwner();
		
		if (!condition.isFullyDomesticated() || true) {
			if (stack != field_190927_a) {
				
				Item item = stack.getItem();
				int domesticationValue = MOBS_CONFIG.getDomesticationValue(item);
				
				if (domesticationValue > 0) {
					condition.addDomestication(domesticationValue);
					System.out.println("Now domestication is " + condition.getDomestication());
					
					if (!condition.canHaveOwner() || item != Items.APPLE) {
						playTameEffect(false);
					}
				}
				
				if (item instanceof ItemFood) {
					ItemFood food = (ItemFood) stack.getItem();
					condition.addFood(food.getHealAmount(stack));
				}
				
				if (domesticationValue > 0 || item instanceof ItemFood) {
					condition.addAge(100);
					// Consume food item
					if (!player.capabilities.isCreativeMode) {
						stack.func_190918_g(1);
					}
					// Don't stop now if we are about to be owned
					if (!willBeOwned) {
						return true;
					}
				}
				
			}
		}
		
		if (willBeOwned) {
			System.out.println("Im tame now lel");
			playTameEffect(true);
			setOwnerId(AccountUUIDs.getId(player.getName()).getUUID());
			if (!player.capabilities.isCreativeMode) {
				stack.func_190918_g(1);
			}
			return true;
		}
		
		if (stack.getItem() == Items.REDSTONE) {
			condition.setDomestication(0);
			playTameEffect(false);
			setOwnerId(null);
			return true;
		}
		
		if (!worldObj.isRemote && stack.getItem() == AvatarItems.itemBisonWhistle && player.isSneaking()) {
			if (player == getOwner()) {
				ItemBisonWhistle.setBoundTo(stack, getUniqueID());
				ItemBisonWhistle.setBisonName(stack, getName());
				MSG_BISON_WHISTLE_ASSIGN.send(player, getName());
			} else {
				MSG_BISON_WHISTLE_NOTOWNED.send(player);
			}
			
			return true;
		}
		
		if (!worldObj.isRemote && player.isSneaking() && stack.getItem() == Items.ARROW) {
			
			int food = (int) (100.0 * condition.getFoodPoints() / 30);
			int health = (int) (100.0 * getHealth() / getMaxHealth());
			
			MSG_SKY_BISON_STATS.send(player, food, health, condition.getDomestication());
			
			return true;
			
		}
		
		if (stack.getItem() == Items.NAME_TAG) {
			setAlwaysRenderNameTag(true);
			return false;
		}
		
		if (!player.isSneaking() && !worldObj.isRemote) {
			player.startRiding(this);
			return true;
		}
		
		if (player.isSneaking() && getOwner() == player) {
			setSitting(!isSitting());
			return true;
		}
		
		return super.processInteract(player, hand);
		
	}
	
	@Override
	public boolean canBeLeashedTo(EntityPlayer player) {
		return condition.getDomestication() >= MOBS_CONFIG.bisonLeashTameness && super.canBeLeashedTo(player);
	}
	
	private void onLiftoff() {
		Raytrace.Result result = new Raytrace.Result();
		ABILITY_AIR_JUMP.execute(new AbilityContext(getData(), this, this, result, ABILITY_AIR_JUMP));
		StatusControl.AIR_JUMP.execute(new BendingContext(getData(), this, this, result));
		getData().removeStatusControl(StatusControl.AIR_JUMP);
	}
	
	private void onLand() {
		worldObj.playSound(null, getPosition(), STONE.getSoundType().getBreakSound(), NEUTRAL, 1, 1);
	}
	
	@Override
	public void eatGrassBonus() {
		condition.addFood(MOBS_CONFIG.bisonGrassFoodBonus);
	}
	
	// ================================================================================
	// GENERAL UPDATE LOGIC
	// ================================================================================
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if (!worldObj.isRemote && !condition.canHaveOwner() && hasOwner()) {
			setOwner(null);
		}
		if (!worldObj.isRemote) {
			setEatGrassTime(aiEatGrass.getEatGrassTime());
		}
		if (worldObj.isRemote && isInLove() && ticksExisted % 10 == 0) {
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			
			this.worldObj.spawnParticle(EnumParticleTypes.HEART,
					this.posX + this.rand.nextFloat() * this.width * 2 - this.width,
					this.posY + 0.5D + this.rand.nextFloat() * this.height,
					this.posZ + this.rand.nextFloat() * this.width * 2 - this.width, d0, d1, d2, new int[0]);
			
		}
		
		float sizeMult = condition.getSizeMultiplier();
		setSize(2.5f * sizeMult, 2 * sizeMult);
		
		condition.onUpdate();
		if (condition.getFoodPoints() == 0) {
			setSitting(true);
		} else if (!hasOwner()) {
			setSitting(false);
		}
		
		if (!isForceLoadingChunks() && hasOwner()) {
			beginForceLoadingChunks();
		}
		if (isForceLoadingChunks()) {
			ForgeChunkManager.forceChunk(ticket, new ChunkPos(getPosition()));
			if (!hasOwner() || getHealth() <= 0) {
				stopForceLoadingChunks();
			}
		}
		
		if (!worldObj.isRemote) {
			
			IBlockState walkingOn = worldObj.getBlockState(getEntityPos(this).setY(posY - 0.01).toBlockPos());
			boolean touchingGround = walkingOn.getMaterial() != Material.AIR;
			
			if (!touchingGround && wasTouchingGround) {
				onLiftoff();
			}
			if (touchingGround && !wasTouchingGround) {
				onLand();
			}
			
			wasTouchingGround = touchingGround;
			
		}
		
		if (getPassengers().isEmpty()) {
			riderTicks = 0;
		}
		if (!condition.canHaveOwner() && riderTicks > 0 && riderTicks % 60 == 0) {
			condition.addDomestication(MOBS_CONFIG.bisonRideOneSecondTameness * 3);
			playTameEffect(false);
		}
		
	}
	
	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
		
		// onGround apparently doesn't work client-side
		IBlockState walkingOn = worldObj.getBlockState(getEntityPos(this).setY(posY - 0.01).toBlockPos());
		boolean touchingGround = walkingOn.getMaterial() != Material.AIR;
		
		if (this.isBeingRidden() && this.canBeSteered()) {
			
			moveHelper.action = Action.WAIT;
			
			EntityLivingBase driver = (EntityLivingBase) getControllingPassenger();
			
			float pitch = abs(driver.rotationPitch) < 20 ? 0 : driver.rotationPitch;
			if (touchingGround && abs(pitch) < 45) {
				pitch = 0;
			}
			Vector look = toRectangular(toRadians(driver.rotationYaw), toRadians(pitch));
			forward = (float) look.copy().setY(0).magnitude();
			
			if (touchingGround && pitch <= -45 && forward > 0) {
				// onLiftoff();
			}
			
			float speedMult = condition.getSpeedMultiplier() * driver.moveForward * 2;
			
			float current = normalizeAngle(this.rotationYaw);
			float target = normalizeAngle(driver.rotationYaw);
			
			float delta = target - current;
			float turnRight = abs(normalizeAngle(target - current));
			float turnLeft = abs(normalizeAngle(current - target));
			
			if (turnRight < turnLeft) {
				rotationYaw += turnRight * 0.3;
			} else {
				rotationYaw -= turnLeft * 0.3;
			}
			rotationYaw = normalizeAngle(rotationYaw);
			
			this.rotationYawHead = driver.rotationYaw;
			this.prevRotationYaw = this.rotationYaw;
			this.rotationPitch = driver.rotationPitch * 0.5F;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.renderYawOffset = this.rotationYaw;
			strafe = driver.moveStrafing * 0.5F;
			
			this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
			
			if (this.canPassengerSteer()) {
				
				float moveAttribute = (float) getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
						.getAttributeValue();
				setAIMoveSpeed(moveAttribute * condition.getSpeedMultiplier());
				
				moveEntityWithHeadingFlying(strafe, forward * speedMult);
				motionY += look.y() * 0.02 * speedMult;
				
			} else if (driver instanceof EntityPlayer) {
				this.motionX = 0.0D;
				this.motionY = 0.0D;
				this.motionZ = 0.0D;
			}
			
			this.prevLimbSwingAmount = this.limbSwingAmount;
			double moveX = this.posX - this.prevPosX;
			double moveZ = this.posZ - this.prevPosZ;
			float moveSinceLastTick = MathHelper.sqrt_double(moveX * moveX + moveZ * moveZ) * 4.0F;
			
			if (moveSinceLastTick > 1.0F) {
				moveSinceLastTick = 1.0F;
			}
			
			this.limbSwingAmount += (moveSinceLastTick - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		} else {
			this.jumpMovementFactor = 0.02F;
			moveEntityWithHeadingFlying(strafe, forward);
		}
		
	}
	
	// ================================================================================
	// COPIED FROM ENTITYFLYING
	// ================================================================================
	
	@Override
	public void fall(float distance, float damageMultiplier) {}
	
	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {}
	
	private void moveEntityWithHeadingFlying(float strafe, float forward) {
		if (this.isInWater()) {
			this.moveRelative(strafe, forward, 0.02F);
			this.moveEntity(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.800000011920929D;
			this.motionY *= 0.800000011920929D;
			this.motionZ *= 0.800000011920929D;
		} else if (this.isInLava()) {
			this.moveRelative(strafe, forward, 0.02F);
			this.moveEntity(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
		} else {
			float f = 0.91F;
			
			if (this.onGround) {
				f = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX),
						MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1,
						MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
			}
			
			float f1 = 0.16277136F / (f * f * f);
			this.moveRelative(strafe, forward, this.onGround ? 0.1F * f1 : 0.02F);
			f = 0.91F;
			
			if (this.onGround) {
				f = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX),
						MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1,
						MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
			}
			
			this.moveEntity(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= f;
			this.motionY *= f;
			this.motionZ *= f;
		}
		
		this.prevLimbSwingAmount = this.limbSwingAmount;
		double d1 = this.posX - this.prevPosX;
		double d0 = this.posZ - this.prevPosZ;
		float f2 = MathHelper.sqrt_double(d1 * d1 + d0 * d0) * 4.0F;
		
		if (f2 > 1.0F) {
			f2 = 1.0F;
		}
		
		this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}
	
	@Override
	public boolean isOnLadder() {
		return false;
	}
	
	// ================================================================================
	// COPIED FROM ENTITYTAMEABLE
	// ================================================================================
	
	protected void playTameEffect(boolean success) {
		
		EnumParticleTypes particle;
		if (success) {
			particle = EnumParticleTypes.HEART;
		} else {
			particle = condition.canHaveOwner() ? EnumParticleTypes.CLOUD : EnumParticleTypes.SMOKE_NORMAL;
		}
		
		for (int i = 0; i < 7; i++) {
			double mx = this.rand.nextGaussian() * 0.02D;
			double my = this.rand.nextGaussian() * 0.02D;
			double mz = this.rand.nextGaussian() * 0.02D;
			this.worldObj.spawnParticle(particle,
					this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
					this.posY + 0.5D + this.rand.nextFloat() * this.height,
					this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width, //
					mx, my, mz, new int[0]);
		}
		
	}
	
}
