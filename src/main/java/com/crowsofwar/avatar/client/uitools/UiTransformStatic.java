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
 * 
 * 
 * @author CrowsOfWar
 */
public class UiTransformStatic implements UiTransform {
	
	private final UiComponent component;
	private StartingPosition pos;
	private Measurement offset;
	private float offsetScale;
	
	public UiTransformStatic(UiComponent component) {
		this.component = component;
		pos = StartingPosition.TOP_LEFT;
		offset = Measurement.fromPixels(0, 0);
		offsetScale = 1;
	}
	
	@Override
	public Measurement coordinates() {
		float x = pos.getX() - pos.getMinusX() * component.width() + offset().xInPixels() * offsetScale;
		float y = pos.getY() - pos.getMinusY() * component.height() + offset().yInPixels() * offsetScale;
		return Measurement.fromPixels(x, y);
	}
	
	@Override
	public StartingPosition position() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setPosition(StartingPosition position) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Measurement offset() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setOffset(Measurement offset) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public float offsetScale() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
