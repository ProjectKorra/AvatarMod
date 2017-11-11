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
	
	private VectorI clientLookBlock;
	private VectorI serverLookBlock;
	private EnumFacing lookSide;
	private Vector lookPos;
	
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
		verifyClientRaytrace();
	}
	
	public BendingContext(BendingData data, EntityLivingBase entity, Bender bender,
			Raytrace.Result raytrace) {
		
		this.data = data;
		this.bender = bender;
		this.clientLookBlock = raytrace.getPos();
		this.lookSide = raytrace.getSide();
		this.lookPos = raytrace.getPosPrecise();
		verifyClientRaytrace();

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
	 * For certain circumstances, the client performs a raytrace and then sends the result to the server, which is
	 * usable here (ex. {@link #isLookingAtBlock()}). Raytrace isn't performed on server since the server and client can
	 * have minor discrepancies, so the server might think the player's rotation is 20 degrees off from what the client
	 * thinks, resulting in glitchy raytracing.
	 * <p>
	 * Performed once to ensure that the client's targeted block is reasonable, to avoid hacking.
	 *
	 */
	private void verifyClientRaytrace() {

		if (clientLookBlock != null) {

			// Simply verify if the client's look-block is reasonable

			Vector benderPos = Vector.getEntityPos(getBenderEntity());
			Vector blockPos = clientLookBlock.precision();
			double dist = benderPos.dist(blockPos);

			if (dist >= 5) {
				AvatarLog.warnHacking(bender.getName(), "Sent suspicious raytrace block, ignoring");

				clientLookBlock = null;
				lookSide = null;
				lookPos = null;

			}

		}

	}

	/**
	 * Consumes the given amount of water either from direct water source, from
	 * a water pouch, or several other sources.
	 * <p>
	 * First looks to see if looking at water block - any values >= 3 will also
	 * consume the water block. Then, tries to see if there is a water pouch
	 * with sufficient amount of water.
	 * <p>
	 * <b>NOTE:</b> If this is not working, ensure that the Ability constructor is calling
	 * requireRaytrace, because otherwise no raytrace will be performed, and then this won't be able
	 * to detect if the player is looking at water.
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
