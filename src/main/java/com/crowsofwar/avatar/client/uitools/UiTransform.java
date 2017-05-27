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
package com.crowsofwar.avatar.client.uitools;

/**
 * A mutable transformation class which keeps track of a UiComponent's position.
 * <p>
 * It starts at a {@link StartingPosition}, which is the component's general
 * location on the screen (top-right, left, bottom, etc). That can be adjusted
 * by {@link #offset() offsetting it}. The actual position is found by calling
 * {@link #coordinates()}.
 * 
 * @author CrowsOfWar
 */
public interface UiTransform {
	
	/**
	 * Called by the UiComponent to update the transform.
	 */
	void update(float partialTicks);
	
	/**
	 * The calculated coordinates based on position and offset
	 */
	Measurement coordinates();
	
	/**
	 * The starting position, such as top-right
	 */
	StartingPosition position();
	
	/**
	 * Relocate the starting position to the given value
	 */
	void setPosition(StartingPosition position);
	
	/**
	 * Returns the offset from the {@link #position() starting position}.
	 */
	Measurement offset();
	
	/**
	 * Set the offsets to the given value
	 */
	void setOffset(Measurement offset);
	
	/**
	 * Adds the given offset to the current offset
	 */
	default void addOffset(Measurement offset) {
		setOffset(offset().plus(offset));
	}
	
	/**
	 * Gets the scale of the actual component. Coordinates may be adjusted to
	 * accommodate for this.
	 */
	float scale();
	
	/**
	 * Sets the scale of the component
	 */
	void setScale(float scale);
	
	/**
	 * Returns the value to multiply by the {@link #offset() offsets}.
	 */
	float offsetScale();
	
	/**
	 * Set the value to multiply {@link #offset() offset} by.
	 */
	void setOffsetScale(float scale);
	
	/**
	 * Get the frame that this transform is inside
	 */
	Frame getFrame();
	
	/**
	 * Put this transform into the given frame
	 */
	void setFrame(Frame frame);
	
	/**
	 * Gets the Z-position of the UiComponent. In the case of two elements
	 * overlapping, it is used to determine which element would display "on top"
	 * of the other.
	 */
	float zLevel();
	
	/**
	 * Sets the z-position of the UiComponent
	 */
	void setZLevel(float zLevel);
	
}
