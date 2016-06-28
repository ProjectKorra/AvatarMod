package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.util.BlockPos;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.RaytraceResult;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Used by AvatarPlayerData. Holds information about
 * the player right now, such as its position, entity,
 * and looking at block.
 *
 */
public class PlayerState {
	
	private EntityPlayer playerEntity;
	private BlockPos clientLookAtBlock;
	private BlockPos serverLookAtBlock;
	private ForgeDirection lookAtSide;
	
	public PlayerState() {
		
	}
	
	public PlayerState(EntityPlayer playerEntity, BlockPos clientLookAtBlock, ForgeDirection lookAtSide) {
		update(playerEntity, clientLookAtBlock, lookAtSide);
	}
	
	public void update(EntityPlayer playerEntity, RaytraceResult raytrace) {
		update(playerEntity, raytrace == null ? null : raytrace.getPos(),
				raytrace == null ? null : raytrace.getDirection());
	}
	
	public void update(EntityPlayer playerEntity, BlockPos clientLookAtBlock, ForgeDirection lookAtSide) {
		this.playerEntity = playerEntity;
		this.clientLookAtBlock = clientLookAtBlock;
		this.lookAtSide = lookAtSide;
	}
	
	public EntityPlayer getPlayerEntity() {
		return playerEntity;
	}

	public BlockPos getClientLookAtBlock() {
		return clientLookAtBlock;
	}
	
	/**
	 * Get the side of the block the player is looking
	 * at
	 * @return
	 */
	public ForgeDirection getLookAtSide() {
		return lookAtSide;
	}
	
	/**
	 * Returns whether the player is looking at a block right now
	 * without verifying if the client is correct.
	 */
	public boolean isLookingAtBlock() {
		return lookAtSide != null && clientLookAtBlock != null;
	}
	
	/**
	 * Returns whether the player is looking at a block right now.
	 * Checks for hacking on the server. If on client side, then
	 * no checks are made.
	 * 
	 * @see #verifyClientLookAtBlock(double, double)
	 */
	public boolean isLookingAtBlock(double raycastDist, double maxDeviation) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) return isLookingAtBlock();
		return lookAtSide != null && verifyClientLookAtBlock(raycastDist, maxDeviation) != null;
	}
	
	/**
	 * Ensure that the client's targeted block is within range
	 * of the server's targeted block. (To avoid hacking) On client side,
	 * simply returns the client's targeted block.
	 * 
	 * @param raycastDist How far away can the block be?
	 * @param maxDeviation How far away can server and client's target positions be?
	 * 
	 * @see Raytrace#getTargetBlock(EntityPlayer, double)
	 */
	public BlockPos verifyClientLookAtBlock(double raycastDist, double maxDeviation) {
		if (clientLookAtBlock == null) return null;
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) return clientLookAtBlock;
		this.serverLookAtBlock = Raytrace.getTargetBlock(playerEntity, raycastDist).getPos();
		double dist = serverLookAtBlock.dist(clientLookAtBlock);
		if (dist <= maxDeviation) {
			return clientLookAtBlock;
		} else {
			AvatarLog.warn("Warning: PlayerState- Client sent too far location "
					+ "to look at block. (" + dist + ") Hacking?");
			Thread.dumpStack();
			return serverLookAtBlock;
		}
	}
	
}
