package com.crowsofwar.avatar.common.bending;

import static com.crowsofwar.avatar.common.AvatarAbility.*;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK_DOWN;

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
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class Firebending implements BendingController {
	
	private final BendingMenuInfo menu;
	
	public Firebending() {
		Color light = new Color(244, 240, 187);
		Color red = new Color(173, 64, 31);
		Color gray = new Color(40, 40, 40);
		ThemeColor background = new ThemeColor(light, red);
		ThemeColor edge = new ThemeColor(red, red);
		ThemeColor icon = new ThemeColor(gray, light);
		menu = new BendingMenuInfo(new MenuTheme(background, edge, icon), AvatarControl.KEY_FIREBENDING,
				AvatarGuiIds.GUI_RADIAL_MENU_FIRE, ACTION_LIGHT_FIRE, ACTION_FIRE_PUNCH,
				ACTION_FIREARC_THROW);
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
		EntityPlayer player = data.getPlayerEntity();
		World world = player.worldObj;
		FirebendingState fs = (FirebendingState) data.getBendingState(this);
		
		if (ability == ACTION_LIGHT_FIRE) {
			VectorI looking = ps.verifyClientLookAtBlock(-1, 5);
			EnumFacing side = ps.getLookAtSide();
			if (ps.isLookingAtBlock(-1, 5)) {
				VectorI setAt = new VectorI(looking.x(), looking.y(), looking.z());
				setAt.offset(side);
				if (world.getBlockState(setAt.toBlockPos()).getBlock() == Blocks.AIR) {
					world.setBlockState(setAt.toBlockPos(), Blocks.FIRE.getDefaultState());
				}
			}
		}
		if (ability == ACTION_FIRE_PUNCH) {
			// Vector look = VectorUtils.fromYawPitch(Math.toRadians(player.rotationYaw),
			// Math.toRadians(player.rotationPitch));
			// Vector motion = VectorUtils.times(look, 10);
			// EntityFlame flame = new EntityFlame(world, player.posX, player.posY + 1.6,
			// player.posZ,
			// motion.xCoord, motion.yCoord, motion.zCoord);
			//
			// world.spawnEntityInWorld(flame);
			
			Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
					Math.toRadians(player.rotationPitch));
			Vector lookPos = new Vector(player).plus(look.times(3));
			EntityFireArc fire = new EntityFireArc(world);
			fire.setPosition(lookPos.x(), lookPos.y(), lookPos.z());
			
			world.spawnEntityInWorld(fire);
			
			fs.setFireArc(fire);
			data.sendBendingState(fs);
			
		}
		
		if (ability == ACTION_FIREARC_THROW) {
			
			EntityFireArc fire = fs.getFireArc();
			if (fire != null) {
				Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
						Math.toRadians(player.rotationPitch));
				fire.addVelocity(look.times(15));
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
				Vector look = Vector.fromYawPitch(Math.toRadians(player.rotationYaw),
						Math.toRadians(player.rotationPitch));
				System.out.println("Eye pos is: " + Vector.getEyePos(player).y());
				Vector lookPos = Vector.getEyePos(player).plus(look.times(3));
				Vector motion = lookPos.minus(new Vector(fire));
				motion.normalize();
				motion.mul(.15);
				fire.moveEntity(motion.x(), motion.y(), motion.z());
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
