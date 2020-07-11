/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.bending.bending.fire;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.client.gui.BendingMenuInfo;
import com.crowsofwar.avatar.client.gui.MenuTheme;
import com.crowsofwar.avatar.client.gui.MenuTheme.ThemeColor;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.UUID;

public class Firebending extends BendingStyle {

    public static final UUID ID = UUID.fromString("8485da8f-20d9-4e98-9a10-721104e962fe");

    private final BendingMenuInfo menu;

    public Firebending() {
        registerAbilities();
        Color light = new Color(244, 240, 187);
        Color red = new Color(173, 64, 31);
        Color gray = new Color(40, 40, 40);
        ThemeColor background = new ThemeColor(light, red);
        ThemeColor edge = new ThemeColor(red, red);
        ThemeColor icon = new ThemeColor(gray, light);
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
        return 0xFF0000;
    }

    @Override
    public BendingMenuInfo getRadialMenu() {
        return menu;
    }

    @Override
    public String getName() {
        return "firebending";
    }

    @Override
    public UUID getId() {
        return ID;
    }

    @Override
    public TextFormatting getTextFormattingColour() {
        return TextFormatting.RED;
    }

    @Override
    public SoundEvent getRadialMenuSound() {
        return SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE;
    }
}
