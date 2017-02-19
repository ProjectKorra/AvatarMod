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

import static net.minecraft.client.renderer.GlStateManager.*;

import net.minecraft.client.renderer.GlStateManager;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class UiComponent {
	
	private UiTransform transform;
	
	public UiComponent() {
		this.transform = new UiTransformStatic(this);
	}
	
	public UiTransform transform() {
		return transform;
	}
	
	protected abstract float componentWidth();
	
	protected abstract float componentHeight();
	
	/**
	 * Get the actual scaled width
	 */
	public float width() {
		return componentWidth() * scale();
	}
	
	/**
	 * Get the actual scaled height
	 */
	public float height() {
		return componentHeight() * scale();
	}
	
	public void draw(float partialTicks) {
		
		//@formatter:off
		pushMatrix();
			
			translate(coordinates().xInPixels(), coordinates().yInPixels(), 0);
			GlStateManager.scale(scale(), scale(), 1f); // unfortunately needed due to shadowing
			componentDraw(partialTicks);
			
		popMatrix();
		//@formatter:on
		
	}
	
	protected abstract void componentDraw(float partialTicks);
	
	// Delegates to transform
	
	public Measurement coordinates() {
		return transform.coordinates();
	}
	
	public StartingPosition position() {
		return transform.position();
	}
	
	public void setPosition(StartingPosition position) {
		transform.setPosition(position);
	}
	
	public Measurement offset() {
		return transform.offset();
	}
	
	public void setOffset(Measurement offset) {
		transform.setOffset(offset);
	}
	
	public float offsetScale() {
		return transform.offsetScale();
	}
	
	public void setOffsetScale(float scale) {
		transform.setOffsetScale(scale);
	}
	
	public float scale() {
		return transform.scale();
	}
	
	public void setScale(float scale) {
		transform.setScale(scale);
	}
	
}
