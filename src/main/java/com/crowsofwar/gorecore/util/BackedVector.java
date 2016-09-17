package com.crowsofwar.gorecore.util;

import java.util.function.Consumer;

/**
 * A vector which is adds Consumers in a way that allows clients to create a view of another vector.
 * 
 * @author CrowsOfWar
 */
public class BackedVector extends Vector {
	
	private final Consumer<Vector> onChanged;
	
	/**
	 * @param onGet
	 * @param onChanged
	 */
	public BackedVector(Consumer<Vector> onChanged) {
		this.onChanged = onChanged;
		System.out.println("Set onChanged to " + onChanged);
	}
	
	@Override
	public Vector setX(double x) {
		super.setX(x);
		onChanged.accept(this);
		return this;
	}
	
	@Override
	public Vector setY(double y) {
		super.setY(y);
		onChanged.accept(this);
		return this;
	}
	
	@Override
	public Vector setZ(double z) {
		super.setZ(z);
		onChanged.accept(this);
		return this;
	}
	
	@Override
	public Vector set(double x, double y, double z) {
		super.setX(x);
		super.setY(y);
		super.setZ(z);
		onChanged.accept(this);
		return this;
	}
	
}
