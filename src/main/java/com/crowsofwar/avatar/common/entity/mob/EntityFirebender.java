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
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.item.scroll.Scrolls;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.format.FormattedMessage;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_NEED_FIRE_TRADE_ITEM;

/**
 * @author CrowsOfWar
 */
public class EntityFirebender extends EntityHumanBender {

	private static final ResourceLocation LOOT_TABLE = LootTableList
			.register(new ResourceLocation("avatarmod", "firebender"));

	public EntityFirebender(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public void applyAbilityLevels(int level) {
		boolean fireShotPath = world.rand.nextBoolean();
		boolean flamethrowerPath = world.rand.nextBoolean();
		//	boolean infernoPunchPath = world.rand.nextBoolean();
		boolean fireBlastPath = world.rand.nextBoolean();
		boolean fireballPath = world.rand.nextBoolean();
		switch (level) {
			case 2:
				getData().getAbilityData("fireball").setLevel(-1);
				getData().getAbilityData("flamethrower").setLevel(0);
				getData().getAbilityData("flame_strike").setLevel(0);
				getData().getAbilityData("fire_shot").setLevel(1);
			//	getData().getAbilityData("inferno_punch").setLevel(-1);
				break;
			case 3:
				getData().getAbilityData("fireball").setLevel(-1);
				getData().getAbilityData("flamethrower").setLevel(0);
				getData().getAbilityData("flame_strike").setLevel(1);
				getData().getAbilityData("fire_shot").setLevel(1);
			//	getData().getAbilityData("inferno_punch").setLevel(-1);
				break;
			case 4:
				getData().getAbilityData("fireball").setLevel(0);
				getData().getAbilityData("flamethrower").setLevel(1);
				getData().getAbilityData("flame_strike").setLevel(1);
				getData().getAbilityData("fire_shot").setLevel(2);
			//	getData().getAbilityData("inferno_punch").setLevel(0);
				break;
			case 5:
				getData().getAbilityData("fireball").setLevel(1);
				getData().getAbilityData("flamethrower").setLevel(2);
				getData().getAbilityData("flame_strike").setLevel(2);
				getData().getAbilityData("fire_shot").setPath(fireShotPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			//	getData().getAbilityData("inferno_punch").setLevel(1);
				break;
			case 6:
				getData().getAbilityData("fireball").setLevel(2);
				getData().getAbilityData("flamethrower").setLevel(2);
				getData().getAbilityData("flame_strike").setPath(fireBlastPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
				getData().getAbilityData("fire_shot").setPath(fireShotPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			//	getData().getAbilityData("inferno_punch").setLevel(1);
				break;
			case 7:
				getData().getAbilityData("fireball").setPath(fireballPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
				getData().getAbilityData("flamethrower").setPath(flamethrowerPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
				getData().getAbilityData("flame_strike").setPath(fireBlastPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
				getData().getAbilityData("fire_shot").setPath(fireShotPath ? AbilityData.AbilityTreePath.FIRST : AbilityData.AbilityTreePath.SECOND);
			//	getData().getAbilityData("inferno_punch").setLevel(2);
				break;
			default:
				getData().getAbilityData("fireball").setLevel(-1);
				getData().getAbilityData("flamethrower").setLevel(-1);
				getData().getAbilityData("flame_strike").setLevel(0);
				getData().getAbilityData("fire_shot").setLevel(0);
			//	getData().getAbilityData("inferno_punch").setLevel(-1);
				break;
		}

	}

	@Override
	public BendingStyle getElement() {
		return new Firebending();
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
		this.tasks.addTask(2 , Objects.requireNonNull(Abilities.getAi("flamethrower", this, getBender())));
		this.tasks.addTask(2, Objects.requireNonNull(Abilities.getAi("fireball", this, getBender())));
		this.tasks.addTask(1, Objects.requireNonNull(Abilities.getAi("fire_shot", this, getBender())));
		//this.tasks.addTask(2, Objects.requireNonNull(Abilities.getAi("fire_blast", this, getBender())));
		this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.3, true));
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		getData().addBendingId(Firebending.ID);
		return super.onInitialSpawn(difficulty, livingdata);
	}


	@Override
	protected int getNumSkins() {
		return 1;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		return super.processInteract(player, hand);
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
	public World getWorld() {
		return null;
	}

	@Override
	public BlockPos getPos() {
		return null;
	}

	@Override
	public void setDead() {
		super.setDead();
		ItemStack stack = new ItemStack(Scrolls.FIRE, 1, getLevel());
		if (AvatarUtils.getRandomNumberInRange(1, 100) < 50 && !world.isRemote) {
			this.entityDropItem(stack, 1.0F);
		}
	}

	@Override
	protected void onDeathUpdate() {
		super.onDeathUpdate();
	}
}

