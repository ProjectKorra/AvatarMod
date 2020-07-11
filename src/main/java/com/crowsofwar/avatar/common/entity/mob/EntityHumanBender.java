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

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.scroll.Scrolls;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.WildCardTradeList;
import com.crowsofwar.gorecore.format.FormattedMessage;
import com.crowsofwar.gorecore.util.GoreCoreNBTUtil;
import com.google.common.base.Predicate;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Random;

import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_NEED_TRADE_ITEM;
import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;

/**
 * @author CrowsOfWar, FavouriteDraogn
 */
public abstract class EntityHumanBender extends EntityBender implements IMerchant, INpc {

	private static final DataParameter<Integer> SYNC_SKIN = EntityDataManager
			.createKey(EntityHumanBender.class, DataSerializers.VARINT);

	private static final DataParameter<Integer> SYNC_SCROLLS_LEFT = EntityDataManager
			.createKey(EntityHumanBender.class, DataSerializers.VARINT);
	/**
	 * The entity selector passed into the new AI methods.
	 */
	protected Predicate<Entity> targetSelector;
	private boolean hasAttemptedTrade;
	/**
	 * The wizard's trades.
	 */
	private MerchantRecipeList trades;
	/**
	 * The wizard's current customer.
	 */
	@Nullable
	private EntityPlayer customer;
	private int timeUntilReset;

	/**
	 * addDefaultEquipmentAndRecipies is called if this is true
	 */
	private boolean updateRecipes;

	//TODO: Chi
	public EntityHumanBender(World world) {
		super(world);
		this.hasAttemptedTrade = false;
	}

	public int getScrollsLeft() {
		return dataManager.get(SYNC_SCROLLS_LEFT);
	}

	public void setScrollsLeft(int scrolls) {
		dataManager.set(SYNC_SCROLLS_LEFT, scrolls);
	}

