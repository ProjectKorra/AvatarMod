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
public interface BendingController extends ReadableWritable {
	
	public static final CreateFromNBT<BendingController> creator = new CreateFromNBT<BendingController>() {
		@Override
		public BendingController create(NBTTagCompound nbt, Object[] methodsExtraData, Object[] extraData) {
			int id = nbt.getInteger("ControllerID");
			try {
				BendingController bc = BendingManager.getBending(id);
				System.out.println("ID: " + id);
				System.out.println("Found bendeing controller: " + bc);
				return bc;
			} catch (Exception e) {
				AvatarLog.error("Could not find bending controller from ID '" + id + "' - please check NBT data");
				e.printStackTrace();
				return null;
			}
		}
	};
	
	
	public static final WriteToNBT<BendingController> writer = new WriteToNBT<BendingController>() {
		@Override
		public void write(NBTTagCompound nbt, BendingController object, Object[] methodsExtraData, Object[] extraData) {
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
	
	
}
