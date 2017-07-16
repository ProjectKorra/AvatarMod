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

package com.crowsofwar.gorecore.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A vector which is adds Consumers in a way that allows clients to get a
 * view of another vector.
 * <p>
 * In a backed vector, the vector's fields x, y, and z have become obsolete.
 * Instead, functions provide the x, y, and z when they are retrieved, and enact
 * necessary behavior when the components are set.
 * 
 * @author CrowsOfWar
 */
public class BackedVector extends Vector {
	
	private final Consumer<Double> setX, setY, setZ;
	private final Supplier<Double> getX, getY, getZ;
	
	/**
	 * Create a backed vector.
	 * 
	 * @param setX
	 *            Called when X was set. Use this to modify the underlying
	 *            vector.
	 * @param setY
	 *            Called when Y was set. Use this to modify the underlying
	 *            vector.
	 * @param setZ
	 *            Called when Z was set. Use this to modify the underlying
	 *            vector.
	 * @param getX
	 *            Called to retrieve the x-value of the vector
	 * @param getY
	 *            Called to retrieve the y-value of the vector
	 * @param getZ
	 *            Called to retrieve the z-value of the vector
	 */
	public BackedVector(Consumer<Double> setX, Consumer<Double> setY, Consumer<Double> setZ,
			Supplier<Double> getX, Supplier<Double> getY, Supplier<Double> getZ) {
		this.setX = setX;
		this.setY = setY;
		this.setZ = setZ;
		this.getX = getX;
		this.getY = getY;
		this.getZ = getZ;
	}
	
	@Override
	public double x() {
		return getX.get();
	}
	
	@Override
	public double y() {
		return getY.get();
	}
	
	@Override
	public double z() {
		return getZ.get();
	}
	
	@Override
	public Vector setX(double x) {
		super.setX(x);
		setX.accept(x);
		return this;
	}
	
	@Override
	public Vector setY(double y) {
		super.setY(y);
		setY.accept(y);
		return this;
	}
	
	@Override
	public Vector setZ(double z) {
		super.setZ(z);
		setZ.accept(z);
		return this;
	}
	
	@Override
	public Vector set(double x, double y, double z) {
		setX(x);
		setY(y);
		setZ(z);
		return this;
	}
	
}