	@Override
	public boolean getAlwaysRenderNameTagForRender() {
		return true;
	}


	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_SKIN, AvatarUtils.getRandomNumberInRange(1, getNumSkins()));
		dataManager.register(SYNC_SCROLLS_LEFT, getLevel());
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3);
	}

	@Override
	protected void initEntityAI() {

		//this.targetTasks.addTask(0, new EntityAIBenderDefendVillage(this));
		this.tasks.addTask(0, new EntityAISwimming(this));
		// Why would you go to the effort of making the IMerchant interface and then have the AI classes only accept
		// EntityVillager?
		this.tasks.addTask(1, new EntityAITradePlayer(this));
		this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
		this.tasks.addTask(4, new EntityAIRestrictOpenDoor(this));
		this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
		this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.6D));
		this.tasks.addTask(7, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(7, new EntityAIWatchClosest2(this, EntityHumanBender.class, 5.0F, 0.02F));
		this.tasks.addTask(7, new EntityAIWander(this, 0.6D));
		this.tasks.addTask(7, new EntityAILookIdle(this));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));

		this.targetSelector = entity -> {

			if (entity != null && !entity.isInvisible() && entity.canBeAttackedWithItem()) {
				if (entity.getTeam() == null || getTeam() == null || entity.getTeam() != null && getTeam() != null && entity.getTeam() == getTeam()) {
					return entity instanceof EntityMob
							&& (((EntityMob) entity).isEntityUndead());
				}
			}

			return false;
		};

		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true));
		// By default, wizards don't attack players unless the player has attacked them.
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 0,
				false, true, this.targetSelector));
		addBendingTasks();

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("trades")) {
			NBTTagCompound nbttagcompound1 = nbt.getCompoundTag("trades");
			this.trades = new WildCardTradeList(nbttagcompound1);
		}

		setSkin(nbt.getInteger("Skin"));
		//	setScrollsLeft(nbt.getInteger("Scrolls"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		if (this.trades != null) {
			GoreCoreNBTUtil.storeTagSafely(nbt, "trades", this.trades.getRecipiesAsTags());
		}

		nbt.setInteger("Skin", getSkin());
		//	nbt.setInteger("Scrolls", getScrollsLeft());
	}

	protected abstract void addBendingTasks();

	protected boolean isTradeItem(Item item) {
		return MOBS_CONFIG.isTradeItem(item);
	}

	protected abstract int getNumSkins();

	public int getSkin() {
		return dataManager.get(SYNC_SKIN);
	}

	public void setSkin(int skin) {
		dataManager.set(SYNC_SKIN, skin);
	}

	protected FormattedMessage getTradeFailMessage() {
		return MSG_NEED_TRADE_ITEM;
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		super.setEquipmentBasedOnDifficulty(difficulty);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty,
											@Nullable IEntityLivingData livingdata) {
		livingdata = super.onInitialSpawn(difficulty, livingdata);
		setEquipmentBasedOnDifficulty(difficulty);
		setHomePosAndDistance(getPosition(), 40);
		setSkin((int) (rand.nextDouble() * getNumSkins()));
		setLevel(AvatarUtils.getRandomNumberInRange(1, MOBS_CONFIG.benderSettings.maxLevel));
		generateRecipes();
		return livingdata;
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		super.writeSpawnData(buffer);
		buffer.writeInt(getSkin());
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		super.readSpawnData(additionalData);
		setSkin(additionalData.readInt());
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int i = 0;

		if (entityIn instanceof EntityLivingBase) {
			f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(),
					((EntityLivingBase) entityIn).getCreatureAttribute());
			i += EnchantmentHelper.getKnockbackModifier(this);
		}

		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

		if (flag) {
			if (i > 0 && entityIn instanceof EntityLivingBase) {
				((EntityLivingBase) entityIn).knockBack(this, i * 0.5F,
						MathHelper.sin(this.rotationYaw * 0.017453292F),
						(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
				this.motionX *= 0.6D;
				this.motionZ *= 0.6D;
			}

			int j = EnchantmentHelper.getFireAspectModifier(this);

			if (j > 0) {
				entityIn.setFire(j * 4);
			}

			if (entityIn instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entityIn;
				ItemStack itemstack = this.getHeldItemMainhand();
				ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack()
						: ItemStack.EMPTY;

				if (!itemstack.isEmpty() && !itemstack1.isEmpty()
						&& itemstack.getItem() instanceof ItemAxe && itemstack1.getItem() == Items.SHIELD) {
					float f1 = 0.25F + EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

					if (this.rand.nextFloat() < f1) {
						entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
						this.world.setEntityState(entityplayer, (byte) 30);
					}
				}
			}

			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}

	@Override
	protected abstract ResourceLocation getLootTable();

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {

		// Won't trade with a player that has attacked them.
		if (this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking()
				&& this.getAttackTarget() != player) {
			generateRecipes();
			if (!this.world.isRemote && !trades.isEmpty()) {
				this.setCustomer(player);
				player.displayVillagerTradeGui(this);
				// player.displayGUIMerchant(this, this.getElement().getWizardName());
			}
			return true;
		} else {
			return false;
		}
	}

	public void setInitialScrolls(int level) {
		setScrollsLeft(level);
	}


	@Override
	protected void updateAITasks() {

		if (!this.isTrading() && this.timeUntilReset > 0) {

			--this.timeUntilReset;

			if (this.timeUntilReset <= 0) {

				if (this.updateRecipes) {

					for (MerchantRecipe merchantrecipe : this.trades) {

						if (merchantrecipe.isRecipeDisabled()) {
							// Increases the number of allowed uses of a disabled recipe by a random number.
							merchantrecipe.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
						}
					}

					if (this.trades.size() < 12) {
						this.addRandomRecipes(1);
						this.addReverseRandomRecipes(1);
					}

					this.updateRecipes = false;
				}

				this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0));
			}
		}

		super.updateAITasks(); // This actually does nothing
	}

	@Override
	public EntityPlayer getCustomer() {
		return this.customer;
	}

	@Override
	public void setCustomer(EntityPlayer player) {
		this.customer = player;
	}

	public boolean isTrading() {
		return this.getCustomer() != null;
	}

	@Override
	public void verifySellingItem(ItemStack stack) {
		// Copied from EntityVillager
		if (!this.world.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
			this.livingSoundTime = -this.getTalkInterval();
			this.playSound(stack.isEmpty() ? SoundEvents.ENTITY_VILLAGER_NO : SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
		}
	}


	public BendingStyle getElement() {
		return new Airbending();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setRecipes(@Nullable MerchantRecipeList recipeList) {
		//Nothing goes here
	}

	@Override
	public void useRecipe(MerchantRecipe merchantrecipe) {

		merchantrecipe.incrementToolUses();
		this.livingSoundTime = -this.getTalkInterval();
		this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());

		// Changed to a 4 in 5 chance of unlocking a new recipe.
		if (this.rand.nextInt(5) > 0) {
			this.timeUntilReset = 40;
			this.updateRecipes = true;

			if (this.getCustomer() != null) {
				this.getCustomer().getName();
			}
		}
	}

	// This is called from the gui in order to display the recipes (no surprise there), and this is actually where
	// the initialisation is done, i.e. the trades don't actually exist until some player goes to trade with the
	// villager, at which point the first is added.
	@Override
	public MerchantRecipeList getRecipes(EntityPlayer par1EntityPlayer) {
		generateRecipes();
		return trades;
	}

	//Same as below, except it swaps the price and the sold item
	private void addReverseRandomRecipes(int numberOfItemsToAdd) {

		MerchantRecipeList recipeList = new MerchantRecipeList();

		for (int i = 0; i < numberOfItemsToAdd; i++) {

			ItemStack itemToSell = ItemStack.EMPTY;
			ItemStack item2nd = ItemStack.EMPTY;
			ItemStack firstPrice = ItemStack.EMPTY;
			ItemStack secondPrice = ItemStack.EMPTY;

			boolean itemAlreadySold = true;

			int tier = 1;
			int maxTier = getLevel();
			int finalTier = tier;
			double tierInc = (maxTier - tier) / 3F;
			boolean greaterTier;

			while (itemAlreadySold) {

				itemAlreadySold = false;

				/* New way of getting random item, by giving a chance to increase the tier which depends on how much the
				 * player has already traded with the wizard. The more the player has traded with the wizard, the more
				 * likely they are to get items of a higher tier. The -4 is to ignore the original 4 trades. For
				 * reference, the chances are as follows: Trades done Basic Apprentice Advanced Master 0 50% 25% 18% 8%
				 * 1 46% 25% 20% 9% 2 42% 24% 22% 12% 3 38% 24% 24% 14% 4 34% 22% 26% 17% 5 30% 21% 28% 21% 6 26% 19%
				 * 30% 24% 7 22% 17% 32% 28% 8 18% 15% 34% 33% */

				double tierIncreaseChance = 0.70 + 0.04 * (Math.max(trades == null ? 0 : this.trades.size() - 4, 0));

				if (rand.nextDouble() < tierIncreaseChance) {
					tier += tierInc;
					if (rand.nextDouble() < tierIncreaseChance) {
						tier += tierInc;
						if (rand.nextDouble() < tierIncreaseChance * 0.6) {
							tier += tierInc;
						}
					}
				}
				tier = Math.min(tier, maxTier);

				finalTier = (Math.min(AvatarUtils.getRandomNumberInRange(Math.max(tier - 1, 1), tier) +
						(int) tierIncreaseChance * AvatarUtils.getRandomNumberInRange(2, 4) + 1, maxTier));
				itemToSell = this.getRandomItemOfTier(finalTier);
				item2nd = this.getRandomItemOfTier(finalTier);
				firstPrice = this.getRandomPrice(finalTier);
				secondPrice = this.getRandomPrice(finalTier);
				if (this.trades != null) {
					for (Object recipe : this.trades) {
						if (ItemStack.areItemStacksEqual(((MerchantRecipe) recipe).getItemToSell(), firstPrice)
						|| ItemStack.areItemStacksEqual(((MerchantRecipe) recipe).getItemToSell(), secondPrice))
							itemAlreadySold = true;
					}
				}
			}

			// Don't know how it can ever be empty here, but it's a failsafe.
			if (itemToSell == ItemStack.EMPTY) return;


			greaterTier = MOBS_CONFIG.getTradeItemTier(firstPrice.getItem()) > finalTier;
			if (greaterTier)
				recipeList.add(new MerchantRecipe(itemToSell, firstPrice));
			else {
				greaterTier = MOBS_CONFIG.getTradeItemTier(secondPrice.getItem()) > finalTier;
				if (greaterTier)
					recipeList.add(new MerchantRecipe(itemToSell, secondPrice));
				else {
					recipeList.add(new MerchantRecipe(itemToSell, item2nd, world.rand.nextBoolean() ? firstPrice : secondPrice));
				}
			}

		}

		Collections.shuffle(recipeList);

		if (this.trades == null) {
			this.trades = new WildCardTradeList();
		}

		this.trades.addAll(recipeList);
	}
	/**
	 * This is called once on initialisation and then once each time the wizard gains new trades (the particle thingy).
	 */
	private void addRandomRecipes(int numberOfItemsToAdd) {

		MerchantRecipeList recipeList = new MerchantRecipeList();

		for (int i = 0; i < numberOfItemsToAdd; i++) {

			ItemStack itemToSell = ItemStack.EMPTY;

			boolean itemAlreadySold = true;

			int tier = 1;
			int maxTier = getLevel();
			int finalTier = tier;
			double tierInc = (maxTier - tier) / 3F;
			boolean greaterTier;

			while (itemAlreadySold) {

				itemAlreadySold = false;

				/* New way of getting random item, by giving a chance to increase the tier which depends on how much the
				 * player has already traded with the wizard. The more the player has traded with the wizard, the more
				 * likely they are to get items of a higher tier. The -4 is to ignore the original 4 trades. For
				 * reference, the chances are as follows: Trades done Basic Apprentice Advanced Master 0 50% 25% 18% 8%
				 * 1 46% 25% 20% 9% 2 42% 24% 22% 12% 3 38% 24% 24% 14% 4 34% 22% 26% 17% 5 30% 21% 28% 21% 6 26% 19%
				 * 30% 24% 7 22% 17% 32% 28% 8 18% 15% 34% 33% */

				double tierIncreaseChance = 0.70 + 0.04 * (Math.max(trades == null ? 0 : this.trades.size() - 4, 0));

				if (rand.nextDouble() < tierIncreaseChance) {
					tier += tierInc;
					if (rand.nextDouble() < tierIncreaseChance) {
						tier += tierInc;
						if (rand.nextDouble() < tierIncreaseChance * 0.6) {
							tier += tierInc;
						}
					}
				}
				tier = Math.min(tier, maxTier);

				finalTier = (Math.min(AvatarUtils.getRandomNumberInRange(Math.max(tier - 1, 1), tier) +
						(int) tierIncreaseChance * AvatarUtils.getRandomNumberInRange(2, 4) + 1, maxTier));
				itemToSell = this.getRandomItemOfTier(finalTier);

				if (this.trades != null) {
					for (Object recipe : this.trades) {
						if (ItemStack.areItemStacksEqual(((MerchantRecipe) recipe).getItemToSell(), itemToSell))
							itemAlreadySold = true;
					}
				}
			}

			// Don't know how it can ever be empty here, but it's a failsafe.
			if (itemToSell == ItemStack.EMPTY) return;

			ItemStack firstPrice = this.getRandomPrice(finalTier);
			ItemStack secondPrice = this.getRandomPrice(finalTier);
			greaterTier = MOBS_CONFIG.getTradeItemTier(firstPrice.getItem()) > finalTier;
			if (greaterTier)
				recipeList.add(new MerchantRecipe(firstPrice, itemToSell));
			else {
				greaterTier = MOBS_CONFIG.getTradeItemTier(secondPrice.getItem()) > finalTier;
				if (greaterTier)
					recipeList.add(new MerchantRecipe(secondPrice, itemToSell));
				else recipeList.add(new MerchantRecipe(firstPrice, secondPrice, itemToSell));
			}

		}

		Collections.shuffle(recipeList);

		if (this.trades == null) {
			this.trades = new WildCardTradeList();
		}

		this.trades.addAll(recipeList);
	}


	// TODO: Switch all of this over to some kind of loot pool system?

	@SuppressWarnings("unchecked")
	private ItemStack getRandomPrice(int tier) {

		Item item = MOBS_CONFIG.getTradeItems().get(AvatarUtils.getRandomNumberInRange(0,
				MOBS_CONFIG.getTradeItems().size() - 1));
		String element = getElement().getName();
		switch (element) {
			case "airbending":
				while (MOBS_CONFIG.isFireTradeItem(item))
					item = MOBS_CONFIG.getTradeItems().get(AvatarUtils.getRandomNumberInRange(0,
							MOBS_CONFIG.getTradeItems().size() - 1));
				break;
			case "firebending":
				while (MOBS_CONFIG.isAirTradeItem(item))
					item = MOBS_CONFIG.getTradeItems().get(AvatarUtils.getRandomNumberInRange(0,
							MOBS_CONFIG.getTradeItems().size() - 1));
				break;
			default:
				break;
		}

		int price;

		if (item == null) {
			AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Invalid item in currency items");
			item = Items.EMERALD; // Fallback item
			price = 2;
		} else {
			price = MOBS_CONFIG.getTradeItemTier(item);
			if (tier < price)
				price = 1;
				//This doubles the amount required per tier, as 2 scrolls of the previous tier
				//are required to make 1 scroll of the next tier.
			else price = (int) Math.pow(2, tier - price);

		}

		// ((tier.ordinal() + 1) * 16 + rand.nextInt(6)) gives a 'value' for the item being bought
		// This is then divided by the value of the currency item to give a price
		// The absolute maximum stack size that can result from this calculation (with value = 1) is 64.
		return new ItemStack(item, price);
	}

	private ItemStack getRandomItemOfTier(int tier) {
		ItemStack toSell;
		boolean rand = world.rand.nextBoolean();
		if (tier > 7)
			tier--;

		if (rand)
			toSell = new ItemStack(Scrolls.ALL, 1, tier - 1);
		else toSell = new ItemStack(Scrolls.getTypeFromElement(getElement().getName()), 1, tier - 1);

		return toSell;
	}

	private void generateRecipes() {
		if (trades == null) {
			trades = new WildCardTradeList();
			ItemStack universalScroll = new ItemStack(Scrolls.ALL, 1, 1);
			ItemStack elementScroll = new ItemStack(Scrolls.getTypeFromElement(getElement()), 1, 2);
			ItemStack staff;
			if (getElement() instanceof Airbending) {
				staff = new Random().nextInt(100) % 2 == 0
						? new ItemStack(AvatarItems.gliderBasic, 1)
						: new ItemStack(AvatarItems.gliderAdv, 1);
			}
			else staff = new ItemStack(Scrolls.ALL, 2, 1);
			this.trades.add(new MerchantRecipe(universalScroll, elementScroll, staff));
			this.addRandomRecipes(3);
			this.addReverseRandomRecipes(3);
		}
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public BlockPos getPos() {
		return new BlockPos(this);
	}

	public static class EntityAILookAtTradePlayer extends EntityAIWatchClosest {

		private final EntityHumanBender bender;

		public EntityAILookAtTradePlayer(EntityHumanBender bender) {
			super(bender, EntityPlayer.class, 8.0F);
			this.bender = bender;
		}

		@Override
		public boolean shouldExecute() {
			if (this.bender.getCustomer() != null) {
				this.closestEntity = this.bender.getCustomer();
				return true;
			} else {
				return false;
			}
		}
	}

	public static class EntityAITradePlayer extends EntityAIBase {

		private final EntityHumanBender bender;

		public EntityAITradePlayer(EntityHumanBender bender) {
			this.bender = bender;
			this.setMutexBits(5);
		}

		@Override
		public boolean shouldExecute() {

			if (!this.bender.isEntityAlive()) {
				return false;
			} else if (this.bender.isInWater()) {
				return false;
			} else if (!this.bender.onGround) {
				return false;
			} else if (this.bender.velocityChanged) {
				return false;
			} else {

				EntityPlayer entityplayer = this.bender.getCustomer();

				if (entityplayer == null) {
					return false;
				} else if (this.bender.getDistanceSq(entityplayer) > 16.0D) {
					return false;
				} else {
					return entityplayer.openContainer != null;
				}
			}
		}

		@Override
		public void startExecuting() {
			this.bender.getNavigator().clearPath();
		}

		@Override
		public void resetTask() {
			this.bender.setCustomer(null);
		}
	}

}
