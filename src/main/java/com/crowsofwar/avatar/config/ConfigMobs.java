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
import com.crowsofwar.avatar.item.scroll.Scrolls.ScrollType;
import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;

import java.util.*;

/**
 * @author CrowsOfWar
 */
public class ConfigMobs {

    //TODO: Make mobs drop support multiple scrolls

    private static final Map<String, Integer> DEFAULT_FOODS = new HashMap<>();
    private static final Map<String, String> TRADE_ITEMS = new HashMap<>();
    private static final Map<String, String> GLIDER_TRADES = new HashMap<>();
    private static final Map<String, Integer> AIRBENDING_TRADE_ITEMS = new HashMap<>();
    private static final Map<String, Integer> FIREBENDING_TRADE_ITEMS = new HashMap<>();

//    //Entity, type
//    private static final Multimap<String, String> DEFAULT_SCROLL_TYPE = ArrayListMultimap.create();

    //Entity, mob drop info
    private static final Map<String, MobDrops> DEFAULT_SCROLL_DROPS = new HashMap<>();

    public static ConfigMobs MOBS_CONFIG = new ConfigMobs();

    static {
        TRADE_ITEMS.put("minecraft:diamond 2", "all");
        TRADE_ITEMS.put("minecraft:gold_ingot 1", "all");
        TRADE_ITEMS.put("minecraft:emerald 3", "all");
        //Required items for trading for a airbending scroll
        TRADE_ITEMS.put("minecraft:elytra 5", "air");
        TRADE_ITEMS.put("minecraft:dragon_breath 6", "air");
        TRADE_ITEMS.put("minecraft:totem_of_undying 4", "air");
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
        DEFAULT_SCROLL_DROPS.put("zombie_pigman", new MobDrops("zombie_pigman",
                new DropInfo(ScrollType.FIRE, 1, 20, 2),
                new DropInfo(ScrollType.FIRE, 2, 10),
                new DropInfo(ScrollType.FIRE, 3, 5),
                new DropInfo(ScrollType.FIRE, 4, 2.5)));
        DEFAULT_SCROLL_DROPS.put("magma_cube", new MobDrops("magma_cube",
                new DropInfo(ScrollType.FIRE, 1, 25, 3),
                new DropInfo(ScrollType.FIRE, 2, 15, 2),
                new DropInfo(ScrollType.FIRE, 3, 2.5)));
        DEFAULT_SCROLL_DROPS.put("wither_skeleton", new MobDrops("wither_skeleton",
                new DropInfo(ScrollType.FIRE, 1, 30, 3),
                new DropInfo(ScrollType.FIRE, 2, 15),
                new DropInfo(ScrollType.FIRE, 3, 10),
                new DropInfo(ScrollType.FIRE, 4, 5),
                new DropInfo(ScrollType.FIRE, 5, 1)));
        DEFAULT_SCROLL_DROPS.put("ghast", new MobDrops("ghast",
                new DropInfo(ScrollType.FIRE, 1, 40),
                new DropInfo(ScrollType.FIRE, 2, 20)));
        DEFAULT_SCROLL_DROPS.put("blaze", new MobDrops("blaze",
                new DropInfo(ScrollType.FIRE, 1, 20, 3),
                new DropInfo(ScrollType.FIRE, 2, 10, 2),
                new DropInfo(ScrollType.FIRE, 3, 5, 2),
                new DropInfo(ScrollType.FIRE, 4, 2.5, 2),
                new DropInfo(ScrollType.FIRE, 5, 0.5)));

        //Air
        DEFAULT_SCROLL_DROPS.put("bat", new MobDrops("bat",
                new DropInfo(ScrollType.AIR, 1, 5),
                new DropInfo(ScrollType.AIR, 2, 1)));
        DEFAULT_SCROLL_DROPS.put("parrot", new MobDrops("parrot",
                new DropInfo(ScrollType.AIR, 1, 5)));
        DEFAULT_SCROLL_DROPS.put("chicken", new MobDrops("chicken",
                new DropInfo(ScrollType.AIR, 1, 7.5)));
        DEFAULT_SCROLL_DROPS.put("sheep", new MobDrops("sheep",
                new DropInfo(ScrollType.AIR, 1, 5)));
        DEFAULT_SCROLL_DROPS.put("shulker", new MobDrops("shulker",
                new DropInfo(ScrollType.AIR, 1, 40, 4),
                new DropInfo(ScrollType.AIR, 2, 20, 3),
                new DropInfo(ScrollType.AIR, 3, 10, 2),
                new DropInfo(ScrollType.AIR, 4, 5),
                new DropInfo(ScrollType.AIR, 5, 2.5),
                new DropInfo(ScrollType.AIR, 6, 1)));


        //Earth
        DEFAULT_SCROLL_DROPS.put("mooshroom", new MobDrops("mooshroom",
                new DropInfo(ScrollType.EARTH, 1, 5.0),
                new DropInfo(ScrollType.EARTH, 2, 2.5)));
        DEFAULT_SCROLL_DROPS.put("cave_spider", new MobDrops("cave_spider",
                new DropInfo(ScrollType.EARTH, 1, 10, 2),
                new DropInfo(ScrollType.EARTH, 1, 5.0, 1)));
        DEFAULT_SCROLL_DROPS.put("silverfish", new MobDrops("silverfish",
                new DropInfo(ScrollType.EARTH, 1, 12.5, 2),
                new DropInfo(ScrollType.EARTH, 2, 5.0),
                new DropInfo(ScrollType.EARTH, 3, 2.5)));
        DEFAULT_SCROLL_DROPS.put("spider", new MobDrops("spider",
                new DropInfo(ScrollType.EARTH, 1, 5.0),
                new DropInfo(ScrollType.EARTH, 2, 1)));
        DEFAULT_SCROLL_DROPS.put("skeleton", new MobDrops("skeleton",
                new DropInfo(ScrollType.EARTH, 1, 7.5),
                new DropInfo(ScrollType.EARTH, 2, 5),
                new DropInfo(ScrollType.EARTH, 3, 1)));
        DEFAULT_SCROLL_DROPS.put("zombie", new MobDrops("zombie",
                new DropInfo(ScrollType.EARTH, 1, 5.0),
                new DropInfo(ScrollType.EARTH, 2, 2.5),
                new DropInfo(ScrollType.EARTH, 3, 1.0)));
        DEFAULT_SCROLL_DROPS.put("husk", new MobDrops("husk",
                new DropInfo(ScrollType.EARTH, 1, 10.0, 2),
                new DropInfo(ScrollType.EARTH, 2, 7.5, 2),
                new DropInfo(ScrollType.EARTH, 3, 5)));

        //Lightning
        DEFAULT_SCROLL_DROPS.put("creeper", new MobDrops("creeper",
                new DropInfo(ScrollType.LIGHTNING, 1, 1)));

        //Combustion
        DEFAULT_SCROLL_DROPS.get("creeper").addDropInfo(
                new DropInfo(ScrollType.COMBUSTION, 1, 2.5),
                new DropInfo(ScrollType.COMBUSTION, 2, 0.5)
        );
        DEFAULT_SCROLL_DROPS.get("blaze").addDropInfo(
                new DropInfo(ScrollType.COMBUSTION, 1, 10, 2),
                new DropInfo(ScrollType.COMBUSTION, 2, 5),
                new DropInfo(ScrollType.COMBUSTION, 3, 1)
        );
        DEFAULT_SCROLL_DROPS.get("ghast").addDropInfo(
                new DropInfo(ScrollType.COMBUSTION, 1, 25.0, 3),
                new DropInfo(ScrollType.COMBUSTION, 2, 15.0, 2),
                new DropInfo(ScrollType.COMBUSTION, 3, 5.0)
        );

        //Sand
        DEFAULT_SCROLL_DROPS.put("husk", new MobDrops("husk",
                new DropInfo(ScrollType.SAND, 1, 15.0),
                new DropInfo(ScrollType.SAND, 2, 5.0)
        ));
        //Ice
//        DEFAULT_SCROLL_DROPS.put("polarbear", 10.0);
//        DEFAULT_SCROLL_TYPE.put("polarbear", "ice");
//        DEFAULT_SCROLL_DROPS.put("stray", 10.0);
//        DEFAULT_SCROLL_TYPE.put("stray", "ice");

        //All
        DEFAULT_SCROLL_DROPS.put("witch", new MobDrops("witch",
                new DropInfo(ScrollType.ALL, 1, 10, 2),
                new DropInfo(ScrollType.ALL, 2, 5, 2),
                new DropInfo(ScrollType.ALL, 3, 2.5)));
        DEFAULT_SCROLL_DROPS.put("enderman", new MobDrops("enderman",
                new DropInfo(ScrollType.ALL, 1, 25, 3),
                new DropInfo(ScrollType.ALL, 2, 15, 2),
                new DropInfo(ScrollType.ALL, 3, 10),
                new DropInfo(ScrollType.ALL, 4, 2.5)));
        DEFAULT_SCROLL_DROPS.get("creeper").addDropInfo(
                new DropInfo(ScrollType.ALL, 1, 15.0),
                new DropInfo(ScrollType.ALL, 2, 7.5)
        );
        DEFAULT_SCROLL_DROPS.get("zombie").addDropInfo(
                new DropInfo(ScrollType.ALL, 1, 10.0),
                new DropInfo(ScrollType.ALL, 2, 5.0)
        );
        DEFAULT_SCROLL_DROPS.get("skeleton").addDropInfo(
                new DropInfo(ScrollType.ALL, 1, 10.0),
                new DropInfo(ScrollType.ALL, 2, 5.0)
        );
        DEFAULT_SCROLL_DROPS.put("ender_dragon", new MobDrops("ender_dragon",
                new DropInfo(ScrollType.ALL, 1, 75, 7),
                new DropInfo(ScrollType.ALL, 2, 65, 6),
                new DropInfo(ScrollType.ALL, 3, 55, 5),
                new DropInfo(ScrollType.ALL, 4, 45, 4),
                new DropInfo(ScrollType.ALL, 5, 35, 3),
                new DropInfo(ScrollType.ALL, 6, 25, 2),
                new DropInfo(ScrollType.ALL, 7, 15, 1)));
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
    @Load
    private Map<String, Collection<Integer>> scrollDropAmounts;
    @Load
    private Map<String, Collection<Integer>> scrollDropTiers;
    //List for transferring/letting the player write
    @Load
    private Map<String, Collection<String>> scrollTypes;
    //Actually storing the data
    private Map<Class<? extends Entity>, MobDrops> scrollDrops;

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

        MOBS_CONFIG.scrollTypes = new HashMap<>();
        MOBS_CONFIG.scrollDropAmounts = new HashMap<>();
        MOBS_CONFIG.scrollDropTiers = new HashMap<>();
        MOBS_CONFIG.scrollDropChances = new HashMap<>();

        for (MobDrops drop : info) {
            //Scroll Types
            MOBS_CONFIG.scrollTypes.put(drop.getMobName(), drop.getDropTypes());
            //Scroll Amounts
            MOBS_CONFIG.scrollDropAmounts.put(drop.getMobName(), drop.getDropAmounts());
            //Scroll Tiers
            MOBS_CONFIG.scrollDropTiers.put(drop.getMobName(), drop.getDropTiers());
            //Scroll Chances
            MOBS_CONFIG.scrollDropChances.put(drop.getMobName(), drop.getDropChances());
        }


    }

