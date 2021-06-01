package com.crowsofwar.avatar.bending.bending.custom.ki;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.client.gui.BendingMenuInfo;
import com.crowsofwar.avatar.client.gui.MenuTheme;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.UUID;

public class Kibending extends BendingStyle {

    public static final UUID ID = UUID.fromString("54cf2a5c-aed1-4c3c-b5c8-376f612e1f5f");

    private BendingMenuInfo menu;

    public Kibending() {
        registerAbilities();
        Color light = new Color(100, 200, 255);
        Color dark = new Color(50, 100, 175);
        Color iconClr = new Color(255, 255, 255);
        MenuTheme.ThemeColor background = new MenuTheme.ThemeColor(light, dark);
        MenuTheme.ThemeColor edge = new MenuTheme.ThemeColor(dark, dark);
        MenuTheme.ThemeColor icon = new MenuTheme.ThemeColor(iconClr, iconClr);
        MenuTheme theme = new MenuTheme(background, edge, icon, 0xffffff);
        this.menu = new BendingMenuInfo(theme, this);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

    }

    @Override
    public int getTextColour() {
        return 0xffffff;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {

    }

    @Override
    public BendingMenuInfo getRadialMenu() {
        return menu;
    }

    @Override
    public String getName() {
        return "kibending";
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public SoundEvent getRadialMenuSound() {
        return SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE;
    }

    @Override
    public TextFormatting getTextFormattingColour() {
        return TextFormatting.AQUA;
    }
}
