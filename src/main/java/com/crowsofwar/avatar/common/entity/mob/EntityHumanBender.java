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

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.*;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.*;

import com.crowsofwar.avatar.common.analytics.*;
import com.crowsofwar.avatar.common.entity.ai.*;
import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;
import com.crowsofwar.gorecore.format.FormattedMessage;

import javax.annotation.Nullable;

import static com.crowsofwar.avatar.common.AvatarChatMessages.*;
import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;

/**
 * @author CrowsOfWar
 */
public abstract class EntityHumanBender extends EntityBender {

	private static final DataParameter<Integer> SYNC_SKIN = EntityDataManager.createKey(EntityHumanBender.class, DataSerializers.VARINT);

	private EntityAiGiveScroll aiGiveScroll;
	private int scrollsLeft;
	private boolean hasAttemptedTrade;

	/**
	 * @param world
	 */
	public EntityHumanBender(World world) {
		super(world);
		scrollsLeft = getScrollsLeft();
		hasAttemptedTrade = false;

	}

	@Override
	public boolean getAlwaysRenderNameTagForRender() {
		return true;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_SKIN, (int) (rand.nextDouble() * getNumSkins()));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));

		tasks.addTask(4, new EntityAiBenderAttackZombie(this));
		targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
		tasks.addTask(4, aiGiveScroll = new EntityAiGiveScroll(this, getScrollType()));
		addBendingTasks();
		tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
		tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(8, new EntityAILookIdle(this));

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setSkin(nbt.getInteger("Skin"));
		scrollsLeft = nbt.getInteger("Scrolls");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("Skin", getSkin());
		nbt.setInteger("Scrolls", scrollsLeft);
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

	protected int getLevel() {
		return 1;
	}

	protected int getScrollsLeft() {
		return rand.nextInt(3) + 1;
	}

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
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
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
		float f = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int i = 0;

		if (entityIn instanceof EntityLivingBase) {
			f += EnchantmentHelper.getModifierForCreature(getHeldItemMainhand(), ((EntityLivingBase) entityIn).getCreatureAttribute());
			i += EnchantmentHelper.getKnockbackModifier(this);
		}

		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

		if (flag) {
			if (i > 0 && entityIn instanceof EntityLivingBase) {
				((EntityLivingBase) entityIn)
								.knockBack(this, i * 0.5F, MathHelper.sin(rotationYaw * 0.017453292F), (-MathHelper.cos(rotationYaw * 0.017453292F)));
				motionX *= 0.6D;
				motionZ *= 0.6D;
			}

			int j = EnchantmentHelper.getFireAspectModifier(this);

			if (j > 0) {
				entityIn.setFire(j * 4);
			}

			if (entityIn instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entityIn;
				ItemStack itemstack = getHeldItemMainhand();
				ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

				if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem() instanceof ItemAxe && itemstack1.getItem() == Items.SHIELD) {
					float f1 = 0.25F + EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

					if (rand.nextFloat() < f1) {
						entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
						world.setEntityState(entityplayer, (byte) 30);
					}
				}
			}

			applyEnchantments(this, entityIn);
		}

		return flag;
	}

	@Override
	protected abstract ResourceLocation getLootTable();

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		hasAttemptedTrade = false;

		ItemStack stack = player.getHeldItem(hand);
		/*int amount = stack.getCount();
		int tradeAmount = getTradeAmount(stack.getItem());**/

		if (isTradeItem(stack.getItem()) && !world.isRemote/* && amount >= tradeAmount**/) {

			if (scrollsLeft > 0) {
				if (aiGiveScroll.giveScrollTo(player)) {
					// Take item
					scrollsLeft--;
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

		} else if (!(isTradeItem(stack.getItem())) && !world.isRemote && !hasAttemptedTrade) {
			getTradeFailMessage().send(player);
			hasAttemptedTrade = true;
			return true;
		}

		return true;

	}
}
