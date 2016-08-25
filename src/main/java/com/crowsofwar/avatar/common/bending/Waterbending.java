package com.crowsofwar.avatar.common.bending;

import static com.crowsofwar.avatar.common.AvatarAbility.ACTION_WATER_ARC;
import static com.crowsofwar.avatar.common.controls.AvatarControl.KEY_WATERBENDING;
import static com.crowsofwar.avatar.common.gui.AvatarGuiIds.GUI_RADIAL_MENU_WATER;

import java.awt.Color;

import com.crowsofwar.avatar.common.AvatarAbility;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class Waterbending implements BendingController {
	
	private BendingMenuInfo menu;
	
	public Waterbending() {
		Color base = new Color(228, 255, 225);
		Color edge = new Color(60, 188, 145);
		Color icon = new Color(129, 149, 148);
		ThemeColor background = new ThemeColor(base, edge);
		menu = new BendingMenuInfo(
				new MenuTheme(new ThemeColor(base, edge), new ThemeColor(edge, edge),
						new ThemeColor(icon, base)),
				KEY_WATERBENDING, GUI_RADIAL_MENU_WATER, ACTION_WATER_ARC);
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
			
			boolean needsSync = false;
			
			if (bendingState.isBendingWater()) {
				EntityWaterArc water = bendingState.getWaterArc();
				water.setGravityEnabled(true);
				bendingState.releaseWater();
				needsSync = true;
			}
			
			VectorI targetPos = state.getClientLookAtBlock();
			if (targetPos != null) {
				Block lookAt = world.getBlockState(targetPos.toBlockPos()).getBlock();
				if (lookAt == Blocks.WATER || lookAt == Blocks.FLOWING_WATER) {
					
					EntityWaterArc water = new EntityWaterArc(world);
					water.setOwner(player);
					water.setPosition(targetPos.x() + 0.5, targetPos.y() - 0.5, targetPos.z() + 0.5);
					water.setGravityEnabled(false);
					bendingState.setWaterArc(water);
					
					world.spawnEntityInWorld(water);
					
					needsSync = true;
					
				}
			}
			
			if (needsSync) data.sendBendingState(bendingState);
			
		}
		
		if (ability == AvatarAbility.ACTION_WATERARC_THROW) {
			
			if (bendingState.isBendingWater()) {
				
				EntityWaterArc water = bendingState.getWaterArc();
				
				Vector force = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
						Math.toRadians(player.rotationPitch));
				force.mul(10);
				water.addVelocity(force);
				water.setGravityEnabled(true);
				
				bendingState.releaseWater();
				data.sendBendingState(bendingState);
				
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
				Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
						Math.toRadians(player.rotationPitch));
				Vector lookPos = Vector.getEyePos(player).plus(look.times(3));
				Vector motion = lookPos.minus(new Vector(water));
				motion.normalize();
				motion.mul(.15);
				water.moveEntity(motion.x(), motion.y(), motion.z());
				water.setOwner(player);
				
				if (water.worldObj.isRemote && water.canPlaySplash()) {
					if (motion.sqrMagnitude() >= 0.004) water.playSplash();
				}
			} else {
				if (!world.isRemote) bendingState.setWaterArc(null);
			}
			
		}
		
	}
	
	@Override
	public AvatarAbility getAbility(AvatarPlayerData data, AvatarControl input) {
		
		if (input == AvatarControl.CONTROL_LEFT_CLICK_DOWN) return AvatarAbility.ACTION_WATERARC_THROW;
		
		return AvatarAbility.NONE;
	}
	
	@Override
	public BendingMenuInfo getRadialMenu() {
		return menu;
	}
	
	@Override
	public String getControllerName() {
		return "waterbending";
	}
	
}
