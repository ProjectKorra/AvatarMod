package com.crowsofwar.avatar.common.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

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

	public static NBTTagCompound convertSave(NBTTagCompound preA5) {

		NBTTagCompound converted = preA5.copy();

		fixAbilityData(converted);

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

}
