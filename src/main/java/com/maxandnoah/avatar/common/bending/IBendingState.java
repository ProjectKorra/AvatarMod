package com.maxandnoah.avatar.common.bending;

import crowsofwar.gorecore.util.GoreCoreNBTInterfaces.ReadableWritable;
import io.netty.buffer.ByteBuf;

/**
 * Allows an IBendingController to store additional information
 * about a player's state. Each IBendingController can have its
 * own implementation of this interface. One IBendingState is
 * attached to each player, which is initialized using the
 * bending controller's {@link IBendingController#createState(com.maxandnoah.avatar.common.data.AvatarPlayerData)
 * createState method}. After the player's bending controller is
 * deactivated, the Bending state will be discarded however.
 * The current state is saved in NBT in case the game saves
 * while the player is bending.
 *
 */
public interface IBendingState extends ReadableWritable {

	void toBytes(ByteBuf buf);
	
	void fromBytes(ByteBuf buf);
	
	
}
