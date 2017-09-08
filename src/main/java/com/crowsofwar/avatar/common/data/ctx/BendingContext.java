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

package com.crowsofwar.avatar.common.data.ctx;

import static com.crowsofwar.avatar.common.config.ConfigChi.CHI_CONFIG;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.network.packets.PacketCErrorMessage;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Result;
import com.crowsofwar.gorecore.util.Vector;
import com.crowsofwar.gorecore.util.VectorI;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Information when something is executed. Only is used server-side.
 * 
 * @author CrowsOfWar
 */
public class BendingContext {
	
	private final BendingData data;
	private final Bender bender;
	
	private final VectorI clientLookBlock;
	private VectorI serverLookBlock;
	private final EnumFacing lookSide;
	private final Vector lookPos;
	
	/**
	 * Create context for execution.
	 * 
	 * @param data
	 *            Player data instance.
	 * @param raytrace
	 *            Result of the raytrace, from client
	 */
	public BendingContext(BendingData data, EntityLivingBase entity, Raytrace.Result raytrace) {
		this.data = data;
		this.bender = Bender.get(entity);
		this.clientLookBlock = raytrace.getPos();
		this.lookSide = raytrace.getSide();
		this.lookPos = raytrace.getPosPrecise();
	}
	
	public BendingContext(BendingData data, EntityLivingBase entity, Bender bender,
			Raytrace.Result raytrace) {
		
		this.data = data;
		this.bender = bender;
		this.clientLookBlock = raytrace.getPos();
		this.lookSide = raytrace.getSide();
		this.lookPos = raytrace.getPosPrecise();
		
	}
	
	public BendingData getData() {
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
	 *
	 * @deprecated use {@link Bender#consumeChi(float)} instead
	 */
	@Deprecated
	public boolean consumeChi(float amount) {
		
		// TODO Account for entity Chi?
		if (!bender.isPlayer()) return true;
		if (bender.isCreativeMode() && CHI_CONFIG.infiniteInCreative) {
			return true;
		}
		
		if (data.chi().consumeChi(amount)) {
			return true;
		}
		
		if (bender.isPlayer() && !bender.getWorld().isRemote) {
			AvatarMod.network.sendTo(new PacketCErrorMessage("avatar.nochi"),
					(EntityPlayerMP) bender.getEntity());
		}
		
		return false;
	}
	
	/**
	 * Consumes the given amount of water either from direct water source, from
	 * a water pouch, or several other sources.
	 * <p>
	 * First looks to see if looking at water block - any values >= 3 will also
	 * consume the water block. Then, tries to see if there is a water pouch
	 * with sufficient amount of water.
	 */
	public boolean consumeWater(int amount) {
		
		World world = bender.getWorld();
		
		if (world.isRainingAt(bender.getEntity().getPosition())) {
			return true;
		}
		
		VectorI targetPos = getClientLookBlock();
		if (targetPos != null) {
			Block lookAt = world.getBlockState(targetPos.toBlockPos()).getBlock();
			if (lookAt == Blocks.WATER || lookAt == Blocks.FLOWING_WATER) {
				
				if (amount >= 3) {
					world.setBlockToAir(targetPos.toBlockPos());
				}
				return true;
				
			}
			
			if (lookAt == Blocks.CAULDRON) {
				IBlockState ibs = world.getBlockState(targetPos.toBlockPos());
				int waterLevel = ibs.getValue(BlockCauldron.LEVEL);
				if (waterLevel > 0) {
					world.setBlockState(targetPos.toBlockPos(),
							ibs.withProperty(BlockCauldron.LEVEL, waterLevel - 1));
					return true;
				}
			}
			
		}
		
		return bender.consumeWaterLevel(amount);
		
	}
	
}
