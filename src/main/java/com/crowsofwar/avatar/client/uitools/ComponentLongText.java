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

import static com.crowsofwar.avatar.client.uitools.ScreenInfo.scaleFactor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;

/**
 * Text that spans multiple lines
 * 
 * @author CrowsOfWar
 */
public class ComponentLongText extends UiComponent {
	
	private String text;
	private Measurement width;
	
	private List<String> lines;
	
	/**
	 * Creates a multiline text component. Note that only the x value of the
	 * width will be used; y is ignored.
	 */
	public ComponentLongText(String text, Measurement width) {
		this.text = text;
		this.width = width;
		calculateLines();
	}
	
	@Override
	protected float componentWidth() {
		return width.xInPixels() / scaleFactor();
	}
	
	@Override
	protected float componentHeight() {
		return mc.fontRendererObj.FONT_HEIGHT * lines.size();
	}
	
	@Override
	protected void componentDraw(float partialTicks, boolean mouseHover) {
		for (int i = 0; i < lines.size(); i++) {
			drawString(mc.fontRendererObj, lines.get(i), 0, i * mc.fontRendererObj.FONT_HEIGHT, 0xffffff);
		}
	}
	
	private void calculateLines() {
		
		FontRenderer fr = mc.fontRendererObj;
		lines = new ArrayList<>();
		
		String currentLine = "";
		
		String[] words = text.split(" ");
		
		for (int i = 0; i < words.length; i++) {
			
			String word = words[i];
			String wouldBe = currentLine + word + " ";
			if (fr.getStringWidth(wouldBe) > width.xInPixels() / scaleFactor()) {
				// The line is too long, push it onto lines and reset
				lines.add(currentLine);
				currentLine = "";
				
				// If a word is too long by itself, ignore it
				// (don't keep trying to put it on a new line, when it wouldn't
				// fit by itself anyways)
				if (fr.getStringWidth(word) <= width.xInPixels() / scaleFactor()) {
					i--;
				} else {
					lines.set(lines.size() - 1, wouldBe);
				}
				
			} else {
				// The line isn't long yet, so keep adding more words
				currentLine = wouldBe;
			}
			
		}
		
		lines.add(currentLine);
		
	}
	
}
