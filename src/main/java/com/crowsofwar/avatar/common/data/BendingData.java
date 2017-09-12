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

import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.crowsofwar.avatar.common.config.ConfigChi.CHI_CONFIG;
import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.nestedCompound;

/**
 * @author CrowsOfWar
 */
public class BendingData {

	// static methods
	public static BendingData get(@Nonnull EntityLivingBase entity) {
		if (entity instanceof EntityPlayer) {
			return AvatarPlayerData.fetcher().fetch((EntityPlayer) entity).getData();
		} else {
			return Bender.get(entity).getData();
		}
	}

	public static BendingData get(World world, UUID playerId) {
		return AvatarPlayerData.fetcher().fetch(world, playerId).getData();
	}

	public static BendingData get(World world, String playerName) {
		return AvatarPlayerData.fetcher().fetch(world, playerName).getData();
	}

	private final Consumer<DataCategory> saveCategory;
	private final Runnable saveAll;

	private final Set<UUID> bendings;
	private final Set<StatusControl> statusControls;
	private final Map<String, AbilityData> abilityData;
	private final Set<TickHandler> tickHandlers;
	private final Map<TickHandler, Integer> tickHandlerDuration;
	private UUID activeBending;
	private Chi chi;
	private MiscData miscData;

	/**
	 * Create a new BendingData
	 *
	 * @param saveCategory Function to save data in one category
	 * @param saveAll      Function to save all data
	 */
	public BendingData(Consumer<DataCategory> saveCategory, Runnable saveAll) {
		this.saveCategory = saveCategory;
		this.saveAll = saveAll;

		bendings = new HashSet<>();
		statusControls = new HashSet<>();
		abilityData = new HashMap<>();
		tickHandlers = new HashSet<>();
		tickHandlerDuration = new HashMap<>();
		activeBending = null;
		chi = new Chi(this);
		miscData = new MiscData(() -> save(DataCategory.MISC_DATA));
	}

	// ================================================================================
	// BENDINGS METHODS
	// ================================================================================

	/**
	 * Checks if the player has that bending style.
	 */
	public boolean hasBendingId(UUID bendingId) {
		return bendings.contains(bendingId);
	}

	/**
	 * @see #hasBendingId(UUID)
	 */
	public boolean hasBending(BendingStyle bending) {
		return hasBendingId(bending.getId());
	}

	/**
	 * Adds a new bending style to the BendingData.
	 */
	public void addBendingId(UUID bendingId) {
		if (bendings.add(bendingId)) {
			save(DataCategory.BENDING_LIST);
		}
	}

	/**
	 * @see #addBendingId(UUID)
	 */
	public void addBending(BendingStyle bending) {
		addBendingId(bending.getId());
	}

	/**
	 * Remove the specified bending style.
	 */
	public void removeBendingId(UUID bendingId) {
		if (bendings.remove(bendingId)) {
			save(DataCategory.BENDING_LIST);
		}
	}

	/**
	 * @see #removeBendingId(UUID)
	 */

	public void removeBending(BendingStyle bending) {
		removeBendingId(bending.getId());
	}

	public List<BendingStyle> getAllBending() {
		return bendings.stream().map(BendingStyles::get).collect(Collectors.toList());
	}

	public List<UUID> getAllBendingIds() {
		return new ArrayList<>(bendings);
	}

	public void setAllBending(List<BendingStyle> bending) {
		List<UUID> bendingIds = bending.stream().map(BendingStyle::getId).collect(Collectors.toList());
		setAllBendingIds(bendingIds);
	}

	public void setAllBendingIds(List<UUID> bendingIds) {
		bendings.clear();
		bendings.addAll(bendingIds);
	}

	public void clearBending() {
		bendings.clear();
	}

	// ================================================================================
	// ACTIVE BENDING
	// ================================================================================

	@Nullable
	public UUID getActiveBendingId() {
		if (!bendings.isEmpty() && activeBending == null) {
			activeBending = bendings.iterator().next();
		}
		if (activeBending != null && !bendings.isEmpty() && !bendings.contains(activeBending)) {
			activeBending = bendings.iterator().next();
		}
		if (bendings.isEmpty() && activeBending != null) {
			activeBending = null;
		}
		return activeBending;
	}

	@Nullable
	public BendingStyle getActiveBending() {
		return BendingStyles.get(getActiveBendingId());
	}

	public void setActiveBendingId(UUID id) {
		if (bendings.contains(id)) {
			activeBending = id;
			save(DataCategory.ACTIVE_BENDING);
		}
	}

	public void setActiveBending(BendingStyle bending) {
		setActiveBendingId(bending.getId());
	}

	// ================================================================================
	// STATUS CONTROLS
	// ================================================================================

