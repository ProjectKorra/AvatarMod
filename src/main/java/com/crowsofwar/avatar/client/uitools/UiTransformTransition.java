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
public class UiTransformTransition implements UiTransform {
	
	private final UiTransform initial, ending;
	private final float maxTicks;
	private float ticks, lastPartialTicks;
	
	public UiTransformTransition(UiTransform initial, UiTransform ending, float seconds) {
		this.initial = initial;
		this.ending = ending;
		this.maxTicks = seconds * 20;
	}
	
	private float percentDone() {
		float value = ticks / maxTicks;
		return value > 1 ? 1 : value;
	}
	
	private float invPercentDone() {
		return 1 - percentDone();
	}
	
	@Override
	public Measurement coordinates() {
		return ending.coordinates().times(percentDone()).plus(initial.coordinates().times(invPercentDone()));
	}
	
	@Override
	public StartingPosition position() {
		float x = ending.position().getX() * percentDone() + initial.position().getX() * invPercentDone();
		float y = ending.position().getY() * percentDone() + initial.position().getY() * invPercentDone();
		return StartingPosition.custom(x, y);
	}
	
	@Override
	public void setPosition(StartingPosition position) {}
	
	@Override
	public Measurement offset() {
		return ending.offset().times(percentDone()).plus(initial.offset().times(invPercentDone()));
	}
	
	@Override
	public void setOffset(Measurement offset) {}
	
	@Override
	public float scale() {
		return ending.scale() * percentDone() + initial.scale() * invPercentDone();
	}
	
	@Override
	public void setScale(float scale) {}
	
	@Override
	public float offsetScale() {
		return ending.offsetScale() * percentDone() + initial.offsetScale() * invPercentDone();
	}
	
	@Override
	public void setOffsetScale(float scale) {}
	
	@Override
	public void update(float partialTicks) {
		float diff = partialTicks - lastPartialTicks;
		if (diff < 0) diff += 1;
		ticks += diff;
		lastPartialTicks = partialTicks;
	}
	
}
