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
    double getHorizontalFlightSpeed();

    void setHorizontalFlightSpeed(double speed);

    //Blocks traveled vertically per movement time.
    double getVerticalFlightSpeed();

    void setVerticalFlightSpeed(double speed);

    //Blocks traveled horizontally per movement time when going fast/pressing shift.
    double getShiftHorizontalFlightSpeed();

    void setShiftHorizontalFlightSpeed(double speed);

    //Blocks traveled vertically per movement time when going fast/pressing shift.
    double getShiftVerticalFlightSpeed();

    void setShiftVerticalFlightSpeed(double speed);

    double getSpaceVerticalFlightSpeed();

    double getSpaceHorizontalFlightSpeed();

    void setSpaceVerticalFlightSpeed(double speed);

    void setSpaceHorizontalFlightSpeed(double speed);

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
