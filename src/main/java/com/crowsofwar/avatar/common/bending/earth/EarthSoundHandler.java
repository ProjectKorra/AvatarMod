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
