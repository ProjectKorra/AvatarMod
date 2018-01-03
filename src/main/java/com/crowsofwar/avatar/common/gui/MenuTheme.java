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

package com.crowsofwar.avatar.common.gui;

import java.awt.*;

public class MenuTheme {

	private final ThemeColor background, edge, icon;
	private final int text;

	public MenuTheme(ThemeColor background, ThemeColor edge, ThemeColor icon, int text) {
		this.background = background;
		this.edge = edge;
		this.icon = icon;
		this.text = text;
	}

	public ThemeColor getBackground() {
		return background;
	}

	public ThemeColor getEdge() {
		return edge;
	}

	public ThemeColor getIcon() {
		return icon;
	}

	public int getText() {
		return text;
	}

	public static class ThemeColor {
		private final int r, g, b, hoverR, hoverG, hoverB;

		public ThemeColor(Color def, Color hover) {
			this(def.getRed(), def.getGreen(), def.getBlue(), hover.getRed(), hover.getGreen(),
					hover.getBlue());
		}

		public ThemeColor(int r, int g, int b, int hoverR, int hoverG, int hoverB) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.hoverR = hoverR;
			this.hoverG = hoverG;
			this.hoverB = hoverB;
		}

		public ThemeColor(int rgb, int rgbHover) {
			this(new Color(rgb), new Color(rgbHover));
		}

		public int getRed(boolean hover) {
			return hover ? hoverR : r;
		}

		public int getGreen(boolean hover) {
			return hover ? hoverG : g;
		}

		public int getBlue(boolean hover) {
			return hover ? hoverB : b;
		}

	}

}
