package com.crowsofwar.avatar.bending.bending.custom.light;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.client.gui.BendingMenuInfo;
import com.crowsofwar.avatar.client.gui.MenuTheme;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.UUID;

public class Lightbending extends BendingStyle {

    public static final UUID ID = UUID.fromString("fcf889bf-3a9e-4586-804a-5fa1d5b2419e");

    private BendingMenuInfo menu;

    public Lightbending() {
        registerAbilities();
        Color light = new Color(255, 255, 255);
        Color dark = new Color(255, 253, 150);
        Color iconClr = new Color(220, 220, 50);
        MenuTheme.ThemeColor background = new MenuTheme.ThemeColor(light, dark);
        MenuTheme.ThemeColor edge = new MenuTheme.ThemeColor(dark, dark);
        MenuTheme.ThemeColor icon = new MenuTheme.ThemeColor(iconClr, iconClr);
        MenuTheme theme = new MenuTheme(background, edge, icon, 0x000000);
        this.menu = new BendingMenuInfo(theme, this);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

    }

    @Override
    public int getTextColour() {
        return 0x000000;
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
        return "lightbending";
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public SoundEvent getRadialMenuSound() {
        return SoundEvents.BLOCK_NOTE_CHIME;
    }

    @Override
    public TextFormatting getTextFormattingColour() {
        return TextFormatting.YELLOW;
    }
}
