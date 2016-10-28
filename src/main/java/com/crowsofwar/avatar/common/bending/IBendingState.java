package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.ReadableWritable;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.WriteToNBT;
import com.crowsofwar.gorecore.util.GoreCoreNBTUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Allows an BendingController to store additional information about a player's
 * state. Each BendingController can have its own implementation of this
 * interface. One IBendingState is attached to each player, which is initialized
 * using the bending controller's
 * {@link BendingController#createState(com.crowsofwar.avatar.common.data.AvatarPlayerData)
 * createState method}. After the player's bending controller is deactivated,
 * the Bending state will be discarded however. The current state is saved in
 * NBT in case the game saves while the player is bending.
 *
 */
public interface IBendingState extends ReadableWritable {
	
	public static WriteToNBT<IBendingState> writer = new WriteToNBT<IBendingState>() {
		@Override
		public void write(NBTTagCompound nbt, IBendingState object, Object[] methodsExtraData,
				Object[] extraData) {
			nbt.setInteger("ControllerID", object.getId());
			object.writeToNBT(GoreCoreNBTUtil.getOrCreateNestedCompound(nbt, "StateData"));
		}
	};
	
	void toBytes(ByteBuf buf);
	
	void fromBytes(ByteBuf buf);
	
	/**
	 * Get the Id of the bending state's BendingController. Should be unique
	 * per-class (not per-instance).
	 * 
	 * @see BendingController#getID()
	 */
	int getId();
	
	default void write(NBTTagCompound nbt) {
		nbt.setInteger("ControllerID", getId());
		this.writeToNBT(nbt);
	}
	
	/**
	 * Creates and initializes a bending state with the given ID. Null if there
	 * was an error.
	 * 
	 * @param id
	 *            The ID of the bending state. See {@link BendingType}.
	 * @param data
	 *            The player data which uses the bending state
	 * @param stateData
	 *            NBT containing additional information for the bending state
	 */
	public static IBendingState find(int id, AvatarPlayerData data, NBTTagCompound stateData) {
		BendingController controller = BendingManager.getBending(id);
		if (controller != null) {
			IBendingState state = controller.createState(data);
			state.readFromNBT(stateData);
			return state;
		} else {
			AvatarLog.warn(AvatarLog.WarningType.INVALID_SAVE,
					"Could not create new bending state with using ControllerID " + id);
			return null;
		}
		
	}
	
}
