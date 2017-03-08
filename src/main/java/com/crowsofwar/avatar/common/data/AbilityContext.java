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

package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.network.packets.PacketCNotEnoughChi;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Result;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
	private final Bender bender;
	
	private final VectorI clientLookBlock;
	private VectorI serverLookBlock;
	private final EnumFacing lookSide;
	private final Vector lookPos;
	
	/**
	 * Create context for ability execution.
	 * 
	 * @param data
	 *            Player data instance.
	 * @param raytrace
	 *            Result of the raytrace, from client
	 */
	public AbilityContext(AvatarPlayerData data, Raytrace.Result raytrace) {
		this.data = data;
		this.bender = new PlayerBender(data.getPlayerEntity());
		this.clientLookBlock = raytrace.getPos();
		this.lookSide = raytrace.getSide();
		this.lookPos = raytrace.getPosPrecise();
	}
	
	public AvatarPlayerData getData() {
		return data;
	}
	
	public Bender getBender() {
		return bender;
	}
	
	public EntityLivingBase getBenderEntity() {
		return bender.getEntity();
	}
	
	public World getWorld() {
		return bender == null ? null : bender.getWorld();
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
	 * Get the lookPos, unverified
	 * 
	 * @return
	 */
	public Vector getLookPos() {
		return lookPos;
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
		Result res = Raytrace.getTargetBlock(bender.getEntity(), raycastDist);
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
	
	/**
	 * Tries to use the given amount of available chi. Returns true if there was
	 * enough chi to remove and it removed it.
	 */
	public boolean consumeChi(float amount) {
		Chi chi = data.chi();
		float available = chi.getAvailableChi();
		if (available >= amount) {
			chi.changeTotalChi(-amount);
			chi.changeAvailableChi(-amount);
			return true;
		}
		
		if (bender.isPlayer()) {
			AvatarMod.network.sendTo(new PacketCNotEnoughChi(), (EntityPlayerMP) bender.getEntity());
		}
		
		return false;
	}
	
}
