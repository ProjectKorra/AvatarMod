package com.crowsofwar.avatar.common.bending.statctrl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.util.Raytrace;

/**
 * Describes a temporary effect where a callback listener is added to a control event. The listener
 * then will perform certain actions associated with that control.
 * <p>
 * For example, the player receives a place-block Status Control, which subscribes to right-click.
 * The status control receives a callback whenever the player uses the right-click control. Then,
 * the status control is removed.
 * <p>
 * Status controls are stored in player-data, but are also sent to the client via packets, which
 * render over the crosshair.
 * 
 * @author CrowsOfWar
 */
public abstract class StatusControl {
	
	public static StatusControl AIR_JUMP = new StatCtrlAirJump();
	
	public static StatusControl PLACE_BLOCK = new StatCtrlPlaceBlock();
	
	public static StatusControl THROW_BLOCK = new StatCtrlThrowBlock();
	
	private static int nextId = 1;
	private static List<StatusControl> allControls;
	
	private final int texture;
	private final Function<AbilityContext, Boolean> callback;
	private final AvatarControl control;
	private final Raytrace.Info raytrace;
	private final CrosshairPosition position;
	private final int id;
	
	public StatusControl(Function<AbilityContext, Boolean> callback, int texture, AvatarControl subscribeTo,
			CrosshairPosition position) {
		this(callback, texture, subscribeTo, position, new Raytrace.Info());
	}
	
	public StatusControl(Function<AbilityContext, Boolean> callback, int texture, AvatarControl subscribeTo,
			CrosshairPosition position, Raytrace.Info raytrace) {
		
		if (allControls == null) allControls = new ArrayList<>();
		
		this.texture = texture;
		this.callback = callback;
		this.control = subscribeTo;
		this.raytrace = raytrace;
		this.position = position;
		this.id = nextId++;
		allControls.add(this);
		
	}
	
	public int id() {
		return id;
	}
	
	public AvatarControl getSubscribedControl() {
		return control;
	}
	
	/**
	 * Execute this status control in the given context.
	 * 
	 * @param ctx
	 *            Information for status control
	 * @return Whether to remove it
	 */
	public boolean execute(AbilityContext ctx) {
		return callback.apply(ctx);
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
		return allControls.get(id - 1);
	}
	
	public enum CrosshairPosition {
		
		ABOVE_CROSSHAIR(0, -10),
		LEFT_OF_CROSSHAIR(14, 4),
		RIGHT_OF_CROSSHAIR(-14, 4),
		BELOW_CROSSHAIR(0, 10);
		
		private final int x, y;
		
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
