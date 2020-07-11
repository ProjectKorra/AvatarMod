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
package com.crowsofwar.avatar.entity.mob;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.util.analytics.AnalyticEvents;
import com.crowsofwar.avatar.util.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.util.data.AvatarWorldData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BenderEntityComponent;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.ai.*;
import com.crowsofwar.avatar.entity.data.AnimalCondition;
import com.crowsofwar.avatar.entity.data.BisonSpawnData;
import com.crowsofwar.avatar.entity.data.SyncedEntity;
import com.crowsofwar.avatar.client.gui.AvatarGuiHandler;
import com.crowsofwar.avatar.client.gui.InventoryBisonChest;
import com.crowsofwar.avatar.registry.AvatarItems;
import com.crowsofwar.avatar.item.ItemBisonArmor.ArmorTier;
import com.crowsofwar.avatar.item.ItemBisonSaddle.SaddleTier;
import com.crowsofwar.avatar.item.ItemBisonWhistle;
import com.crowsofwar.avatar.util.AvatarDataSerializers;
import com.crowsofwar.avatar.util.PlayerViewRegistry;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.gorecore.util.AccountUUIDs;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Type;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.crowsofwar.avatar.network.AvatarChatMessages.*;
import static com.crowsofwar.avatar.config.ConfigMobs.MOBS_CONFIG;
import static com.crowsofwar.avatar.util.AvatarUtils.*;
import static com.crowsofwar.gorecore.util.Vector.getEntityPos;
import static com.crowsofwar.gorecore.util.Vector.toRectangular;
import static java.lang.Math.*;
import static net.minecraft.entity.SharedMonsterAttributes.ARMOR;
import static net.minecraft.init.Blocks.STONE;
import static net.minecraft.item.ItemStack.EMPTY;
import static net.minecraft.util.SoundCategory.NEUTRAL;

/**
 * EntityGhast EntityTameable EntityHorse
 *
 * @author CrowsOfWar
 */
public class EntitySkyBison extends EntityBender implements IEntityOwnable, IInventoryChangedListener {

	private static final DataParameter<Optional<UUID>> SYNC_OWNER = EntityDataManager
			.createKey(EntitySkyBison.class, DataSerializers.OPTIONAL_UNIQUE_ID);

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

	private static final DataParameter<Boolean> SYNC_LOVE_PARTICLES = EntityDataManager
			.createKey(EntitySkyBison.class, DataSerializers.BOOLEAN);

	private static final DataParameter<Integer> SYNC_ID = EntityDataManager.createKey(EntitySkyBison.class,
			DataSerializers.VARINT);

	private static final DataParameter<SaddleTier> SYNC_SADDLE = EntityDataManager
			.createKey(EntitySkyBison.class, AvatarDataSerializers.SERIALIZER_SADDLE);

	private static final DataParameter<ArmorTier> SYNC_ARMOR = EntityDataManager
			.createKey(EntitySkyBison.class, AvatarDataSerializers.SERIALIZER_ARMOR);

	private final SyncedEntity<EntityLivingBase> ownerAttr;
	private final AnimalCondition condition;
	private Vector originalPos;
	private boolean madeSitByPlayer = false;
	/**
	 * Note: Is null clientside.
	 */
	private EntityAiBisonEatGrass aiEatGrass;
	private int riderTicks;

	private ForgeChunkManager.Ticket ticket;
	private InventoryBisonChest chest;

	private boolean wasTouchingGround;


	/**
	 * @param world
	 */
	public EntitySkyBison(World world) {
		super(world);


		moveHelper = new SkyBisonMoveHelper(this);
		ownerAttr = new SyncedEntity<>(this, SYNC_OWNER);
		condition = new AnimalCondition(this, 30, 20, SYNC_FOOD, SYNC_DOMESTICATION, SYNC_AGE);
		setSize(2.5f, 2);

		this.noClip = false;


		initChest();

	}

