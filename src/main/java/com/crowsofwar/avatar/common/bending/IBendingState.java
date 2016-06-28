package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;

import crowsofwar.gorecore.util.GoreCoreNBTUtil;
import crowsofwar.gorecore.util.GoreCoreNBTInterfaces.CreateFromNBT;
import crowsofwar.gorecore.util.GoreCoreNBTInterfaces.ReadableWritable;
import crowsofwar.gorecore.util.GoreCoreNBTInterfaces.WriteToNBT;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Allows an IBendingController to store additional information
 * about a player's state. Each IBendingController can have its
 * own implementation of this interface. One IBendingState is
 * attached to each player, which is initialized using the
 * bending controller's {@link IBendingController#createState(com.crowsofwar.avatar.common.data.AvatarPlayerData)
 * createState method}. After the player's bending controller is
 * deactivated, the Bending state will be discarded however.
 * The current state is saved in NBT in case the game saves
 * while the player is bending.
 *
 */
public interface IBendingState extends ReadableWritable {

	public static CreateFromNBT<IBendingState> creator = new CreateFromNBT<IBendingState>() {
		@Override
		public IBendingState create(NBTTagCompound nbt, Object[] methodsExtraData, Object[] extraData) {
			IBendingController controller = BendingManager.getBending(nbt.getInteger("ControllerID"));
			if (controller != null) {
				IBendingState state = controller.createState((AvatarPlayerData) extraData[0]);
				state.readFromNBT(GoreCoreNBTUtil.getOrCreateNestedCompound(nbt, "StateData"));
				return state;
			}
			
			return null;
		}
		
	};
	public static WriteToNBT<IBendingState> writer = new WriteToNBT<IBendingState>() {
		@Override
		public void write(NBTTagCompound nbt, IBendingState object, Object[] methodsExtraData, Object[] extraData) {
			nbt.setInteger("ControllerID", object.getId());
			object.writeToNBT(GoreCoreNBTUtil.getOrCreateNestedCompound(nbt, "StateData"));
		}
	};
	
	void toBytes(ByteBuf buf);
	
	void fromBytes(ByteBuf buf);
	
	/**
	 * Get the Id of the bending state's IBendingController. Should be
	 * unique per-class (not per-instance).
	 * @see IBendingController#getID()
	 */
	int getId();
	
}
