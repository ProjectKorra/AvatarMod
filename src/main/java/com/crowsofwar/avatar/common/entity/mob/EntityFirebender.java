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

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityFirebender extends EntityHumanBender {
	
	public static final ResourceLocation LOOT_TABLE = LootTableList
			.register(new ResourceLocation("avatarmod", "firebender"));
	
	/**
	 * @param world
	 */
	public EntityFirebender(World world) {
		super(world);
		
		AbilityData flamethrowerData = new AbilityData(getData(), BendingAbility.ABILITY_FLAMETHROWER);
		flamethrowerData.setLevel(2);
		getData().setAbilityData(BendingAbility.ABILITY_FLAMETHROWER, flamethrowerData);
		
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50);
	}
	
	@Override
	protected void addBendingTasks() {
		this.tasks.addTask(1, BendingAbility.ABILITY_FLAMETHROWER.getAi(this, this));
		this.tasks.addTask(3, BendingAbility.ABILITY_FIREBALL.getAi(this, this));
		this.tasks.addTask(2, BendingAbility.ABILITY_FIRE_ARC.getAi(this, this));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 1, true));
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
