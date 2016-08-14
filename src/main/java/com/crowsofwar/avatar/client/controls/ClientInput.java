package com.crowsofwar.avatar.client.controls;

import static com.crowsofwar.avatar.common.bending.BendingManager.BENDINGID_AIRBENDING;
import static com.crowsofwar.avatar.common.bending.BendingManager.BENDINGID_EARTHBENDING;
import static com.crowsofwar.avatar.common.bending.BendingManager.BENDINGID_FIREBENDING;
import static com.crowsofwar.avatar.common.bending.BendingManager.BENDINGID_WATERBENDING;
import static com.crowsofwar.avatar.common.bending.BendingManager.getBending;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_LEFT_CLICK_DOWN;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_MIDDLE_CLICK;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_MIDDLE_CLICK_DOWN;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK;
import static com.crowsofwar.avatar.common.controls.AvatarControl.CONTROL_RIGHT_CLICK_DOWN;
import static com.crowsofwar.avatar.common.controls.AvatarControl.NONE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.AvatarAbility;
import com.crowsofwar.avatar.common.bending.IBendingController;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.controls.IControlsHandler;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.network.packets.PacketSUseBendingController;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.RaytraceResult;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Large class that manages input on the client-side. After input is received, it is sent to the
 * server using packets.
 *
 */
@SideOnly(Side.CLIENT)
public class ClientInput implements IControlsHandler {
	
	private final Minecraft mc;
	private GameSettings gameSettings;
	private Map<String, KeyBinding> keybindings;
	private boolean mouseLeft, mouseRight, mouseMiddle;
	private boolean wasLeft, wasRight, wasMiddle;
	
	/**
	 * A list of all bending controllers which can be activated by keyboard
	 */
	private final List<IBendingController> keyboardBending;
	
	private boolean press;
	
	public ClientInput() {
		gameSettings = Minecraft.getMinecraft().gameSettings;
		mouseLeft = mouseRight = mouseMiddle = wasLeft = wasRight = wasMiddle = false;
		mc = Minecraft.getMinecraft();
		
		keybindings = new HashMap();
		
		keyboardBending = new ArrayList<>();
		addBendingButton(BENDINGID_EARTHBENDING, Keyboard.KEY_Z);
		addBendingButton(BENDINGID_FIREBENDING, Keyboard.KEY_X);
		addBendingButton(BENDINGID_WATERBENDING, Keyboard.KEY_C);
		addBendingButton(BENDINGID_AIRBENDING, Keyboard.KEY_F);
		
	}
	
	private void addBendingButton(int id, int keycode) {
		IBendingController controller = getBending(id);
		addKeybinding(controller.getRadialMenu().getKey(), keycode, "main");
		keyboardBending.add(controller);
	}
	
	private KeyBinding addKeybinding(AvatarControl control, int key, String cat) {
		KeyBinding kb = new KeyBinding("avatar." + control.getName(), key, "avatar.category." + cat);
		keybindings.put(control.getName(), kb);
		ClientRegistry.registerKeyBinding(kb);
		return kb;
		
	}
	
	@Override
	public boolean isControlPressed(AvatarControl control) {
		if (control == NONE) return false;
		
		if (control.isKeybinding()) {
			String keyName = control.getName();
			KeyBinding kb = keybindings.get(keyName);
			if (kb == null) AvatarLog.warn("Avatar key '" + keyName + "' is undefined");
			return kb == null ? false : kb.isPressed();
		} else {
			if (control == CONTROL_LEFT_CLICK) return mouseLeft;
			if (control == CONTROL_RIGHT_CLICK) return mouseRight;
			if (control == CONTROL_MIDDLE_CLICK) return mouseMiddle;
			if (control == CONTROL_LEFT_CLICK_DOWN) return mouseLeft && !wasLeft;
			if (control == CONTROL_RIGHT_CLICK_DOWN) return mouseRight && !wasRight;
			if (control == CONTROL_MIDDLE_CLICK_DOWN) return mouseMiddle && !wasMiddle;
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
		
		for (IBendingController controller : keyboardBending) {
			openBendingMenu(controller);
		}
		
	}
	
	/**
	 * Tries to open the specified bending controller if its key is pressed.
	 */
	private void openBendingMenu(IBendingController controller) {
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(mc.thePlayer,
				"Error while getting player data for open-bending-menu checks");
		BendingMenuInfo menu = controller.getRadialMenu();
		if (data.hasBending(controller.getID()) && isControlPressed(menu.getKey())) {
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			AvatarMod.network.sendToServer(new PacketSUseBendingController(controller.getID()));
			player.openGui(AvatarMod.instance, menu.getGuiId(), player.worldObj, 0, 0, 0);
		}
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent e) {
		wasLeft = mouseLeft;
		wasRight = mouseRight;
		wasMiddle = mouseMiddle;
		
		if (mc.inGameHasFocus) {
			mouseLeft = Mouse.isButtonDown(0);
			mouseRight = Mouse.isButtonDown(1);
			mouseMiddle = Mouse.isButtonDown(2);
		} else {
			mouseLeft = mouseRight = mouseMiddle = false;
		}
		
		EntityPlayer player = mc.thePlayer;
		
		if (player != null && true) {
			// Send any input to the server
			// AvatarPlayerData data = AvatarPlayerDataFetcherClient.instance.getDataPerformance(
			// Minecraft.getMinecraft().thePlayer);
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetchPerformance(player);
			
			if (data != null && data.getActiveBendingController() != null) {
				List<AvatarControl> pressed = getAllPressed();
				for (AvatarControl control : pressed) {
					AvatarAbility ability = data.getActiveBendingController().getAbility(data, control);
					if (ability != AvatarAbility.NONE) {
						RaytraceResult raytrace = ability.needsRaytrace()
								? Raytrace.getTargetBlock(player, ability.getRaytraceDistance(), ability.isRaycastLiquids()) : null;
						AvatarMod.network.sendToServer(new PacketSUseAbility(ability, raytrace != null ? raytrace.getPos() : null,
								raytrace != null ? raytrace.getDirection() : null));
					}
				}
			}
		}
		
	}
	
	@Override
	public List<AvatarControl> getAllPressed() {
		List<AvatarControl> list = new ArrayList<AvatarControl>();
		
		for (int i = 0; i < AvatarControl.values().length; i++) {
			AvatarControl control = AvatarControl.values()[i];
			if (isControlPressed(control)) list.add(control);
		}
		
		return list;
	}
	
}
