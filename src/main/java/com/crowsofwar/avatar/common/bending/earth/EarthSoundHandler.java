package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.util.event.Subject;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EarthSoundHandler {
	
	private static void onPickedUp(EarthbendingEvent.BlockPickedUp e) {
		System.out.println("Block picked up");
	}
	
	private static void onBlockPlaceReach(EarthbendingEvent.BlockPlacedReached e) {
		System.out.println("Block placed reached");
	}
	
	private static void onBlockThrown(EarthbendingEvent.BlockThrown e) {
		System.out.println("Block thrown");
	}
	
	public static void register() {
		Subject registerTo = BendingManager.getBending(BendingManager.BENDINGID_EARTHBENDING);
		registerTo.addObserver(EarthSoundHandler::onPickedUp, EarthbendingEvent.BlockPickedUp.class);
		registerTo.addObserver(EarthSoundHandler::onBlockPlaceReach,
				EarthbendingEvent.BlockPlacedReached.class);
		registerTo.addObserver(EarthSoundHandler::onBlockThrown, EarthbendingEvent.BlockThrown.class);
	}
	
}
