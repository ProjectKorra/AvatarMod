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

package com.crowsofwar.avatar.common.data;

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.findNestedCompound;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;

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
	 * @see BendingController#getType()
	 */
	public BendingType getType() {
		return BendingType.find(getId());
	}
	
	/**
	 * @deprecated Use {@link #getType()} instead
	 */
	@Deprecated
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
		
		nbt.setInteger("ControllerID", getId());
		NBTTagCompound stateData = findNestedCompound(nbt, "StateData");
		stateData.setInteger("ProgressPoints", progressionPoints);
		this.writeToNBT(stateData);
		
	}
	
	/**
	 * Read the NBT. Should be parent of StateData compound
	 */
	public final void read(NBTTagCompound nbt) {
		
		NBTTagCompound stateData = findNestedCompound(nbt, "StateData");
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
