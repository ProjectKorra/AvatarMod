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
import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Random;

/**
 * @author CrowsOfWar
 */
public class EntityFirebender extends EntityHumanBender {

	public static final ResourceLocation LOOT_TABLE = LootTableList
			.register(new ResourceLocation("avatarmod", "firebender"));
	private Random rand = new Random();
	private int level = rand.nextInt(1) + 3;

	/**
	 * @param world
	 */
	public EntityFirebender(World world) {
		super(world);

		getData().getAbilityData("fireball").setLevel(level);
		getData().getAbilityData("flamethrower").setLevel(level);

	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50);
	}

	@Override
	protected void addBendingTasks() {
		this.tasks.addTask(1, Abilities.getAi("flamethrower", this, getBender()));
		this.tasks.addTask(1, Abilities.getAi("fireball", this, getBender()));
		this.tasks.addTask(2, Abilities.getAi("fire_arc", this, getBender()));
		//this.tasks.addTask(3, Abilities.getAi("inferno_punch", this, getBender()));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.2, true));
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}

	@Override
	protected ScrollType getScrollType() {
		return ScrollType.FIRE;
	}

	@Override
	protected int getNumSkins() {
		return 1;
	}

}
