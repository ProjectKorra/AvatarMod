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
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BenderEntityComponent;
import com.crowsofwar.avatar.common.item.scroll.Scrolls.ScrollType;

import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;

/**
 * @author CrowsOfWar
 */
public class EntityWaterbender extends EntityHumanBender {

	public static final ResourceLocation LOOT_TABLE = LootTableList
			.register(new ResourceLocation("avatarmod", "waterbender"));

	public EntityWaterbender(World world) {
		super(world);
	}

	@Override
	protected Bender initBender() {
		return new WaterbenderBenderComponent();
	}

	@Override
	public void applyAbilityLevels(int level) {

	}

	@Override
	protected void addBendingTasks() {
		this.tasks.addTask(1, Abilities.get("wave").getAi(this, getBender()));
		// this.tasks.addTask(2, ABILITY_WATER_ARC.getAi(this, this));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 1, true));
	}


	@Override
	protected int getNumSkins() {
		return 1;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}


	private class WaterbenderBenderComponent extends BenderEntityComponent {

		private WaterbenderBenderComponent() {
			super(EntityWaterbender.this);
		}

		@Override
		public boolean consumeWaterLevel(int amount) {
			return true;
		}

	}

}
