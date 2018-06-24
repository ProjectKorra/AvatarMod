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

package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.bending.air.*;
import com.crowsofwar.avatar.common.bending.earth.*;
import com.crowsofwar.avatar.common.bending.fire.*;
import com.crowsofwar.avatar.common.bending.ice.StatCtrlShieldShatter;
import com.crowsofwar.avatar.common.bending.lightning.StatCtrlThrowLightningSpear;
import com.crowsofwar.avatar.common.bending.sand.StatCtrlSandstormRedirect;
import com.crowsofwar.avatar.common.bending.water.*;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.util.Raytrace;

import java.util.*;

/**
 * Describes a temporary effect where a callback listener is added to a control
 * event. The listener then will perform certain actions associated with that
 * control.
 * <p>
 * For example, the player receives a place-block Status Control, which
 * subscribes to right-click. The status control receives a callback whenever
 * the player uses the right-click control. Then, the status control is removed.
 * <p>
 * Status controls are stored in player-data, but are also sent to the client
 * via packets, which render over the crosshair.
 *
 * @author CrowsOfWar
 */
public abstract class StatusControl {

	// @formatter:off
	public static final StatusControl
			AIR_JUMP = new StatCtrlAirJump(),
			FIRE_JUMP = new StatCtrlFireJump(),
			PLACE_BLOCK = new StatCtrlPlaceBlock(),
			THROW_BLOCK = new StatCtrlThrowBlock(),
			THROW_WATER = new StatCtrlThrowWater(),
			START_FLAMETHROW = new StatCtrlSetFlamethrowing(true),
			STOP_FLAMETHROW = new StatCtrlSetFlamethrowing(false),
			THROW_FIRE = new StatCtrlThrowFire(),
			THROW_BUBBLE = new StatCtrlThrowBubble(),
			SKATING_JUMP = new StatCtrlSkateJump(),
			SKATING_START = new StatCtrlSkateStart(),
			THROW_FIREBALL = new StatCtrlThrowFireball(),
			THROW_CLOUDBURST = new StatCtrlThrowCloudBall(),
			THROW_LIGHTNINSPEAR = new StatCtrlThrowLightningSpear(),
			BUBBLE_EXPAND = new StatCtrlBubbleExpand(),
			BUBBLE_CONTRACT = new StatCtrlBubbleContract(),
			SHIELD_SHATTER = new StatCtrlShieldShatter(),
			DROP_WALL = new StatCtrlDropWall(),
			SANDSTORM_REDIRECT = new StatCtrlSandstormRedirect();
	// @formatter:on

	private static int nextId = 0;
	private static List<StatusControl> allControls;

	private final int texture;
	private final AvatarControl control;
	private final CrosshairPosition position;
	private final int id;
	private Raytrace.Info raytrace;

	public StatusControl(int texture, AvatarControl subscribeTo, CrosshairPosition position) {

		if (allControls == null) allControls = new ArrayList<>();

		this.texture = texture;
		control = subscribeTo;
		raytrace = new Raytrace.Info();
		this.position = position;
		id = ++nextId;
		allControls.add(this);

	}

	public static StatusControl lookup(int id) {
		id--;
		return id >= 0 && id < allControls.size() ? allControls.get(id) : null;
	}

	/**
	 * Require that a raytrace be cast client-side, which is sent to the server.
	 * It is then accessible in {@link #execute(BendingContext)}.
	 *
	 * @param range          Range to raytrace. -1 for player reach
	 * @param raycastLiquids Whether to keep going when hit liquids
	 */
	protected void requireRaytrace(int range, boolean raycastLiquids) {
		raytrace = new Raytrace.Info(range, raycastLiquids);
	}

	/**
	 * Execute this status control in the given context. Only called
	 * server-side.
	 *
	 * @param ctx Information for status control
	 * @return Whether to remove it
	 */
	public abstract boolean execute(BendingContext ctx);

	public int id() {
		return id;
	}

	public AvatarControl getSubscribedControl() {
		return control;
	}

	public Raytrace.Info getRaytrace() {
		return raytrace;
	}

	public int getTextureU() {
		return (texture * 16) % 256;
	}

	public int getTextureV() {
		return (texture / 16) * 16;
	}

	public CrosshairPosition getPosition() {
		return position;
	}

	public enum CrosshairPosition {

		ABOVE_CROSSHAIR(4, 14), LEFT_OF_CROSSHAIR(14, 3), RIGHT_OF_CROSSHAIR(-6, 3), BELOW_CROSSHAIR(4, -8);

		private final int x, y;

		/**
		 * Some notes on coordinates:<br />
		 * +y = up<br />
		 * +x = left
		 *
		 * @param x
		 * @param y
		 */
		CrosshairPosition(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int xOffset() {
			return x;
		}

		public int yOffset() {
			return y;
		}

	}

}
