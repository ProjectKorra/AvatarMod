package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.util.event.Observer;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EarthSoundHandler implements Observer<EarthbendingEvent> {
	
	@Override
	public void notify(EarthbendingEvent e) {
		if (e instanceof EarthbendingEvent.BlockPickedUp) {
			System.out.println("Block picked up");
		}
		if (e instanceof EarthbendingEvent.BlockPlacedReached) {
			System.out.println("Block placed reached");
		}
		if (e instanceof EarthbendingEvent.BlockThrown) {
			System.out.println("Block thrown");
		}
	}
	
}
