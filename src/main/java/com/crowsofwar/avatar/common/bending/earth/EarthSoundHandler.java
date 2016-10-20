package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.entity.EntityFloatingBlock;
import com.crowsofwar.avatar.common.entity.EntityRavine;
import com.crowsofwar.avatar.common.util.event.Subject;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EarthSoundHandler {
	
	private static void floatingPlayBreak(FloatingBlockEvent e) {
		EntityFloatingBlock floating = e.getFloatingBlock();
		World world = floating.worldObj;
		Block block = e.getFloatingBlock().getBlockState().getBlock();
		SoundType sound = block.getSoundType();
		if (sound != null) {
			world.playSound(null, floating.getPosition(), sound.getBreakSound(), SoundCategory.PLAYERS,
					sound.getVolume(), sound.getPitch());
		}
	}
	
	private static void floatingPlayPlace(FloatingBlockEvent.BlockPlacedReached e) {
		EntityFloatingBlock floating = e.getFloatingBlock();
		SoundType sound = floating.getBlock().getSoundType();
		if (sound != null) {
			floating.worldObj.playSound(null, floating.getPosition(), sound.getPlaceSound(),
					SoundCategory.PLAYERS, sound.getVolume(), sound.getPitch());
		}
	}
	
	private static void onRavineDestroyBlock(RavineEvent.DestroyBlock e) {
		EntityRavine ravine = e.getRavine();
		Block destroyed = e.getBlockState().getBlock();
		SoundEvent sound = destroyed == Blocks.FIRE ? SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE
				: destroyed.getSoundType().getBreakSound();
		ravine.worldObj.playSound(null, e.getDestroyedAt(), sound, SoundCategory.BLOCKS, 1, 1);
	}
	
	public static void register() {
		Subject registerTo = BendingManager.getBending(BendingType.EARTHBENDING);
		registerTo.addObserver(EarthSoundHandler::floatingPlayBreak, FloatingBlockEvent.BlockPickedUp.class);
		registerTo.addObserver(EarthSoundHandler::floatingPlayPlace,
				FloatingBlockEvent.BlockPlacedReached.class);
		registerTo.addObserver(EarthSoundHandler::floatingPlayBreak,
				FloatingBlockEvent.BlockThrownReached.class);
		registerTo.addObserver(EarthSoundHandler::onRavineDestroyBlock, RavineEvent.DestroyBlock.class);
	}
	
}
