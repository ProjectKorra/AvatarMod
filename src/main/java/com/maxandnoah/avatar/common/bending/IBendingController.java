package com.maxandnoah.avatar.common.bending;

import com.maxandnoah.avatar.AvatarLog;
import com.maxandnoah.avatar.common.data.AvatarPlayerData;

import crowsofwar.gorecore.util.GoreCoreNBTInterfaces.CreateFromNBT;
import crowsofwar.gorecore.util.GoreCoreNBTInterfaces.ReadableWritable;
import crowsofwar.gorecore.util.GoreCoreNBTInterfaces.WriteToNBT;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Base class for bending abilities. All bending classes extend this one.
 * They can save data to NBT if necessary.
 * Functionality for bending should be in subclasses. Bending controllers
 * are singletons, but must be accessed through {@link BendingManager}.
 * <br /><br />
 * For the sake of abstraction, you won't need to refer to bending controllers
 * by their concrete names.
 * <br /><br />
 * Subclasses have access to client input via optionally* implementable hook
 * methods. They also are automatically subscribed to both the FML and Forge
 * event buses. Hook methods contain parameters which provide necessary
 * information such as the player entity.
 * <br /><br />
 * *Optionally = the subclass must declare the method, but does not need
 * to put any code inside of it.
 *
 */
public interface IBendingController extends ReadableWritable {
	
	public static final CreateFromNBT<IBendingController> creator = new CreateFromNBT<IBendingController>() {
		@Override
		public IBendingController create(NBTTagCompound nbt, Object[] methodsExtraData, Object[] extraData) {
			int id = nbt.getInteger("ControllerID");
			try {
				IBendingController bc = BendingManager.getBending(id);
				return bc;
			} catch (Exception e) {
				AvatarLog.error("Could not find bending controller from ID '" + id + "' - please check NBT data");
				e.printStackTrace();
				return null;
			}
		}
	};
	
	
	public static final WriteToNBT<IBendingController> writer = new WriteToNBT<IBendingController>() {
		@Override
		public void write(NBTTagCompound nbt, IBendingController object, Object[] methodsExtraData, Object[] extraData) {
			nbt.setInteger("ControllerID", object.getID());
		}
	};
	
	/**
	 * Get an identifier for this bending ability.
	 * Should be unique per-class. (not per-instance)
	 */
	int getID();
	
	void onUpdate();
	
	/**
	 * Optional hook method that gets called when the player presses
	 * a control from the Avatar mod.
	 */
	void onKeypress(String key, AvatarPlayerData data);
	
	/**
	 * Called to create an IBendingState for the player. This allows
	 * the IBendingController to store specific metadata for each player,
	 * making things much easier.
	 * <br /><br />
	 * Keep in mind - when loading
	 * a saved state, it will be read from NBT. However, when creating
	 * a new bending state when an ability is activated, it will NOT
	 * read from NBT. So ensure that all values are initialized.
	 * @return
	 */
	IBendingState createState(AvatarPlayerData data);
	
}
