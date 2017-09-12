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

package com.crowsofwar.avatar.common.util;

import com.crowsofwar.avatar.AvatarLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.crowsofwar.avatar.AvatarLog.WarningType.INVALID_SAVE;

public class AvatarUtils {

	public static <T extends Entity> Comparator<T> getSortByDistanceComparator
			(Function<T, Float> distanceSupplier) {

		return (e1, e2) -> {
			float d1 = distanceSupplier.apply(e1);
			float d2 = distanceSupplier.apply(e2);

			if (d1 < d2) {
				return -1;
			} else {
				return d1 > d2 ? 1 : 0;
			}
		};

	}

	/**
	 * Solves the issue where players on singleplayer/LAN will sometimes not
	 * have velocity or position changed.
	 */
	public static void afterVelocityAdded(Entity entity) {
		if (entity instanceof EntityPlayerMP) {
			((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityTeleport(entity));
			((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));
		}
	}

	/**
	 * Ensures that the angle is in the range of 0-360.
	 */
	public static float normalizeAngle(float angle) {
		while (angle < 0) {
			angle += 360;
		}
		angle %= 360;
		return angle;
	}

	/**
	 * Clears the list(or collection), and adds items from the NBT list. Does
	 * not permit null values.
	 *
	 * @param itemProvider Loads items from the list. Takes an NBT for that item, and
	 *                     returns the actual item object.
	 * @param nbt          NBT compound to load the list from
	 * @param listName     The name of the list tag
	 */
	public static <T> void readList(Collection<T> list, Function<NBTTagCompound, T> itemProvider,
									NBTTagCompound nbt, String listName) {

		list.clear();

		NBTTagList listTag = nbt.getTagList(listName, 10);
		for (int i = 0; i < listTag.tagCount(); i++) {
			NBTTagCompound item = listTag.getCompoundTagAt(i);
			T read = itemProvider.apply(item);
			if (read != null) {
				list.add(read);
			} else {
				AvatarLog.warn(INVALID_SAVE,
						"Invalid list " + listName + ", contains unknown value: " + item);
			}
		}

	}

	/**
	 * Writes the list to NBT.
	 *
	 * @param list     The list to write
	 * @param writer   Responsible for actually writing the desired data to NBT.
	 *                 Takes the NBT to write to & the item.
	 * @param nbt      NBT compound to write list to
	 * @param listName The name of the list tag
	 */
	public static <T> void writeList(Collection<T> list, BiConsumer<NBTTagCompound, T> writer,
									 NBTTagCompound nbt, String listName) {

		NBTTagList listTag = new NBTTagList();

		for (T item : list) {

			NBTTagCompound nbtItem = new NBTTagCompound();
			writer.accept(nbtItem, item);
			listTag.appendTag(nbtItem);

		}

		nbt.setTag(listName, listTag);

	}

	/**
	 * Clears the map and adds the saved entries. Does not permit null keys or
	 * values.
	 *
	 * @param map           Map to read into
	 * @param keyProvider   Creates a key object from a given NBT. The NBT is only used by
	 *                      the key.
	 * @param valueProvider Creates a value object from a given NBT. The NBT is only used
	 *                      by the value.
	 * @param nbt           NBT to read from
	 * @param mapName       Name to store it as
	 */
	public static <K, V> void readMap(Map<K, V> map, Function<NBTTagCompound, K> keyProvider,
									  Function<NBTTagCompound, V> valueProvider, NBTTagCompound nbt, String mapName) {

		map.clear();

		NBTTagList listTag = nbt.getTagList(mapName, 10);
		for (int i = 0; i < listTag.tagCount(); i++) {
			NBTTagCompound item = listTag.getCompoundTagAt(i);

			K key = keyProvider.apply(item.getCompoundTag("Key"));
			V value = valueProvider.apply(item.getCompoundTag("Value"));

			if (key == null) {
				// look @ dis industry standard pro debugging techniques
				AvatarLog.error("MapError: Issue reading map " + mapName + "'s key for item " + i);
				AvatarLog.error("MapError: Item compound- " + item);
				AvatarLog.error("MapError: Key compound- " + item.getCompoundTag("Key"));
				throw new DiskException("readMap- Cannot have null key for map (see log for " +
						"details)");
			}
			if (value == null) {
				AvatarLog.error("MapError: Issue reading map " + mapName + "'s value for item" + i);
				AvatarLog.error("MapError: Item compound- " + item);
				AvatarLog.error("MapError: Value compound- " + item.getCompoundTag("Value"));
				throw new DiskException("readMap- Cannot have null value for map (see log for " +
						"details)");
			}

			map.put(key, value);
		}

	}

	/**
	 * Writes the map's entries to the NBT. Does not permit null keys or values.
	 *
	 * @param map         Map to write
	 * @param keyWriter   Given a key object & NBT, writes the key to disk.
	 * @param valueWriter Given a value object & NBT, writes the value to disk.
	 * @param nbt         NBT to read from
	 * @param mapName     Name to store map as
	 */
	public static <K, V> void writeMap(Map<K, V> map, BiConsumer<NBTTagCompound, K> keyWriter,
									   BiConsumer<NBTTagCompound, V> valueWriter, NBTTagCompound nbt, String mapName) {

		NBTTagList listTag = new NBTTagList();
		Set<Map.Entry<K, V>> entries = map.entrySet();

		for (Map.Entry<K, V> entry : entries) {

			if (entry.getKey() == null)
				throw new DiskException("writeMap- does not permit null keys in map " + map);
			if (entry.getValue() == null)
				throw new DiskException("writeMap- does not permit null values in map " + map);

			NBTTagCompound item = new NBTTagCompound();
			NBTTagCompound keyNbt = new NBTTagCompound();
			NBTTagCompound valNbt = new NBTTagCompound();
			keyWriter.accept(keyNbt, entry.getKey());
			valueWriter.accept(valNbt, entry.getValue());
			item.setTag("Key", keyNbt);
			item.setTag("Value", valNbt);
			listTag.appendTag(item);
		}

		nbt.setTag(mapName, listTag);

	}

	/**
	 * Reads the inventory contents. A list called <code>listName</code> is used
	 * under that NBT tag.
	 */
	public static void readInventory(IInventory inventory, NBTTagCompound nbt, String listName) {
		NBTTagList list = nbt.getTagList(listName, 10);
		for (int i = 0; i < list.tagCount(); i++) {

			NBTTagCompound slotNbt = list.getCompoundTagAt(i);
			int slot = slotNbt.getInteger("Slot");

			if (slotNbt.getBoolean("Empty")) {
				inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
			} else {
				inventory.setInventorySlotContents(slot, new ItemStack(slotNbt));
			}

		}
	}

	/**
	 * Writes the inventory contents to the list tag called
	 * <code>listName</code>.
	 */
	public static void writeInventory(IInventory inventory, NBTTagCompound nbt, String listName) {

		NBTTagList list = new NBTTagList();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {

			ItemStack stack = inventory.getStackInSlot(i);

			NBTTagCompound slotNbt = new NBTTagCompound();
			slotNbt.setBoolean("Empty", stack.isEmpty());
			slotNbt.setInteger("Slot", i);

			if (!stack.isEmpty()) {
				stack.writeToNBT(slotNbt);
			}

			list.appendTag(slotNbt);

		}

		nbt.setTag(listName, list);

	}

	/**
	 * An exception thrown by reading/writing methods for NBT
	 *
	 * @author CrowsOfWar
	 */
	public static class DiskException extends RuntimeException {

		private DiskException(String message) {
			super(message);
		}

	}

}
