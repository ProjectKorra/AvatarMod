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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
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
			LootPool main = e.getTable().getPool("main");
			main.addEntry(new LootEntryItem(Items.BLAZE_ROD, 99999, 100, new LootFunction[0],
					new LootCondition[0], "custom_blaze_rod"));
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
	
}
