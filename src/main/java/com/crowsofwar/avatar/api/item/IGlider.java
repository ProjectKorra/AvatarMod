package com.crowsofwar.avatar.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;

public interface IGlider extends INBTSerializable<NBTTagCompound> {
    //ToDo

    //==============Flight==================

    //Blocks traveled horizontally per movement time.
    float getMaxSpeed();

    void setMaxSpeed(float speed);

    //Blocks traveled vertically per movement time.
    float getMinSpeed();

    void setMinSpeed(float speed);

    //Blocks traveled vertically per movement time.
    float getYBoost();

    void setYBoost(float boost);

    //Blocks traveled vertically per movement time.
    float getFallReduction();

    void setFallReduction(float reduction);

    //Blocks traveled vertically per movement time.
    float getPitchOffset();

    void setPitchOffset(float offset);

    //===============Wind====================

    double getWindMultiplier();

    void setWindMultiplier(double windMultiplier);

    double getAirResistance();

    void setAirResistance(double airResistance);

    //=============Durability================

    int getTotalDurability();

    void setTotalDurability(int durability);

    int getCurrentDurability(ItemStack glider);

    void setCurrentDurability(ItemStack glider, int durability);

    boolean isBroken(ItemStack glider);

    //==============Upgrades====================
    ArrayList<ItemStack> getUpgrades(ItemStack glider);

    void removeUpgrade(ItemStack glider, ItemStack upgrade);

    void addUpgrade(ItemStack glider, ItemStack upgrade);

    //==============Misc=========================
    ResourceLocation getModelTexture(ItemStack glider);

    void setModelTexture(ResourceLocation resourceLocation);

}
