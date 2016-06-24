package com.maxandnoah.avatar.client;

import com.maxandnoah.avatar.AvatarMod;
import com.maxandnoah.avatar.common.AvatarKeybinding;
import com.maxandnoah.avatar.common.IControlsHandler;
import com.maxandnoah.avatar.common.gui.AvatarGuiIds;
import com.maxandnoah.avatar.common.network.packets.PacketSCheatEarthbending;
import com.maxandnoah.avatar.common.network.packets.PacketSCheckBendingList;
import com.maxandnoah.avatar.common.network.packets.PacketSKeypress;
import com.maxandnoah.avatar.common.network.packets.PacketSToggleBending;
import com.maxandnoah.avatar.common.util.BlockPos;
import com.maxandnoah.avatar.common.util.Raytrace;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crowsofwar.gorecore.util.GoreCorePlayerUUIDs;
import crowsofwar.gorecore.util.GoreCorePlayerUUIDs.GetUUIDResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

import static com.maxandnoah.avatar.common.AvatarKeybinding.*;
import static com.maxandnoah.avatar.common.util.VectorUtils.add;
import static com.maxandnoah.avatar.common.util.VectorUtils.copy;
import static com.maxandnoah.avatar.common.util.VectorUtils.mult;
import static com.maxandnoah.avatar.common.util.VectorUtils.raytrace;
import static java.lang.Math.toRadians;

/**
 * Large class that manages input on the client-side.
 * After input is received, it is sent to the server using packets.
 *
 */
@SideOnly(Side.CLIENT)
public class ClientInput {
	
	private IControlsHandler keyHandler;
	private GameSettings gameSettings;
	
	private boolean press;
	
	public ClientInput() {
		keyHandler = AvatarMod.proxy.getKeyHandler();
		gameSettings = Minecraft.getMinecraft().gameSettings;
	}
	
	@SubscribeEvent
	public void onTick(WorldTickEvent e) {
		if (e.side == Side.SERVER) return;
		
		
	}
	
	@SubscribeEvent
	public void onKeyPressed(InputEvent.KeyInputEvent e) {
		if (keyHandler.isControlPressed(KEY_BENDING_LIST)) {
			GetUUIDResult result = GoreCorePlayerUUIDs.getUUID(Minecraft.getMinecraft().thePlayer.getCommandSenderName());
			if (result.isResultSuccessful()) {
				AvatarMod.network.sendToServer(new PacketSCheckBendingList(result.getUUID()));
			}
		}
		
		if (keyHandler.isControlPressed(KEY_CHEAT_EARTHBENDING)) {
			System.out.println("Sending cheat-earthbending packet to server");
			AvatarMod.network.sendToServer(new PacketSCheatEarthbending());
		}
		
//		if (keyHandler.isKeyPressed(CONTROL_TOGGLE_BENDING)) {
//			AvatarMod.network.sendToServer(new PacketSToggleBending());
//		}
		
		if (keyHandler.isControlPressed(KEY_RADIAL_MENU)) {
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			player.openGui(AvatarMod.instance, AvatarGuiIds.GUI_RADIAL_MENU, player.worldObj, 0, 0, 0);
		}
		
	}
	
	private void checkKeypress(AvatarKeybinding control) {
		String name = control.getName();
		if (keyHandler.isControlPressed(control)) {
			AvatarMod.network.sendToServer(new PacketSKeypress(name, Raytrace.getTargetBlock(
					Minecraft.getMinecraft().thePlayer, -1)));
		}
	}
	
//	@SubscribeEvent
//	public void onMouseInput(InputEvent.MouseInputEvent e) {
//		// http://legacy.lwjgl.org/javadoc/org/lwjgl/input/Mouse.html
//		//System.out.println(Mouse.getEventButton());
//	}
	
}
