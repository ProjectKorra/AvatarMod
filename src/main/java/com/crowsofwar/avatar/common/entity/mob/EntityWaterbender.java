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

import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_WATER_ARC;
import static com.crowsofwar.avatar.common.bending.BendingAbility.ABILITY_WAVE;

import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityWaterbender extends EntityHumanBender {
	
	public static final ResourceLocation LOOT_TABLE = LootTableList
			.register(new ResourceLocation("avatarmod", "waterbender"));
	
	public EntityWaterbender(World world) {
		super(world);
	}
	
	@Override
	protected void addBendingTasks() {
		this.tasks.addTask(1, ABILITY_WAVE.getAi(this, this));
		this.tasks.addTask(2, ABILITY_WATER_ARC.getAi(this, this));
	}
	
	@Override
	protected ScrollType getScrollType() {
		return ScrollType.WATER;
	}
	
	@Override
	protected int getNumSkins() {
		return 1;
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return LOOT_TABLE;
	}
	
	@Override
	public boolean consumeWaterLevel(int amount) {
		return true;
	}
	
}