	/**
	 * Returns the sky bison in that world with the specified id, or null if no
	 * sky bison with that id.
	 */
	public static EntitySkyBison findBison(World world, int id) {
		List<EntitySkyBison> list = world.getEntities(EntitySkyBison.class, bison -> bison.getId() == id);
		return list.isEmpty() ? null : list.get(0);
	}

	/**
	 * Returns the sky bison in that world with the specified uuid, or null if
	 * no sky bison with that uuid.
	 */
	public static EntitySkyBison findBison(World world, UUID id) {
		List<EntitySkyBison> list = world.getEntities(EntitySkyBison.class,
				bison -> bison.getUniqueID().equals(id));
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	protected Bender initBender() {
		return new BisonBenderComponent();
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		int domestication = MOBS_CONFIG.bisonSettings.bisonMinDomestication
				+ rand.nextInt(MOBS_CONFIG.bisonSettings.bisonMaxDomestication - MOBS_CONFIG.bisonSettings.bisonMinDomestication);

		dataManager.register(SYNC_OWNER, Optional.absent());
		dataManager.register(SYNC_SITTING, false);
		dataManager.register(SYNC_FOOD, 20f);
		dataManager.register(SYNC_DOMESTICATION, domestication);
		dataManager.register(SYNC_EAT_GRASS, -1);
		dataManager.register(SYNC_AGE, 0);
		dataManager.register(SYNC_LOVE_PARTICLES, false);
		dataManager.register(SYNC_ID,
				world.isRemote ? -1 : AvatarWorldData.getDataFromWorld(world).nextEntityId());
		dataManager.register(SYNC_SADDLE, null);
		dataManager.register(SYNC_ARMOR, null);

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));

		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, new EntityAiBisonDefendOwner(this));
		this.targetTasks.addTask(3, new EntityAiBisonHelpOwnerTarget(this));

		this.tasks.addTask(4, Objects.requireNonNull(Abilities.get("air_bubble")).getAi(this, getBender()));
		this.tasks.addTask(1, Objects.requireNonNull(Abilities.get("air_gust")).getAi(this, getBender()));
		this.tasks.addTask(3, Objects.requireNonNull(Abilities.get("airblade")).getAi(this, getBender()));


		this.tasks.addTask(2, new EntityAiBisonFollowAttacker(this));
		this.tasks.addTask(3, new EntityAiBisonSit(this));
		this.tasks.addTask(4, new EntityAiBisonBreeding(this));
		this.tasks.addTask(5, new EntityAiBisonTempt(this, 10));
		this.tasks.addTask(6, aiEatGrass = new EntityAiBisonEatGrass(this));
		this.tasks.addTask(6, new EntityAiBisonLand(this));
		this.tasks.addTask(7, new EntityAiBisonFollowOwner(this));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.tasks.addTask(9, new EntityAiBisonWander(this));

	}

	// Note: Not called when using /summon with NBT tags (w/o nbt will call this)
	@Override
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty,
											@Nullable IEntityLivingData livingData) {
		getData().addBendingId(Airbending.ID);


		boolean sterile = false;
		if (livingData instanceof BisonSpawnData) {
			sterile = ((BisonSpawnData) livingData).isSterile();
		}
		condition.setSterile(sterile);
		condition.setBreedTimer((int) (MOBS_CONFIG.bisonSettings.bisonBreedMaxMinutes * 1200));

		IBlockState walkingOn = world.getBlockState(getEntityPos(this).minusY(0.01)
				.toBlockPos());
		wasTouchingGround = walkingOn.getMaterial() != Material.AIR;

		condition.setAge(rand.nextInt(48000) + 40000);


		originalPos = Vector.getEntityPos(this);
		return super.onInitialSpawn(difficulty, livingData);

	}

	// ================================================================================
	// DATA ACCESS / DATAMANAGER
	// ================================================================================

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		originalPos = Vector.readFromNbt(nbt);
		ownerAttr.readFromNbt(nbt);
		setSitting(nbt.getBoolean("Sitting"));
		condition.readFromNbt(nbt);
		setEatGrassTime(nbt.getInteger("EatGrass"));
		riderTicks = nbt.getInteger("RiderTicks");
		setLoveParticles(nbt.getBoolean("InLove"));
		setId(nbt.getInteger("BisonId"));
		// id 0 is invalid; regenerate id if it is invalid
		// this usually happens in /summon command
		if (nbt.getInteger("BisonId") == 0) {
			setId(AvatarWorldData.getDataFromWorld(world).nextEntityId());
		}

		// Update chest size based on just read data
		initChest();
		readInventory(chest, nbt, "Inventory");

	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		ownerAttr.writeToNbt(nbt);
		nbt.setBoolean("Sitting", isSitting());
		condition.writeToNbt(nbt);
		nbt.setInteger("EatGrass", getEatGrassTime());
		nbt.setInteger("RiderTicks", riderTicks);
		nbt.setBoolean("InLove", isLoveParticles());
		nbt.setInteger("BisonId", getId());

		// originalPos is null when /summoned
		// this method called by WAILA when you look at bison
		if (originalPos != null) {
			originalPos.writeToNbt(nbt);
		} else {
			Vector.getEntityPos(this).writeToNbt(nbt);
		}

		writeInventory(chest, nbt, "Inventory");

	}

	public Vector getOriginalPos() {

		// might not be init in rare cases, for example when /summon'd with nbt
		// arg
		if (originalPos.equals(Vector.ZERO)) {
			originalPos = Vector.getEntityPos(this);
		}

		return originalPos;
	}

	@Override
	public UUID getOwnerId() {
		return ownerAttr.getEntityId();
	}

	public void setOwnerId(@Nullable UUID id) {
		ownerAttr.setEntityId(id);
	}

	public boolean hasOwner() {
		return getOwnerId() != null;
	}

	@Override
	@Nullable
	public EntityPlayer getOwner() {
		return (EntityPlayer) ownerAttr.getEntity();
	}

	public void setOwner(@Nullable EntityPlayer owner) {
		ownerAttr.setEntity(owner);
	}

	public boolean isSitting() {
		return dataManager.get(SYNC_SITTING);
	}

	public void setSitting(boolean sitting) {
		dataManager.set(SYNC_SITTING, sitting);
	}

	public float getSpeedMultiplier() {
		float armorSpeed = getArmor() == null ? 1 : getArmor().getSpeedMultiplier();
		float attackSpeed = getAttackTarget() != null ? 1.25f : 1f;
		return condition.getSpeedMultiplier() * armorSpeed * attackSpeed;
	}

	public AnimalCondition getCondition() {
		return condition;
	}

	/**
	 * Returns whether the bison will stop eating grass
	 */
	public boolean isFull() {
		return condition.getFoodPoints() > 25;
	}

	/**
	 * Returns whether the bison will bother to eat grass. However, this isn't
	 * the same as {@link #isFull() not full}; returns the bison is really
	 * hungry
	 */
	public boolean wantsGrass() {
		return condition.getFoodPoints() < 15 && getAttackTarget() == null;
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

	public boolean isLoveParticles() {
		return dataManager.get(SYNC_LOVE_PARTICLES);
	}

	public void setLoveParticles(boolean inLove) {
		dataManager.set(SYNC_LOVE_PARTICLES, inLove);
	}

	/**
	 * Gets the tier of the current saddle equipped, or null if there is no
	 * saddle.
	 */
	public SaddleTier getSaddle() {
		return dataManager.get(SYNC_SADDLE);
	}

	public void setSaddle(SaddleTier saddle) {
		dataManager.set(SYNC_SADDLE, saddle);
	}

	/**
	 * Gets the tier of the current armor equipped, or null if there is no
	 * armor.
	 */
	public ArmorTier getArmor() {
		return dataManager.get(SYNC_ARMOR);
	}

	public void setArmor(ArmorTier armor) {
		dataManager.set(SYNC_ARMOR, armor);
	}

	public int getId() {
		return dataManager.get(SYNC_ID);
	}

	public void setId(int id) {
		dataManager.set(SYNC_ID, id);
	}

	// ================================================================================
	// CHUNK LOADING
	// ================================================================================

	public boolean isForceLoadingChunks() {
		return ticket != null;
	}

	public void beginForceLoadingChunks() {
		if (!isForceLoadingChunks()) {
			ticket = ForgeChunkManager.requestTicket(AvatarMod.instance, world, Type.ENTITY);
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

			//Fix this as well, since the max age is now 7. Yay.
			float sizeOffset = condition.getAgeDays() < 7 ? condition.getAgeDays() / condition.getAdultAge() / 2F : 0.5F;
			double offset = 0.75;
			double angle = (index + 0.5) * Math.PI - toRadians(rotationYaw);
			double yOffset = passenger.getYOffset() + (2.5 * (sizeOffset + 0.35));

			if (passenger == getControllingPassenger() && !this.isSitting()) {
				angle = -toRadians(passenger.rotationYaw);
				offset = 1;
				yOffset = passenger.getYOffset() + (2.5 * (sizeOffset + 0.40));
			}
			if (passenger == getControllingPassenger() && this.isSitting()) {
				angle = -toRadians(passenger.rotationYaw);
				offset = 1;
				yOffset = passenger.getYOffset() + (2.5 * (sizeOffset + 0.35));
			}

			if (/*!AvatarMod.realFirstPersonRender2Compat && **/(PlayerViewRegistry.getPlayerViewMode(passenger.getUniqueID()) >= 2 ||
					PlayerViewRegistry.getPlayerViewMode(passenger.getUniqueID()) <= -1)) {
				passenger.setPosition(posX + sin(angle) * offset, posY + yOffset,
						posZ + cos(angle) * offset);
			} else {
				passenger.setPosition(posX + sin(angle) * offset - sin(angle), posY + yOffset,
						posZ + cos(angle) * offset - cos(angle));
			}

			if (passenger != getControllingPassenger()) {
				if (motionX != 0 || motionY != 0 || motionZ != 0) {
					passenger.rotationYaw = this.rotationYawHead;
					passenger.rotationPitch = this.rotationPitch;
				}
			}

		}

	}

	@Override
	protected boolean canFitPassenger(Entity passenger) {
		int passengers = getPassengers().size();
		int saddleSize = getSaddle() == null ? 0 : getSaddle().getMaxPassengers();
		return passengers < condition.getMaxRiders() && passengers < saddleSize;
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

		if (stack != EMPTY) {

			Item item = stack.getItem();
			int domesticationValue = MOBS_CONFIG.getDomesticationValue(item);

			if (domesticationValue > 0) {
				condition.addDomestication(domesticationValue);

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
					stack.shrink(1);
				}
				// Don't stop now if we are about to be owned
				if (!willBeOwned) {
					return true;
				}
			}

		}

		if (willBeOwned) {
			playTameEffect(true);
			setOwnerId(AccountUUIDs.getId(player.getName()));
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}

			// Hacky hack to have own CriteriaTrigger for bison taming, without actually making a
			// legit one because Criterion system is an overcomplicated bloated mess of bullcrap

			// (Animal taming trigger doesn't work since this isn't a subclass of EntityAnimal)

			if (!world.isRemote) {

				// This would never trigger normally since bison whistle doesn't have durability
				CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger((EntityPlayerMP) player, new
						ItemStack(AvatarItems.itemBisonWhistle), 0);

			}

			// Send bison tamed analytic
			if (!world.isRemote) {
				AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onBisonTamed());
			}

			return true;
		}

		if (stack.getItem() == Items.REDSTONE) {
			condition.setDomestication(0);
			playTameEffect(false);
			setOwnerId(null);
			return true;
		}

		if (stack.getItem() == AvatarItems.itemBisonSaddle && SaddleTier.isValidId(stack.getMetadata())) {
			if (!world.isRemote) {
				chest.setInventorySlotContents(0, stack.copy());
				updateEquipment();
			}
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
			return true;
		}
		if (stack.getItem() == AvatarItems.itemBisonArmor && ArmorTier.isValidId(stack.getMetadata())) {
			if (!world.isRemote) {
				chest.setInventorySlotContents(1, stack.copy());
				updateEquipment();
			}
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
			return true;
		}

		if (!world.isRemote && stack.getItem() == AvatarItems.itemBisonWhistle && player.isSneaking()) {
			if (player == getOwner()) {
				ItemBisonWhistle.setBoundTo(stack, getUniqueID());
				ItemBisonWhistle.setBisonName(stack, getName());
				MSG_BISON_WHISTLE_ASSIGN.send(player, getName());
			} else {
				if (getOwner() != null) {
					MSG_BISON_WHISTLE_NOTOWNED.send(player);
				} else {
					MSG_BISON_WHISTLE_UNTAMED.send(player);
				}
			}

			return true;
		}

		if (!world.isRemote && stack.getItem() == Items.ARROW) {

			int food = (int) (100.0 * condition.getFoodPoints() / 30);
			int health = (int) (100.0 * getHealth() / getMaxHealth());

			MSG_SKY_BISON_STATS.send(player, food, health, condition.getDomestication());

			return true;

		}

		// Temporary debug info -- only to be used in the development environment
		if (!world.isRemote && stack.getItem() == Items.BONE) {
			try {
				// Use reflection to get currently executing tasks
				Field field = EntityAITasks.class.getDeclaredField("executingTaskEntries");
				field.setAccessible(true);
				Set<EntityAITasks.EntityAITaskEntry> executingTasks = (Set<EntityAITasks.EntityAITaskEntry>) field.get(tasks);

				System.out.println("Currently executing tasks:");
				for (EntityAITasks.EntityAITaskEntry entry : executingTasks) {
					System.out.println(" - " + entry.action);
				}
				System.out.println(".");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (stack.getItem() == Items.NAME_TAG) {
			setAlwaysRenderNameTag(true);
			return false;
		}

		if (stack.getItem() == Item.getItemFromBlock(Blocks.TALLGRASS)) {
			if (condition.getBreedTimer() == 0) {
				float min = MOBS_CONFIG.bisonSettings.bisonBreedMinMinutes;
				float max = MOBS_CONFIG.bisonSettings.bisonBreedMaxMinutes;
				float minutes = min + rand.nextFloat() * (max - min);
				condition.setBreedTimer((int) (minutes * 1200));
				if (!player.capabilities.isCreativeMode) {
					stack.shrink(1);
				}
				return true;
			}
		}

		if (getOwner() == player && stack.getItem() == Item.getItemFromBlock(Blocks.CHEST)) {
			// Send id as the x-coordinate; used by guiHandler to determine
			// which bison is being opened
			player.openGui(AvatarMod.instance, AvatarGuiHandler.GUI_ID_BISON_CHEST, world, getId(), 0, 0);
			return true;
		}

		if (player.isSneaking() && getOwner() == player) {
			if (!isSitting()) {
				Bender b = Bender.get(getOwner());
				if (b != null && !world.isRemote) {
					b.sendMessage("avatar.bisonSitting");
				}
			}
			setSitting(!isSitting());
			madeSitByPlayer = true;
			return true;
		}

		return super.processInteract(player, hand);

	}

	/**
	 * Called whenever a player left-clicks (attacks) a sky bison. Return true
	 * to cancel normal behavior and treat the interaction specially, otherwise
	 * will attack as normal.
	 */
	public boolean onLeftClick(EntityPlayer player) {

		if (this.hasOwner()) {
			if (player.isSneaking()) {
				// Open GUI

				// Send id as the x-coordinate; used by guiHandler to determine
				// which bison is being opened
				player.openGui(AvatarMod.instance, AvatarGuiHandler.GUI_ID_BISON_CHEST, world, getId(), 0,
						0);
			} else {
				// Mount bison
				if (!world.isRemote) {
					player.startRiding(this);
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean canBeLeashedTo(EntityPlayer player) {
		return condition.getDomestication() >= MOBS_CONFIG.bisonSettings.bisonRiderTameness && super.canBeLeashedTo(player);
	}

	private void onLiftoff() {
		if (!isEatingGrass()) {
			getBender().executeAbility(Abilities.get("air_jump"), false);
			StatusControlController.AIR_JUMP.execute(new BendingContext(getData(), this, getBender(), new
					Raytrace.Result()));
			getData().removeStatusControl(StatusControlController.AIR_JUMP);
		}
	}

	private void onLand() {
		world.playSound(null, getPosition(), STONE.getSoundType().getBreakSound(), NEUTRAL, 1, 1);
		if (condition.getFoodPoints() == 0 && getOwner() != null) {
			Bender b = Bender.get(getOwner());
			if (b != null && !world.isRemote) {
				b.sendMessage("avatar.bisonNoFood");
			}
		}
	}

	@Override
	public void eatGrassBonus() {
		condition.addFood(MOBS_CONFIG.bisonSettings.bisonGrassFoodBonus);
	}

	// ================================================================================
	// CHEST / INVENTORY
	// ================================================================================

	/**
	 * Updates chest size based on current chest slots
	 */
	private void initChest() {

		InventoryBisonChest old = chest;
		chest = new InventoryBisonChest(getChestSlots());
		if (hasCustomName()) {
			chest.setCustomName(getName());
		}

		if (old != null) {
			old.removeInventoryChangeListener(this);

			// Transfer old stacks into new inventory
			int slots = Math.min(old.getSizeInventory(), chest.getSizeInventory());
			for (int i = 0; i < slots; ++i) {
				ItemStack stack = old.getStackInSlot(i);
				if (!stack.isEmpty()) {
					chest.setInventorySlotContents(i, stack.copy());
				}
			}

		}

		chest.addInventoryChangeListener(this);
		updateEquipment();

	}

	/**
	 * Hack used by readEntityFromNbt to read th
	 */
	private void setFullInventorySize() {

	}

	@Override
	public void onInventoryChanged(IInventory invBasic) {
		updateEquipment();
	}

	/**
	 * Updates equipment based on inventory contents
	 */
	private void updateEquipment() {

		// Update saddle
		if (!world.isRemote) {

			ItemStack saddleStack = chest.getStackInSlot(0);
			int saddleId = saddleStack.getMetadata();
			if (saddleStack.getItem() == AvatarItems.itemBisonSaddle && SaddleTier.isValidId(saddleId)) {
				setSaddle(SaddleTier.get(saddleId));
			} else {
				setSaddle(null);
			}

			ItemStack armorStack = chest.getStackInSlot(1);
			int armorId = armorStack.getMetadata();
			if (armorStack.getItem() == AvatarItems.itemBisonArmor && ArmorTier.isValidId(armorId)) {
				setArmor(ArmorTier.get(armorId));
			} else {
				setArmor(null);
			}

		}

		getEntityAttribute(ARMOR).setBaseValue(getArmorPoints());

	}

	public InventoryBisonChest getInventory() {
		return chest;
	}

	public boolean canPlayerViewInventory(EntityPlayer player) {
		return getOwner() == player && condition.getDomestication() >= MOBS_CONFIG.bisonSettings.bisonChestTameness;
	}

	public int getChestSlots() {
		if (condition.getDomestication() >= MOBS_CONFIG.bisonSettings.bisonChestTameness && condition.getAgeDays() > 1) {

			int age = (int) (condition.getAgeDays() - 1);

			if (age >= 5) {
				return 27;
			} else if (age >= 3) {
				return 18;
			} else {
				return 9;
			}

		} else {
			return 0;
		}
	}

	public float getArmorPoints() {
		float saddle = getSaddle() == null ? 0 : getSaddle().getArmorPoints();
		float armor = getArmor() == null ? 0 : getArmor().getArmorPoints();
		return saddle + armor;
	}

	@Override
	public void onDeath(DamageSource cause) {
		super.onDeath(cause);
		if (!world.isRemote) {
			for (int i = 0; i < chest.getSizeInventory(); i++) {
				ItemStack stack = chest.getStackInSlot(i);
				if (!stack.isEmpty()) {
					entityDropItem(stack, 0);
				}
			}

			// Log bison kills
			if (cause.getTrueSource() instanceof EntityPlayer) {
				AvatarLog.info("Bison " + getName() + " (owned by " + getOwner() + ") was just killed" +
						" by " + cause.getTrueSource().getName());
			}

		}
	}

	// ================================================================================
	// GENERAL UPDATE LOGIC
	// ================================================================================

	@Override
	public void onUpdate() {
		super.onUpdate();
		setLevel((int) Math.min(condition.getAgeDays(), 7));
		//Applies AI stuff as it ages
		applyAbilityLevels(getLevel());

		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20 + Math.min(80 * condition.getAgeDays()
				/ condition.getAdultAge(), 80));
		if (this.isSitting() && hasOwner() && (world.getBlockState(getEntityPos(this)
				.toBlockPos()).getBlock() != Blocks.AIR)) {
			this.motionX = this.motionY = this.motionZ = 0;
		}

		// Client-side chest sometimes doesn't have enough slots, since when the
		// # of slots changes, it doesn't necessarily re-init chest
		if (world.isRemote && chest.getSizeInventory() - 2 != getChestSlots()) {
			initChest();
		}

		if (!world.isRemote && !condition.canHaveOwner() && hasOwner()) {
			setOwner(null);
		}
		if (!world.isRemote) {
			setEatGrassTime(aiEatGrass.getEatGrassTime());
			setLoveParticles(condition.isReadyToBreed());
		}
		if (world.isRemote && isLoveParticles() && ticksExisted % 10 == 0) {
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;

			this.world.spawnParticle(EnumParticleTypes.HEART,
					this.posX + this.rand.nextFloat() * this.width * 2 - this.width,
					this.posY + 0.5D + this.rand.nextFloat() * this.height,
					this.posZ + this.rand.nextFloat() * this.width * 2 - this.width, d0, d1, d2);

		}

		// Adjusts bounding box based on entity's scaling size
		float sizeMult = condition.getSizeMultiplier();
		setSize(3.67F * sizeMult, 3.34F * sizeMult);

		condition.onUpdate();

		if (!madeSitByPlayer && this.isSitting() && condition.getFoodPoints() > 0) {
			this.setSitting(false);
		}

		if (condition.getFoodPoints() == 0 && getOwner() != null) {
			setSitting(true);
			madeSitByPlayer = false;
		} else if (!hasOwner()) {
			setSitting(false);
		}

		//Stops crashes; weird bugs will ensue, but blame cubic chunks
		if (!AvatarMod.cubicChunks) {
			if (!isForceLoadingChunks() && hasOwner()) {
				beginForceLoadingChunks();
			}
			if (isForceLoadingChunks()) {
				ForgeChunkManager.forceChunk(ticket, new ChunkPos(getPosition()));
				if (!hasOwner() || getHealth() <= 0) {
					stopForceLoadingChunks();
				}
			}
		}
		if (!world.isRemote) {

			IBlockState walkingOn = world.getBlockState(getEntityPos(this).withY(0.01)
					.toBlockPos());
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
			condition.addDomestication(MOBS_CONFIG.bisonSettings.bisonRideOneSecondTameness * 3);
			playTameEffect(false);
		}

	}

	@Override
	public void applyAbilityLevels(int level) {

	}


	// moveWithHeading
	@Override
	public void travel(float strafe, float jump, float forward) {

		if (isEatingGrass()) {
			motionY -= 0.08;
		}

		// onGround apparently doesn't work client-side
		IBlockState walkingOn = world.getBlockState(getEntityPos(this).withY(0.01).toBlockPos
				());
		boolean touchingGround = walkingOn.getMaterial() != Material.AIR;

		if (this.isBeingRidden() && this.canBeSteered()) {

			moveHelper.action = Action.WAIT;

			EntityLivingBase driver = (EntityLivingBase) getControllingPassenger();

			float pitch = abs(driver.rotationPitch) < 20 ? 0 : driver.rotationPitch;
			if (touchingGround && abs(pitch) < 45) {
				pitch = 0;
			}
			Vector look = toRectangular(toRadians(driver.rotationYaw), toRadians(pitch));
			forward = (float) look.withY(0).magnitude();

			float speedMult = getSpeedMultiplier() * driver.moveForward * 1.25F;

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

				travelFlying(strafe, jump * speedMult, forward);
				motionY += look.y() * 0.02 * speedMult;

			} else if (driver instanceof EntityPlayer) {
				this.motionX = 0.0D;
				this.motionY = 0.0D;
				this.motionZ = 0.0D;
			}

			this.prevLimbSwingAmount = this.limbSwingAmount;
			double moveX = this.posX - this.prevPosX;
			double moveZ = this.posZ - this.prevPosZ;
			float moveSinceLastTick = MathHelper.sqrt(moveX * moveX + moveZ * moveZ) * 4.0F;

			if (moveSinceLastTick > 1.0F) {
				moveSinceLastTick = 1.0F;
			}

			this.limbSwingAmount += (moveSinceLastTick - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		} else {
			this.jumpMovementFactor = 0.02F;
			travelFlying(strafe, jump, forward);
		}

	}

	// ================================================================================
	// COPIED FROM ENTITYFLYING
	// ================================================================================

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
	}

	private void travelFlying(float strafe, float jump, float forward) {
		if (this.isInWater()) {
			this.moveRelative(strafe, jump, forward, 0.02F);
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.800000011920929D;
			this.motionY *= 0.800000011920929D;
			this.motionZ *= 0.800000011920929D;
		} else if (this.isInLava()) {
			this.moveRelative(strafe, jump, forward, 0.02F);
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
		} else {
			float f = 0.91F;

			if (this.onGround) {
				f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX),
						MathHelper.floor(this.getEntityBoundingBox().minY) - 1,
						MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
			}

			float f1 = 0.16277136F / (f * f * f);
			this.moveRelative(strafe, jump, forward, this.onGround ? 0.1F * f1 : 0.06F);
			f = 0.91F;

			if (this.onGround) {
				f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX),
						MathHelper.floor(this.getEntityBoundingBox().minY) - 1,
						MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
			}

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= f;
			this.motionY *= f;
			this.motionZ *= f;
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double d1 = this.posX - this.prevPosX;
		double d0 = this.posZ - this.prevPosZ;
		float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

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
			this.world.spawnParticle(particle,
					this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
					this.posY + 0.5D + this.rand.nextFloat() * this.height,
					this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width, //
					mx, my, mz);
		}

	}

	private class BisonBenderComponent extends BenderEntityComponent {

		private BisonBenderComponent() {
			super(EntitySkyBison.this);
			// Since this is an inner class, it has a reference to the outer class that created
			// it; that can be retrieved with "EntitySkyBison.this"
		}

		@Override
		public boolean isFlying() {
			return true;
		}

	}

}