    public void loadLists() {
        //Turns information basic into a mob drop map
        MOBS_CONFIG.scrollDrops = new HashMap<>();
        int i = 0;
        for (String mobName : scrollTypes.keySet()) {
            scrollDrops.put(MobDrops.getClassFromName(mobName),
                    MobDrops.fromCollections(mobName, new ArrayList<>(scrollTypes.values()).get(i),
                            new ArrayList<>(scrollDropAmounts.values()).get(i), new ArrayList<>(scrollDropTiers.values()).get(i),
                            new ArrayList<>(scrollDropChances.values()).get(i)));
            i++;
        }
        //We did it gamers

        bisonFoodList = new HashMap<>();
        for (Map.Entry<String, Integer> entry : bisonFoods.entrySet()) {
            String name = entry.getKey();
            Item item = Item.getByNameOrId(name);
            if (item != null) {
                bisonFoodList.put(item, entry.getValue());
            } else {
                AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Invalid bison food; item " + name + " not found");
            }
        }
        tradeItems = new HashMap<>();
        for (Map.Entry<String, String> entry : scrollTradeItems.entrySet()) {
            String name = entry.getKey();
            String tierS = name.split(" ")[1];
            name = name.split(" ")[0];
            int tier = 1;
            try {
                tier = Integer.parseInt(tierS);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Please enter a number for the tier next to the item name.");
            }
            Item item = Item.getByNameOrId(name);
            if (item != null) {
                tradeItems.put(item, new Pair<>(tier, entry.getValue()));
            } else {
                AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Invalid trade item; item " + name + " not found");
            }
        }
    }

