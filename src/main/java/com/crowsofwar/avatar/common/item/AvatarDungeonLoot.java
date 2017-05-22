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

import static net.minecraft.world.storage.loot.LootTableList.ENTITIES_BAT;
import static net.minecraft.world.storage.loot.LootTableList.ENTITIES_CHICKEN;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
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
		if (isLootTable(e, ENTITIES_BAT, ENTITIES_CHICKEN)) {
			
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
	
	private void addLoot(LootTableLoadEvent e, LootItem... items) {
		LootPool pool = new LootPool(new LootEntry[0], new LootCondition[0], new RandomValueRange(1, 1),
				new RandomValueRange(1, 1), "custom_avatar_loot_pools");
		
		for (int i = 0; i < items.length; i++) {
			LootItem item = items[i];
			
			LootCondition[] conditions = new LootCondition[0];
			LootFunction stackSize = new SetCount(conditions,
					new RandomValueRange(item.minStack, item.maxStack));
			LootFunction metadata = new SetMetadata(conditions, new RandomValueRange(item.metadata));
			
			pool.addEntry(new LootEntryItem(Items.BLAZE_ROD, 99999, 100,
					new LootFunction[] { stackSize, metadata }, conditions, "custom_" + i));
			
		}
		
		e.getTable().addPool(pool);
	}
	
	private static class LootItem {
		
		private final Item item;
		private final int minStack, maxStack;
		private final int metadata;
		
		public LootItem(Item item, int minStack, int maxStack, int metadata) {
			this.item = item;
			this.minStack = minStack;
			this.maxStack = maxStack;
			this.metadata = metadata;
		}
		
	}
	
}
