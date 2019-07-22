package com.crowsofwar.avatar.glider.common.recipe;

import com.crowsofwar.avatar.glider.api.item.IGlider;
import com.crowsofwar.avatar.glider.api.upgrade.UpgradeItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class RecipeHelper {

    /**
     * Helper method for getting the first glider in the recipes grid (which will be the one used)
     * @param inventoryCrafting - the inventory to search
     * @return - the glider to be crafted
     */
    public static ItemStack getFirstUpgradableGlider(InventoryCrafting inventoryCrafting) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                ItemStack itemstack = inventoryCrafting.getStackInRowAndColumn(j, i);
                if (!itemstack.isEmpty() && (itemstack.getItem() instanceof IGlider)) {
                    return itemstack;
                }
            }
        }
        return null;
    }

    /**
     * Helper method for getting the first upgrade in the recipes grid (which will be the one used)
     * @param inventoryCrafting - the inventory to search
     * @return - the upgrade to be used
     */
    public static ItemStack getFirstUpgrade(InventoryCrafting inventoryCrafting){
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                ItemStack itemstack = inventoryCrafting.getStackInRowAndColumn(j, i);
                if (!itemstack.isEmpty()) {
                    for (ItemStack upgrade : UpgradeItems.getPossibleUpgradeList()) {
                        if (ItemStack.areItemStacksEqual(upgrade, itemstack)) {
                            ItemStack returnStack = itemstack.copy(); //copy stack
                            returnStack.setCount(1); //only apply 1 upgrade (stack size of 1)
                            return returnStack;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Helper method for getting the first glider in the recipes grid (which will be the one used)
     * @param inventoryCrafting - the inventory to search
     * @return - the integer of the slot number that the glider is in (-1 if no slot found)
     */
    public static int getFirstGliderInGridSlotNumber(InventoryCrafting inventoryCrafting) {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemstack = inventoryCrafting.getStackInSlot(i);
            if (!itemstack.isEmpty() && (itemstack.getItem() instanceof IGlider))
                return i;
        }
        return -1;
    }

    public static boolean containsStack(ArrayList<ItemStack> list, ItemStack stack) {
        for (ItemStack listItem : list) {
            if (ItemStack.areItemStacksEqual(listItem, stack)) {
                return true;
            }
        }
        return false;
    }

}
