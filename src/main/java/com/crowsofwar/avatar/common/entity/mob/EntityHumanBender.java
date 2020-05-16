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

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.analytics.AnalyticEvents;
import com.crowsofwar.avatar.common.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.common.entity.ai.EntityAiGiveScroll;
import com.crowsofwar.avatar.common.item.scroll.ItemScroll;
import com.crowsofwar.avatar.common.item.scroll.Scrolls.ScrollType;
import com.crowsofwar.avatar.common.util.WildCardTradeList;
import com.crowsofwar.gorecore.format.FormattedMessage;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.function.Predicate;

import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_HUMANBENDER_NO_SCROLLS;
import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_NEED_TRADE_ITEM;
import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;

/**
 * @author CrowsOfWar
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
	private EntityAiGiveScroll aiGiveScroll;
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
		dataManager.register(SYNC_SKIN, (int) (rand.nextDouble() * getNumSkins()));
		dataManager.register(SYNC_SCROLLS_LEFT, getLevel());
		setInitialScrolls(getLevel());
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
				if (entity.getTeam() == null || entity.getTeam() != null && entity.getTeam() == getTeam()) {
					return entity instanceof EntityMob
							&& (((EntityMob) entity).isEntityUndead());
				}
			}

			return false;
		};

		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		// By default, wizards don't attack players unless the player has attacked them.
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 0,
				false, true, (com.google.common.base.Predicate<? super EntityLiving>) this.targetSelector));
		addBendingTasks();

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setSkin(nbt.getInteger("Skin"));
		setScrollsLeft(nbt.getInteger("Scrolls"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("Skin", getSkin());
		nbt.setInteger("Scrolls", getScrollsLeft());
	}

	protected abstract void addBendingTasks();

	protected abstract ScrollType getScrollType();

	protected boolean isTradeItem(Item item) {
		return MOBS_CONFIG.isTradeItem(item);
	}

	protected int getTradeAmount(Item item) {
		return MOBS_CONFIG.getTradeItemAmount(item);
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
		setHomePosAndDistance(getPosition(), 20);

		return livingdata;
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
		if (this.getLastAttackedEntity() != player) {
			hasAttemptedTrade = false;

			ItemStack stack = player.getHeldItem(hand);
		/*int amount = stack.getCount();
		int tradeAmount = getTradeAmount(stack.getItem());**/

			if (this.isTradeItem(stack.getItem()) && !world.isRemote/* && amount >= tradeAmount**/) {

				if (getScrollsLeft() > 0) {
					if (aiGiveScroll.giveScrollTo(player)) {
						System.out.println("Trade started");
						// Take item
						setScrollsLeft(getScrollsLeft() - 1);
						if (!player.capabilities.isCreativeMode) {
							stack.shrink(1);
						}

					}
					hasAttemptedTrade = true;
				} else {
					MSG_HUMANBENDER_NO_SCROLLS.send(player);
					AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onNpcNoScrolls());
				}

				return true;

			} else if (!(this.isTradeItem(stack.getItem())) && !world.isRemote && !hasAttemptedTrade) {
				getTradeFailMessage().send(player);
				hasAttemptedTrade = true;
				return true;
			}


		}
		return true;
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

	@
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {

		super.writeEntityToNBT(nbt);

		if (this.trades != null) {
			NBTExtras.storeTagSafely(nbt, "trades", this.trades.getRecipiesAsTags());
		}

		nbt.setInteger("element", this.getElement().ordinal());
		nbt.setInteger("skin", this.textureIndex);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {

		super.readEntityFromNBT(nbt);

		if (nbt.hasKey("trades")) {
			NBTTagCompound nbttagcompound1 = nbt.getCompoundTag("trades");
			this.trades = new WildCardTradeList(nbttagcompound1);
		}

		this.setElement(Element.values()[nbt.getInteger("element")]);
		this.textureIndex = nbt.getInteger("skin");
	}

	@Override
	public void setRecipes(@Nullable MerchantRecipeList recipeList) {
		//Nothing goes here
	}

	@Override
	public void useRecipe(MerchantRecipe merchantrecipe) {

		merchantrecipe.incrementToolUses();
		this.livingSoundTime = -this.getTalkInterval();
		this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());

		/* if (this.getCustomer() != null) {

			if (merchantrecipe.getItemToSell().getItem() instanceof ItemScroll) {

				Spell spell = Spell.byMetadata(merchantrecipe.getItemToSell().getItemDamage());

				if (spell.getTier() == Tier.MASTER)
					WizardryAdvancementTriggers.buy_master_spell.triggerFor(this.getCustomer());

				// Spell discovery (a lot of this is the same as in the event handler)
				WizardData data = WizardData.get(this.getCustomer());

				if (data != null) {

					if (!MinecraftForge.EVENT_BUS.post(new DiscoverSpellEvent(this.getCustomer(), spell,
							DiscoverSpellEvent.Source.PURCHASE)) && data.discoverSpell(spell)) {

						data.sync();

						if (!world.isRemote && !this.getCustomer().isCreative() && Wizardry.settings.discoveryMode) {
							// Sound and text only happen server-side, in survival, with discovery mode on
							WizardryUtilities.playSoundAtPlayer(this.getCustomer(), WizardrySounds.MISC_DISCOVER_SPELL, 1.25f, 1);
							this.getCustomer().sendMessage(new TextComponentTranslation("spell.discover",
									spell.getNameForTranslationFormatted()));
						}
					}
				}
			}
		}**/

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

		if (this.trades == null) {

			this.trades = new WildCardTradeList();

			// All wizards will buy spell books
			ItemStack anySpellBook = new ItemStack(WizardryItems.spell_book, 1, OreDictionary.WILDCARD_VALUE);
			ItemStack crystalStack = new ItemStack(WizardryItems.magic_crystal, 5);

			this.trades.add(new MerchantRecipe(anySpellBook, crystalStack));

			this.addRandomRecipes(3);
		}

		return this.trades;
	}

	/**
	 * This is called once on initialisation and then once each time the wizard gains new trades (the particle thingy).
	 */
	private void addRandomRecipes(int numberOfItemsToAdd) {

		MerchantRecipeList merchantrecipelist;
		merchantrecipelist = new MerchantRecipeList();

		for (int i = 0; i < numberOfItemsToAdd; i++) {

			ItemStack itemToSell = ItemStack.EMPTY;

			boolean itemAlreadySold = true;

			Tier tier = Tier.NOVICE;

			while (itemAlreadySold) {

				itemAlreadySold = false;

				/* New way of getting random item, by giving a chance to increase the tier which depends on how much the
				 * player has already traded with the wizard. The more the player has traded with the wizard, the more
				 * likely they are to get items of a higher tier. The -4 is to ignore the original 4 trades. For
				 * reference, the chances are as follows: Trades done Basic Apprentice Advanced Master 0 50% 25% 18% 8%
				 * 1 46% 25% 20% 9% 2 42% 24% 22% 12% 3 38% 24% 24% 14% 4 34% 22% 26% 17% 5 30% 21% 28% 21% 6 26% 19%
				 * 30% 24% 7 22% 17% 32% 28% 8 18% 15% 34% 33% */

				double tierIncreaseChance = 0.5 + 0.04 * (Math.max(this.trades.size() - 4, 0));

				tier = Tier.NOVICE;

				if (rand.nextDouble() < tierIncreaseChance) {
					tier = Tier.APPRENTICE;
					if (rand.nextDouble() < tierIncreaseChance) {
						tier = Tier.ADVANCED;
						if (rand.nextDouble() < tierIncreaseChance * 0.6) {
							tier = Tier.MASTER;
						}
					}
				}

				itemToSell = this.getRandomItemOfTier(tier);

				for (Object recipe : merchantrecipelist) {
					if (ItemStack.areItemStacksEqual(((MerchantRecipe) recipe).getItemToSell(), itemToSell))
						itemAlreadySold = true;
				}

				if (this.trades != null) {
					for (Object recipe : this.trades) {
						if (ItemStack.areItemStacksEqual(((MerchantRecipe) recipe).getItemToSell(), itemToSell))
							itemAlreadySold = true;
					}
				}
			}

			// Don't know how it can ever be empty here, but it's a failsafe.
			if (itemToSell.isEmpty()) return;

			ItemStack secondItemToBuy = tier == Tier.MASTER ? new ItemStack(WizardryItems.astral_diamond)
					: new ItemStack(WizardryItems.magic_crystal, tier.ordinal() * 3 + 1 + rand.nextInt(4));

			merchantrecipelist.add(new MerchantRecipe(this.getRandomPrice(tier), secondItemToBuy, itemToSell));
		}

		Collections.shuffle(merchantrecipelist);

		if (this.trades == null) {
			this.trades = new WildCardTradeList();
		}

		this.trades.addAll(merchantrecipelist);
	}

	// TODO: Switch all of this over to some kind of loot pool system?

	@SuppressWarnings("unchecked")
	private ItemStack getRandomPrice(int tier) {


		Map<Pair<ResourceLocation, Short>, Integer> map = Wizardry.settings.currencyItems;
		// This isn't that efficient but it's not called very often really so it doesn't matter
		Pair<ResourceLocation, Short> itemName = map.keySet().toArray(new Pair[0])[rand.nextInt(map.size())];
		Item item = Item.REGISTRY.getObject(itemName.getLeft());
		short meta = itemName.getRight();
		int value;

		if (item == null) {
			AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Invalid item in currency items");
			item = Items.EMERALD; // Fallback item
			value = 6;
		} else {
			value = map.get(itemName);
		}

		// ((tier.ordinal() + 1) * 16 + rand.nextInt(6)) gives a 'value' for the item being bought
		// This is then divided by the value of the currency item to give a price
		// The absolute maximum stack size that can result from this calculation (with value = 1) is 64.
		return new ItemStack(item, value);
	}

	private ItemStack getRandomItemOfTier(int tier) {

		int randomiser;

		// All enabled spells of the given tier
		List<Spell> spells = Spell.getSpells(new Spell.TierElementFilter(tier, null, SpellProperties.Context.TRADES));
		// All enabled spells of the given tier that match this wizard's element
		List<Spell> specialismSpells = Spell.getSpells(new Spell.TierElementFilter(tier, this.getElement(), SpellProperties.Context.TRADES));

		// Wizards don't sell scrolls
		spells.removeIf(s -> !s.isEnabled(SpellProperties.Context.BOOK));
		specialismSpells.removeIf(s -> !s.isEnabled(SpellProperties.Context.BOOK));

		// This code is sooooooo much neater with the new filter system!
		switch (tier) {

			case NOVICE:
				randomiser = rand.nextInt(5);
				if (randomiser < 4 && !spells.isEmpty()) {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0 && !specialismSpells.isEmpty()) {
						// This means it is more likely for spell books sold to be of the same element as the wizard if the
						// wizard has an element.
						return new ItemStack(WizardryItems.spell_book, 1,
								specialismSpells.get(rand.nextInt(specialismSpells.size())).metadata());
					} else {
						return new ItemStack(WizardryItems.spell_book, 1, spells.get(rand.nextInt(spells.size())).metadata());
					}
				} else {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0) {
						// This means it is more likely for wands sold to be of the same element as the wizard if the wizard
						// has an element.
						return new ItemStack(WizardryItems.getWand(tier, this.getElement()));
					} else {
						return new ItemStack(
								WizardryItems.getWand(tier, Element.values()[rand.nextInt(Element.values().length)]));
					}
				}

			case APPRENTICE:
				randomiser = rand.nextInt(Wizardry.settings.discoveryMode ? 12 : 10);
				if (randomiser < 5 && !spells.isEmpty()) {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0 && !specialismSpells.isEmpty()) {
						// This means it is more likely for spell books sold to be of the same element as the wizard if the
						// wizard has an element.
						return new ItemStack(WizardryItems.spell_book, 1,
								specialismSpells.get(rand.nextInt(specialismSpells.size())).metadata());
					} else {
						return new ItemStack(WizardryItems.spell_book, 1, spells.get(rand.nextInt(spells.size())).metadata());
					}
				} else if (randomiser < 6) {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0) {
						// This means it is more likely for wands sold to be of the same element as the wizard if the wizard
						// has an element.
						return new ItemStack(WizardryItems.getWand(tier, this.getElement()));
					} else {
						return new ItemStack(
								WizardryItems.getWand(tier, Element.values()[rand.nextInt(Element.values().length)]));
					}
				} else if (randomiser < 8) {
					return new ItemStack(WizardryItems.arcane_tome, 1, 1);
				} else if (randomiser < 10) {
					EntityEquipmentSlot slot = WizardryUtilities.ARMOUR_SLOTS[rand.nextInt(WizardryUtilities.ARMOUR_SLOTS.length)];
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0) {
						// This means it is more likely for armour sold to be of the same element as the wizard if the
						// wizard has an element.
						return new ItemStack(WizardryItems.getArmour(this.getElement(), slot));
					} else {
						return new ItemStack(
								WizardryItems.getArmour(Element.values()[rand.nextInt(Element.values().length)], slot));
					}
				} else {
					// Don't need to check for discovery mode here since it is done above
					return new ItemStack(WizardryItems.identification_scroll);
				}

			case ADVANCED:
				randomiser = rand.nextInt(12);
				if (randomiser < 5 && !spells.isEmpty()) {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0 && !specialismSpells.isEmpty()) {
						// This means it is more likely for spell books sold to be of the same element as the wizard if the
						// wizard has an element.
						return new ItemStack(WizardryItems.spell_book, 1,
								specialismSpells.get(rand.nextInt(specialismSpells.size())).metadata());
					} else {
						return new ItemStack(WizardryItems.spell_book, 1, spells.get(rand.nextInt(spells.size())).metadata());
					}
				} else if (randomiser < 6) {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0) {
						// This means it is more likely for wands sold to be of the same element as the wizard if the wizard
						// has an element.
						return new ItemStack(WizardryItems.getWand(tier, this.getElement()));
					} else {
						return new ItemStack(
								WizardryItems.getWand(tier, Element.values()[rand.nextInt(Element.values().length)]));
					}
				} else if (randomiser < 8) {
					return new ItemStack(WizardryItems.arcane_tome, 1, 2);
				} else {
					List<Item> upgrades = new ArrayList<Item>(WandHelper.getSpecialUpgrades());
					randomiser = rand.nextInt(upgrades.size());
					return new ItemStack(upgrades.get(randomiser));
				}

			case MASTER:
				// If a regular wizard rolls a master trade, it can only be a simple master wand or a tome of arcana
				randomiser = this.getElement() != Element.MAGIC ? rand.nextInt(8) : 5 + rand.nextInt(3);

				if (randomiser < 5 && this.getElement() != Element.MAGIC && !specialismSpells.isEmpty()) {
					// Master spells can only be sold by a specialist in that element.
					return new ItemStack(WizardryItems.spell_book, 1,
							specialismSpells.get(rand.nextInt(specialismSpells.size())).metadata());

				} else if (randomiser < 6) {
					if (this.getElement() != Element.MAGIC && rand.nextInt(4) > 0) {
						// Master elemental wands can only be sold by a specialist in that element.
						return new ItemStack(WizardryItems.getWand(tier, this.getElement()));
					} else {
						return new ItemStack(WizardryItems.master_wand);
					}
				} else {
					return new ItemStack(WizardryItems.arcane_tome, 1, 3);
				}
		}

		return new ItemStack(Blocks.STONE);
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
