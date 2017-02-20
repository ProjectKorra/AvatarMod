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
public class UiTransformBasic implements UiTransform {
	
	private final UiComponent component;
	private StartingPosition pos;
	private Measurement offset;
	private float offsetScale, componentScale;
	private Frame frame;
	
	public UiTransformBasic(UiComponent component) {
		this.component = component;
		pos = StartingPosition.TOP_LEFT;
		offset = Measurement.fromPixels(0, 0);
		offsetScale = 1;
		componentScale = 1;
		frame = Frame.SCREEN;
	}
	
	@Override
	public Measurement coordinates() {
		float w = frame.getDimensions().xInPixels();
		float h = frame.getDimensions().yInPixels();
		
		float x = frame.getCoordsMin().xInPixels() + pos.getX() * w;
		x += offset().xInPixels() * offsetScale - pos.getMinusX() * component.width();
		
		float y = frame.getCoordsMin().yInPixels() + pos.getY() * h;
		y += offset().yInPixels() * offsetScale - pos.getMinusY() * component.height();
		
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
	
	@Override
	public void update(float partialTicks) {}
	
	@Override
	public Frame getFrame() {
		return frame;
	}
	
	@Override
	public void setFrame(Frame frame) {
		this.frame = frame;
	}
	
}