	public boolean hasStatusControl(StatusControl control) {
		return statusControls.contains(control);
	}

	public void addStatusControl(StatusControl control) {
		if (statusControls.add(control)) {
			save(DataCategory.STATUS_CONTROLS);
		}
	}

	public void removeStatusControl(StatusControl control) {
		if (statusControls.remove(control)) {
			save(DataCategory.STATUS_CONTROLS);
		}
	}

	public List<StatusControl> getAllStatusControls() {
		return new ArrayList<>(statusControls);
	}

	public void setAllStatusControls(List<StatusControl> controls) {
		statusControls.clear();
		statusControls.addAll(controls);
	}

	public void clearStatusControls() {
		statusControls.clear();
	}

	// ================================================================================
	// ABILITY DATA
	// ================================================================================

	public boolean hasAbilityData(String abilityName) {
		return abilityData.get(abilityName) != null;
	}

	public boolean hasAbilityData(Ability ability) {
		return hasAbilityData(ability.getName());
	}

	/**
	 * Retrieves data about the given ability. Will get data if necessary.
	 */

	public AbilityData getAbilityData(String abilityName) {
		AbilityData data = abilityData.get(abilityName);
		if (data == null) {
			data = new AbilityData(this, Abilities.get(abilityName));
			abilityData.put(abilityName, data);
			save(DataCategory.ABILITY_DATA);
		}

		return data;
	}

	public AbilityData getAbilityData(Ability ability) {
		return getAbilityData(ability.getName());
	}

	public void setAbilityData(String abilityName, AbilityData data) {
		abilityData.put(abilityName, data);
	}

	public void setAbilityData(Ability ability, AbilityData data) {
		setAbilityData(ability.getName(), data);
	}

	/**
	 * Gets a list of all ability data contained in this player data.
	 */

	public List<AbilityData> getAllAbilityData() {
		return new ArrayList<>(abilityData.values());
	}

	public Map<String, AbilityData> getAbilityDataMap() {
		return new HashMap<>(abilityData);
	}

	public void setAbilityDataMap(Map<String, AbilityData> map) {
		abilityData.clear();
		abilityData.putAll(map);
	}

	/**
	 * Removes all ability data associations
	 */

	public void clearAbilityData() {
		abilityData.clear();
	}

	// ================================================================================
	// CHI
	// ================================================================================

	/**
	 * Gets the chi information about the bender
	 */

	public Chi chi() {
		return chi;
	}

	public void setChi(Chi chi) {
		this.chi = chi;
		save(DataCategory.CHI);
	}

	public void updateMaxChi() {
		float chi = 0;
		chi += getAllBending().size() * CHI_CONFIG.bonusLearnedBending;
		for (AbilityData aData : getAllAbilityData()) {
			if (!aData.isLocked() && hasBendingId(aData.getAbility().getBendingId())) {
				chi += CHI_CONFIG.bonusAbility;
				chi += aData.getLevel() * CHI_CONFIG.bonusAbilityLevel;
			}
		}
		if (chi >= CHI_CONFIG.maxChiCap) chi = CHI_CONFIG.maxChiCap;

		// needed to avoid StackOverflowError
		if (chi != chi().getMaxChi()) {
			float old = chi().getMaxChi();
			chi().setMaxChi(chi);

			// Don't need to wait for new chi to regen
			if (chi > old) {
				chi().changeTotalChi(chi - old);
			}
		}
	}

	// ================================================================================
	// TICK HANDLERS
	// ================================================================================

	public boolean hasTickHandler(TickHandler handler) {
		return tickHandlers.contains(handler);
	}

	/**
	 * Returns how many ticks the given TH has been executing for. Is reset on the world reload
	 * and unsynchronized, so this should only be used temporarily. Returns -1 if doesn't have
	 * the TickHandler.
	 */
	public int getTickHandlerDuration(TickHandler handler) {
		if (hasTickHandler(handler)) {
			return tickHandlerDuration.get(handler);
		} else {
			return -1;
		}
	}

	public void setTickHandlerDuration(TickHandler handler, int duration) {
		if (hasTickHandler(handler)) {
			tickHandlerDuration.put(handler, duration);
		}
	}

	public void addTickHandler(TickHandler handler) {
		if (tickHandlers.add(handler)) {
			tickHandlerDuration.put(handler, 0);
			save(DataCategory.TICK_HANDLERS);
		}
	}

	public void removeTickHandler(TickHandler handler) {
		if (tickHandlers.remove(handler)) {
			tickHandlerDuration.remove(handler);
			save(DataCategory.TICK_HANDLERS);
		}
	}

