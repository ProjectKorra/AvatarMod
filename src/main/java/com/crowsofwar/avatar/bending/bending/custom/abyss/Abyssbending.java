package com.crowsofwar.avatar.bending.bending.custom.abyss;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.client.gui.BendingMenuInfo;
import com.crowsofwar.avatar.client.gui.MenuTheme;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.UUID;

public class Abyssbending extends BendingStyle {

    public static final UUID ID = UUID.fromString("b7d068e0-13c5-4b1f-b1fc-a2681f2108b5");

    private BendingMenuInfo menu;

    public Abyssbending() {
        registerAbilities();
        Color light = new Color(100, 200, 255);
        Color dark = new Color(50, 100, 175);
        Color iconClr = new Color(25, 25, 25);
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
        return "abyssbending";
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public SoundEvent getRadialMenuSound() {
        return SoundEvents.ENTITY_ENDERDRAGON_SHOOT;
    }

    @Override
    public TextFormatting getTextFormattingColour() {
        return TextFormatting.WHITE;
    }
}