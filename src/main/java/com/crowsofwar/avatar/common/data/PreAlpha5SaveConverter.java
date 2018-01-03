package com.crowsofwar.avatar.common.data;

import net.minecraft.nbt.NBTTagCompound;

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


		return converted;


	}

}
