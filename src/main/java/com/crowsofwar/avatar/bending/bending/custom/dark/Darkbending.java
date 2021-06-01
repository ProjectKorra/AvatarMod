package com.crowsofwar.avatar.bending.bending.custom.dark;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.client.gui.BendingMenuInfo;
import com.crowsofwar.avatar.client.gui.MenuTheme;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.UUID;

public class Darkbending extends BendingStyle {

    public static final UUID ID = UUID.fromString("a7dcf80c-75ed-49b5-980d-1bebd7e37c2a");

    private BendingMenuInfo menu;

    public Darkbending() {
        registerAbilities();
        Color light = new Color(150, 0, 150);
        Color dark = new Color(50, 0, 50);
        Color iconClr = new Color(0, 0, 0);
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
        return "darkbending";
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public SoundEvent getRadialMenuSound() {
        return SoundEvents.BLOCK_END_GATEWAY_SPAWN;
    }

    @Override
    public TextFormatting getTextFormattingColour() {
        return TextFormatting.BLACK;
    }
}
