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

package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Result;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Information when an ability is executed. Only is used server-side.
 * 
 * @author CrowsOfWar
 */
public class AbilityContext {
	
	private final AvatarPlayerData data;
	private final EntityPlayer playerEntity;
	
	private final VectorI clientLookBlock;
	private VectorI serverLookBlock;
	private final EnumFacing lookSide;
	
	/**
	 * Create context for ability execution.
	 * 
	 * @param data
	 *            Player data instance.
	 * @param raytrace
	 *            Result of the raytrace
	 */
	public AbilityContext(AvatarPlayerData data, Raytrace.Result raytrace) {
		this.data = data;
		this.playerEntity = data.getPlayerEntity();
		this.clientLookBlock = raytrace.getPos();
		this.lookSide = raytrace.getSide();
	}
	
	public AvatarPlayerData getData() {
		return data;
	}
	
	public EntityPlayer getPlayerEntity() {
		return playerEntity;
	}
	
	public World getWorld() {
		return playerEntity == null ? null : playerEntity.worldObj;
	}
	
	public VectorI getClientLookBlock() {
		return clientLookBlock;
	}
	
	/**
	 * Get the side of the block the player is looking at
	 * 
	 * @return
	 */
	public EnumFacing getLookSide() {
		return lookSide;
	}
	
	/**
	 * Returns whether the player is looking at a block right now without
	 * verifying if the client is correct.
	 */
	public boolean isLookingAtBlock() {
		return lookSide != null && clientLookBlock != null;
	}
	
	/**
	 * Returns whether the player is looking at a block right now. Checks for
	 * hacking on the server. If on client side, then no checks are made.
	 * 
	 * @see #verifyClientLookBlock(double, double)
	 */
	public boolean isLookingAtBlock(double raycastDist, double maxDeviation) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) return isLookingAtBlock();
		return lookSide != null && verifyClientLookBlock(raycastDist, maxDeviation) != null;
	}
	
	/**
	 * Ensure that the client's targeted block is within range of the server's
	 * targeted block. (To avoid hacking) On client side, simply returns the
	 * client's targeted block.
	 * 
	 * @param raycastDist
	 *            How far away can the block be?
	 * @param maxDeviation
	 *            How far away can server and client's target positions be?
	 * 
	 * @see Raytrace#getTargetBlock(EntityPlayer, double)
	 */
	public VectorI verifyClientLookBlock(double raycastDist, double maxDeviation) {
		if (clientLookBlock == null) return null;
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) return clientLookBlock;
		Result res = Raytrace.getTargetBlock(playerEntity, raycastDist);
		if (!res.hitSomething()) return null;
		this.serverLookBlock = res.getPos();
		double dist = serverLookBlock.dist(clientLookBlock);
		if (dist <= maxDeviation) {
			return clientLookBlock;
		} else {
			AvatarLog.warnHacking("unknown player",
					"Client sent too far location " + "to look at block. (" + dist + ")");
			Thread.dumpStack();
			return serverLookBlock;
		}
	}
	
	public void addStatusControl(StatusControl control) {
		data.addStatusControl(control);
		data.sync();
	}
	
	public void removeStatusControl(StatusControl control) {
		data.removeStatusControl(control);
		data.sync();
	}
	
}
