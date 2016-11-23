package com.crowsofwar.avatar.common.gui;

import java.awt.Color;

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
