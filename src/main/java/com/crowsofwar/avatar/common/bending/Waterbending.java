package com.crowsofwar.avatar.common.bending;

import java.awt.Color;

import com.crowsofwar.avatar.common.AvatarAbility;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
import com.crowsofwar.avatar.common.gui.AvatarGuiIds;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;

import static com.crowsofwar.avatar.common.AvatarAbility.*;
import static com.crowsofwar.avatar.common.gui.AvatarGuiIds.*;
import static com.crowsofwar.avatar.common.controls.AvatarControl.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class Waterbending implements IBendingController {

	private BendingMenuInfo menu;
	
	public Waterbending() {
		Color base = new Color(246, 250, 250);
		Color edge = new Color(52, 224, 211);
		Color icon = new Color(129, 149, 148);
		ThemeColor background = new ThemeColor(base, edge);
		menu = new BendingMenuInfo(new MenuTheme(new ThemeColor(base, edge), new ThemeColor(edge, edge),
				new ThemeColor(icon, base)), KEY_WATERBENDING, GUI_RADIAL_MENU_WATER, ACTION_PEE);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}

	@Override
	public int getID() {
		return BendingManager.BENDINGID_WATERBENDING;
	}

	@Override
	public void onAbility(AvatarAbility ability, AvatarPlayerData data) {
		PlayerState state = data.getState();
		EntityPlayer player = state.getPlayerEntity();
		
		if (ability == ACTION_PEE) {
			System.out.println("well... this is TECHNICALLY waterbending... right??");
		}
		
	}

	@Override
	public IBendingState createState(AvatarPlayerData data) {
		return new WaterbendingState(data);
	}

	@Override
	public void onUpdate(AvatarPlayerData data) {
		
	}

	@Override
	public AvatarAbility getAbility(AvatarPlayerData data, AvatarControl input) {
		return AvatarAbility.NONE;
	}

	@Override
	public BendingMenuInfo getRadialMenu() {
		return menu;
	}

}
