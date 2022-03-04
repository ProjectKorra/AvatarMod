package com.crowsofwar.avatar.bending.bending.custom.hyper;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.client.gui.BendingMenuInfo;
import com.crowsofwar.avatar.client.gui.MenuTheme;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.UUID;

public class Hyperbending extends BendingStyle {

    public static final UUID ID = UUID.fromString("0c69057b-bdc4-406b-b185-a3c76c164df3");

    private BendingMenuInfo menu;

    public Hyperbending() {
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
        return "hyperbending";
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