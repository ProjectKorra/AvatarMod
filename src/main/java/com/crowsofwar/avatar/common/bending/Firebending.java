package com.crowsofwar.avatar.common.bending;

import static com.crowsofwar.avatar.common.AvatarAbility.*;
import static com.crowsofwar.avatar.common.controls.AvatarControl.*;

import com.crowsofwar.avatar.common.AvatarAbility;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
import com.crowsofwar.avatar.common.gui.AvatarGuiIds;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.util.BlockPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class Firebending implements IBendingController {
	
	private final BendingMenuInfo menu;
	
	public Firebending() {
		menu = new BendingMenuInfo(KEY_FIREBENDING, AvatarGuiIds.GUI_RADIAL_MENU_FIRE, ACTION_LIGHT_FIRE);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public int getID() {
		return BendingManager.BENDINGID_FIREBENDING;
	}
	
	@Override
	public void onAbility(AvatarAbility ability, AvatarPlayerData data) {
		PlayerState ps = data.getState();
		EntityPlayer player = ps.getPlayerEntity();
		World world = player.worldObj;
		
		if (ability == ACTION_LIGHT_FIRE) {
			BlockPos looking = ps.verifyClientLookAtBlock(-1, 5);
			if (world.getBlock(looking.x, looking.y + 1, looking.z) == Blocks.air)
				world.setBlock(looking.x, looking.y + 1, looking.z, Blocks.fire);
		}
	}
	
	@Override
	public IBendingState createState(AvatarPlayerData data) {
		return new FirebendingState();
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
