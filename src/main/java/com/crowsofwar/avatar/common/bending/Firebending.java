package com.crowsofwar.avatar.common.bending;

import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK_DOWN;

import java.awt.Color;

import com.crowsofwar.avatar.common.bending.ability.AbilityFireArc;
import com.crowsofwar.avatar.common.bending.ability.AbilityFireThrow;
import com.crowsofwar.avatar.common.bending.ability.AbilityLightFire;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
import com.crowsofwar.avatar.common.entity.EntityFireArc;
import com.crowsofwar.avatar.common.gui.AvatarGuiIds;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class Firebending extends BendingController {
	
	private final BendingMenuInfo menu;
	private final BendingAbility<FirebendingState> abilityLightFire, abilityFireArc, abilityFireThrow;
	
	public Firebending() {
		
		this.abilityLightFire = new AbilityLightFire(this);
		this.abilityFireArc = new AbilityFireArc(this);
		this.abilityFireThrow = new AbilityFireThrow(this);
		
		Color light = new Color(244, 240, 187);
		Color red = new Color(173, 64, 31);
		Color gray = new Color(40, 40, 40);
		ThemeColor background = new ThemeColor(light, red);
		ThemeColor edge = new ThemeColor(red, red);
		ThemeColor icon = new ThemeColor(gray, light);
		menu = new BendingMenuInfo(new MenuTheme(background, edge, icon), AvatarControl.KEY_FIREBENDING,
				AvatarGuiIds.GUI_RADIAL_MENU_FIRE, abilityLightFire, abilityFireArc, abilityFireThrow);
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
	public BendingAbility<FirebendingState> getAbility(AvatarPlayerData data, AvatarControl input) {
		if (input == CONTROL_LEFT_CLICK_DOWN) {
			FirebendingState state = (FirebendingState) data.getBendingState(this);
			if (state != null && state.isManipulatingFire()) return abilityFireThrow;
		}
		
		return null;
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
