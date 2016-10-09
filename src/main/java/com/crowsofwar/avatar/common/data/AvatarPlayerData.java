package com.crowsofwar.avatar.common.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.IBendingState;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.statctrl.StatusControl;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.data.GoreCoreDataSaver;
import com.crowsofwar.gorecore.data.GoreCorePlayerData;
import com.crowsofwar.gorecore.data.PlayerDataFetcher;
import com.crowsofwar.gorecore.data.PlayerDataFetcherServer;
import com.crowsofwar.gorecore.data.PlayerDataFetcherSided;
import com.crowsofwar.gorecore.util.GoreCoreNBTUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class AvatarPlayerData extends GoreCorePlayerData {
	
	// TODO change player data lists into sets, when applicable
	
	private static PlayerDataFetcher<AvatarPlayerData> fetcher;
	
	private Map<Integer, BendingController> bendingControllers;
	private List<BendingController> bendingControllerList;
	/**
	 * Bending controller currently in use, null if no ability is activated
	 */
	private BendingController activeBending;
	
	private Map<Integer, IBendingState> bendingStates;
	private List<IBendingState> bendingStateList;
	
	private Set<StatusControl> statusControls;
	
	private PlayerState state;
	
	public AvatarPlayerData(GoreCoreDataSaver dataSaver, UUID playerID, EntityPlayer player) {
		super(dataSaver, playerID, player);
		bendingControllers = new HashMap<Integer, BendingController>();
		bendingControllerList = new ArrayList<BendingController>();
		bendingStates = new HashMap<Integer, IBendingState>();
		bendingStateList = new ArrayList<IBendingState>();
		statusControls = new HashSet<>();
		state = new PlayerState();
	}
	
	@Override
	protected void readPlayerDataFromNBT(NBTTagCompound nbt) {
		bendingControllerList = AvatarUtils.readFromNBT(BendingController.creator, nbt, "BendingAbilities");
		// bendingStateList = AvatarUtils.readFromNBT(IBendingState.creator, nbt, "BendingData");
		bendingStateList = GoreCoreNBTUtil.readListFromNBT(nbt, "BendingData", IBendingState.creator, this);
		
		bendingControllers.clear();
		for (BendingController controller : bendingControllerList) {
			bendingControllers.put(controller.getID(), controller);
		}
		
		bendingStates.clear();
		for (IBendingState state : bendingStateList) {
			bendingStates.put(state.getId(), state);
		}
		
		activeBending = getBendingController(nbt.getInteger("ActiveBending"));
		
		AvatarUtils.readList(statusControls, nbtTag -> StatusControl.lookup(nbtTag.getInteger("Id")), nbt,
				"StatusControls");
		
	}
	
	@Override
	protected void writePlayerDataToNBT(NBTTagCompound nbt) {
		
		AvatarUtils.writeToNBT(bendingControllerList, nbt, "BendingAbilities", BendingController.writer);
		AvatarUtils.writeToNBT(bendingStateList, nbt, "BendingData", IBendingState.writer);
		nbt.setInteger("ActiveBending", activeBending == null ? -1 : activeBending.getID());
		AvatarUtils.writeList(statusControls, (nbtTag, control) -> nbtTag.setInteger("Id", control.id()), nbt,
				"StatusControls");
	}
	
	@Override
	protected void saveChanges() {
		super.saveChanges();
		updateClient();
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
			IBendingState state = bending.createState(this);
			bendingStates.put(bending.getID(), state);
			bendingStateList.add(state);
			saveChanges();
		} else {
			AvatarLog.warn(
					"Cannot add BendingController " + bending + "' because player already has instance.");
		}
	}
	
	public void addBending(int bendingID) {
		addBending(BendingManager.getBending(bendingID));
	}
	
	/**
	 * Remove the specified bending controller. Please note, this will be saved, so is permanent
	 * (unless another bending controller is added).
	 */
	public void removeBending(BendingController bending) {
		if (hasBending(bending.getID())) {
			// remove state before controller- getBendingState only works with controller present
			IBendingState state = getBendingState(bending);
			bendingStates.remove(bending.getID());
			bendingStateList.remove(state);
			bendingControllers.remove(bending.getID());
			bendingControllerList.remove(bending);
			if (activeBending == bending) activeBending = null;
			saveChanges();
		} else {
			AvatarLog.warn("Cannot remove BendingController '" + bending
					+ "' because player does not have that instance.");
		}
	}
	
	/**
	 * Remove the bending controller with that ID. This will be saved.
	 * 
	 * @param id
	 */
	public void removeBending(int id) {
		if (hasBending(id)) {
			removeBending(getBendingController(id));
		} else {
			AvatarLog.warn(
					"Cannot remove bending with ID '" + id + "' because player does not have that instance.");
		}
	}
	
	/**
	 * hashtag aman. Will be saved.
	 */
	public void takeBending() {
		
		Iterator<BendingController> iterator = bendingControllerList.iterator();
		while (iterator.hasNext()) {
			BendingController bending = iterator.next();
			
			IBendingState state = getBendingState(bending);
			bendingControllers.remove(bending.getID());
			iterator.remove();
			bendingStates.remove(bending.getID());
			bendingStateList.remove(state);
			if (activeBending == bending) activeBending = null;
		}
		saveChanges();
		
	}
	
	/**
	 * Set the active bending controller to the one with that Id. Pass -1 to deactivate bending.
	 */
	public void setActiveBendingController(int controllerId) {
		setActiveBendingController(controllerId == -1 ? null : BendingManager.getBending(controllerId));
	}
	
	/**
	 * Set the active bending controller. Pass null as the argument to deactivate bending.
	 */
	public void setActiveBendingController(BendingController controller) {
		activeBending = controller;
		saveChanges();
	}
	
	/**
	 * No longer used. Cannot activate new bending controllers.
	 */
	@Deprecated
	public BendingController getActiveBendingController() {
		return activeBending;
	}
	
	/**
	 * Returns whether the player is currently bending.
	 */
	public boolean isBending() {
		return activeBending != null;
	}
	
	public List<BendingController> getBendingControllers() {
		return bendingControllerList;
	}
	
	/**
	 * Get the BendingController with that ID. Returns null if there is no bending controller for
	 * that ID.
	 * 
	 * @param id
	 * @return
	 */
	public BendingController getBendingController(int id) {
		return bendingControllers.get(id);
	}
	
	public PlayerState getState() {
		return state;
	}
	
	/**
	 * Gets extra metadata for the given bending controller with that ID, or null if there is no
	 * bending controller.
	 */
	public IBendingState getBendingState(int id) {
		if (!hasBending(id)) {
			AvatarLog.warn("Tried to access BendingState with Id " + id
					+ ", but player does not have the BendingController");
		}
		return hasBending(id) ? bendingStates.get(id) : null;
	}
	
	/**
	 * Get extra metadata for the given bending controller, returns null if no Bending controller.
	 */
	public <STATE extends IBendingState> STATE getBendingState(BendingController<STATE> controller) {
		return (STATE) getBendingState(controller.getID());
	}
	
	public List<IBendingState> getAllBendingStates() {
		return bendingStateList;
	}
	
	public void setBendingState(IBendingState state) {
		bendingStates.put(state.getId(), state);
		bendingStateList.add(state);
	}
	
	public Set<StatusControl> getActiveStatusControls() {
		return Collections.unmodifiableSet(statusControls);
	}
	
	public boolean hasStatusControl(StatusControl status) {
		return statusControls.contains(status);
	}
	
	public void addStatusControl(StatusControl control) {
		statusControls.add(control);
		saveChanges();
	}
	
	public void removeStatusControl(StatusControl control) {
		statusControls.remove(control);
		saveChanges();
	}
	
	/**
	 * Sends a packet to update the client with information about this player data.
	 */
	public void updateClient() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && getPlayerEntity() != null) {
			AvatarMod.network.sendTo(new PacketCPlayerData(this), (EntityPlayerMP) getPlayerEntity());
		}
	}
	
	/**
	 * Send the given bending state to the client.
	 */
	public void sendBendingState(IBendingState state) {
		updateClient(); // TODO send optimized packet only about bending state
	}
	
	public static void initFetcher(PlayerDataFetcher<AvatarPlayerData> clientFetcher) {
		fetcher = new PlayerDataFetcherSided<AvatarPlayerData>(clientFetcher,
				new PlayerDataFetcherServer<AvatarPlayerData>(AvatarWorldData.FETCHER));
	}
	
	public static PlayerDataFetcher<AvatarPlayerData> fetcher() {
		return fetcher;
	}
	
}
