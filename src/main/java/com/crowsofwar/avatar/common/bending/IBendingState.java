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

package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.CreateFromNBT;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.ReadableWritable;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.WriteToNBT;
import com.crowsofwar.gorecore.util.GoreCoreNBTUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Allows an BendingController to store additional information about a player's state. Each
 * BendingController can have its own implementation of this interface. One IBendingState is
 * attached to each player, which is initialized using the bending controller's
 * {@link BendingController#createState(com.crowsofwar.avatar.common.data.AvatarPlayerData)
 * createState method}. After the player's bending controller is deactivated, the Bending state will
 * be discarded however. The current state is saved in NBT in case the game saves while the player
 * is bending.
 *
 */
public interface IBendingState extends ReadableWritable {
	
	public static CreateFromNBT<IBendingState> creator = new CreateFromNBT<IBendingState>() {
		@Override
		public IBendingState create(NBTTagCompound nbt, Object[] methodsExtraData, Object[] extraData) {
			BendingController controller = BendingManager.getBending(nbt.getInteger("ControllerID"));
			if (controller != null) {
				IBendingState state = controller.createState((AvatarPlayerData) extraData[0]);
				state.readFromNBT(GoreCoreNBTUtil.getOrCreateNestedCompound(nbt, "StateData"));
				return state;
			} else {
				AvatarLog.error("Could not create new bending state with using ControllerID " + nbt.getInteger("ControllerID"));
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
	 * Get the Id of the bending state's BendingController. Should be unique per-class (not
	 * per-instance).
	 * 
	 * @see BendingController#getID()
	 */
	int getId();
	
}
