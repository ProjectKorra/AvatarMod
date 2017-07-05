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
import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;
import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ConfigMobs {
	
	public static ConfigMobs MOBS_CONFIG = new ConfigMobs();
	
	private static final Map<String, Integer> DEFAULT_FOODS = new HashMap<>();
	private static final Map<String, Double> DEFAULT_SCROLL_DROP = new HashMap<>();
	private static final Map<String, String> DEFAULT_SCROLL_TYPE = new HashMap<>();
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
		
		DEFAULT_SCROLL_DROP.put("polar_bear", 5.0);
		DEFAULT_SCROLL_TYPE.put("polar_bear", "water");
		DEFAULT_SCROLL_DROP.put("squid", 1.0);
		DEFAULT_SCROLL_TYPE.put("squid", "water");
		DEFAULT_SCROLL_DROP.put("guardian", 8.0);
		DEFAULT_SCROLL_TYPE.put("guardian", "water");
		DEFAULT_SCROLL_DROP.put("elder_guardian", 15.0);
		DEFAULT_SCROLL_TYPE.put("elder_guardian", "water");
		
		DEFAULT_SCROLL_DROP.put("zombie_pigman", 3.0);
		DEFAULT_SCROLL_TYPE.put("zombie_pigman", "fire");
		DEFAULT_SCROLL_DROP.put("magma_cube", 8.0);
		DEFAULT_SCROLL_TYPE.put("magma_cube", "fire");
		DEFAULT_SCROLL_DROP.put("wither_skeleton", 8.0);
		DEFAULT_SCROLL_TYPE.put("wither_skeleton", "fire");
		DEFAULT_SCROLL_DROP.put("ghast", 30.0);
		DEFAULT_SCROLL_TYPE.put("ghast", "fire");
		DEFAULT_SCROLL_DROP.put("blaze", 5.0);
		DEFAULT_SCROLL_TYPE.put("blaze", "fire");
		
		DEFAULT_SCROLL_DROP.put("bat", 5.0);
		DEFAULT_SCROLL_TYPE.put("bat", "earth");
		DEFAULT_SCROLL_DROP.put("mooshroom", 3.0);
		DEFAULT_SCROLL_TYPE.put("mooshroom", "earth");
		DEFAULT_SCROLL_DROP.put("cave_spider", 3.0);
		DEFAULT_SCROLL_TYPE.put("cave_spider", "earth");
		DEFAULT_SCROLL_DROP.put("silverfish", 8.0);
		DEFAULT_SCROLL_TYPE.put("silverfish", "earth");
		
		DEFAULT_SCROLL_DROP.put("creeper", 3.0);
		DEFAULT_SCROLL_DROP.put("skeleton", 2.0);
		DEFAULT_SCROLL_DROP.put("zombie", 2.0);
		DEFAULT_SCROLL_DROP.put("spider", 2.0);
		DEFAULT_SCROLL_DROP.put("witch", 8.0);
		DEFAULT_SCROLL_DROP.put("husk", 4.0);
		DEFAULT_SCROLL_DROP.put("stray", 4.0);
		
	}
	
	@Load
	public int bisonMinDomestication = 500, bisonMaxDomestication = 800;
	
	@Load
	public int bisonRiderTameness = 800, bisonOwnableTameness = 900, bisonLeashTameness = 1000,
			bisonChestTameness = 1000;
	
	@Load
	public int bisonGrassFoodBonus = 5, bisonRideOneSecondTameness = 3;
	
	@Load
	public float bisonBreedMinMinutes = 60, bisonBreedMaxMinutes = 120;
	
	@Load
	private Map<String, Integer> bisonFoods;
	private Map<Item, Integer> bisonFoodList;
	
	@Load
	private Map<String, Double> scrollDropChance;
	@Load
	private Map<String, String> scrollType;
	
	public static void load() {
		MOBS_CONFIG.bisonFoods = DEFAULT_FOODS;
		MOBS_CONFIG.scrollDropChance = DEFAULT_SCROLL_DROP;
		MOBS_CONFIG.scrollType = DEFAULT_SCROLL_TYPE;
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
	
	public int getDomesticationValue(Item item) {
		return bisonFoodList.containsKey(item) ? bisonFoodList.get(item) : 0;
	}
	
	public boolean isBisonFood(Item item) {
		return bisonFoodList.containsKey(item);
	}
	
	/**
	 * Get the default scroll drop chance for that entity in percentage (0-100)
	 */
	public double getScrollDropChance(Entity entity) {
		String key = EntityList.getEntityString(entity);
		if (key != null) {
			key = key.toLowerCase();
			return scrollDropChance.get(key) != null ? scrollDropChance.get(key) : 0;
		}
		return 0;
	}
	
	/**
	 * Gets the scroll type for that entity to drop. By default, is
	 * ScrollType.ALL.
	 */
	public ScrollType getScrollType(Entity entity) {
		
		String key = EntityList.getEntityString(entity).toLowerCase();
		String typeName = scrollType.get(key);
		
		if (typeName != null) {
			
			ScrollType type = ScrollType.ALL;
			for (ScrollType t : ScrollType.values()) {
				if (t.name().toLowerCase().equals(typeName.toLowerCase())) {
					type = t;
					break;
				}
			}
			
			return type;
			
		}
		
		return ScrollType.ALL;
		
	}
	
}
