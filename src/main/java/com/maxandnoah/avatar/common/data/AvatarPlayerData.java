package com.maxandnoah.avatar.common.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.maxandnoah.avatar.AvatarLog;
import com.maxandnoah.avatar.common.bending.BendingController;
import com.maxandnoah.avatar.common.bending.BendingManager;
import com.maxandnoah.avatar.common.util.AvatarUtils;

import crowsofwar.gorecore.data.GoreCoreDataSaver;
import crowsofwar.gorecore.data.GoreCorePlayerData;
import crowsofwar.gorecore.util.GoreCoreNBTUtil;
import net.minecraft.nbt.NBTTagCompound;

public class AvatarPlayerData extends GoreCorePlayerData {
	
	private Map<Integer, BendingController> bendingControllers;
	private List<BendingController> bendingControllerList;
	/**
	 * Bending controller currently in use, null if no ability is activated
	 */
	private BendingController activeBending;
	
	public AvatarPlayerData(GoreCoreDataSaver dataSaver, UUID playerID) {
		super(dataSaver, playerID);
		bendingControllers = new HashMap<Integer, BendingController>();
	}
	
	@Override
	protected void readPlayerDataFromNBT(NBTTagCompound nbt) {
		bendingControllerList = AvatarUtils.readFromNBT(BendingController.creator, nbt, "BendingAbilities");
		for (BendingController controller : bendingControllerList) {
			bendingControllers.put(controller.getID(), controller);
		}
		activeBending = getBendingController(nbt.getInteger("ActiveBending"));
	}
	
	@Override
	protected void writePlayerDataToNBT(NBTTagCompound nbt) {
		AvatarUtils.writeToNBT(bendingControllerList, nbt, "BendingAbilities", BendingController.writer);
		nbt.setInteger("ActiveBending", activeBending == null ? -1 : activeBending.getID());
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
	
	public void addBending(BendingController bending) {
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
	public void removeBending(BendingController bending) {
		if (hasBending(bending.getID())) {
			bendingControllers.remove(bending);
			bendingControllerList.remove(bending);
			saveChanges();
		} else {
			AvatarLog.warn("Cannot remove BendingController '" + bending + "' because player does not have that instance.");
		}
	}
	
	public void removeBending(int id) {
		if (hasBending(id)) {
			removeBending(getBendingController(id));
		} else {
			AvatarLog.warn("Cannot remove bending with ID '" + id + "' because player does not have that instance.");
		}
	}
	
	/**
	 * hashtag aman. (note: This will be saved, and is permanent.)
	 */
	public void takeBending() {
		bendingControllers.clear();
		bendingControllerList.clear();
		saveChanges();
	}
	
	public void setActiveBendingController(BendingController controller) {
		activeBending = controller;
		saveChanges();
	}
	
	public BendingController getActiveBendingController() {
		return activeBending;
	}
	
	public boolean isBending() {
		return activeBending != null;
	}
	
	public List<BendingController> getBendingControllers() {
		return bendingControllerList;
	}
	
	/**
	 * Get the BendingController with that ID. Returns null if there is no
	 * bending controller for that ID.
	 * @param id
	 * @return
	 */
	public BendingController getBendingController(int id) {
		return bendingControllers.get(id);
	}
	
}
