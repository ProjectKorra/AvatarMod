package com.crowsofwar.avatar.util.data;

import com.crowsofwar.avatar.bending.bending.air.Airbending;
import com.crowsofwar.avatar.bending.bending.earth.Earthbending;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.bending.bending.water.Waterbending;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Provides support for reading pre-a5.0 BendingData from NBT.
 * <p>
 * There were many changes in a5.0 save structure, so trying to read pre-a5.0 save data will
 * NOT work. Need a converter to convert this pre-a5.0 data to modern structure.
 */
public class PreAlpha5SaveConverter {

	/*
	Changes in the format from a4.6 to a5.0:

	Ability data KEY:
	* transfer _AbilityName --> Name

	Ability data VALUE:
	* before "AbilityId", now need "Name"

	Bending controllers
	* before "ControllerID" type INT, now "ControllerID" type UUID

	 */

	/**
	 * Previously, bending IDs were stored as integers. Now, they use UUIDs. This map converts old
	 * integer IDs to new UUID IDs.
	 */
	private static final Map<Integer, UUID> bendingIdConversion;

	static {
		bendingIdConversion = new HashMap<>();
		bendingIdConversion.put(1, Earthbending.ID);
		bendingIdConversion.put(2, Firebending.ID);
		bendingIdConversion.put(3, Waterbending.ID);
		bendingIdConversion.put(4, Airbending.ID);
	}

	public static NBTTagCompound convertSave(NBTTagCompound preA5, int currentSaveVersion) {

		NBTTagCompound converted = preA5.copy();

		fixAbilityData(converted);
		fixBendingControllers(converted);
		converted.setInteger("SaveVersion", currentSaveVersion);

		return converted;

	}

	private static void fixAbilityData(NBTTagCompound nbt) {

		NBTTagList listTag = nbt.getTagList("AbilityData", 10);
		for (int i = 0; i < listTag.tagCount(); i++) {
			NBTTagCompound item = listTag.getCompoundTagAt(i);

			// Fix ability data key
			// Simple fix, need to move String value with key "_AbilityName" to new key "Name"
			NBTTagCompound keyTag = item.getCompoundTag("Key");
			String abilityName = keyTag.getString("_AbilityName");

			// Just make sure that it's actually legacy
			if (abilityName.equals("")) {
				return;
			}

			keyTag.setString("Name", abilityName);
			keyTag.removeTag("_AbilityName");
			keyTag.removeTag("Id");

			// Fix ability data value
			// Previously used "AbilityId" tag, now just needs "Name" tag
			NBTTagCompound valueTag = item.getCompoundTag("Value");
			valueTag.removeTag("AbilityId");
			valueTag.setString("Name", abilityName);

		}

	}

	private static void fixBendingControllers(NBTTagCompound nbt) {

		NBTTagList listTag = nbt.getTagList("BendingControllers", 10);
		for (int i = 0; i < listTag.tagCount(); i++) {
			NBTTagCompound item = listTag.getCompoundTagAt(i);

			// Just make sure that the ControllerID is the legacy type
			if (item.hasKey("ControllerID", 3)) {

				// Fix ControllerID value
				// Previously this was stored as an int
				// Now this is stored as a UUID
				int oldId = item.getInteger("ControllerID");
				item.removeTag("ControllerID");

				UUID newId = bendingIdConversion.get(oldId);
				item.setUniqueId("ControllerID", newId);

			}

		}

	}

}
