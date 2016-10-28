package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.network.packets.PacketCRemoveStatusControl;
import com.crowsofwar.avatar.common.network.packets.PacketCStatusControl;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.avatar.common.util.Raytrace.Result;
import com.crowsofwar.gorecore.util.VectorI;

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
	 * Returns whether the player is looking at a block right now without verifying if the client is
	 * correct.
	 */
	public boolean isLookingAtBlock() {
		return lookSide != null && clientLookBlock != null;
	}
	
	/**
	 * Returns whether the player is looking at a block right now. Checks for hacking on the server.
	 * If on client side, then no checks are made.
	 * 
	 * @see #verifyClientLookBlock(double, double)
	 */
	public boolean isLookingAtBlock(double raycastDist, double maxDeviation) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) return isLookingAtBlock();
		return lookSide != null && verifyClientLookBlock(raycastDist, maxDeviation) != null;
	}
	
	/**
	 * Ensure that the client's targeted block is within range of the server's targeted block. (To
	 * avoid hacking) On client side, simply returns the client's targeted block.
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
		AvatarMod.network.sendTo(new PacketCStatusControl(control), (EntityPlayerMP) playerEntity);
		data.addStatusControl(control);
	}
	
	public void removeStatusControl(StatusControl control) {
		AvatarMod.network.sendTo(new PacketCRemoveStatusControl(control), (EntityPlayerMP) playerEntity);
		data.removeStatusControl(control);
	}
	
}
