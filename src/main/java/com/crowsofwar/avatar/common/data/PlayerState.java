package com.crowsofwar.avatar.common.data;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.util.BlockPos;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.RaytraceResult;

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
		update(playerEntity, raytrace.getPos(), raytrace.getDirection());
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
	 * Ensure that the client's targeted block is within range
	 * of the server's targeted block. (To avoid hacking)
	 * 
	 * @param raycastDist How far away can the block be?
	 * @param maxDeviation How far away can server and client's target positions be?
	 * 
	 * @see Raytrace#getTargetBlock(EntityPlayer, double)
	 */
	public BlockPos verifyClientLookAtBlock(double raycastDist, double maxDeviation) {
		if (clientLookAtBlock == null) return null;
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
