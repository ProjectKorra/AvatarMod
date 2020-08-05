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

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.client.gui.AvatarUiRenderer;
import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingStyle;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.controls.IControlsHandler;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.StatusControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.event.BendingCycleEvent;
import com.crowsofwar.avatar.common.event.BendingUseEvent;
import com.crowsofwar.avatar.common.network.packets.*;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Result;
import com.crowsofwar.gorecore.format.FormattedMessageProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.*;

import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_DONT_HAVE_BENDING;
import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static com.crowsofwar.avatar.common.controls.AvatarControl.*;

/**
 * Large class that manages input on the client-side. After input is received,
 * it is sent to the server using packets.
 */
@SideOnly(Side.CLIENT)
public class ClientInput implements IControlsHandler {

	private final Minecraft mc;
	/**
	 * A list of all bending controllers which can be activated by keyboard
	 */
	private final List<BendingStyle> keyboardBending;
	private final boolean[] wasAbilityDown;
	private GameSettings gameSettings;
	private Map<String, KeyBinding> keybindings;
	private boolean mouseLeft, mouseRight, mouseMiddle;
	private boolean wasLeft, wasRight, wasMiddle;
	private boolean press;

	public ClientInput() {
		gameSettings = Minecraft.getMinecraft().gameSettings;
		mouseLeft = mouseRight = mouseMiddle = wasLeft = wasRight = wasMiddle = false;
		mc = Minecraft.getMinecraft();

		keybindings = new HashMap<>();

		keyboardBending = new ArrayList<>();
		addKeybinding("Bend", Keyboard.KEY_LMENU, "main");
		addKeybinding("BendingCycleLeft", Keyboard.KEY_Z, "main");
		addKeybinding("BendingCycleRight", Keyboard.KEY_V, "main");
		addKeybinding("Skills", Keyboard.KEY_K, "main");
		addKeybinding("Switch", Keyboard.KEY_R, "main");
		addKeybinding("TransferBison", Keyboard.KEY_O, "main");

		wasAbilityDown = new boolean[Abilities.all().size()];

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
		if (control == CONTROL_LEFT_CLICK_UP) return !mouseLeft && wasLeft;
		if (control == CONTROL_RIGHT_CLICK_UP) return !mouseRight && wasRight;
		if (control == CONTROL_MIDDLE_CLICK_UP) return !mouseMiddle && wasMiddle;
		if (control == CONTROL_SHIFT) return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		AvatarLog.warn(AvatarLog.WarningType.INVALID_CODE, "ClientInput- Unknown control: " + control);
		return false;

	}

