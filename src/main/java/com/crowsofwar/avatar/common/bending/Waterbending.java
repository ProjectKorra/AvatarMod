package com.crowsofwar.avatar.common.bending;

import static com.crowsofwar.avatar.common.AvatarAbility.ACTION_WATER_ARC;
import static com.crowsofwar.avatar.common.controls.AvatarControl.KEY_WATERBENDING;
import static com.crowsofwar.avatar.common.gui.AvatarGuiIds.GUI_RADIAL_MENU_WATER;
import static com.crowsofwar.avatar.common.util.VectorUtils.*;

import java.awt.Color;

import com.crowsofwar.avatar.common.AvatarAbility;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;
import com.crowsofwar.avatar.common.util.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Waterbending implements IBendingController {

	private BendingMenuInfo menu;
	
	public Waterbending() {
		Color base = new Color(246, 250, 250);
		Color edge = new Color(52, 224, 211);
		Color icon = new Color(129, 149, 148);
		ThemeColor background = new ThemeColor(base, edge);
		menu = new BendingMenuInfo(new MenuTheme(new ThemeColor(base, edge), new ThemeColor(edge, edge),
				new ThemeColor(icon, base)), KEY_WATERBENDING, GUI_RADIAL_MENU_WATER, ACTION_WATER_ARC);
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
		World world = player.worldObj;
		WaterbendingState bendingState = (WaterbendingState) data.getBendingState(this);
		
		if (ability == ACTION_WATER_ARC) {
			
			if (bendingState.isBendingWater()) {
				EntityWaterArc water = bendingState.getWaterArc();
				water.setGravityEnabled(true);
				bendingState.setWaterArc(null);
			}
			
			BlockPos targetPos = state.getClientLookAtBlock();
			if (targetPos != null) {
				Block lookAt = world.getBlock(targetPos.x, targetPos.y, targetPos.z);
				System.out.println("target: " + lookAt);
				if (lookAt == Blocks.water || lookAt == Blocks.flowing_water) {
					
					EntityWaterArc water = new EntityWaterArc(world);
					water.setOwner(player);
					water.setPosition(targetPos.x + 0.5, targetPos.y - 0.5, targetPos.z + 0.5);
					water.setGravityEnabled(false);
					bendingState.setWaterArc(water);
					
					world.spawnEntityInWorld(water);
					
				}
			}
			
		}
		
	}

	@Override
	public IBendingState createState(AvatarPlayerData data) {
		return new WaterbendingState(data);
	}

	@Override
	public void onUpdate(AvatarPlayerData data) {
		
		PlayerState state = data.getState();
		EntityPlayer player = state.getPlayerEntity();
		World world = player.worldObj;
		WaterbendingState bendingState = (WaterbendingState) data.getBendingState(this);
		
		if (bendingState.isBendingWater()) {
			
			EntityWaterArc water = bendingState.getWaterArc();
			if (water != null) {
				Vec3 look = fromYawPitch(Math.toRadians(player.rotationYaw), Math.toRadians(player.rotationPitch));
				Vec3 lookPos = plus(getEyePos(player), times(look, 3));
				Vec3 motion = minus(lookPos, getEntityPos(water));
				motion.normalize();
				mult(motion, .05*3);
				water.moveEntity(motion.xCoord, motion.yCoord, motion.zCoord);
				water.setOwner(player);
			} else {
				if (!world.isRemote) bendingState.setWaterArc(null);
			}
			
		}
		
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
