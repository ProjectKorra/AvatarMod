package com.maxandnoah.avatar.common.data;

import com.maxandnoah.avatar.AvatarLog;
import com.maxandnoah.avatar.common.util.BlockPos;
import com.maxandnoah.avatar.common.util.Raytrace;

import net.minecraft.entity.player.EntityPlayer;

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
	private boolean isFresh;
	
	public PlayerState() {
		isFresh = false;
	}
	
	public PlayerState(EntityPlayer playerEntity, BlockPos clientLookAtBlock) {
		update(playerEntity, clientLookAtBlock);
	}
	
	public void update(EntityPlayer playerEntity, BlockPos clientLookAtBlock) {
		this.playerEntity = playerEntity;
		this.clientLookAtBlock = clientLookAtBlock;
		this.isFresh = true;
	}
	
	public EntityPlayer getPlayerEntity() {
		return playerEntity;
	}

	public BlockPos getClientLookAtBlock() {
		return clientLookAtBlock;
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
		this.serverLookAtBlock = Raytrace.getTargetBlock(playerEntity, raycastDist);
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
	
	/**
	 * Returns whether the player state is 'fresh'. If this is
	 * true, player state can be used and was updated recently.
	 */
	public boolean isFresh() {
		return isFresh;
	}
	
	public void setNotFresh() {
		this.isFresh = false;
	}
	
}
