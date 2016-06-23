package com.maxandnoah.avatar.client.gui;

/**
 * Holds information for the RadialMenu about a segment. Contains
 * information on its rotation (position), and whether it's clicked.
 *
 */
public class RadialSegment {
	
	private final RadialMenu gui;
	private final float angle;

	public RadialSegment(RadialMenu gui, float angle) {
		this.gui = gui;
		this.angle = angle;
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
		
		return r >= 100 && currentAngle >= minAngle && currentAngle <= maxAngle;
	}
	
	public float getAngle() {
		return angle;
	}
	
}
