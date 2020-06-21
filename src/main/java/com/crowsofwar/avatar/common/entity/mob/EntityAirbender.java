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
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.item.scroll.ItemScrollAir;
import com.crowsofwar.avatar.common.item.scroll.Scrolls;
import com.crowsofwar.avatar.common.item.scroll.Scrolls.ScrollType;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.format.FormattedMessage;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_NEED_AIR_TRADE_ITEM;
import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class EntityAirbender extends EntityHumanBender {

	private static final ResourceLocation LOOT_TABLE = LootTableList
			.register(new ResourceLocation("avatarmod", "airbender"));

	public EntityAirbender(World world) {
		super(world);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25 + getLevel() / 100F);

	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	protected FormattedMessage getTradeFailMessage() {
		return MSG_NEED_AIR_TRADE_ITEM;
	}

	@Override
	protected void addBendingTasks() {
		//Normal values: 2, 1, 2, 4, 3, 4
		//Going to change them for testing
		this.tasks.addTask(4, Objects.requireNonNull(Abilities.getAi("air_bubble", this, Bender.get(this))));
		this.tasks.addTask(4, Objects.requireNonNull(Abilities.getAi("air_gust", this, Bender.get(this))));
		this.tasks.addTask(4, Objects.requireNonNull(Abilities.getAi("airblade", this, Bender.get(this))));
		this.tasks.addTask(1, Objects.requireNonNull(Abilities.getAi("air_burst", this, Bender.get(this))));
		this.tasks.addTask(4, Objects.requireNonNull(Abilities.getAi("cloudburst", this, Bender.get(this))));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.4 + getLevel() / 20F, true));
	}


	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}

	@Override
	protected int getNumSkins() {
		return 7;
	}

	@Override
	public void applyAbilityLevels(int level) {

		boolean airGustPath = world.rand.nextBoolean();
		boolean cloudBurstPath = world.rand.nextBoolean();
		boolean airBubblePath = world.rand.nextBoolean();
		boolean airbladePath = world.rand.nextBoolean();
		if (level == 1) {
			getData().getAbilityData("air_bubble").setLevel(-1);
			getData().getAbilityData("air_gust").setLevel(0);
			getData().getAbilityData("airblade").setLevel(0);
			getData().getAbilityData("cloudburst").setLevel(-1);
			getData().getAbilityData("air_burst").setLevel(0);
		}
		if (level == 2) {
			getData().getAbilityData("air_bubble").setLevel(-1);
			getData().getAbilityData("air_gust").setLevel(1);
			getData().getAbilityData("airblade").setLevel(0);
			getData().getAbilityData("cloudburst").setLevel(0);
			getData().getAbilityData("air_burst").setLevel(-1);
		}
		if (level == 3) {
			getData().getAbilityData("air_bubble").setLevel(0);
			getData().getAbilityData("air_gust").setLevel(2);
			getData().getAbilityData("airblade").setLevel(1);
			getData().getAbilityData("cloudburst").setLevel(0);
			getData().getAbilityData("air_burst").setLevel(-1);
		}
		if (level == 4) {
			getData().getAbilityData("air_bubble").setLevel(0);
			getData().getAbilityData("air_gust").setLevel(2);
			getData().getAbilityData("airblade").setLevel(2);
			getData().getAbilityData("cloudburst").setLevel(1);
			getData().getAbilityData("air_burst").setLevel(0);
		}
		if (level == 5) {
			getData().getAbilityData("air_bubble").setLevel(1);
			getData().getAbilityData("air_gust").setPath(airGustPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("airblade").setPath(airbladePath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("cloudburst").setLevel(1);
			getData().getAbilityData("air_burst").setLevel(0);
		}
		if (level == 6) {
			getData().getAbilityData("air_bubble").setLevel(2);
			getData().getAbilityData("air_gust").setPath(airGustPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("airblade").setPath(airbladePath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("cloudburst").setPath(cloudBurstPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("air_burst").setLevel(1);
		}
		if (level == 7) {
			getData().getAbilityData("air_bubble").setPath(airBubblePath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("air_gust").setPath(airGustPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("airblade").setPath(airbladePath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("cloudburst").setPath(cloudBurstPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("air_burst").setLevel(2);
		}

	}


	@Override
	public void setDead() {
		ItemStack stack = new ItemStack(Scrolls.AIR, 1, getLevel());
		if (world.rand.nextBoolean() && !world.isRemote) {
			this.entityDropItem(stack, 1.0F);
		}
		super.setDead();
	}


	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		TextComponentString textcomponentstring = new TextComponentString("Level " + getLevel() + " " + ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getName()));
		textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
		textcomponentstring.getStyle().setInsertion(this.getCachedUniqueIdString());
		return textcomponentstring;
	}

}
