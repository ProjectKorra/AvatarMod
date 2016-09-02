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
	
	private static void onPickedUp(FloatingBlockEvent.BlockPickedUp e) {
		EntityFloatingBlock floating = e.getFloatingBlock();
		World world = floating.worldObj;
		Block block = e.getFloatingBlock().getBlockState().getBlock();
		SoundType sound = block.getSoundType();
		if (sound != null) {
			world.playSound(null, floating.getPosition(), sound.getBreakSound(), SoundCategory.PLAYERS,
					sound.getVolume(), sound.getPitch());
		}
	}
	
	private static void onBlockPlaceReach(FloatingBlockEvent.BlockPlacedReached e) {
		EntityFloatingBlock floating = e.getFloatingBlock();
		SoundType sound = floating.getBlock().getSoundType();
		if (sound != null) {
			floating.worldObj.playSound(null, floating.getPosition(), sound.getPlaceSound(),
					SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
		}
	}
	
	private static void onBlockThrown(FloatingBlockEvent.BlockThrownReached e) {
		EntityFloatingBlock floating = e.getFloatingBlock();
		SoundType sound = floating.getBlock().getSoundType();
		if (sound != null) {
			floating.worldObj.playSound(null, floating.getPosition(), sound.getBreakSound(),
					SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
		}
	}
	
	public static void register() {
		Subject registerTo = BendingManager.getBending(BendingType.EARTHBENDING);
		registerTo.addObserver(EarthSoundHandler::onPickedUp, FloatingBlockEvent.BlockPickedUp.class);
		registerTo.addObserver(EarthSoundHandler::onBlockPlaceReach,
				FloatingBlockEvent.BlockPlacedReached.class);
		registerTo.addObserver(EarthSoundHandler::onBlockThrown, FloatingBlockEvent.BlockThrownReached.class);
	}
	
}
