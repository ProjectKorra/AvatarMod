package com.crowsofwar.avatar.client.gui;

/**
 * Holds information for the RadialMenu about a segment. Contains
 * information on its rotation (position), and whether it's clicked.
 *
 */
public class RadialSegment {
	
	private final RadialMenu gui;
	private final float angle;
	private final int index;
	private final int icon;
	
	public RadialSegment(RadialMenu gui, int index, int icon) {
		this.gui = gui;
		this.angle = 22.5f + index * 45;
		this.index = index;
		this.icon = icon;
	}
	
	/**
	 * Returns whether the mouse is currently hovering 
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public boolean isMouseHover(int mouseX, int mouseY) {
		
		int mouseCenteredX = mouseX - gui.width / 2;
		int mouseCenteredY = mouseY - gui.height / 2;
		double r = Math.sqrt(mouseCenteredX*mouseCenteredX + mouseCenteredY*mouseCenteredY) / RadialMenu.menuScale;
		double currentAngle = Math.toDegrees(Math.atan2(mouseCenteredY, mouseCenteredX)) + 90;
		double minAngle = angle - 44;
		if (minAngle < 0) minAngle += 360;
		double maxAngle = angle;
		boolean addCurrentAngle = currentAngle < 0;
		if (minAngle > maxAngle) {
			maxAngle += 360;
			addCurrentAngle = true;
		}
		if (addCurrentAngle) currentAngle += 360;
		
		return r >= 100 && r <= 200 && currentAngle >= minAngle && currentAngle <= maxAngle;
	}
	
	public float getAngle() {
		return angle;
	}
	
	public int getTextureU() {
		return (icon * 16) % 256;
	}
	
	public int getTextureV() {
		return icon / 256;
	}
	
}
