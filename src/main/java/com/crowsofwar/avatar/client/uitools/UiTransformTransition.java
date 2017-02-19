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
	private float ticks;
	
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
	public float scale() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void setScale(float scale) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public float offsetScale() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void setOffsetScale(float scale) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(float partialTicks) {
		ticks += partialTicks;
	}
	
}
