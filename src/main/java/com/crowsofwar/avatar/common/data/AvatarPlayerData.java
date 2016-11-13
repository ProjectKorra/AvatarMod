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
import com.crowsofwar.avatar.AvatarLog.WarningType;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingAbility;
import com.crowsofwar.avatar.common.bending.BendingController;
import com.crowsofwar.avatar.common.bending.BendingManager;
import com.crowsofwar.avatar.common.bending.BendingType;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.network.packets.PacketCPlayerData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.data.GoreCoreDataSaver;
import com.crowsofwar.gorecore.data.GoreCorePlayerData;
import com.crowsofwar.gorecore.data.PlayerDataFetcher;
import com.crowsofwar.gorecore.data.PlayerDataFetcherServer;
import com.crowsofwar.gorecore.data.PlayerDataFetcherSided;
import com.google.common.collect.ImmutableList;

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
	
	private Map<Integer, BendingState> bendingStates;
	private List<BendingState> bendingStateList;
	
	private Set<StatusControl> statusControls;
	
	private Map<BendingAbility, AbilityData> abilityData;
	
	private PlayerState state;
	
	public AvatarPlayerData(GoreCoreDataSaver dataSaver, UUID playerID, EntityPlayer player) {
		super(dataSaver, playerID, player);
		bendingControllers = new HashMap<Integer, BendingController>();
		bendingControllerList = new ArrayList<BendingController>();
		bendingStates = new HashMap<Integer, BendingState>();
		bendingStateList = new ArrayList<BendingState>();
		statusControls = new HashSet<>();
		abilityData = new HashMap<>();
		state = new PlayerState();
	}
	
	@Override
	protected void readPlayerDataFromNBT(NBTTagCompound readFrom) {
		
		AvatarPlayerData playerData = this;
		AvatarUtils.readList(bendingControllerList,
				compound -> BendingController.find(compound.getInteger("ControllerID")), readFrom,
				"BendingAbilities");
		
		AvatarUtils.readList(bendingStateList, compound -> {
			
			return BendingState.find(playerData, compound);
			
		}, readFrom, "BendingData");
		
		bendingControllers.clear();
		for (BendingController controller : bendingControllerList) {
			bendingControllers.put(controller.getID(), controller);
		}
		
		bendingStates.clear();
		for (BendingState state : bendingStateList) {
			bendingStates.put(state.getId(), state);
		}
		
		AvatarUtils.readList(statusControls, nbtTag -> StatusControl.lookup(nbtTag.getInteger("Id")),
				readFrom, "StatusControls");
		
		AvatarUtils.readMap(abilityData, nbt -> BendingManager.getAbility(nbt.getInteger("Id")), nbt -> {
			BendingAbility ability = BendingManager.getAbility(nbt.getInteger("AbilityId"));
			AbilityData data = new AbilityData(this, ability);
			data.readFromNbt(nbt);
			return data;
		}, readFrom, "AbilityData");
		
	}
	
	@Override
	protected void writePlayerDataToNBT(NBTTagCompound writeTo) {
		
		AvatarUtils.writeList(bendingControllerList,
				(compound, controller) -> compound.setInteger("ControllerID", controller.getID()), writeTo,
				"BendingAbilities");
		AvatarUtils.writeList(bendingStateList, (compound, bendingState) -> bendingState.write(compound),
				writeTo, "BendingData");
		AvatarUtils.writeList(statusControls, (nbtTag, control) -> nbtTag.setInteger("Id", control.id()),
				writeTo, "StatusControls");
		
		AvatarUtils.writeMap(abilityData, (nbt, ability) -> nbt.setInteger("Id", ability.getId()),
				(nbt, data) -> {
					nbt.setInteger("AbilityId", data.getAbility().getId());
					data.writeToNbt(nbt);
				}, writeTo, "AbilityData");
		
	}
	
	@Override
	protected void saveChanges() {
		super.saveChanges();
		if (getPlayerEntity() == null || !getPlayerEntity().worldObj.isRemote) {
			updateClient();
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
	
	/**
	 * Check if the player has that type of bending
	 */
	public boolean hasBending(BendingType type) {
		return hasBending(type.id());
	}
	
	public void addBending(BendingController bending) {
		if (!hasBending(bending.getID())) {
			bendingControllers.put(bending.getID(), bending);
			bendingControllerList.add(bending);
			BendingState state = bending.createState(this);
			bendingStates.put(bending.getID(), state);
			bendingStateList.add(state);
			saveChanges();
		} else {
			AvatarLog.warn(WarningType.INVALID_CODE,
					"Cannot add BendingController " + bending + "' because player already has instance.");
		}
	}
	
	public void addBending(int bendingID) {
		addBending(BendingManager.getBending(bendingID));
	}
	
	/**
	 * Remove the specified bending controller. Please note, this will be saved,
	 * so is permanent (unless another bending controller is added).
	 */
	public void removeBending(BendingController bending) {
		if (hasBending(bending.getID())) {
			// remove state before controller- getBendingState only works with
			// controller present
			BendingState state = getBendingState(bending);
			bendingStates.remove(bending.getID());
			bendingStateList.remove(state);
			bendingControllers.remove(bending.getID());
			bendingControllerList.remove(bending);
			saveChanges();
		} else {
			AvatarLog.warn(WarningType.INVALID_CODE, "Cannot remove BendingController '" + bending
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
			AvatarLog.warn(WarningType.INVALID_CODE,
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
			
			BendingState state = getBendingState(bending);
			bendingControllers.remove(bending.getID());
			iterator.remove();
			bendingStates.remove(bending.getID());
			bendingStateList.remove(state);
		}
		saveChanges();
		
	}
	
	public List<BendingController> getBendingControllers() {
		return bendingControllerList;
	}
	
	/**
	 * Get the BendingController with that ID. Returns null if there is no
	 * bending controller for that ID.
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
	 * Gets extra metadata for the given bending controller with that ID, or
	 * null if there is no bending controller.
	 */
	public BendingState getBendingState(int id) {
		if (!hasBending(id)) {
			AvatarLog.warn(WarningType.INVALID_CODE, "Tried to access BendingState with Id " + id
					+ ", but player does not have the BendingController");
		}
		if (hasBending(id) && !bendingStates.containsKey(id)) {
			bendingStates.put(id, getBendingController(id).createState(this));
			saveChanges();
		}
		return hasBending(id) ? bendingStates.get(id) : null;
	}
	
	/**
	 * Gets extra metadata for the given bending controller with that ID, or
	 * null if there is no bending controller.
	 */
	public BendingState getBendingState(BendingType type) {
		return getBendingState(type.id());
	}
	
	/**
	 * Get extra metadata for the given bending controller, returns null if no
	 * Bending controller.
	 */
	public <STATE extends BendingState> STATE getBendingState(BendingController controller) {
		return (STATE) getBendingState(controller.getID());
	}
	
	public List<BendingState> getAllBendingStates() {
		return bendingStateList;
	}
	
	public void setBendingState(BendingState state) {
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
	 * Retrieves data about the given ability. Will create data if necessary.
	 */
	public AbilityData getAbilityData(BendingAbility ability) {
		if (!abilityData.containsKey(ability)) {
			abilityData.put(ability, new AbilityData(this, ability));
		}
		return abilityData.get(ability);
	}
	
	/**
	 * Gets a list of all ability data contained in this player data. The list
	 * is immutable.
	 */
	public List<AbilityData> getAllAbilityData() {
		return ImmutableList.copyOf(abilityData.values());
	}
	
	public void clearAbilityData() {
		abilityData.clear();
	}
	
	/**
	 * Sends a packet to update the client with information about this player
	 * data.
	 */
	public void updateClient() {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && getPlayerEntity() != null) {
			AvatarMod.network.sendTo(new PacketCPlayerData(this), (EntityPlayerMP) getPlayerEntity());
		}
	}
	
	/**
	 * Send the given bending state to the client.
	 */
	public void sendBendingState(BendingState state) {
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
