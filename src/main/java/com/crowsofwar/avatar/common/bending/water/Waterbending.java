package com.crowsofwar.avatar.common.bending.water;

import static com.crowsofwar.avatar.common.controls.AvatarControl.KEY_WATERBENDING;
import static com.crowsofwar.avatar.common.gui.AvatarGuiIds.GUI_RADIAL_MENU_WATER;

import java.awt.Color;

import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.IBendingState;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
import com.crowsofwar.avatar.common.entity.EntityWaterArc;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class Waterbending extends BendingController {
	
	private BendingMenuInfo menu;
	private final BendingAbility<WaterbendingState> abilityWaterArc, abilityWaterThrow;
	
	public Waterbending() {
		this.abilityWaterArc = new AbilityWaterArc(this);
		this.abilityWaterThrow = new AbilityWaterThrow(this);
		
		Color base = new Color(228, 255, 225);
		Color edge = new Color(60, 188, 145);
		Color icon = new Color(129, 149, 148);
		ThemeColor background = new ThemeColor(base, edge);
		menu = new BendingMenuInfo(
				new MenuTheme(new ThemeColor(base, edge), new ThemeColor(edge, edge),
						new ThemeColor(icon, base)),
				KEY_WATERBENDING, GUI_RADIAL_MENU_WATER, abilityWaterArc);
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
	public BendingAbility<WaterbendingState> getAbility(AvatarPlayerData data, AvatarControl input) {
		
		if (input == AvatarControl.CONTROL_LEFT_CLICK_DOWN) return abilityWaterThrow;
		
		return null;
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
