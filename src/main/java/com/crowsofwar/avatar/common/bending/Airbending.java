package com.crowsofwar.avatar.common.bending;

import static com.crowsofwar.avatar.common.AvatarAbility.ACTION_AIRBEND_TEST;
import static com.crowsofwar.avatar.common.AvatarAbility.ACTION_AIR_GUST;
import static com.crowsofwar.avatar.common.AvatarAbility.NONE;
import static com.crowsofwar.avatar.common.util.VectorUtils.fromYawPitch;
import static com.crowsofwar.avatar.common.util.VectorUtils.getEyePos;
import static com.crowsofwar.avatar.common.util.VectorUtils.times;

import java.awt.Color;

import com.crowsofwar.avatar.common.AvatarAbility;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.PlayerState;
import com.crowsofwar.avatar.common.entity.EntityAirGust;
import com.crowsofwar.avatar.common.gui.AvatarGuiIds;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.gui.MenuTheme;
import com.crowsofwar.avatar.common.gui.MenuTheme.ThemeColor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Airbending implements IBendingController {
	
	private BendingMenuInfo menu;
	
	public Airbending() {
		Color light = new Color(220, 220, 220);
		Color dark = new Color(172, 172, 172);
		Color iconClr = new Color(196, 109, 0);
		ThemeColor background = new ThemeColor(light, dark);
		ThemeColor edge = new ThemeColor(dark, dark);
		ThemeColor icon = new ThemeColor(iconClr, iconClr);
		MenuTheme theme = new MenuTheme(background, edge, icon);
		this.menu = new BendingMenuInfo(theme, AvatarControl.KEY_AIRBENDING, AvatarGuiIds.GUI_RADIAL_MENU_AIR,
				ACTION_AIR_GUST);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		
	}
	
	@Override
	public int getID() {
		return BendingManager.BENDINGID_AIRBENDING;
	}
	
	@Override
	public void onAbility(AvatarAbility ability, AvatarPlayerData data) {
		
		PlayerState state = data.getState();
		EntityPlayer player = state.getPlayerEntity();
		World world = player.worldObj;
		
		if (ability == ACTION_AIRBEND_TEST) {
			
			player.addVelocity(0, 1, 0);
			
			// Note: This always is called on server-side
			((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
			
		}
		
		if (ability == ACTION_AIR_GUST) {
			
			Vec3 look = fromYawPitch(Math.toRadians(player.rotationYaw), Math.toRadians(player.rotationPitch));
			Vec3 pos = getEyePos(player);
			
			EntityAirGust gust = new EntityAirGust(world);
			gust.setVelocity(times(look, 10));
			gust.setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
			gust.setOwner(player);
			
			world.spawnEntityInWorld(gust);
			
		}
		
	}
	
	@Override
	public IBendingState createState(AvatarPlayerData data) {
		return new AirbendingState();
	}
	
	@Override
	public void onUpdate(AvatarPlayerData data) {
		
	}
	
	@Override
	public AvatarAbility getAbility(AvatarPlayerData data, AvatarControl input) {
		return NONE;
	}
	
	@Override
	public BendingMenuInfo getRadialMenu() {
		return menu;
	}
	
	@Override
	public String getControllerName() {
		return "airbending";
	}
	
}
