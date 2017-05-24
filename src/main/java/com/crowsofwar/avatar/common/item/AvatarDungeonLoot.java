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
package com.crowsofwar.avatar.common.item;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static net.minecraft.world.storage.loot.LootTableList.*;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryEmpty;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarDungeonLoot {
	
	private AvatarDungeonLoot() {}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new AvatarDungeonLoot());
	}
	
	@SubscribeEvent
	public void onLootLoad(LootTableLoadEvent e) {
		
		if (!STATS_CONFIG.addDungeonLoot) {
			return;
		}
		
		if (isLootTable(e, CHESTS_NETHER_BRIDGE, CHESTS_END_CITY_TREASURE, CHESTS_STRONGHOLD_CORRIDOR)) {
			addLoot(e, 50, //
					new LootItem(AvatarItems.itemBisonWhistle, 20),
					new LootItem(AvatarItems.itemBisonSaddle, 20, 1),
					new LootItem(AvatarItems.itemBisonSaddle, 10, 0),
					new LootItem(AvatarItems.itemBisonSaddle, 10, 2),
					new LootItem(AvatarItems.itemBisonSaddle, 5, 3),
					new LootItem(AvatarItems.itemWaterPouch, 30));
			addLoot(e, 120, //
					new LootItem(AvatarItems.itemScroll, 20, 0), //
					new LootItem(AvatarItems.itemScroll, 5, 1), //
					new LootItem(AvatarItems.itemScroll, 5, 2), //
					new LootItem(AvatarItems.itemScroll, 5, 3), //
					new LootItem(AvatarItems.itemScroll, 5, 4));
		}
		
		if (isLootTable(e, CHESTS_STRONGHOLD_LIBRARY, CHESTS_ABANDONED_MINESHAFT, CHESTS_SIMPLE_DUNGEON)) {
			addLoot(e, 50, //
					new LootItem(AvatarItems.itemBisonArmor, 20, 1),
					new LootItem(AvatarItems.itemBisonArmor, 10, 0),
					new LootItem(AvatarItems.itemBisonArmor, 10, 1),
					new LootItem(AvatarItems.itemBisonArmor, 5, 1),
					new LootItem(AvatarItems.itemWaterPouch, 30));
			addLoot(e, 100, //
					new LootItem(AvatarItems.itemScroll, 20, 0), //
					new LootItem(AvatarItems.itemScroll, 5, 1), //
					new LootItem(AvatarItems.itemScroll, 5, 2), //
					new LootItem(AvatarItems.itemScroll, 5, 3), //
					new LootItem(AvatarItems.itemScroll, 5, 4));
		}
		
		if (isLootTable(e, CHESTS_VILLAGE_BLACKSMITH, CHESTS_IGLOO_CHEST, CHESTS_DESERT_PYRAMID,
				CHESTS_JUNGLE_TEMPLE)) {
			addLoot(e, 20, //
					new LootItem(AvatarItems.itemScroll, 20, 0), //
					new LootItem(AvatarItems.itemScroll, 5, 1), //
					new LootItem(AvatarItems.itemScroll, 5, 2), //
					new LootItem(AvatarItems.itemScroll, 5, 3), //
					new LootItem(AvatarItems.itemScroll, 5, 4));
		}
		
		if (isLootTable(e, CHESTS_SPAWN_BONUS_CHEST)) {
			addLoot(e, 0, new LootItem(AvatarItems.itemScroll, 1, 0));
			addLoot(e, 0, new LootItem(AvatarItems.itemScroll, 1, 0));
			addLoot(e, 0, new LootItem(AvatarItems.itemScroll, 1, 0));
		}
		
	}
	
	private boolean isLootTable(LootTableLoadEvent e, ResourceLocation... names) {
		for (ResourceLocation name : names) {
			if (e.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	private void addLoot(LootTableLoadEvent e, int emptyWeight, LootItem... items) {
		LootPool pool = new LootPool(new LootEntry[0], new LootCondition[0], new RandomValueRange(1, 1),
				new RandomValueRange(1, 1), "custom_avatar_loot_pools");
		
		pool.addEntry(new LootEntryEmpty(emptyWeight, 1, new LootCondition[0], "empty"));
		
		for (int i = 0; i < items.length; i++) {
			LootItem item = items[i];
			
			LootCondition[] conditions = new LootCondition[0];
			LootFunction stackSize = new SetCount(conditions,
					new RandomValueRange(item.minStack, item.maxStack));
			LootFunction metadata = new SetMetadata(conditions, new RandomValueRange(item.metadata));
			
			pool.addEntry(new LootEntryItem(item.item, item.weight, 1,
					new LootFunction[] { stackSize, metadata }, conditions, "custom_" + i));
			
		}
		
		e.getTable().addPool(pool);
	}
	
	private static class LootItem {
		
		private final Item item;
		private final int weight;
		private final int minStack, maxStack;
		private final int metadata;
		
		public LootItem(Item item, int weight) {
			this(item, weight, 0);
		}
		
		public LootItem(Item item, int weight, int metadata) {
			this(item, weight, metadata, 1, 1);
		}
		
		public LootItem(Item item, int weight, int metadata, int minStack, int maxStack) {
			this.item = item;
			this.weight = weight;
			this.metadata = metadata;
			this.minStack = minStack;
			this.maxStack = maxStack;
		}
		
	}
	
}
