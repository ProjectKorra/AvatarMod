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

import akka.japi.Pair;
import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.common.item.scroll.Scrolls.ScrollType;
import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author CrowsOfWar
 */
public class ConfigMobs {

    //TODO: Make mobs drop support multiple scrolls

    private static final Multimap<String, Integer> DEFAULT_FOODS = ArrayListMultimap.create();
    private static final Multimap<String, String> TRADE_ITEMS = ArrayListMultimap.create();
    private static final Multimap<String, String> GLIDER_TRADES = ArrayListMultimap.create();
    private static final Multimap<String, Integer> AIRBENDING_TRADE_ITEMS = ArrayListMultimap.create();
    private static final Multimap<String, Integer> FIREBENDING_TRADE_ITEMS = ArrayListMultimap.create();

    //Entity, type
    private static final Multimap<String, String> DEFAULT_SCROLL_TYPE = ArrayListMultimap.create();

    //Entity, drop chance.
    private static final Multimap<String, Double> DEFAULT_SCROLL_DROPS = ArrayListMultimap.create();

    public static ConfigMobs MOBS_CONFIG = new ConfigMobs();

    static {
        TRADE_ITEMS.put("minecraft:diamond 1", "all");
        TRADE_ITEMS.put("minecraft:gold_ingot 1", "all");
        TRADE_ITEMS.put("minecraft:emerald 1", "all");
        //Required items for trading for a airbending scroll
        TRADE_ITEMS.put("minecraft:elytra 4", "air");
        TRADE_ITEMS.put("minecraft:dragon_breath 5", "air");
        TRADE_ITEMS.put("minecraft:totem_of_undying 3", "air");
        //Required items for trading for a firebending scroll
        TRADE_ITEMS.put("minecraft:magma_cream 2", "fire");
        TRADE_ITEMS.put("minecraft:blaze_rod 1", "fire");

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

        //Water.
        DEFAULT_SCROLL_DROPS.put("polarbear", 10.0);
        DEFAULT_SCROLL_TYPE.put("polarbear", "water");
        DEFAULT_SCROLL_TYPE.put("squid", "water");
        DEFAULT_SCROLL_DROPS.put("guardian", 15.0);
        DEFAULT_SCROLL_TYPE.put("guardian", "water");
        DEFAULT_SCROLL_DROPS.put("elderguardian", 40.0);
        DEFAULT_SCROLL_TYPE.put("elderguardian", "water");

        //Fire
        //Some mob names are weird, don't change them.
        DEFAULT_SCROLL_DROPS.put("pigzombie", 20.0);
        DEFAULT_SCROLL_TYPE.put("pigzombie", "fire");
        DEFAULT_SCROLL_DROPS.put("lavaslime", 15.0);
        DEFAULT_SCROLL_TYPE.put("lavaslime", "fire");
        DEFAULT_SCROLL_DROPS.put("witherskeleton", 25.0);
        DEFAULT_SCROLL_TYPE.put("witherskeleton", "fire");
        DEFAULT_SCROLL_DROPS.put("ghast", 40.0);
        DEFAULT_SCROLL_TYPE.put("ghast", "fire");
        DEFAULT_SCROLL_DROPS.put("blaze", 30.0);
        DEFAULT_SCROLL_TYPE.put("blaze", "fire");

        //Air
        DEFAULT_SCROLL_DROPS.put("bat", 5.0);
        DEFAULT_SCROLL_TYPE.put("bat", "air");
        DEFAULT_SCROLL_DROPS.put("parrot", 5.0);
        DEFAULT_SCROLL_TYPE.put("parrot", "air");
        DEFAULT_SCROLL_DROPS.put("chicken", 7.5);
        DEFAULT_SCROLL_TYPE.put("chicken", "air");
        DEFAULT_SCROLL_DROPS.put("sheep", 5.0);
        DEFAULT_SCROLL_TYPE.put("sheep", "air");
        DEFAULT_SCROLL_DROPS.put("shulker", 50.0);
        DEFAULT_SCROLL_TYPE.put("shulker", "air");


        //Earth
        DEFAULT_SCROLL_DROPS.put("mooshroom", 5.0);
        DEFAULT_SCROLL_TYPE.put("mooshroom", "earth");
        DEFAULT_SCROLL_DROPS.put("cavespider", 10.0);
        DEFAULT_SCROLL_TYPE.put("cavespider", "earth");
        DEFAULT_SCROLL_DROPS.put("silverfish", 12.5);
        DEFAULT_SCROLL_TYPE.put("silverfish", "earth");
        DEFAULT_SCROLL_DROPS.put("spider", 5.0);
        DEFAULT_SCROLL_TYPE.put("spider", "earth");
        DEFAULT_SCROLL_DROPS.put("skeleton", 5.0);
        DEFAULT_SCROLL_TYPE.put("skeleton", "earth");
        DEFAULT_SCROLL_DROPS.put("zombie", 5.0);
        DEFAULT_SCROLL_TYPE.put("zombie", "earth");

        //Lightning
        DEFAULT_SCROLL_DROPS.put("creeper", 1.0);
        DEFAULT_SCROLL_TYPE.put("creeper", "lightning");

        //Combustion
        DEFAULT_SCROLL_DROPS.put("creeper", 2.5);
        DEFAULT_SCROLL_TYPE.put("creeper", "combustion");

        //Sand
        DEFAULT_SCROLL_DROPS.put("husk", 10.0);
        DEFAULT_SCROLL_TYPE.put("husk", "sand");

        //Ice
        DEFAULT_SCROLL_DROPS.put("polarbear", 10.0);
        DEFAULT_SCROLL_TYPE.put("polarbear", "ice");
        DEFAULT_SCROLL_DROPS.put("stray", 10.0);
        DEFAULT_SCROLL_TYPE.put("stray", "ice");

        //All
        DEFAULT_SCROLL_DROPS.put("witch", 10.0);
        DEFAULT_SCROLL_DROPS.put("enderman", 12.5);
        DEFAULT_SCROLL_DROPS.put("creeper", 5.0);
    }

