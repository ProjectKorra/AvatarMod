package com.crowsofwar.avatar.common.bending;

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.avatar.common.bending.air.StatCtrlAirJump;
import com.crowsofwar.avatar.common.bending.earth.StatCtrlPlaceBlock;
import com.crowsofwar.avatar.common.bending.earth.StatCtrlThrowBlock;
import com.crowsofwar.avatar.common.bending.fire.StatCtrlSetFlamethrowing;
import com.crowsofwar.avatar.common.bending.water.StatCtrlThrowWater;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.util.Raytrace;

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
	
	public static final StatusControl AIR_JUMP = new StatCtrlAirJump();
	
	public static final StatusControl PLACE_BLOCK = new StatCtrlPlaceBlock();
	
	public static final StatusControl THROW_BLOCK = new StatCtrlThrowBlock();
	
	public static final StatusControl THROW_WATER = new StatCtrlThrowWater();
	
	public static final StatusControl START_FLAMETHROW = new StatCtrlSetFlamethrowing(true);
	
	public static final StatusControl STOP_FLAMETHROW = new StatCtrlSetFlamethrowing(false);
	
	private static int nextId = 0;
	private static List<StatusControl> allControls;
	
	private final int texture;
	private final AvatarControl control;
	private Raytrace.Info raytrace;
	private final CrosshairPosition position;
	private final int id;
	
	public StatusControl(int texture, AvatarControl subscribeTo, CrosshairPosition position) {
		
		if (allControls == null) allControls = new ArrayList<>();
		
		this.texture = texture;
		this.control = subscribeTo;
		this.raytrace = new Raytrace.Info();
		this.position = position;
		this.id = ++nextId;
		System.out.println("Assigned id " + id);
		allControls.add(this);
		
	}
	
	/**
	 * Require that a raytrace be cast client-side, which is sent to the server.
	 * It is then accessible in {@link #execute(AbilityContext)}.
	 * 
	 * @param range
	 *            Range to raytrace. -1 for player reach
	 * @param raycastLiquids
	 *            Whether to keep going when hit liquids
	 */
	protected void requireRaytrace(int range, boolean raycastLiquids) {
		this.raytrace = new Raytrace.Info(range, raycastLiquids);
	}
	
	/**
	 * Execute this status control in the given context. Only called
	 * server-side.
	 * 
	 * @param ctx
	 *            Information for status control
	 * @return Whether to remove it
	 */
	public abstract boolean execute(AbilityContext ctx);
	
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
	
	public static StatusControl lookup(int id) {
		id--;
		return id >= 0 && id < allControls.size() ? allControls.get(id) : null;
	}
	
	public enum CrosshairPosition {
		
		ABOVE_CROSSHAIR(4, 14),
		LEFT_OF_CROSSHAIR(14, 3),
		RIGHT_OF_CROSSHAIR(-6, 3),
		BELOW_CROSSHAIR(4, -8);
		
		private final int x, y;
		
		/**
		 * Some notes on coordinates:<br />
		 * +y = up<br />
		 * +x = left
		 * 
		 * @param x
		 * @param y
		 */
		private CrosshairPosition(int x, int y) {
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
