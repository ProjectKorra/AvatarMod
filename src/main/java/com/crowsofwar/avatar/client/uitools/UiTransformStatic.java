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

import static com.crowsofwar.avatar.client.uitools.ScreenInfo.screenWidth;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class UiTransformStatic implements UiTransform {
	
	private final UiComponent component;
	private StartingPosition pos;
	private Measurement offset;
	private float offsetScale, componentScale;
	
	public UiTransformStatic(UiComponent component) {
		this.component = component;
		pos = StartingPosition.TOP_LEFT;
		offset = Measurement.fromPixels(0, 0);
		offsetScale = 1;
		componentScale = 1;
	}
	
	@Override
	public Measurement coordinates() {
		float x = pos.getX() - pos.getMinusX() * component.width() + offset().xInPixels() * offsetScale;
		float y = pos.getY() - pos.getMinusY() * component.height() + offset().yInPixels() * offsetScale;
		System.out.println("Screen width" + screenWidth());
		return Measurement.fromPixels(x, y);
	}
	
	@Override
	public StartingPosition position() {
		return pos;
	}
	
	@Override
	public void setPosition(StartingPosition position) {
		this.pos = position;
	}
	
	@Override
	public Measurement offset() {
		return offset;
	}
	
	@Override
	public void setOffset(Measurement offset) {
		this.offset = offset;
	}
	
	@Override
	public float offsetScale() {
		return offsetScale;
	}
	
	@Override
	public void setOffsetScale(float scale) {
		this.offsetScale = scale;
	}
	
	@Override
	public float scale() {
		return componentScale;
	}
	
	@Override
	public void setScale(float scale) {
		this.componentScale = scale;
	}
	
}
