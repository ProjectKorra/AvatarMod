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
import com.crowsofwar.avatar.common.item.scroll.Scrolls;
import com.crowsofwar.avatar.common.util.AvatarUtils;
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

        //Strongholds
        if (isLootTable(e, CHESTS_STRONGHOLD_LIBRARY, CHESTS_STRONGHOLD_CORRIDOR, CHESTS_STRONGHOLD_CROSSING)) {
            addLoot(e, 150,
                    new LootItem(Scrolls.ALL, 40 - AvatarUtils.getRandomNumberInRange(4, 6) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(4, 6)),
                    new LootItem(Scrolls.EARTH, 40 - AvatarUtils.getRandomNumberInRange(4, 6) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(4, 6)),
                    new LootItem(Scrolls.WATER, 35 - AvatarUtils.getRandomNumberInRange(4, 6) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(4, 6)),
                    new LootItem(Scrolls.AIR, 35 - AvatarUtils.getRandomNumberInRange(4, 6) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(4, 6)),
                    new LootItem(Scrolls.FIRE, 35 - AvatarUtils.getRandomNumberInRange(4, 6) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(4, 6)),
                    new LootItem(Scrolls.LIGHTNING, 20 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)),
                    new LootItem(Scrolls.COMBUSTION, 20 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)),
                    new LootItem(Scrolls.ICE, 20 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)),
                    new LootItem(Scrolls.SAND, 20 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)));
        }

        //Nether
        if (isLootTable(e, CHESTS_NETHER_BRIDGE)) {
            addLoot(e, 150,
                    new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(0),
                    new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(1),
                    new LootItem(AvatarItems.itemOstrichEquipment, 20).withMetadata(0),
                    new LootItem(AvatarItems.itemBisonArmor, 20).withMetadata(1),
                    new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(0),
                    new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(2),
                    new LootItem(AvatarItems.itemBisonArmor, 5).withMetadata(3),
                    new LootItem(AvatarItems.itemBisonWhistle, 25));
            addLoot(e, 120,
                    new LootItem(Scrolls.FIRE, 60 - AvatarUtils.getRandomNumberInRange(4, 6) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(4, 6)),
                    new LootItem(Scrolls.COMBUSTION, 40 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)));
        }

        //Village
        if (isLootTable(e, CHESTS_VILLAGE_BLACKSMITH)) {
            addLoot(e, 150,
                    new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(0),
                    new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(1),
                    new LootItem(AvatarItems.itemOstrichEquipment, 20).withMetadata(2),
                    new LootItem(AvatarItems.itemBisonArmor, 20).withMetadata(1),
                    new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(0),
                    new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(2),
                    new LootItem(AvatarItems.itemBisonArmor, 5).withMetadata(3),
                    new LootItem(AvatarItems.itemBisonWhistle, 20));
            addLoot(e, 120,
                    new LootItem(Scrolls.FIRE, 40 - AvatarUtils.getRandomNumberInRange(1, 4) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 4)),
                    new LootItem(Scrolls.AIR, 30 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)),
                    new LootItem(Scrolls.EARTH, 30 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)),
                    new LootItem(Scrolls.ALL, 30 - -AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)));

        }

        //End
        if (isLootTable(e, CHESTS_END_CITY_TREASURE)) {
            addLoot(e, 150,
                    new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(0),
                    new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(1),
                    new LootItem(AvatarItems.itemOstrichEquipment, 30).withMetadata(2),
                    new LootItem(AvatarItems.itemBisonArmor, 20).withMetadata(1),
                    new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(0),
                    new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(2),
                    new LootItem(AvatarItems.itemBisonArmor, 35).withMetadata(3));
            addLoot(e, 175,
                    new LootItem(Scrolls.ALL, 50 - AvatarUtils.getRandomNumberInRange(4, 6) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(4, 6)),
                    new LootItem(Scrolls.EARTH, 50 - AvatarUtils.getRandomNumberInRange(4, 6) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(4, 6)),
                    new LootItem(Scrolls.WATER, 45 - AvatarUtils.getRandomNumberInRange(4, 6) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(4, 6)),
                    new LootItem(Scrolls.AIR, 45 - AvatarUtils.getRandomNumberInRange(4, 6) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(4, 6)),
                    new LootItem(Scrolls.FIRE, 45 - AvatarUtils.getRandomNumberInRange(4, 6) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(4, 6)),
                    new LootItem(Scrolls.LIGHTNING, 30 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)),
                    new LootItem(Scrolls.COMBUSTION, 30 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)),
                    new LootItem(Scrolls.ICE, 30 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)),
                    new LootItem(Scrolls.SAND, 30 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)));
        }

        //Earth structures
        if (isLootTable(e, CHESTS_WOODLAND_MANSION, CHESTS_JUNGLE_TEMPLE)) {
            addLoot(e, 100,
                    new LootItem(Scrolls.EARTH, 60 - AvatarUtils.getRandomNumberInRange(3, 5) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(3, 5)),
                    new LootItem(Scrolls.ALL, 40 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)));
            addLoot(e, 110,
                    new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(0),
                    new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(1),
                    new LootItem(AvatarItems.itemOstrichEquipment, 20).withMetadata(0),
                    new LootItem(AvatarItems.itemBisonArmor, 20).withMetadata(1),
                    new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(0),
                    new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(2),
                    new LootItem(AvatarItems.itemBisonArmor, 5).withMetadata(3),
                    new LootItem(AvatarItems.itemBisonWhistle, 25));
        }

        //Dungeons / Mineshafts
        if (isLootTable(e, CHESTS_SIMPLE_DUNGEON, CHESTS_ABANDONED_MINESHAFT)) {
            addLoot(e, 120,
                    new LootItem(Scrolls.ALL, 40 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)),
                    new LootItem(Scrolls.EARTH, 40 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)),
                    new LootItem(Scrolls.WATER, 35 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)),
                    new LootItem(Scrolls.AIR, 35 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)),
                    new LootItem(Scrolls.FIRE, 35 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)));
        }

        //Desert
        if (isLootTable(e, CHESTS_DESERT_PYRAMID)) {
            addLoot(e, 120,
                    new LootItem(Scrolls.EARTH, 40 - AvatarUtils.getRandomNumberInRange(2, 4) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(2, 4)),
                    new LootItem(Scrolls.SAND, 50 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)));
        }

        //Tundra/Ice
        if (isLootTable(e, CHESTS_IGLOO_CHEST)) {
            addLoot(e, 120,
                    new LootItem(Scrolls.WATER, 60 - AvatarUtils.getRandomNumberInRange(2, 4) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(2, 4)),
                    new LootItem(Scrolls.ICE, 60 - AvatarUtils.getRandomNumberInRange(1, 3) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 3)));
        }

        //Bonus Chest/Starter Chest
        if (isLootTable(e, CHESTS_SPAWN_BONUS_CHEST)) {
            addLoot(e, 200,
                    new LootItem(Scrolls.ALL, 40 - AvatarUtils.getRandomNumberInRange(1, 2) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 2)),
                    new LootItem(Scrolls.EARTH, 40 - AvatarUtils.getRandomNumberInRange(1, 2) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 2)),
                    new LootItem(Scrolls.WATER, 40 - AvatarUtils.getRandomNumberInRange(1, 2) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 2)),
                    new LootItem(Scrolls.AIR, 40 - AvatarUtils.getRandomNumberInRange(1, 2) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 2)),
                    new LootItem(Scrolls.FIRE, 40 - AvatarUtils.getRandomNumberInRange(1, 2) * 5)
                            .withMetadata(AvatarUtils.getRandomNumberInRange(1, 2)));
        }

        if (isLootTable(e, CHESTS_NETHER_BRIDGE, CHESTS_END_CITY_TREASURE, CHESTS_STRONGHOLD_CORRIDOR)) {
            addLoot(e, 95, //
                    new LootItem(AvatarItems.itemBisonWhistle, 20),
                    new LootItem(AvatarItems.itemBisonSaddle, 20).withMetadata(1),
                    new LootItem(AvatarItems.itemBisonSaddle, 10).withMetadata(0),
                    new LootItem(AvatarItems.itemBisonSaddle, 10).withMetadata(2),
                    new LootItem(AvatarItems.itemBisonSaddle, 5).withMetadata(3),
                    new LootItem(AvatarItems.itemWaterPouch, 30));

            addLoot(e, 65, //
                    new LootItem(AvatarItems.itemOstrichEquipment, 10).withMetadata(0),
                    new LootItem(AvatarItems.itemOstrichEquipment, 10).withMetadata(1),
                    new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(0));
        }

        if (isLootTable(e, CHESTS_STRONGHOLD_LIBRARY, CHESTS_ABANDONED_MINESHAFT, CHESTS_SIMPLE_DUNGEON)) {
            addLoot(e, 80, //
                    new LootItem(AvatarItems.itemBisonArmor, 20).withMetadata(1),
                    new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(0),
                    new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(2),
                    new LootItem(AvatarItems.itemBisonArmor, 5).withMetadata(3),
                    new LootItem(AvatarItems.itemWaterPouch, 30));
            addLoot(e, 90, //
                    new LootItem(AvatarItems.itemOstrichEquipment, 10).withMetadata(0),
                    new LootItem(AvatarItems.itemOstrichEquipment, 10).withMetadata(1),
                    new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(0));
        }


        if (isLootTable(e, CHESTS_IGLOO_CHEST, CHESTS_DESERT_PYRAMID,
                CHESTS_JUNGLE_TEMPLE)) {
            addLoot(e, 60, //
                    new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(0),
                    new LootItem(AvatarItems.itemOstrichEquipment, 15).withMetadata(1),
                    new LootItem(AvatarItems.itemOstrichEquipment, 20).withMetadata(0),
                    new LootItem(AvatarItems.itemBisonArmor, 20).withMetadata(1),
                    new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(0),
                    new LootItem(AvatarItems.itemBisonArmor, 10).withMetadata(2),
                    new LootItem(AvatarItems.itemBisonArmor, 5).withMetadata(3));
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
