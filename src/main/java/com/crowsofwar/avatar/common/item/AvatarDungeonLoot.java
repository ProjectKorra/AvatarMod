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

import com.crowsofwar.avatar.AvatarInfo;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraft.world.storage.loot.functions.SetNBT;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static net.minecraft.world.storage.loot.LootTableList.*;

/**
 * @author CrowsOfWar
 */
@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarDungeonLoot {

	@SubscribeEvent
	public static void onLootLoad(LootTableLoadEvent e) {

		if (!STATS_CONFIG.addDungeonLoot) {
			return;
		}

		if (isLootTable(e, CHESTS_NETHER_BRIDGE, CHESTS_END_CITY_TREASURE, CHESTS_STRONGHOLD_CORRIDOR)) {
			addLoot(e, 50, //
					new LootItem(AvatarItems.itemBisonWhistle, 20),
					new LootItem(AvatarItems.itemBisonSaddle, 20).withMetadata(1),
					new LootItem(AvatarItems.itemBisonSaddle, 10).withMetadata(0),
					new LootItem(AvatarItems.itemBisonSaddle, 10).withMetadata(2),
					new LootItem(AvatarItems.itemBisonSaddle, 5).withMetadata(3),
					new LootItem(AvatarItems.itemWaterPouch, 30));
			addLoot(e, 120, //
					new LootItem(AvatarItems.itemScroll, 20), //
					new LootItem(AvatarItems.itemScroll, 5).withMetadata(1), //
					new LootItem(AvatarItems.itemScroll, 5).withMetadata(2), //
					new LootItem(AvatarItems.itemScroll, 5).withMetadata(3), //
					new LootItem(AvatarItems.itemScroll, 5).withMetadata(4));
			addLoot(e, 65, //
					new LootItem(AvatarItems.itemOstrichEquipment, 10).withMetadata(0),
					new LootItem(AvatarItems.itemOstrichEquipment, 10).withMetadata(1),
					new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(0));
		}

		if (isLootTable(e, CHESTS_STRONGHOLD_LIBRARY, CHESTS_ABANDONED_MINESHAFT, CHESTS_SIMPLE_DUNGEON)) {
			addLoot(e, 50, //
					new LootItem(AvatarItems.itemBisonArmor, 20).withMetadata(1),
					new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(0),
					new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(2),
					new LootItem(AvatarItems.itemBisonArmor, 5).withMetadata(3),
					new LootItem(AvatarItems.itemWaterPouch, 30));
			addLoot(e, 100, //
					new LootItem(AvatarItems.itemScroll, 20), //
					new LootItem(AvatarItems.itemScroll, 5).withMetadata(1), //
					new LootItem(AvatarItems.itemScroll, 5).withMetadata(2), //
					new LootItem(AvatarItems.itemScroll, 5).withMetadata(3), //
					new LootItem(AvatarItems.itemScroll, 5).withMetadata(4));
			addLoot(e, 65, //
					new LootItem(AvatarItems.itemOstrichEquipment, 10).withMetadata(0),
					new LootItem(AvatarItems.itemOstrichEquipment, 10).withMetadata(1),
					new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(0));
		}

		if (isLootTable(e, CHESTS_VILLAGE_BLACKSMITH, CHESTS_IGLOO_CHEST, CHESTS_DESERT_PYRAMID,
				CHESTS_JUNGLE_TEMPLE)) {
			addLoot(e, 20, //
					new LootItem(AvatarItems.itemScroll, 20), //
					new LootItem(AvatarItems.itemScroll, 5).withMetadata(1), //
					new LootItem(AvatarItems.itemScroll, 5).withMetadata(2), //
					new LootItem(AvatarItems.itemScroll, 5).withMetadata(3), //
					new LootItem(AvatarItems.itemScroll, 5).withMetadata(4));
			addLoot(e, 65, //
					new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(0),
					new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(1),
					new LootItem(AvatarItems.itemOstrichEquipment, 20).withMetadata(0));
		}

		if (isLootTable(e, CHESTS_SPAWN_BONUS_CHEST)) {
			addLoot(e, 0, new LootItem(AvatarItems.itemScroll, 1));
			addLoot(e, 0, new LootItem(AvatarItems.itemScroll, 1));
			addLoot(e, 0, new LootItem(AvatarItems.itemScroll, 1));
		}

		if (isLootTable(e, CHESTS_NETHER_BRIDGE)) {
			addLoot(e, 20, new LootItem(AvatarItems.itemScroll, 80).withMetadata(8)); // combustion
		}
		if (isLootTable(e, CHESTS_DESERT_PYRAMID)) {
			addLoot(e, 70, new LootItem(AvatarItems.itemScroll, 30).withMetadata(7)); // sand
		}
		if (isLootTable(e, CHESTS_IGLOO_CHEST)) {
			addLoot(e, 0, new LootItem(AvatarItems.itemScroll, 100).withMetadata(6)); // ice
			addLoot(e, 60, new LootItem(AvatarItems.itemScroll, 40).withMetadata(6));
		}
		if (isLootTable(e, CHESTS_STRONGHOLD_LIBRARY)) {
			addLoot(e, 0, new LootItem(AvatarItems.itemScroll, 100).withMetadata(5)); // lightning
		}
		if (isLootTable(e, CHESTS_WOODLAND_MANSION)) {
			addLoot(e, 0, new LootItem(AvatarItems.itemScroll, 100).withMetadata(1)); // Earth
		}

	}

	private static boolean isLootTable(LootTableLoadEvent e, ResourceLocation... names) {
		for (ResourceLocation name : names) {
			if (e.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	private static void addLoot(LootTableLoadEvent e, int emptyWeight, LootItem... items) {

		String lootPoolName = "custom_avatar_loot_pools";
		int j = 2;
		while (e.getTable().getPool(lootPoolName) != null) {
			lootPoolName = "custom_avatar_loot_pools_" + j;
			j++;
		}

		LootPool pool = new LootPool(new LootEntry[0], new LootCondition[0], new RandomValueRange(1, 1),
				new RandomValueRange(1, 1), lootPoolName);

		pool.addEntry(new LootEntryEmpty(emptyWeight, 1, new LootCondition[0], "empty"));

		for (int i = 0; i < items.length; i++) {
			LootItem item = items[i];

			LootCondition[] conditions = new LootCondition[0];
			LootFunction stackSize = new SetCount(conditions,
					new RandomValueRange(item.minStack, item.maxStack));
			LootFunction metadata = new SetMetadata(conditions, new RandomValueRange(item.metadata));
			LootFunction nbt = new SetNBT(conditions, item.nbt);

			pool.addEntry(new LootEntryItem(item.item, item.weight, 1,
					new LootFunction[]{stackSize, metadata, nbt}, conditions, "custom_" + i));

		}

		e.getTable().addPool(pool);
	}

	private static class LootItem {

		private final Item item;
		private final int weight;
		private int minStack, maxStack;
		private int metadata;
		private NBTTagCompound nbt;

		public LootItem(Item item, int weight) {
			this.item = item;
			this.weight = weight;
			this.metadata = 0;
			this.minStack = 1;
			this.maxStack = 1;
			this.nbt = new NBTTagCompound();
		}

		private LootItem withMetadata(int metadata) {
			this.metadata = metadata;
			return this;
		}

		private LootItem withStackSize(int min, int max) {
			this.minStack = min;
			this.maxStack = max;
			return this;
		}

		private LootItem withNbt(NBTTagCompound nbt) {
			this.nbt = nbt;
			return this;
		}

	}

}
