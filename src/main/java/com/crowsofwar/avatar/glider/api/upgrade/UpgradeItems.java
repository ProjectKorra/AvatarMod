package com.crowsofwar.avatar.glider.api.upgrade;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class UpgradeItems {

    private static ArrayList<ItemStack> possibleUpgradeList;

    public static void initUpgradesList() {
        possibleUpgradeList = new ArrayList<>();
//        addToUpgradesList(new ItemStack(Items.COMPASS)); //ToDo: Add back with functionality at some point
    }

    public static void addToUpgradesList(ItemStack stack) {
        possibleUpgradeList.add(stack);
    }

    public static ArrayList<ItemStack> getPossibleUpgradeList() {
        return possibleUpgradeList;
    }

    private static void removeFromUpgradesList(ItemStack stack) {
        possibleUpgradeList.remove(stack);
    }

}
