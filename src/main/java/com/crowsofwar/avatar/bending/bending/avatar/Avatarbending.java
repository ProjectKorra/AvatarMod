package com.crowsofwar.avatar.bending.bending.avatar;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.client.gui.BendingMenuInfo;
import com.crowsofwar.avatar.client.gui.MenuTheme;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.UUID;

public class Avatarbending extends BendingStyle {
    public static final UUID ID = UUID.fromString("27ed29be-27bc-420e-9a83-f937f6a54b27");

    private final BendingMenuInfo menu;

    public Avatarbending() {
        registerAbilities();
        Color base = new Color(228, 255, 225);
        Color edge = new Color(60, 188, 145);
        Color icon = new Color(129, 149, 148);
        MenuTheme.ThemeColor background = new MenuTheme.ThemeColor(base, edge);
        menu = new BendingMenuInfo(new MenuTheme(background, new MenuTheme.ThemeColor(edge, edge),
                new MenuTheme.ThemeColor(icon, base), 0x57E8F2), this);
    }

    @Override
    public int getTextColour() {
        return 0x0066CC;
    }

    @Override
    public BendingMenuInfo getRadialMenu() {
        return menu;
    }

    @Override
    public String getName() {
        return "avatarbending";
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
        return TextFormatting.DARK_AQUA;
    }
}