    public int getDomesticationValue(Item item) {
        return bisonFoodList.getOrDefault(item, 0);
    }

    public List<Item> getTradeItems() {
        List<Item> items = new ArrayList<>();
        for (Map.Entry<Item, Pair<Integer, String>> entry : tradeItems.entrySet()) {
            items.add(entry.getKey());
        }
        return items;
    }

    public boolean isTradeItem(Item item) {
        return tradeItems.containsKey(item);
    }

    public String getTradeItemElement(Item item) {
        return tradeItems.get(item).second();
    }

    public int getTradeItemTier(Item item) {
        return tradeItems.get(item).first();
    }

    public boolean isAirTradeItem(Item item) {
        return tradeItems.get(item).second().equals("air");
    }

    public boolean isFireTradeItem(Item item) {
        return tradeItems.get(item).second().equals("fire");
    }

    public boolean isBisonFood(Item item) {
        return bisonFoodList.containsKey(item);
    }

    /**
     * @param entity The entity's who's info we're getting
     * @return The MobDrop information about the entity, containing an array of DropInfo for each drop
     * (tier, amount, chance, type).
     */
    public MobDrops getMobDropInfo(Entity entity) {
        return MOBS_CONFIG.scrollDrops.get(entity.getClass());
    }


    public static class BenderSettings {
        @Load
        public final int maxNumberOfBenders = 3;

        @Load
        public final int maxLevel = 7;
    }

    public static class ScrollSettings {
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