	public List<TickHandler> getAllTickHandlers() {
		return new ArrayList<>(tickHandlers);
	}

	public void setAllTickHandlers(List<TickHandler> handlers) {
		tickHandlers.clear();
		tickHandlers.addAll(handlers);

		Map<TickHandler, Integer> newDurations = handlers.stream().collect(Collectors.toMap(Function.identity()
				, h -> 0));
		tickHandlerDuration.clear();
		tickHandlerDuration.putAll(newDurations);
	}

	public void clearTickHandlers() {
		tickHandlers.clear();
		tickHandlerDuration.clear();
	}

	// ================================================================================
	// MISC
	// ================================================================================

	public MiscData getMiscData() {
		return miscData;
	}

	public void setMiscData(MiscData md) {
		this.miscData = md;
	}

	public float getFallAbsorption() {
		return miscData.getFallAbsorption();
	}

	public void setFallAbsorption(float fallAbsorption) {
		miscData.setFallAbsorption(fallAbsorption);
	}

	public int getTimeInAir() {
		return miscData.getTimeInAir();
	}

	public void setTimeInAir(int time) {
		miscData.setTimeInAir(time);
	}

	public int getAbilityCooldown() {
		return miscData.getAbilityCooldown();
	}

	public void setAbilityCooldown(int cooldown) {
		miscData.setAbilityCooldown(cooldown);
	}

	public void decrementCooldown() {
		miscData.decrementCooldown();
	}

	public boolean isWallJumping() {
		return miscData.isWallJumping();
	}

	public void setWallJumping(boolean wallJumping) {
		miscData.setWallJumping(wallJumping);
	}

	public int getPetSummonCooldown() {
		return miscData.getPetSummonCooldown();
	}

	public void setPetSummonCooldown(int cooldown) {
		miscData.setPetSummonCooldown(cooldown);
	}

	public boolean getBisonFollowMode() {
		return miscData.getBisonFollowMode();
	}

	public void setBisonFollowMode(boolean followMode) {
		getMiscData().setBisonFollowMode(followMode);
	}

	public boolean getCanUseAbilities() {
		return miscData.getCanUseAbilities();
	}

	public void setCanUseAbilities(boolean canUseAbilities) {
		miscData.setCanUseAbilities(canUseAbilities);
	}

	public void writeToNbt(NBTTagCompound writeTo) {

		// @formatter:off

		AvatarUtils.writeList(bendings,
				(nbt, controllerId) -> nbt.setUniqueId("ControllerID", controllerId),
				writeTo,
				"BendingControllers");

		AvatarUtils.writeList(statusControls,
				(nbt, control) -> nbt.setInteger("Id", control.id()),
				writeTo,
				"StatusControls");

		AvatarUtils.writeMap(getAbilityDataMap(),
				// AbilityData key compound - identification info for the AD
				(nbt, abilityName) -> nbt.setString("Name", abilityName),
				// AbilityData value compound - write the actual data
				(nbt, data) -> {
					nbt.setString("Name", data.getAbilityName());
					data.writeToNbt(nbt);
				},
				writeTo,
				"AbilityData");

		getMiscData().writeToNbt(nestedCompound(writeTo, "Misc"));

		chi().writeToNBT(writeTo);

		AvatarUtils.writeList(tickHandlers,
				(nbt, handler) -> nbt.setInteger("Id", handler.id()),
				writeTo,
				"TickHandlers");

		// @formatter:on

	}

	public void readFromNbt(NBTTagCompound readFrom) {

		// @formatter:off

		AvatarUtils.readList(bendings,
				nbt -> nbt.getUniqueId("ControllerID"),
				readFrom,
				"BendingControllers");

		AvatarUtils.readList(statusControls,
				nbt -> StatusControl.lookup(nbt.getInteger("Id")),
				readFrom,
				"StatusControls");

		AvatarUtils.readMap(abilityData,
				// AbilityData key compound - identify the AD
				nbt -> nbt.getString("Name"),
				// AbilityData value compound - actual AD data
				nbt -> {
					String abilityName = nbt.getString("Name");
					AbilityData data = new AbilityData(this, abilityName);
					data.readFromNbt(nbt);
					return data;
				}, readFrom, "AbilityData");

		getMiscData().readFromNbt(nestedCompound(readFrom, "Misc"));

		chi().readFromNBT(readFrom);

		AvatarUtils.readList(tickHandlers, //
				nbt -> TickHandler.fromId(nbt.getInteger("Id")), //
				readFrom, "TickHandlers");

		// @formatter:on

	}

	/**
	 * Save this BendingData
	 */

	public void save(DataCategory category) {
		saveCategory.accept(category);
	}

	public void saveAll() {
		saveAll.run();
	}

}