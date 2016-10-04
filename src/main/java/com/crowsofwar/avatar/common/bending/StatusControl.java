package com.crowsofwar.avatar.common.bending;

import java.util.ArrayList;
import java.util.List;
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
	
	private static int nextId = 1;
	private static final List<StatusControl> allControls = new ArrayList<>();
	
	private final int texture;
	private final Consumer<AbilityContext> callback;
	private final int id;
	
	private StatusControl(Consumer<AbilityContext> callback, int texture) {
		this.texture = texture;
		this.callback = callback;
		this.id = nextId++;
		allControls.add(this);
	}
	
	public int id() {
		return id;
	}
	
	public static StatusControl lookup(int id) {
		return allControls.get(id - 1);
	}
	
}