	@Override
	public boolean isControlDown(AvatarControl control) {

		if (control == CONTROL_LEFT_CLICK) return mouseLeft;
		if (control == CONTROL_RIGHT_CLICK) return mouseRight;
		if (control == CONTROL_MIDDLE_CLICK) return mouseMiddle;
		if (control == CONTROL_LEFT_CLICK_DOWN) return mouseLeft;
		if (control == CONTROL_RIGHT_CLICK_DOWN) return mouseRight;
		if (control == CONTROL_MIDDLE_CLICK_DOWN) return mouseMiddle;
		if (control == CONTROL_LEFT_CLICK_UP) return !mouseLeft;
		if (control == CONTROL_RIGHT_CLICK_UP) return !mouseRight;
		if (control == CONTROL_MIDDLE_CLICK_UP) return !mouseMiddle;
		if (control == CONTROL_SHIFT) return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		AvatarLog.warn(AvatarLog.WarningType.INVALID_CODE, "ClientInput- Unknown control: " + control);
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
			BendingData data = BendingData.getFromEntity(mc.player);
			if (data != null) {
				BendingStyle active = data.getActiveBending();
				if (active == null) {
					AvatarMod.network.sendToServer(new PacketSOpenUnlockGui());
				} else {
					AvatarMod.network.sendToServer(new PacketSSkillsMenu(active.getId()));
				}
			}
			if (AvatarControl.KEY_TRANSFER_BISON.isPressed()) {
				AvatarMod.network.sendToServer(new PacketSConfirmTransfer());
			}
		}

	}

	private boolean isAbilityPressed(Ability ability) {
		Integer key = CLIENT_CONFIG.keymappings.get(ability);
		if (key != null) {
			if (key < 0 && Mouse.isButtonDown(key + 100)) return true;
			return key >= 0 && Keyboard.isKeyDown(key);
		}
		return false;
	}

	/**
	 * Tries to open the specified bending controller if its key is pressed.
	 */
	private void tryOpenBendingMenu() {
		BendingData data = BendingData.getFromEntity(mc.player);
		if (AvatarControl.KEY_USE_BENDING.isPressed() && !AvatarUiRenderer.hasBendingGui()) {
			if (data != null && data.getActiveBending() != null) {
				if (!MinecraftForge.EVENT_BUS.post(new BendingUseEvent(mc.player, data.getActiveBending())))
					AvatarUiRenderer.openBendingGui(data.getActiveBendingId());
			} else {
				if (CLIENT_CONFIG.displayGetBendingMessage) {
					String message = I18n.format(MSG_DONT_HAVE_BENDING.getTranslateKey());
					message = FormattedMessageProcessor.formatText(MSG_DONT_HAVE_BENDING, message, mc.player.getName());
					mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));

				}
			}

		}

	}

	private void tryCycleBending() {
		if (AvatarControl.KEY_BENDING_CYCLE_LEFT.isPressed() && !AvatarUiRenderer.hasBendingGui()) {
			if (!MinecraftForge.EVENT_BUS.post(new BendingCycleEvent(mc.player, false)))
				AvatarMod.network.sendToServer(new PacketSCycleBending(false));
		}
		if (AvatarControl.KEY_BENDING_CYCLE_RIGHT.isPressed() && !AvatarUiRenderer.hasBendingGui()) {
			if (!MinecraftForge.EVENT_BUS.post(new BendingCycleEvent(mc.player, true)))
				AvatarMod.network.sendToServer(new PacketSCycleBending(true));
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

		EntityPlayer player = mc.player;

		if (player != null && player.world != null) {
			// Send any input to the server
			if (Objects.requireNonNull(Bender.get(player)).getInfo().getId() != null) {
				BendingData data = BendingData.getFromEntity(player);
				if (data != null) {

					if (mc.inGameHasFocus) {
						Collection<AvatarControl> pressed = getAllPressed();
						Collection<StatusControl> statusControls = data.getAllStatusControls();
						for (StatusControl sc : statusControls) {
							if (pressed.contains(sc.getSubscribedControl())) {
								Result raytrace = Raytrace.getTargetBlock(player, sc.getRaytrace());
								//Called client side
								if (sc.execute(new BendingContext(data, player, raytrace)))
									data.removeStatusControl(sc);
								//Then server side
								AvatarMod.network.sendToServer(new PacketSUseStatusControl(sc, raytrace));


							}
						}
					}

					boolean isSwitchPathKeyDown = AvatarControl.KEY_SWITCH.isDown();

					List<Ability> allAbilities = Abilities.all();
					for (int i = 0; i < allAbilities.size(); i++) {
						Ability ability = allAbilities.get(i);
						boolean down = isAbilityPressed(ability);

						if (!CLIENT_CONFIG.conflicts.containsKey(ability)) CLIENT_CONFIG.conflicts.put(ability, false);
						boolean conflict = CLIENT_CONFIG.conflicts.get(ability);

						if (!conflict && mc.inGameHasFocus && mc.currentScreen == null && down && !wasAbilityDown[i]) {
							Raytrace.Result raytrace = Raytrace.getTargetBlock(mc.player, ability.getRaytrace());
							if (data.hasBendingId(ability.getBendingId()) && player.isCreative() || data.canUse(ability)) {
								//Client side
								Bender.get(player).executeAbility(ability, raytrace, isSwitchPathKeyDown);
								//Server side
								AvatarMod.network.sendToServer(new PacketSUseAbility(ability, raytrace, isSwitchPathKeyDown));
							}
						}
						wasAbilityDown[i] = down;
					}

				}
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