    @Load
    public BenderSettings benderSettings = new BenderSettings();
    @Load
    public ScrollSettings scrollSettings = new ScrollSettings();
    @Load
    public BisonSettings bisonSettings = new BisonSettings();
    @Load
    private Multimap<String, Integer> bisonFoods;
    private Multimap<Item, Integer> bisonFoodList;
    @Load
    private Multimap<String, Double> scrollDropChance;
    @Load
    private Multimap<String, String> scrollType;
    @Load
    private Multimap<String, String> scrollTradeItems;
    private Multimap<Item, Pair<Integer, String>> tradeItems;

    public static void load() {
        MOBS_CONFIG.scrollTradeItems = TRADE_ITEMS;
        MOBS_CONFIG.bisonFoods = DEFAULT_FOODS;
        MOBS_CONFIG.scrollType = DEFAULT_SCROLL_TYPE;
        MOBS_CONFIG.scrollDropChance = DEFAULT_SCROLL_DROPS;
        ConfigLoader.load(MOBS_CONFIG, "avatar/mobs.yml");
        MOBS_CONFIG.loadLists();

    }

    public void loadLists() {
        bisonFoodList = ArrayListMultimap.create();
        for (Map.Entry<String, Integer> entry : bisonFoods.entries()) {
            String name = entry.getKey();
            Item item = Item.getByNameOrId(name);
            if (item != null) {
                bisonFoodList.put(item, entry.getValue());
            } else {
                AvatarLog.warn(WarningType.CONFIGURATION, "Invalid bison food; item " + name + " not found");
            }
        }
        tradeItems = ArrayListMultimap.create();
        for (Map.Entry<String, String> entry : scrollTradeItems.entries()) {
            String name = entry.getKey();
            String tierS = name.split(" ")[1];
            name = name.split(" ")[0];
            int tier = 1;
            try {
                tier = Integer.parseInt(tierS);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                AvatarLog.warn(WarningType.CONFIGURATION, "Please enter a number for the tier next to the item name.");
            }
            Item item = Item.getByNameOrId(name);
            if (item != null) {
                tradeItems.put(item, new Pair<>(tier, entry.getValue()));
            } else {
                AvatarLog.warn(WarningType.CONFIGURATION, "Invalid trade item; item " + name + " not found");
            }
        }
    }

