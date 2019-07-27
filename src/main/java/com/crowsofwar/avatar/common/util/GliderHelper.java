package com.crowsofwar.avatar.common.util;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.GliderInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;

public class GliderHelper {

    public static ArrayList<ItemStack> getUpgradesFromNBT(ItemStack glider) {
        ArrayList<ItemStack> upgradesArrayList = new ArrayList<>();
        if (glider != null && !glider.isEmpty()) {
            NBTTagCompound nbtTagCompound = glider.getTagCompound();
            if (nbtTagCompound != null) {
                if(nbtTagCompound.hasKey(GliderInfo.NBT_KEYS.UPGRADES)) {
                    NBTTagList tagList = nbtTagCompound.getTagList(GliderInfo.NBT_KEYS.UPGRADES, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
                    for (int i = 0; i < tagList.tagCount(); i++) {
                        NBTTagCompound stackTag = tagList.getCompoundTagAt(i);
                        ItemStack upgrade = new ItemStack(stackTag);
                        if (!upgrade.isEmpty() && !glider.isEmpty())
                            upgradesArrayList.add(upgrade);
                    }
                }
            }
        }
        return upgradesArrayList;
    }
}
