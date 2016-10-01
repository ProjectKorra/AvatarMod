package com.crowsofwar.avatar.common.bending;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.avatar.common.util.event.EventNotifier;
import com.crowsofwar.avatar.common.util.event.Observer;
import com.crowsofwar.avatar.common.util.event.Subject;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.CreateFromNBT;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.ReadableWritable;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.WriteToNBT;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Base class for bending abilities. All bending classes extend this one. They can save data to NBT
 * if necessary. Functionality for bending should be in subclasses. Bending controllers are
 * singletons, but must be accessed through {@link BendingManager}.
 * <p>
 * For the sake of abstraction, you won't need to refer to bending controllers by their concrete
 * names.
 * <p>
 * Subclasses have access to client input via optionally* implementable hook methods.
 * <p>
 * *Optionally = the subclass must declare the method, but does not need to put any code inside of
 * it.
 *
 * @param <STATE>
 *            The IBendingState this controller is using
 * 
 */
public abstract class BendingController<STATE extends IBendingState> implements ReadableWritable, Subject {
	
	public static final CreateFromNBT<BendingController> creator = new CreateFromNBT<BendingController>() {
		@Override
		public BendingController create(NBTTagCompound nbt, Object[] methodsExtraData, Object[] extraData) {
			int id = nbt.getInteger("ControllerID");
			try {
				BendingController bc = BendingManager.getBending(id);
				return bc;
			} catch (Exception e) {
				AvatarLog.error(
						"Could not find bending controller from ID '" + id + "' - please check NBT data");
				e.printStackTrace();
				return null;
			}
		}
	};
	
	public static final WriteToNBT<BendingController> writer = new WriteToNBT<BendingController>() {
		@Override
		public void write(NBTTagCompound nbt, BendingController object, Object[] methodsExtraData,
				Object[] extraData) {
			nbt.setInteger("ControllerID", object.getID());
		}
	};
	
	/**
	 * RNG available for convenient use.
	 */
	public static final Random random = new Random();
	
	private final List<BendingAbility<STATE>> abilities;
	private final Subject eventNotifier;
	
	public BendingController() {
		this.abilities = new ArrayList<>();
		this.eventNotifier = new EventNotifier();
	}
	
	protected void addAbility(BendingAbility<STATE> ability) {
		this.abilities.add(ability);
	}
	
	/**
	 * @deprecated
	 * 			
	 * 			Get an identifier for this bending ability. Should be unique per-class. (not
	 *             per-instance)
	 * @see #getType()
	 */
	@Deprecated
	public abstract int getID();
	
	/**
	 * Gets an identifier for this bending controller.
	 */
	public BendingType getType() {
		return BendingType.values()[getID()];
	}
	
	/**
	 * Called to create an IBendingState for the player. This allows the BendingController to store
	 * specific metadata for each player, making things much easier. <br />
	 * <br />
	 * Keep in mind - when loading a saved state, it will be read from NBT. However, when creating a
	 * new bending state when an ability is activated, it will NOT read from NBT. So ensure that all
	 * values are initialized.
	 * 
	 * @return
	 */
	public STATE createState(AvatarPlayerData data) {
		return null;
	}
	
	/**
	 * Get the ability to be executed for the given client input.
	 * 
	 * @param data
	 *            Player data containing necessary information
	 * @param input
	 *            Input received from client
	 * @return The ability to execute, or null for none.
	 */
	public abstract BendingAbility getAbility(AvatarPlayerData data, AvatarControl input);
	
	/**
	 * Get information about this bending controller's radial menu.
	 */
	public abstract BendingMenuInfo getRadialMenu();
	
	/**
	 * Get the name of this bending controller in lowercase. e.g. "earthbending"
	 */
	public abstract String getControllerName();
	
	public List<BendingAbility<STATE>> getAllAbilities() {
		return this.abilities;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {}
	
	@Override
	public <E> void addObserver(Observer<E> obs, Class<E> eventClass) {
		eventNotifier.addObserver(obs, eventClass);
	}
	
	@Override
	public <E> void removeObserver(Observer<E> obs, Class<E> eventClass) {
		eventNotifier.removeObserver(obs, eventClass);
	}
	
	@Override
	public void post(Object e) {
		eventNotifier.post(e);
	}
	
}
