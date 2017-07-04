/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.client;

import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_DONT_HAVE_BENDING;
import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.AvatarUiRenderer;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.controls.IControlsHandler;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.network.packets.PacketSConfirmTransfer;
import com.crowsofwar.avatar.common.network.packets.PacketSCycleBending;
import com.crowsofwar.avatar.common.network.packets.PacketSOpenUnlockGui;
import com.crowsofwar.avatar.common.network.packets.PacketSSkillsMenu;
import com.crowsofwar.avatar.common.network.packets.PacketSUseAbility;
import com.crowsofwar.avatar.common.network.packets.PacketSUseStatusControl;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.chat.ChatSender;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Large class that manages input on the client-side. After input is received,
 * it is sent to the server using packets.
 *
 */
@SideOnly(Side.CLIENT)
public class ClientInput implements IControlsHandler {
	
	private final Minecraft mc;
	private GameSettings gameSettings;
	private Map<String, KeyBinding> keybindings;
	private boolean mouseLeft, mouseRight, mouseMiddle, space;
	private boolean wasLeft, wasRight, wasMiddle, wasSpace;
	
	/**
	 * A list of all bending controllers which can be activated by keyboard
	 */
	private final List<BendingStyle> keyboardBending;
	
	private final boolean[] wasAbilityDown;
	
	private boolean press;
	
	public ClientInput() {
		gameSettings = Minecraft.getMinecraft().gameSettings;
		mouseLeft = mouseRight = mouseMiddle = wasLeft = wasRight = wasMiddle = false;
		mc = Minecraft.getMinecraft();
		
		keybindings = new HashMap();
		
		keyboardBending = new ArrayList<>();
		addKeybinding("Bend", Keyboard.KEY_X, "main");
		addKeybinding("BendingCycleLeft", Keyboard.KEY_Z, "main");
		addKeybinding("BendingCycleRight", Keyboard.KEY_C, "main");
		addKeybinding("Skills", Keyboard.KEY_K, "main");
		addKeybinding("TransferBison", Keyboard.KEY_L, "main");
		
		this.wasAbilityDown = new boolean[BendingManager.allAbilities().size()];
		
	}
	
	private KeyBinding addKeybinding(String name, int key, String cat) {
		KeyBinding kb = new KeyBinding("avatar." + name, key, "avatar.category." + cat);
		keybindings.put(name, kb);
		ClientRegistry.registerKeyBinding(kb);
		return kb;
		
	}
	
	@Override
	public boolean isControlPressed(AvatarControl control) {
		
		if (control == CONTROL_LEFT_CLICK) return mouseLeft;
		if (control == CONTROL_RIGHT_CLICK) return mouseRight;
		if (control == CONTROL_MIDDLE_CLICK) return mouseMiddle;
		if (control == CONTROL_LEFT_CLICK_DOWN) return mouseLeft && !wasLeft;
		if (control == CONTROL_RIGHT_CLICK_DOWN) return mouseRight && !wasRight;
		if (control == CONTROL_MIDDLE_CLICK_DOWN) return mouseMiddle && !wasMiddle;
		if (control == CONTROL_SPACE) return space;
		if (control == CONTROL_SPACE_DOWN) return space && !wasSpace;
		if (control == CONTROL_LEFT_CLICK_UP) return !mouseLeft && wasLeft;
		if (control == CONTROL_RIGHT_CLICK_UP) return !mouseRight && wasRight;
		if (control == CONTROL_MIDDLE_CLICK_UP) return !mouseMiddle && wasMiddle;
		if (control == CONTROL_SHIFT) return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		AvatarLog.warn("ClientInput- Unknown control: " + control);
		return false;
		
	}
	
	@Override
	public int getKeyCode(AvatarControl control) {
		String keyName = control.getName().substring("avatar.".length());
		KeyBinding kb = keybindings.get(keyName);
		if (kb == null) AvatarLog.warn("Key control '" + keyName + "' is undefined");
		return kb == null ? -1 : kb.getKeyCode();
	}
	
	@Override
	public String getDisplayName(AvatarControl control) {
		if (control.isKeybinding()) {
			KeyBinding kb = keybindings.get(control.getName().substring("avatar.".length()));
			return kb == null ? null : kb.getDisplayName();
		} else {
			return null;
		}
	}
	
