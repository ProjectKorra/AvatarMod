package com.maxandnoah.avatar.common.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.maxandnoah.avatar.AvatarLog;
import com.maxandnoah.avatar.common.bending.IBendingController;
import com.maxandnoah.avatar.common.bending.IBendingState;
import com.maxandnoah.avatar.common.bending.BendingManager;
import com.maxandnoah.avatar.common.util.AvatarUtils;
import com.maxandnoah.avatar.common.util.BlockPos;

import crowsofwar.gorecore.data.GoreCoreDataSaver;
import crowsofwar.gorecore.data.GoreCorePlayerData;
import crowsofwar.gorecore.util.GoreCoreNBTUtil;
import net.minecraft.nbt.NBTTagCompound;

public class AvatarPlayerData extends GoreCorePlayerData {
	
	private Map<Integer, IBendingController> bendingControllers;
	private List<IBendingController> bendingControllerList;
	/**
	 * Bending controller currently in use, null if no ability is activated
	 */
	private IBendingController activeBending;
	/**
	 * Additional information used by the active IBendingController. Null
	 * if activeBending is null.
	 */
	private IBendingState bendingState;
	
	private PlayerState state;
	
	public AvatarPlayerData(GoreCoreDataSaver dataSaver, UUID playerID) {
		super(dataSaver, playerID);
		bendingControllers = new HashMap<Integer, IBendingController>();
		bendingControllerList = new ArrayList<IBendingController>();
		state = new PlayerState();
	}
	
	@Override
	protected void readPlayerDataFromNBT(NBTTagCompound nbt) {
		bendingControllerList = AvatarUtils.readFromNBT(IBendingController.creator, nbt, "BendingAbilities");
		
		bendingControllers.clear();
		for (IBendingController controller : bendingControllerList) {
			bendingControllers.put(controller.getID(), controller);
		}
		
		activeBending = getBendingController(nbt.getInteger("ActiveBending"));
		if (activeBending != null) {
			bendingState = activeBending.createState(this);
			bendingState.readFromNBT(GoreCoreNBTUtil.getOrCreateNestedCompound(nbt, "BendingState"));
		}
		
	}
	
	@Override
	protected void writePlayerDataToNBT(NBTTagCompound nbt) {
		AvatarUtils.writeToNBT(bendingControllerList, nbt, "BendingAbilities", IBendingController.writer);
		nbt.setInteger("ActiveBending", activeBending == null ? -1 : activeBending.getID());
		if (activeBending != null) {
			bendingState.writeToNBT(GoreCoreNBTUtil.getOrCreateNestedCompound(nbt, "BendingState"));
		}
	}
	
	public boolean isBender() {
		return !bendingControllers.isEmpty();
	}
	
	/**
	 * Check if the player has that type of bending
	 */
	public boolean hasBending(int id) {
		return bendingControllers.containsKey(id);
	}
	
	public boolean isEarthbender() {
		return hasBending(BendingManager.BENDINGID_EARTHBENDING);
	}
	
	public void addBending(IBendingController bending) {
		if (!hasBending(bending.getID())) {
			bendingControllers.put(bending.getID(), bending);
			bendingControllerList.add(bending);
			saveChanges();
		} else {
			AvatarLog.warn("Cannot add BendingController " + bending + "' because player already has instance.");
		}
	}
	
	public void addBending(int bendingID) {
		addBending(BendingManager.getBending(bendingID));
	}
	
	/**
	 * Remove the specified bending controller. Please note, this
	 * will be saved, so is permanent (unless another bending controller
	 * is added).
	 */
	public void removeBending(IBendingController bending) {
		if (hasBending(bending.getID())) {
			bendingControllers.remove(bending);
			bendingControllerList.remove(bending);
			saveChanges();
		} else {
			AvatarLog.warn("Cannot remove BendingController '" + bending + "' because player does not have that instance.");
		}
	}
	
	/**
	 * Remove the bending controller with that ID. This will be saved.
	 * @param id
	 */
	public void removeBending(int id) {
		if (hasBending(id)) {
			removeBending(getBendingController(id));
		} else {
			AvatarLog.warn("Cannot remove bending with ID '" + id + "' because player does not have that instance.");
		}
	}
	
	/**
	 * hashtag aman. Will be saved.
	 */
	public void takeBending() {
		bendingControllers.clear();
		bendingControllerList.clear();
		saveChanges();
	}
	
	/**
	 * Set the active bending controller. Pass null as the argument
	 * to deactivate bending.
	 */
	public void setActiveBendingController(IBendingController controller) {
		activeBending = controller;
		bendingState = controller == null ? null : controller.createState(this);
		saveChanges();
	}
	
	public IBendingController getActiveBendingController() {
		return activeBending;
	}
	
	/**
	 * Returns whether the player is currently bending.
	 */
	public boolean isBending() {
		return activeBending != null;
	}
	
	public List<IBendingController> getBendingControllers() {
		return bendingControllerList;
	}
	
	/**
	 * Get the BendingController with that ID. Returns null if there is no
	 * bending controller for that ID.
	 * @param id
	 * @return
	 */
	public IBendingController getBendingController(int id) {
		return bendingControllers.get(id);
	}
	
	public PlayerState getState() {
		return state;
	}
	
	/**
	 * Gets extra metadata for the current bending state.
	 */
	public IBendingState getBendingState() {
		return bendingState;
	}
	
}
