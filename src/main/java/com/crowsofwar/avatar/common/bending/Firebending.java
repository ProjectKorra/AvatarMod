package com.crowsofwar.avatar.common.bending;

import static com.crowsofwar.avatar.common.AvatarAbility.*;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK_DOWN;
import static com.crowsofwar.avatar.common.util.VectorUtils.*;

import java.awt.Color;

import com.crowsofwar.avatar.common.AvatarAbility;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.gui.AvatarGuiIds;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;
import com.crowsofwar.avatar.common.util.AvBlockPos;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Firebending implements IBendingController {
	
	private final BendingMenuInfo menu;
	
	public Firebending() {
		Color light = new Color(244, 240, 187);
		Color red = new Color(173, 64, 31);
		Color gray = new Color(40, 40, 40);
		ThemeColor background = new ThemeColor(light, red);
		ThemeColor edge = new ThemeColor(red, red);
		ThemeColor icon = new ThemeColor(gray, light);
		menu = new BendingMenuInfo(new MenuTheme(background, edge, icon), AvatarControl.KEY_FIREBENDING, AvatarGuiIds.GUI_RADIAL_MENU_FIRE,
				ACTION_LIGHT_FIRE, ACTION_FIRE_PUNCH, ACTION_FIREARC_THROW);
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
		FirebendingState fs = (FirebendingState) data.getBendingState(this);
		
		if (ability == ACTION_LIGHT_FIRE) {
			AvBlockPos looking = ps.verifyClientLookAtBlock(-1, 5);
			ForgeDirection side = ps.getLookAtSide();
			if (ps.isLookingAtBlock(-1, 5)) {
				AvBlockPos setAt = new AvBlockPos(looking.x, looking.y, looking.z);
				setAt.offset(side);
				if (world.getBlock(setAt.x, setAt.y, setAt.z) == Blocks.air) world.setBlock(setAt.x, setAt.y, setAt.z, Blocks.fire);
			}
		}
		if (ability == ACTION_FIRE_PUNCH) {
			// Vec3d look = VectorUtils.fromYawPitch(Math.toRadians(player.rotationYaw),
			// Math.toRadians(player.rotationPitch));
			// Vec3d motion = VectorUtils.times(look, 10);
			// EntityFlame flame = new EntityFlame(world, player.posX, player.posY + 1.6,
			// player.posZ,
			// motion.xCoord, motion.yCoord, motion.zCoord);
			//
			// world.spawnEntityInWorld(flame);
			
			Vec3d look = fromYawPitch(Math.toRadians(player.rotationYaw), Math.toRadians(player.rotationPitch));
			Vec3d lookPos = plus(getEntityPos(player), times(look, 3));
			EntityFireArc fire = new EntityFireArc(world);
			fire.setPosition(lookPos.xCoord, lookPos.yCoord, lookPos.zCoord);
			
			world.spawnEntityInWorld(fire);
			
			fs.setFireArc(fire);
			data.sendBendingState(fs);
			
		}
		
		if (ability == ACTION_FIREARC_THROW) {
			
			EntityFireArc fire = fs.getFireArc();
			if (fire != null) {
				Vec3d look = fromYawPitch(Math.toRadians(player.rotationYaw), Math.toRadians(player.rotationPitch));
				fire.addVelocity(times(look, 15));
				fire.setGravityEnabled(true);
				fs.setNoFireArc();
				data.sendBendingState(fs);
			}
			
		}
		
	}
	
	@Override
	public IBendingState createState(AvatarPlayerData data) {
		return new FirebendingState(data);
	}
	
	@Override
	public void onUpdate(AvatarPlayerData data) {
		PlayerState state = data.getState();
		EntityPlayer player = state.getPlayerEntity();
		World world = player.worldObj;
		FirebendingState fs = (FirebendingState) data.getBendingState(this);
		if (fs.isManipulatingFire()) {
			EntityFireArc fire = fs.getFireArc();
			if (fire != null) {
				Vec3d look = fromYawPitch(Math.toRadians(player.rotationYaw), Math.toRadians(player.rotationPitch));
				Vec3d lookPos = plus(getEyePos(player), times(look, 3));
				Vec3d motion = minus(lookPos, getEntityPos(fire));
				motion.normalize();
				mult(motion, .05 * 3);
				fire.moveEntity(motion.xCoord, motion.yCoord, motion.zCoord);
				fire.setOwner(player);
			} else {
				if (!world.isRemote) fs.setNoFireArc();
			}
		}
	}
	
	@Override
	public AvatarAbility getAbility(AvatarPlayerData data, AvatarControl input) {
		if (input == CONTROL_LEFT_CLICK_DOWN) {
			FirebendingState state = (FirebendingState) data.getBendingState(this);
			if (state != null && state.isManipulatingFire()) return ACTION_FIREARC_THROW;
		}
		
		return AvatarAbility.NONE;
	}
	
	@Override
	public BendingMenuInfo getRadialMenu() {
		return menu;
	}
	
	@Override
	public String getControllerName() {
		return "firebending";
	}
	
}