	@SubscribeEvent
	public void onKeyPressed(InputEvent.KeyInputEvent e) {
		
		tryOpenBendingMenu();
		tryCycleBending();
		
		if (AvatarControl.KEY_SKILLS.isPressed()) {
			BendingData data = AvatarPlayerData.fetcher().fetch(mc.thePlayer);
			List<BendingStyle> controllers = data.getAllBending();
			if (controllers.isEmpty()) {
				AvatarMod.network.sendToServer(new PacketSOpenUnlockGui());
			} else {
				AvatarMod.network.sendToServer(new PacketSSkillsMenu(controllers.get(0).getId()));
			}
		}
		if (AvatarControl.KEY_TRANSFER_BISON.isPressed()) {
			AvatarMod.network.sendToServer(new PacketSConfirmTransfer());
		}
		
	}
	
	private boolean isAbilityPressed(Ability ability) {
		Integer key = CLIENT_CONFIG.keymappings.get(ability);
		if (key != null) {
			if (key < 0 && Mouse.isButtonDown(key + 100)) return true;
			if (key >= 0 && Keyboard.isKeyDown(key)) return true;
		}
		return false;
	}
	
	/**
	 * Tries to open the specified bending controller if its key is pressed.
	 */
	private void tryOpenBendingMenu() {
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(mc.thePlayer);
		if (AvatarControl.KEY_USE_BENDING.isPressed() && !AvatarUiRenderer.hasBendingGui()) {
			
			if (data.getActiveBending() != null) {
				AvatarUiRenderer.openBendingGui(data.getActiveBendingId());
			} else {
				
				String message = I18n.format(MSG_DONT_HAVE_BENDING.getTranslateKey());
				message = ChatSender.instance.processText(message, MSG_DONT_HAVE_BENDING,
						mc.thePlayer.getName());
				mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
				
			}
			
		}
		
	}
	
	private void tryCycleBending() {
		AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(mc.thePlayer);
		if (AvatarControl.KEY_BENDING_CYCLE_LEFT.isPressed() && !AvatarUiRenderer.hasBendingGui()) {
			AvatarMod.network.sendToServer(new PacketSCycleBending(false));
		}
		if (AvatarControl.KEY_BENDING_CYCLE_RIGHT.isPressed() && !AvatarUiRenderer.hasBendingGui()) {
			AvatarMod.network.sendToServer(new PacketSCycleBending(true));
		}
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent e) {
		wasLeft = mouseLeft;
		wasRight = mouseRight;
		wasMiddle = mouseMiddle;
		wasSpace = space;
		
		if (mc.inGameHasFocus) {
			mouseLeft = Mouse.isButtonDown(0);
			mouseRight = Mouse.isButtonDown(1);
			mouseMiddle = Mouse.isButtonDown(2);
		} else {
			mouseLeft = mouseRight = mouseMiddle = false;
		}
		
		space = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		
		EntityPlayer player = mc.thePlayer;
		
		if (player != null && player.worldObj != null) {
			// Send any input to the server
			AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
			
			if (data != null) {
				
				if (mc.inGameHasFocus) {
					Collection<AvatarControl> pressed = getAllPressed();
					Collection<StatusControl> statusControls = data.getAllStatusControls();
					
					Iterator<StatusControl> sci = statusControls.iterator();
					while (sci.hasNext()) {
						StatusControl sc = sci.next();
						if (pressed.contains(sc.getSubscribedControl())) {
							Raytrace.Result raytrace = Raytrace.getTargetBlock(player, sc.getRaytrace());
							
							AvatarMod.network.sendToServer(new PacketSUseStatusControl(sc, raytrace));
						}
					}
				}
				
			}
			
			List<Ability> allAbilities = BendingManager.allAbilities();
			for (int i = 0; i < allAbilities.size(); i++) {
				Ability ability = allAbilities.get(i);
				boolean down = isAbilityPressed(ability);
				
				if (!CLIENT_CONFIG.conflicts.containsKey(ability))
					CLIENT_CONFIG.conflicts.put(ability, false);
				boolean conflict = CLIENT_CONFIG.conflicts.get(ability);
				
				if (!conflict && mc.inGameHasFocus && mc.currentScreen == null && down
						&& !wasAbilityDown[i]) {
					Raytrace.Result raytrace = Raytrace.getTargetBlock(mc.thePlayer, ability.getRaytrace());
					AvatarMod.network.sendToServer(new PacketSUseAbility(ability, raytrace));
				}
				wasAbilityDown[i] = down;
			}
			
		}
		
	}
	
	@Override
	public List<AvatarControl> getAllPressed() {
		List<AvatarControl> list = new ArrayList<>();
		
		for (AvatarControl control : AvatarControl.ALL_CONTROLS) {
			if (control.isPressed()) {
				list.add(control);
			}
		}
		
		return list;
	}
	
}
