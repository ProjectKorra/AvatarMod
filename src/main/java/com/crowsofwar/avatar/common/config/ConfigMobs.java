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
package com.crowsofwar.avatar.common.config;

import java.util.HashMap;
import java.util.Map;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

import net.minecraft.item.Item;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ConfigMobs {
	
	public static ConfigMobs MOBS_CONFIG = new ConfigMobs();
	
	private static final Map<String, Integer> DEFAULT_FOODS = new HashMap<>();
	static {
		// Wheat
		DEFAULT_FOODS.put("minecraft:bread", 5);
		DEFAULT_FOODS.put("minecraft:hay_block", 75);
		DEFAULT_FOODS.put("minecraft:wheat", 8);
		// Natural items
		DEFAULT_FOODS.put("minecraft:grass", 9);
		DEFAULT_FOODS.put("minecraft:leaves", 3);
		DEFAULT_FOODS.put("minecraft:leaves2", 3);
		// Misc
		DEFAULT_FOODS.put("minecraft:carrot", 6);
		DEFAULT_FOODS.put("minecraft:golden_carrot", 30);
		DEFAULT_FOODS.put("minecraft:potato", 6);
		DEFAULT_FOODS.put("minecraft:baked_potato", 5);
		DEFAULT_FOODS.put("minecraft:apple", 10);
		DEFAULT_FOODS.put("minecraft:beetroot", 8);
		DEFAULT_FOODS.put("minecraft:cake", 45);
		DEFAULT_FOODS.put("minecraft:sugar", 2);
	}
	
	@Load
	public int bisonBaseTameness = 5;
	
	@Load
	private Map<String, Integer> bisonFoods = DEFAULT_FOODS;
	public Map<Item, Integer> bisonFoodList;
	
	public static void load() {
		ConfigLoader.load(MOBS_CONFIG, "avatar/mobs.yml");
		MOBS_CONFIG.loadLists();
	}
	
	private void loadLists() {
		bisonFoodList = new HashMap<>();
		for (Map.Entry<String, Integer> entry : bisonFoods.entrySet()) {
			String name = entry.getKey();
			Item item = Item.getByNameOrId(name);
			if (item != null) {
				bisonFoodList.put(item, entry.getValue());
			} else {
				AvatarLog.warn(WarningType.CONFIGURATION, "Invalid bison food; item " + name + " not found");
			}
		}
	}
	
}
