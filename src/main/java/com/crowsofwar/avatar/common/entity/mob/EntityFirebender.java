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
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.item.scroll.Scrolls.ScrollType;
import com.crowsofwar.gorecore.format.FormattedMessage;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_NEED_FIRE_TRADE_ITEM;
import static com.crowsofwar.avatar.common.bending.StatusControl.*;
import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;

/**
 * @author CrowsOfWar
 */
public class EntityFirebender extends EntityHumanBender {

	int level = 1;

	private static final ResourceLocation LOOT_TABLE = LootTableList
			.register(new ResourceLocation("avatarmod", "firebender"));
	/**
	 * @param world
	 */
	public EntityFirebender(World world) {
		super(world);
		getData().addBendingId(Firebending.ID);
	}



	@Override
	protected void entityInit() {
		super.entityInit();
		boolean fireShotPath = world.rand.nextBoolean();
		boolean flamethrowerPath = world.rand.nextBoolean();
		boolean infernoPunchPath = world.rand.nextBoolean();
		boolean fireBlastPath = world.rand.nextBoolean();
		boolean fireballPath = world.rand.nextBoolean();
		if (getLevel() <= 1) {
			getData().getAbilityData("fireball").setLevel(-1);
			getData().getAbilityData("flamethrower").setLevel(-1);
			getData().getAbilityData("fire_blast").setLevel(0);
			getData().getAbilityData("fire_shot").setLevel(0);
			getData().getAbilityData("inferno_punch").setLevel(-1);
		}
		if (getLevel() == 2) {
			getData().getAbilityData("fireball").setLevel(-1);
			getData().getAbilityData("flamethrower").setLevel(-1);
			getData().getAbilityData("fire_blast").setLevel(0);
			getData().getAbilityData("fire_shot").setLevel(1);
			getData().getAbilityData("inferno_punch").setLevel(-1);
		}
		if (getLevel() == 3) {
			getData().getAbilityData("fireball").setLevel(-1);
			getData().getAbilityData("flamethrower").setLevel(0);
			getData().getAbilityData("fire_blast").setLevel(1);
			getData().getAbilityData("fire_shot").setLevel(1);
			getData().getAbilityData("inferno_punch").setLevel(-1);
		}
		if (getLevel() == 4) {
			getData().getAbilityData("fireball").setLevel(0);
			getData().getAbilityData("flamethrower").setLevel(1);
			getData().getAbilityData("fire_blast").setLevel(1);
			getData().getAbilityData("fire_shot").setLevel(2);
			getData().getAbilityData("inferno_punch").setLevel(-1);
		}
		if (getLevel() == 5) {
			getData().getAbilityData("fireball").setLevel(1);
			getData().getAbilityData("flamethrower").setLevel(2);
			getData().getAbilityData("fire_blast").setLevel(2);
			getData().getAbilityData("fire_shot").setPath(fireShotPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("inferno_punch").setLevel(0);
		}
		if (getLevel() == 6) {
			getData().getAbilityData("fireball").setLevel(2);
			getData().getAbilityData("flamethrower").setLevel(2);
			getData().getAbilityData("fire_blast").setPath(fireBlastPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("fire_shot").setPath(fireShotPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("inferno_punch").setLevel(1);
		}
		if (getLevel() == 7) {
			getData().getAbilityData("fireball").setPath(fireballPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("flamethrower").setPath(flamethrowerPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("fire_blast").setPath(fireBlastPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("fire_shot").setPath(fireShotPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			getData().getAbilityData("inferno_punch").setLevel(2);
		}
	}

	@Override
	public void applyAbilityLevels(int level) {

	}

	@Override
	protected FormattedMessage getTradeFailMessage() {
		return MSG_NEED_FIRE_TRADE_ITEM;
	}


	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2);
	}

	@Override
	protected void addBendingTasks() {
		this.tasks.addTask(3, Objects.requireNonNull(Abilities.getAi("flamethrower", this, getBender())));
		this.tasks.addTask(2, Objects.requireNonNull(Abilities.getAi("fireball", this, getBender())));
		this.tasks.addTask(1, Objects.requireNonNull(Abilities.getAi("fire_blast", this, getBender())));
		this.tasks.addTask(3, Objects.requireNonNull(Abilities.getAi("inferno_punch", this, getBender())));
		if (getData().hasStatusControl(INFERNO_PUNCH_MAIN) || getData().hasStatusControl(INFERNO_PUNCH_FIRST) || getData().hasStatusControl(INFERNO_PUNCH_SECOND)) {
			this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.35, true));
		}
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.3, true));
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		getData().addBendingId(Firebending.ID);
		/*if (level == 0 && !world.isRemote) {
			Random rand = new Random();
			int level = rand.nextInt(3) + 1;
			if (level < 2) {
				getData().getAbilityData("fireball").setLevel(-1);
				getData().getAbilityData("flamethrower").setLevel(0);
				getData().getAbilityData("fire_arc").setLevel(0);
				getData().getAbilityData("inferno_punch").setLevel(-1);
				this.level = 1;
				scrollsLeft = 1;
			}
			if (level == 2) {
				getData().getAbilityData("fireball").setLevel(0);
				getData().getAbilityData("flamethrower").setLevel(0);
				getData().getAbilityData("fire_arc").setLevel(1);
				getData().getAbilityData("inferno_punch").setLevel(-1);
				scrollsLeft = 2;
				this.level = 2;
			}
			if (level > 2) {
				getData().getAbilityData("fireball").setLevel(1);
				getData().getAbilityData("flamethrower").setLevel(1);
				getData().getAbilityData("fire_arc").setLevel(2);
				getData().getAbilityData("inferno_punch").setLevel(0);
				scrollsLeft = 3;
				this.level = 3;
			}
		}**/
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@Override
	protected ScrollType getScrollType() {
		return ScrollType.FIRE;
	}

	@Override
	protected int getNumSkins() {
		return 1;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		return super.processInteract(player, hand);
	}

	@Override
	protected boolean isTradeItem(Item item) {
		return super.isTradeItem(item) || MOBS_CONFIG.isFireTradeItem(item);
	}
}

