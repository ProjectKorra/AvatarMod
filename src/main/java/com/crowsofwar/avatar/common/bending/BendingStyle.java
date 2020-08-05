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

package com.crowsofwar.avatar.common.bending;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.gui.BendingMenuInfo;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.CreateFromNBT;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.ReadableWritable;
import com.crowsofwar.gorecore.util.GoreCoreNBTInterfaces.WriteToNBT;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Base class for bending abilities. All bending classes extend this one. They
 * can save data to NBT if necessary. Functionality for bending should be in
 * subclasses. Bending styles are singletons, but must be accessed through
 * {@link BendingStyles}.
 * <p>
 * For the sake of abstraction, you won't need to refer to bending styles
 * by their concrete names.
 * <p>
 * Subclasses have access to client input via optionally* implementable hook
 * methods.
 * <p>
 * *Optionally = the subclass must declare the method, but does not need to put
 * any code inside of it.
 */
public abstract class BendingStyle implements ReadableWritable {

	public static final CreateFromNBT<BendingStyle> creator = new CreateFromNBT<BendingStyle>() {
		@Override
		public BendingStyle create(NBTTagCompound nbt, Object[] methodsExtraData, Object[] extraData) {
			UUID id = nbt.getUniqueId("ControllerID");
			try {
				BendingStyle bc = BendingStyles.get(id);
				return bc;
			} catch (Exception e) {
				AvatarLog.error(
						"Could not find bending controller from ID '" + id + "' - please check NBT data");
				e.printStackTrace();
				return null;
			}
		}
	};

	public static final WriteToNBT<BendingStyle> writer = new WriteToNBT<BendingStyle>() {
		@Override
		public void write(NBTTagCompound nbt, BendingStyle object, Object[] methodsExtraData,
						  Object[] extraData) {
			nbt.setUniqueId("ControllerID", object.getId());
		}
	};

	/**
	 * RNG available for convenient use.
	 */
	public static final Random random = new Random();

	private final List<Ability> abilities;

	public void registerAbilities() {
		for (Ability ability : Abilities.getAbilitiesToRegister(getId()))
			addAbility(ability);
	}

	/**
	 * @see #getParentBendingId()
	 */
	@Nullable
	private UUID parentBendingId;


	//This is so player-specific custom elements can't be used by other players when using commands.
	//This also means if you aren't the right player, you can't obtain it.
	//Ex: Eternal Dragonbending is usable by me (FavouriteDragon) only
	public boolean canEntityUse() {
		return true;
	}

	/**
	 * Constructor used for main bending styles (i.e. non-specialty), like firebending
	 */
	public BendingStyle() {
		this(null);
	}

	/**
	 * Constructor used for specialty bending styles, like lightningbending.
	 *
	 * @param parentBendingId The ID of the main bending style which this specialty bending is based
	 *                        off of, e.g. firebending
	 */
	public BendingStyle(UUID parentBendingId) {
		this.abilities = new ArrayList<>();
		this.parentBendingId = parentBendingId;
	}

	protected void addAbility(String abilityName) {
		this.abilities.add(Abilities.get(abilityName));
	}

	protected void addAbility(Ability ability) {
		this.abilities.add(ability);
	}

	/**
	 * Get information about this bending controller's radial menu.
	 */
	public abstract BendingMenuInfo getRadialMenu();

	/**
	 * Get the name of this bending controller in lowercase. e.g. "earthbending"
	 */
	public abstract String getName();

	public int getTextColour() {
		return 0xffffff;
	}

	//Since I'm too lazy to change Crows' skill menu gui system, this provides compatiblity with it. Yay.
	public TextFormatting getTextFormattingColour() {
		return TextFormatting.WHITE;
	}

	public abstract UUID getId();

	public byte getNetworkId() {
		return BendingStyles.getNetworkId(getId());
	}

	public List<Ability> getAllAbilities() {
		return this.abilities;
	}

	public boolean isSpecialtyBending() {
		return parentBendingId != null;
	}

	public boolean isParentBending() {
		return parentBendingId == null;
	}

	public SoundEvent getRadialMenuSound() {
		return SoundEvents.UI_BUTTON_CLICK;
	}

	//Returns a bending style based on a given UUID.
	@Nullable
	public BendingStyle getBending(String UUID) {
		if (this.getId().toString().equals(UUID)) {
			return this;
		}
		else return null;
	}

	/**
	 * If this bending style is a specialty bending (e.g. lightningbending), then this points to the
	 * ID of its main/"parent" bending style (e.g. firebending). If the bending style is already a
	 * main bending style, then this will be null.
	 *
	 * @see #isSpecialtyBending()
	 */
	@Nullable
	public UUID getParentBendingId() {
		return parentBendingId;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
	}

}
