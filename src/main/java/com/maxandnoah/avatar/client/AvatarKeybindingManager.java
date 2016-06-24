package com.maxandnoah.avatar.client;

import java.util.HashMap;
import static com.maxandnoah.avatar.common.AvatarAction.*;
import static com.maxandnoah.avatar.common.AvatarKeybinding.*;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.maxandnoah.avatar.AvatarLog;
import com.maxandnoah.avatar.common.AvatarControl;
import com.maxandnoah.avatar.common.AvatarKeybinding;
import com.maxandnoah.avatar.common.IControlsHandler;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

@SideOnly(Side.CLIENT)
public class AvatarKeybindingManager implements IControlsHandler {
	
	private Map<String, KeyBinding> keybindings;
	
	public AvatarKeybindingManager() {
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
		String keyName = control.getName();
		KeyBinding kb = keybindings.get(keyName);
		if (kb == null) AvatarLog.warn("Key control '" + keyName + "' is undefined");
		return kb == null ? false : kb.isPressed();
	}

	@Override
	public int getKeyCode(AvatarKeybinding control) {
		String keyName = control.getName();
		KeyBinding kb = keybindings.get(keyName);
		if (kb == null) AvatarLog.warn("Key control '" + keyName + "' is undefined");
		return kb == null ? -1 : kb.getKeyCode();
	}

}
