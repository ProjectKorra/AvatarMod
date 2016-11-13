package com.crowsofwar.avatar.common.data;

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.getOrCreateNestedCompound;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Allows an BendingController to store additional information about a player's
 * state. Each BendingController can have its own implementation of this
 * interface. One BendingState is attached to each player, which is initialized
 * using the bending controller's
 * {@link BendingController#createState(com.crowsofwar.avatar.common.data.AvatarPlayerData)
 * createState method}. After the player's bending controller is deactivated,
 * the Bending state will be discarded however. The current state is saved in
 * NBT in case the game saves while the player is bending.
 *
 */
public abstract class BendingState {
	
	protected final AvatarPlayerData data;
	private int progressionPoints;
	
	public BendingState(AvatarPlayerData data) {
		this.data = data;
		this.progressionPoints = 0;
	}
	
	public final void toBytes(ByteBuf buf) {
		buf.writeInt(progressionPoints);
		writeBytes(buf);
	}
	
	public final void fromBytes(ByteBuf buf) {
		progressionPoints = buf.readInt();
		readBytes(buf);
	}
	
	/**
	 * Subclass dependent networking method. Writes the bending state into the
	 * ByteBuf.
	 */
	protected abstract void writeBytes(ByteBuf buf);
	
	/**
	 * Subclass dependent networking method. Reads the bending state from the
	 * ByteBuf.
	 */
	protected abstract void readBytes(ByteBuf buf);
	
	/**
	 * Get the Id of the bending state's BendingController. Should be unique
	 * per-class (not per-instance).
	 * 
	 * @see BendingController#getID()
	 */
	public abstract int getId();
	
	public int getProgressPoints() {
		return progressionPoints;
	}
	
	public void setProgressPoints(int pps) {
		this.progressionPoints = pps;
		save();
	}
	
	public void addProgressPoint() {
		setProgressPoints(progressionPoints + 1);
	}
	
	public void removeProgressPoint() {
		setProgressPoints(progressionPoints <= 0 ? 0 : progressionPoints - 1);
	}
	
	protected void save() {
		data.saveChanges();
	}
	
	/**
	 * Write to the NBT. Should be parent of StateData compound
	 */
	public final void write(NBTTagCompound nbt) {
		System.out.println("Saving: " + nbt);
		
		nbt.setInteger("ControllerID", getId());
		NBTTagCompound stateData = getOrCreateNestedCompound(nbt, "StateData");
		stateData.setInteger("ProgressPoints", progressionPoints);
		this.writeToNBT(stateData);
		
	}
	
	/**
	 * Read the NBT. Should be parent of StateData compound
	 */
	public final void read(NBTTagCompound nbt) {
		System.out.println("Read: " + nbt);
		
		NBTTagCompound stateData = getOrCreateNestedCompound(nbt, "StateData");
		progressionPoints = stateData.getInteger("ProgressPoints");
		readFromNBT(stateData);
	}
	
	protected abstract void writeToNBT(NBTTagCompound nbt);
	
	protected abstract void readFromNBT(NBTTagCompound nbt);
	
	/**
	 * Creates and initializes a bending state with the given ID. Null if there
	 * was an error.
	 * 
	 * @param data
	 *            The player data which uses the bending state
	 * @param nbt
	 *            Parent of the StateData compound. Contains additional
	 *            information for the bending state, as well as the ID of which
	 *            BendingState to use.
	 */
	public static BendingState find(AvatarPlayerData data, NBTTagCompound nbt) {
		int id = nbt.getInteger("ControllerID");
		System.out.println("----- ID IS: " + id);
		BendingController controller = BendingManager.getBending(id);
		if (controller != null) {
			BendingState state = controller.createState(data);
			state.read(nbt);
			return state;
		} else {
			AvatarLog.warn(AvatarLog.WarningType.INVALID_SAVE,
					"Could not create new bending state with using ControllerID " + id);
			return null;
		}
		
	}
	
}
