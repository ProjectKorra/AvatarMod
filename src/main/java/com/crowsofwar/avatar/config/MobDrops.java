package com.crowsofwar.avatar.config;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.item.scroll.Scrolls;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

import java.util.*;
import java.util.stream.Collectors;

public class MobDrops {

    private final String mobName;
    private DropInfo[] dropInformation;
    private Class<? extends Entity> mob;

    public MobDrops(Class<? extends Entity> clazz, DropInfo... info) {
        this.mob = clazz;
        this.mobName = this.mob.getName();
        this.dropInformation = info;
    }

    public MobDrops(String mobName, DropInfo... info) {
        this.mobName = mobName;
        this.mob = EntityList.getClassFromName(mobName);
        this.dropInformation = info;
    }

    public static MobDrops fromCollections(Class<? extends Entity> clazz, Collection<String> scrollTypes, Collection<Integer> dropAmounts,
                                           Collection<Integer> dropTiers, Collection<Double> dropChances) {
        DropInfo[] info = new DropInfo[scrollTypes.size()];

        int i = 0;
        for (DropInfo dropInfo : info) {
            info[i] = new DropInfo(Scrolls.ScrollType.ALL, 1, 1D, 1);
            i++;
        }

        //Reset indexer each loop
        i = 0;
        for (String s : scrollTypes) {
            info[i].setType(Scrolls.ScrollType.getTypeFromString(s));
            i++;
        }

        i = 0;
        for (Integer num : dropAmounts) {
            info[i].setAmount(num);
            i++;
        }

        i = 0;
        for (Integer num : dropTiers) {
            info[i].setTier(num);
            i++;
        }

        i = 0;
        for (Double num : dropChances) {
            info[i].setDropChance(num);
            i++;
        }

        //Literally why
        return new MobDrops(clazz, info);
    }

    public static MobDrops fromCollections(String mobName, Collection<String> scrollTypes, Collection<Integer> dropAmounts,
                                           Collection<Integer> dropTiers, Collection<Double> dropChances) {
        DropInfo[] info = new DropInfo[scrollTypes.size()];

        int i = 0;
        for (DropInfo dropInfo : info) {
            info[i] = new DropInfo(Scrolls.ScrollType.ALL, 1, 1D, 1);
            i++;
        }

        //Reset indexer each loop
        i = 0;
        for (String s : scrollTypes) {
            info[i].setType(Scrolls.ScrollType.getTypeFromString(s));
            i++;
        }

        i = 0;
        for (Integer num : dropAmounts) {
            info[i].setAmount(num);
            i++;
        }

        i = 0;
        for (Integer num : dropTiers) {
            info[i].setTier(num);
            i++;
        }

        i = 0;
        for (Double num : dropChances) {
            info[i].setDropChance(num);
            i++;
        }

        return new MobDrops(mobName, info);
    }


    public void addDropInfo(DropInfo... info) {
        //LinkedLists serve for the purpose of this and make the lists mutable
        List<DropInfo> dropInfo = new LinkedList<>(Arrays.asList(getDropInformation()));
        List<DropInfo> newInfo = new LinkedList<>(Arrays.asList(info));
        dropInfo.addAll(newInfo);
        this.dropInformation = dropInfo.toArray(new DropInfo[0]);
    }

    public Class<? extends Entity> getMob() {
        return mob;
    }

    public void setMob(Class<? extends Entity> mob) {
        this.mob = mob;
    }

    public DropInfo[] getDropInformation() {
        return dropInformation;
    }

    public Collection<String> getReducedDropTypes() {
        Scrolls.ScrollType currentType = Scrolls.ScrollType.ALL;
        String[] allTypes = new String[this.dropInformation.length];
        int diffTypes = 0;
        for (int i = 0; i < this.dropInformation.length; i++) {
            if (!dropInformation[i].getType().equals(currentType)) {
                currentType = this.dropInformation[i].getType();
                allTypes[i] = currentType.getBendingName();
                diffTypes++;
            }
        }
        String[] types = new String[diffTypes];
        int i = 0;
        for (String type : allTypes) {
            if (type != null) {
                types[i] = type;
                i++;
            }
        }
        return Arrays.stream(types).collect(Collectors.toList());
    }

    public Collection<String> getDropTypes() {
        String[] allTypes = new String[this.dropInformation.length];
        for (int i = 0; i < this.dropInformation.length; i++) {
            allTypes[i] = this.dropInformation[i].getType().getBendingName();
        }
        return Arrays.stream(allTypes).collect(Collectors.toList());
    }

    public Collection<Integer> getDropAmounts() {
        Integer[] chances = new Integer[this.dropInformation.length];
        for (int i = 0; i < this.dropInformation.length; i++) {
            chances[i] = this.dropInformation[i].getAmount();
        }
        return Arrays.stream(chances).collect(Collectors.toList());
    }

    public Collection<Integer> getDropTiers() {
        Integer[] chances = new Integer[this.dropInformation.length];
        for (int i = 0; i < this.dropInformation.length; i++) {
            chances[i] = this.dropInformation[i].getTier();
        }
        return Arrays.stream(chances).collect(Collectors.toList());
    }

    public Collection<Double> getDropChances() {
        Double[] chances = new Double[this.dropInformation.length];
        for (int i = 0; i < this.dropInformation.length; i++) {
            chances[i] = this.dropInformation[i].getDropChance();
        }
        return Arrays.stream(chances).collect(Collectors.toList());
    }

    public Map.Entry<String, Collection<String>> getScrollTypeEntry() {
        return new AbstractMap.SimpleEntry<>(this.mob.getName(), getDropTypes());
    }

    public Map.Entry<String, Collection<Integer>> getDropAmountEntry() {
        return new AbstractMap.SimpleEntry<>(this.mob.getName(), getDropAmounts());
    }

    public Map.Entry<String, Collection<Integer>> getDropTierEntry() {
        return new AbstractMap.SimpleEntry<>(this.mob.getName(), getDropTiers());
    }

    public Map.Entry<String, Collection<Double>> getDropChanceEntry() {
        return new AbstractMap.SimpleEntry<>(this.mob.getName(), getDropChances());
    }

    public String getMobName() {
        return this.mobName;
    }
}
