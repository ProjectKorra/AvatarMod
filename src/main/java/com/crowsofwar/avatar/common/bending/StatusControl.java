package com.crowsofwar.avatar.common.bending;

import java.util.function.Consumer;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class StatusControl {
	
	public static final StatusControl AIR_JUMP = new StatusControl(ctx -> {
		System.out.println("jump " + ctx.getPlayerEntity());
	}, 0);
	
	private final int texture;
	private final Consumer<AbilityContext> callback;
	
	private StatusControl(Consumer<AbilityContext> callback, int texture) {
		this.texture = texture;
		this.callback = callback;
	}
	
}
