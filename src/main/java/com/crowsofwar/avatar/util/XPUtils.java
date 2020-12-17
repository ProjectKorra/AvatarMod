package com.crowsofwar.avatar.util;

import net.minecraft.entity.player.EntityPlayer;

//Shamelessly stolen from hammercore
public class XPUtils {
    public static int takeXP(EntityPlayer player, int xp) {
        int total = getXPTotal(player);
        int taken = Math.min(xp, total);
        total -= taken;
        setPlayersExpTo(player, total);
        return taken;
    }

    public static void giveXP(EntityPlayer player, int xp) {
        setPlayersExpTo(player, getXPTotal(player) + xp);
    }

    public static void setPlayersExpTo(EntityPlayer player, int total) {
        player.experienceLevel = Math.max(getLevelFromXPValue(total), 0);
        player.experience = Math.max(getCurrentFromXPValue(total), 0F);
    }

    public static int getXPTotal(int xpLevel, float current) {
        return (int) (getXPValueFromLevel(xpLevel) + getXPValueToNextLevel(xpLevel) * current);
    }

    public static int getXPTotal(EntityPlayer player) {
        return (int) (getXPValueFromLevel(player.experienceLevel) + (getXPValueToNextLevel(player.experienceLevel) * player.experience));
    }

    public static int getLevelFromXPValue(int value) {
        int level = 0;
        if (value >= getXPValueFromLevel(30))
            level = (int) (.07142857142857142 * (Math.sqrt(56D * value - 32511D) + 303D));
        else if (value >= getXPValueFromLevel(15))
            level = (int) (.16666666666666666 * (Math.sqrt(24D * value - 5159D) + 59D));
        else
            level = (int) (value / 17D);
        return level;
    }

    public static float getCurrentFromXPValue(int value) {
        if (value == 0)
            return 0F;
        int level = getLevelFromXPValue(value);
        float needed = getXPValueFromLevel(level);
        float next = getXPValueToNextLevel(level);
        float difference = value - needed;
        float current = difference / next;
        return current;
    }

    public static int getXPValueFromLevel(int xpLevel) {
        int val = 0;
        if (xpLevel >= 30)
            val = (int) (3.5 * Math.pow(xpLevel, 2D) - 151.5 * xpLevel + 2220D);
        else if (xpLevel >= 15)
            val = (int) (1.5D * Math.pow(xpLevel, 2D) - 29.5 * xpLevel + 360D);
        else
            val = 17 * xpLevel;
        return val;
    }

    public static int getXPValueToNextLevel(int xpLevel) {
        int val = 0;
        if (xpLevel >= 30)
            val = 7 * xpLevel - 148;
        else if (xpLevel >= 15)
            val = 3 * xpLevel - 28;
        else
            val = 17;
        return val;
    }
}
