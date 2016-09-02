package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.util.event.Subject;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EarthSoundHandler {
	
	private static void onPickedUp(EarthbendingEvent.BlockPickedUp e) {
		System.out.println("Block picked up");
		EntityFloatingBlock floating = e.getFloatingBlock();
		World world = floating.worldObj;
		Block block = e.getFloatingBlock().getBlockState().getBlock();
		SoundType sound = block.getSoundType();
		if (sound != null) {
			world.playSound(null, floating.getPosition(), sound.getBreakSound(), SoundCategory.PLAYERS,
					sound.getVolume(), sound.getPitch());
		}
	}
	
	private static void onBlockPlaceReach(EarthbendingEvent.BlockPlacedReached e) {
		System.out.println("Block placed reached");
		EntityFloatingBlock floating = e.getFloatingBlock();
		SoundType sound = floating.getBlock().getSoundType();
		if (sound != null) {
			floating.worldObj.playSound(null, floating.getPosition(), sound.getBreakSound(),
					SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
		}
	}
	
	private static void onBlockThrown(EarthbendingEvent.BlockThrown e) {
		System.out.println("Block thrown");
	}
	
	public static void register() {
		Subject registerTo = BendingManager.getBending(BendingType.EARTHBENDING);
		registerTo.addObserver(EarthSoundHandler::onPickedUp, EarthbendingEvent.BlockPickedUp.class);
		registerTo.addObserver(EarthSoundHandler::onBlockPlaceReach,
				EarthbendingEvent.BlockPlacedReached.class);
		registerTo.addObserver(EarthSoundHandler::onBlockThrown, EarthbendingEvent.BlockThrown.class);
	}
	
}
