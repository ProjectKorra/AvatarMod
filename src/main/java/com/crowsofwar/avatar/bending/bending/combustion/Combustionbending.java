package com.crowsofwar.avatar.bending.bending.combustion;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.client.gui.BendingMenuInfo;
import com.crowsofwar.avatar.client.gui.MenuTheme;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.UUID;

public class Combustionbending extends BendingStyle {

	public static final UUID ID = UUID.fromString("8485da8f-20d9-4354-6e47-df13446d7da5");

	private final BendingMenuInfo menu;

	public Combustionbending() {
		super(Firebending.ID);

		registerAbilities();
		Color light = new Color(244, 240, 187);
		Color red = new Color(173, 64, 31);
		Color gray = new Color(40, 40, 40);
		MenuTheme.ThemeColor background = new MenuTheme.ThemeColor(light, red);
		MenuTheme.ThemeColor edge = new MenuTheme.ThemeColor(red, red);
		MenuTheme.ThemeColor icon = new MenuTheme.ThemeColor(gray, light);
		menu = new BendingMenuInfo(new MenuTheme(background, edge, icon, 0xFAAA5A), this);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

	}

	@Override
	public int getTextColour() {
		return 0x606060;
	}

	@Override
	public BendingMenuInfo getRadialMenu() {
		return menu;
	}

	@Override
	public String getName() {
		return "combustionbending";
	}

	@Override
	public UUID getId() {
		return ID;
	}

	@Override
	public TextFormatting getTextFormattingColour() {
		return TextFormatting.GRAY;
	}

	@Override
	public SoundEvent getRadialMenuSound() {
		return SoundEvents.ENTITY_GENERIC_EXPLODE;
	}
}