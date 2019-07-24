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

import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.air.Airbending;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.scroll.Scrolls.ScrollType;
import com.crowsofwar.gorecore.format.FormattedMessage;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_NEED_AIR_TRADE_ITEM;
import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class EntityAirbender extends EntityHumanBender {

	private static final ResourceLocation LOOT_TABLE = LootTableList
			.register(new ResourceLocation("avatarmod", "airbender"));
	private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(EntityAirbender.class, DataSerializers.VARINT);


	public EntityAirbender(World world) {
		super(world);

	}

	public int getLevel() {
		return dataManager.get(LEVEL);
	}

	public void setLevel(int level) {
		dataManager.set(LEVEL, level);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);

	}

	@Override
	protected void entityInit() {
		super.entityInit();
		getData().addBendingId(Airbending.ID);
		dataManager.register(LEVEL, 1);

	}

	@Override
	protected FormattedMessage getTradeFailMessage() {
		return MSG_NEED_AIR_TRADE_ITEM;
	}

	@Override
	protected void addBendingTasks() {
		this.tasks.addTask(4, Objects.requireNonNull(Abilities.getAi("air_bubble", this, Bender.get(this))));
		this.tasks.addTask(1, Objects.requireNonNull(Abilities.getAi("air_gust", this, Bender.get(this))));
		this.tasks.addTask(2, Objects.requireNonNull(Abilities.getAi("airblade", this, Bender.get(this))));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.4, true));
	}


	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}

	@Override
	protected ScrollType getScrollType() {
		return ScrollType.AIR;
	}

	@Override
	protected int getNumSkins() {
		return 7;
	}

	@Override
	protected boolean isTradeItem(Item item) {
		return super.isTradeItem(item) || MOBS_CONFIG.isAirTradeItem(item);
	}

	@Override
	protected int getTradeAmount(Item item) {
		return super.getTradeAmount(item) + MOBS_CONFIG.getAirTradeItemAmount(item);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

	}

	@Override
	public void applyAbilityLevels(int level) {
		if (level == 1) {
			getData().getAbilityData("air_bubble").setLevel(-1);
			getData().getAbilityData("air_gust").setLevel(0);
			getData().getAbilityData("airblade").setLevel(0);
		}
		if (level == 2) {
			getData().getAbilityData("air_bubble").setLevel(-1);
			getData().getAbilityData("air_gust").setLevel(1);
			getData().getAbilityData("airblade").setLevel(0);
		}
		if (level >= 3) {
			getData().getAbilityData("air_bubble").setLevel(0);
			getData().getAbilityData("air_gust").setLevel(2);
			getData().getAbilityData("airblade").setLevel(1);
			ItemStack staff = new ItemStack(AvatarItems.airbenderStaff, 1);
			this.setHeldItem(EnumHand.MAIN_HAND, staff);
		}
	}


	@Override
	public void setDead() {
		//if (!world.isRemote && level >= 3) {
		//	this.entityDropItem(new ItemStack(AvatarItems.airbenderStaff, 1), 0);
		//}
		super.setDead();
	}


	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		TextComponentString textcomponentstring = new TextComponentString("Level " + getLevel() + " " + ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getName()));
		textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
		textcomponentstring.getStyle().setInsertion(this.getCachedUniqueIdString());
		return textcomponentstring;
		//return super.getDisplayName();
	}

	@Override
	public void setElement() {
		super.setElement();
		getData().addBending(new Airbending());
	}
}
