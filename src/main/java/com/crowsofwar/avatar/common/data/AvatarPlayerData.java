package com.crowsofwar.avatar.common.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.IBendingController;
import com.crowsofwar.avatar.common.bending.IBendingState;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.BlockPos;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import crowsofwar.gorecore.data.GoreCoreDataSaver;
import crowsofwar.gorecore.data.GoreCorePlayerData;
import crowsofwar.gorecore.util.GoreCoreNBTUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class AvatarPlayerData extends GoreCorePlayerData {
	
	private Map<Integer, IBendingController> bendingControllers;
	private List<IBendingController> bendingControllerList;
	/**
	 * Bending controller currently in use, null if no ability is activated
	 */
	private IBendingController activeBending;
	
	private Map<Integer, IBendingState> bendingStates;
	private List<IBendingState> bendingStateList;
	
	private PlayerState state;
	
	public AvatarPlayerData(GoreCoreDataSaver dataSaver, UUID playerID) {
		super(dataSaver, playerID);
		bendingControllers = new HashMap<Integer, IBendingController>();
		bendingControllerList = new ArrayList<IBendingController>();
		bendingStates = new HashMap<Integer, IBendingState>();
		bendingStateList = new ArrayList<IBendingState>();
		state = new PlayerState();
	}
	
	@Override
	protected void readPlayerDataFromNBT(NBTTagCompound nbt) {
		bendingControllerList = AvatarUtils.readFromNBT(IBendingController.creator, nbt, "BendingAbilities");
//		bendingStateList = AvatarUtils.readFromNBT(IBendingState.creator, nbt, "BendingData");
		bendingStateList = GoreCoreNBTUtil.readListFromNBT(nbt, "BendingData", IBendingState.creator, this);
		
		bendingControllers.clear();
		for (IBendingController controller : bendingControllerList) {
			bendingControllers.put(controller.getID(), controller);
		}
		
		bendingStates.clear();
		for (IBendingState state : bendingStateList) {
			bendingStates.put(state.getId(), state);
		}
		
		activeBending = getBendingController(nbt.getInteger("ActiveBending"));
		
	}
	
	@Override
	protected void writePlayerDataToNBT(NBTTagCompound nbt) {
		AvatarUtils.writeToNBT(bendingControllerList, nbt, "BendingAbilities", IBendingController.writer);
		AvatarUtils.writeToNBT(bendingStateList, nbt, "BendingData", IBendingState.writer);
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
	
	public void addBending(IBendingController bending) {
		if (!hasBending(bending.getID())) {
			bendingControllers.put(bending.getID(), bending);
			bendingControllerList.add(bending);
			IBendingState state = bending.createState(this);
			bendingStates.put(bending.getID(), state);
			bendingStateList.add(state);
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
	 * Set the active bending controller to the one with that Id.
	 * Pass -1 to deactivate bending.
	 */
	public void setActiveBendingController(int controllerId) {
		setActiveBendingController(controllerId == -1 ? null : BendingManager.getBending(controllerId));
	}
	
	/**
	 * Set the active bending controller. Pass null as the argument
	 * to deactivate bending.
	 */
	public void setActiveBendingController(IBendingController controller) {
		activeBending = controller;
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
	 * Gets extra metadata for the given bending controller with that
	 * ID, or null if there is no bending controller.
	 */
	public IBendingState getBendingState(int id) {
		return hasBending(id) ? bendingStates.get(id) : null;
	}
	
	/**
	 * Get extra metadata for the given bending controller, returns null
	 * if no Bending controller.
	 */
	public IBendingState getBendingState(IBendingController controller) {
		return getBendingState(controller.getID());
	}
	
	public List<IBendingState> getAllBendingStates() {
		return bendingStateList;
	}
	
	public void setBendingState(IBendingState state) {
		bendingStates.put(state.getId(), state);
		bendingStateList.add(state);
	}
	
	/**
	 * Sends a packet to update the client with information
	 * about this player data.
	 */
	public void updateClient() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			AvatarMod.network.sendTo(new PacketCPlayerData(this), (EntityPlayerMP) getState().getPlayerEntity());
	}
	
	/**
	 * Send the given bending state to the client.
	 */
	public void sendBendingState(IBendingState state) {
		updateClient(); // TODO send optimized packet only about bending state
	}
	
}
