package com.maxandnoah.avatar.client.controls;

import static com.maxandnoah.avatar.client.controls.AvatarKeybinding.*;
import static com.maxandnoah.avatar.client.controls.AvatarOtherControl.*;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.maxandnoah.avatar.AvatarLog;
import com.maxandnoah.avatar.AvatarMod;
import com.maxandnoah.avatar.common.AvatarControl;
import com.maxandnoah.avatar.common.IControlsHandler;
import com.maxandnoah.avatar.common.gui.AvatarGuiIds;
import com.maxandnoah.avatar.common.network.packets.PacketSCheatEarthbending;
import com.maxandnoah.avatar.common.network.packets.PacketSCheckBendingList;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crowsofwar.gorecore.util.GoreCorePlayerUUIDs;
import crowsofwar.gorecore.util.GoreCorePlayerUUIDs.GetUUIDResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Large class that manages input on the client-side.
 * After input is received, it is sent to the server using packets.
 *
 */
@SideOnly(Side.CLIENT)
public class ClientInput implements IControlsHandler {
	
	private GameSettings gameSettings;
	private Map<String, KeyBinding> keybindings;
	private boolean mouseLeft, mouseRight, mouseMiddle;
	
	private boolean press;
	
	public ClientInput() {
		gameSettings = Minecraft.getMinecraft().gameSettings;
		mouseLeft = mouseRight = mouseMiddle = false;
		
		keybindings = new HashMap();
		
		addKeybinding(KEY_BENDING_LIST, Keyboard.KEY_Z, "main");
		addKeybinding(KEY_CHEAT_EARTHBENDING, Keyboard.KEY_X, "main");
		addKeybinding(KEY_RADIAL_MENU, Keyboard.KEY_LMENU, "main");
		
	}
	
	private KeyBinding addKeybinding(AvatarControl control, int key, String cat) {
		KeyBinding kb = new KeyBinding("avatar." + control.getName(), key,
				"avatar.category." + cat);
		keybindings.put(control.getName(), kb);
		ClientRegistry.registerKeyBinding(kb);
		return kb;
		
	}
	
	@Override
	public boolean isControlPressed(AvatarControl control) {
		
		if (control.isKeybinding()) {
			String keyName = control.getName();
			KeyBinding kb = keybindings.get(keyName);
			if (kb == null) AvatarLog.warn("Avatar control '" + keyName + "' is undefined");
			return kb == null ? false : kb.isPressed();
		} else {
			if (control == CONTROL_LEFT_CLICK) return mouseLeft;
			if (control == CONTROL_RIGHT_CLICK) return mouseRight;
			if (control == CONTROL_MIDDLE_CLICK) return mouseMiddle;
			AvatarLog.warn("ClientInput- Unknown control: " + control);
			return false;
		}
		
	}

	@Override
	public int getKeyCode(AvatarControl control) {
		String keyName = control.getName();
		KeyBinding kb = keybindings.get(keyName);
		if (kb == null) AvatarLog.warn("Key control '" + keyName + "' is undefined");
		return kb == null ? -1 : kb.getKeyCode();
	}
	
	@SubscribeEvent
	public void onKeyPressed(InputEvent.KeyInputEvent e) {
		if (isControlPressed(KEY_BENDING_LIST)) {
			GetUUIDResult result = GoreCorePlayerUUIDs.getUUID(Minecraft.getMinecraft().thePlayer.getCommandSenderName());
			if (result.isResultSuccessful()) {
				AvatarMod.network.sendToServer(new PacketSCheckBendingList(result.getUUID()));
			}
		}
		
		if (isControlPressed(KEY_CHEAT_EARTHBENDING)) {
			System.out.println("Sending cheat-earthbending packet to server");
			AvatarMod.network.sendToServer(new PacketSCheatEarthbending());
		}
		
		if (isControlPressed(KEY_RADIAL_MENU)) {
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			player.openGui(AvatarMod.instance, AvatarGuiIds.GUI_RADIAL_MENU, player.worldObj, 0, 0, 0);
		}
		
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent e) {
		mouseLeft = Mouse.isButtonDown(0);
		mouseRight = Mouse.isButtonDown(1);
		mouseMiddle = Mouse.isButtonDown(2);
	}
	
}