    public int getDomesticationValue(Item item) {
        return (int) bisonFoodList.get(item).toArray()[0];
    }

    public List<Item> getTradeItems() {
        List<Item> items = new ArrayList<>();
        for (Map.Entry<Item, Pair<Integer, String>> entry : tradeItems.entries()) {
            items.add(entry.getKey());
        }
        return items;
    }

    public boolean isTradeItem(Item item) {
        return tradeItems.containsKey(item);
    }

    public String getTradeItemElement(Item item) {
        return new ArrayList<>(tradeItems.get(item)).get(0).second();
    }

    public int getTradeItemTier(Item item) {
        return new ArrayList<>(tradeItems.get(item)).get(0).first();
    }

    public boolean isAirTradeItem(Item item) {
        return new ArrayList<>(tradeItems.get(item)).get(0).second().equals("air");
    }

    public boolean isFireTradeItem(Item item) {
        return new ArrayList<>(tradeItems.get(item)).get(0).second().equals("fire");
    }

    public boolean isBisonFood(Item item) {
        return bisonFoodList.containsKey(item);
    }

    /**
     * Gets the scroll type for that entity to drop. By default, is
     * ScrollType.ALL.
     */
    public ScrollType getScrollType(Entity entity) {

        String entityName = EntityList.getEntityString(entity);
        if (entityName != null) {
            String key = entityName.toLowerCase();
            String typeName = new ArrayList<>(scrollType.get(key)).get(0);

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
        }

        return ScrollType.ALL;
    }


    /**
     * Get the default scroll drop chance for that entity in percentage (0-100)
     */
    public double getScrollDropChance(Entity entity) {
        String entityName = EntityList.getEntityString(entity);
        if (entityName != null) {
            String key = entityName.toLowerCase();
            return scrollDropChance.get(key) != null ? new ArrayList<>(scrollDropChance.get(key)).get(0) : 0;
        }
        return 0;
    }


    public static class BenderSettings {
        @Load
        public final int maxNumberOfBenders = 3;

        @Load
        public final int maxLevel = 7;
    }

    public static class ScrollSettings {
        //HOw much percent it takes to increase the tier.
        @Load
        public final double percentPerTier = 10.0;

        //How much percent it takes to increase the amount of scrolls dropped.
        @Load
        public final double percentPerNumber = 10.0;

        //How much the next chance to drop a tier above decreases by.
        @Load
        public final double tierChanceDecreaseMult = (1 / 2F);

        //How much the chance to drop more scrolls decreases by.
        @Load
        public final double numberChanceDecreaseMult = (2 / 3F);

        //Randomises drop amount and tier.
        @Load
        public final boolean chaos = false;

        //Randomises everything.
        @Load
        public final boolean absoluteChaos = false;
    }

    public static class BisonSettings {
        @Load
        public int bisonMinDomestication = 500, bisonMaxDomestication = 800;

        @Load
        public int bisonRiderTameness = 800, bisonOwnableTameness = 900, bisonLeashTameness = 1000,
                bisonChestTameness = 1000;

        @Load
        public int bisonGrassFoodBonus = 5, bisonRideOneSecondTameness = 3;

        @Load
        public float bisonBreedMinMinutes = 60, bisonBreedMaxMinutes = 120;
    }

}
