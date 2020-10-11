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
package com.crowsofwar.avatar.config;

import akka.japi.Pair;
import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.item.scroll.Scrolls.ScrollType;
import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.Item;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author CrowsOfWar
 */
public class ConfigMobs {

    //TODO: Make mobs drop support multiple scrolls

    private static final Map<String, Integer> DEFAULT_FOODS = new HashMap<>();
    private static final Multimap<String, String> TRADE_ITEMS = ArrayListMultimap.create();
    private static final Multimap<String, String> GLIDER_TRADES = ArrayListMultimap.create();
    private static final Multimap<String, Integer> AIRBENDING_TRADE_ITEMS = ArrayListMultimap.create();
    private static final Multimap<String, Integer> FIREBENDING_TRADE_ITEMS = ArrayListMultimap.create();

//    //Entity, type
//    private static final Multimap<String, String> DEFAULT_SCROLL_TYPE = ArrayListMultimap.create();

    //Entity, mob drop info
    private static final Map<String, MobDrops> DEFAULT_SCROLL_DROPS = new HashMap<>();

    public static ConfigMobs MOBS_CONFIG = new ConfigMobs();

    static {
        TRADE_ITEMS.put("minecraft:diamond 1", "all");
        TRADE_ITEMS.put("minecraft:gold_ingot 1", "all");
        TRADE_ITEMS.put("minecraft:emerald 2", "all");
        //Required items for trading for a airbending scroll
        TRADE_ITEMS.put("minecraft:elytra 5", "air");
        TRADE_ITEMS.put("minecraft:dragon_breath 6", "air");
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
//        DEFAULT_SCROLL_DROPS.put("polarbear", 10.0);
//        DEFAULT_SCROLL_TYPE.put("polarbear", "water");
//        DEFAULT_SCROLL_TYPE.put("squid", "water");
//        DEFAULT_SCROLL_DROPS.put("guardian", 15.0);
//        DEFAULT_SCROLL_TYPE.put("guardian", "water");
//        DEFAULT_SCROLL_DROPS.put("elderguardian", 40.0);
//        DEFAULT_SCROLL_TYPE.put("elderguardian", "water");

        //Fire
        //Some mob names are weird, don't change them.
        //Also, the classes here are just for example. I set them later.
        DEFAULT_SCROLL_DROPS.put("pigzombie", new MobDrops(EntityPigZombie.class,
                new DropInfo(ScrollType.FIRE, 1, 20, 2),
                new DropInfo(ScrollType.FIRE, 2, 10),
                new DropInfo(ScrollType.FIRE, 3, 5),
                new DropInfo(ScrollType.FIRE, 4, 2.5)));
        DEFAULT_SCROLL_DROPS.put("lavaslime", new MobDrops(EntityMagmaCube.class,
                new DropInfo(ScrollType.FIRE, 1, 25, 3),
                new DropInfo(ScrollType.FIRE, 2, 15, 2),
                new DropInfo(ScrollType.FIRE, 3, 2.5)));
        DEFAULT_SCROLL_DROPS.put("witherskeleton", new MobDrops(EntityWitherSkeleton.class,
                new DropInfo(ScrollType.FIRE, 1, 30, 3),
                new DropInfo(ScrollType.FIRE, 2, 15),
                new DropInfo(ScrollType.FIRE, 3, 10),
                new DropInfo(ScrollType.FIRE, 4, 5),
                new DropInfo(ScrollType.FIRE, 5, 1)));
        DEFAULT_SCROLL_DROPS.put("ghast", new MobDrops(EntityGhast.class,
                new DropInfo(ScrollType.FIRE, 1, 40),
                new DropInfo(ScrollType.FIRE, 2, 20)));
        DEFAULT_SCROLL_DROPS.put("blaze", new MobDrops(EntityBlaze.class,
                new DropInfo(ScrollType.FIRE, 1, 20, 3),
                new DropInfo(ScrollType.FIRE, 2, 10, 2),
                new DropInfo(ScrollType.FIRE, 3, 5, 2),
                new DropInfo(ScrollType.FIRE, 4, 2.5, 2),
                new DropInfo(ScrollType.FIRE, 5, 0.5)));

        //Air
        DEFAULT_SCROLL_DROPS.put("bat", new MobDrops(EntityBat.class,
                new DropInfo(ScrollType.AIR, 1, 5),
                new DropInfo(ScrollType.AIR, 2, 1)));
        DEFAULT_SCROLL_DROPS.put("parrot", new MobDrops(EntityParrot.class,
                new DropInfo(ScrollType.AIR, 1, 5)));
        DEFAULT_SCROLL_DROPS.put("chicken", new MobDrops(EntityChicken.class,
                new DropInfo(ScrollType.AIR, 1, 7.5)));
        DEFAULT_SCROLL_DROPS.put("sheep", new MobDrops(EntitySheep.class,
                new DropInfo(ScrollType.AIR, 1, 5)));
        DEFAULT_SCROLL_DROPS.put("shulker", new MobDrops(EntityShulker.class,
                new DropInfo(ScrollType.AIR, 1, 40, 4),
                new DropInfo(ScrollType.AIR, 2, 20, 3),
                new DropInfo(ScrollType.AIR, 3, 10, 2),
                new DropInfo(ScrollType.AIR, 4, 5),
                new DropInfo(ScrollType.AIR, 5, 2.5),
                new DropInfo(ScrollType.AIR, 6, 1)));


        //Earth
        DEFAULT_SCROLL_DROPS.put("mooshroom", 5.0);
        DEFAULT_SCROLL_DROPS.put("cavespider", 10.0);
        DEFAULT_SCROLL_DROPS.put("silverfish", 12.5);
        DEFAULT_SCROLL_DROPS.put("spider", 5.0);
        DEFAULT_SCROLL_DROPS.put("skeleton", 5.0);
        DEFAULT_SCROLL_DROPS.put("zombie", 5.0);

        //Lightning
        DEFAULT_SCROLL_DROPS.put("creeper", 1.0);
        DEFAULT_SCROLL_TYPE.put("creeper", "lightning");

        //Combustion
        DEFAULT_SCROLL_DROPS.put("creeper", 2.5);
        DEFAULT_SCROLL_DROPS.put("blaze");
        DEFAULT_SCROLL_DROPS.put("ghast");

        //Sand
        DEFAULT_SCROLL_DROPS.put("husk", 10.0);
        //Ice
//        DEFAULT_SCROLL_DROPS.put("polarbear", 10.0);
//        DEFAULT_SCROLL_TYPE.put("polarbear", "ice");
//        DEFAULT_SCROLL_DROPS.put("stray", 10.0);
//        DEFAULT_SCROLL_TYPE.put("stray", "ice");

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
    private Map<String, Integer> bisonFoods;
    private Map<Item, Integer> bisonFoodList;
    @Load
    private Map<String, Collection<Double>> scrollDropChances;
    private Map<String, Collection<Integer>> scrollDropAmounts;
    private Map<String, Collections<Integer>> scrollDropTiers;
    //List for transferring/letting the player write
    private Map<String, Collection<String>> scrollTypes;
    //Actually storing the data
    private Map<Entity, MobDrops> scrollDrops;
    @Load
    private Map<String, String> scrollTradeItems;
    private Map<Item, Pair<Integer, String>> tradeItems;

    public static void load() {
        MOBS_CONFIG.scrollTradeItems = TRADE_ITEMS;
        MOBS_CONFIG.bisonFoods = DEFAULT_FOODS;
        loadPreLists();
        ConfigLoader.load(MOBS_CONFIG, "avatar/mobs.yml");
        MOBS_CONFIG.loadLists();

    }

    public static void loadPreLists() {
        List<MobDrops> info = new ArrayList<>(DEFAULT_SCROLL_DROPS.values());

        for (MobDrops drop : info) {
            //Scroll Types
            MOBS_CONFIG.scrollTypes.put(drop.getScrollTypeEntry());
            //Scroll Amounts
            MOBS_CONFIG.scrollDropAmounts.put(drop.getDropAmountEntry());
            //Scroll Tiers
            MOBS_CONFIG.scrollDropTiers.put(drop.getDropTierEntry());
            //Scroll Chances
            MOBS_CONFIG.scrollDropChances.put(drop.getDropChanceEntry());
        }


    }

    public void loadLists() {
       /* bisonFoodList = ArrayListMultimap.create();
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
        }**/
    }

    public int getDomesticationValue(Item item) {
        if (bisonFoodList.containsKey(item))
            return (int) bisonFoodList.get(item).toArray()[0];
        else return 0;
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
    public List<ScrollType> getScrollTypes(Entity entity) {

        String entityName = EntityList.getEntityString(entity);
        List<ScrollType> scrollTypes = new ArrayList<>();
        if (entityName != null) {
            String key = entityName.toLowerCase();
            List<String> types = (new ArrayList<>(scrollType.get(key)));
            for (String typeName : types) {
                if (typeName != null) {
                    ScrollType type = ScrollType.ALL;
                    for (ScrollType t : ScrollType.values()) {
                        if (t.name().toLowerCase().equals(typeName.toLowerCase())) {
                            type = t;
                            break;
                        }
                    }

                    scrollTypes.add(type);
                }
            }
        }

        return scrollTypes;
    }


    /**
     * Get the default scroll drop chance for that entity in percentage (0-100)
     */
    public double getScrollDropChance(Entity entity, int index) {
        String entityName = EntityList.getEntityString(entity);
        if (entityName != null) {
            String key = entityName.toLowerCase();
            return scrollDropChance.containsKey(key) ? new ArrayList<>(scrollDropChance.get(key)).get(index) : 0;
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
