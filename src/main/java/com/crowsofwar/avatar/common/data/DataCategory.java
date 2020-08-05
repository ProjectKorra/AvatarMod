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

import com.crowsofwar.avatar.common.network.DataTransmitter;
import com.crowsofwar.avatar.common.network.DataTransmitters;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Separates all of the methods of BendingData into different categories. This
 * is so networking packets can only send some categories and not others
 * depending on what's changed.
 * <p>
 * For example, if a player's chi is recharging, the packets only need to send
 * info about the Chi and would add the {@link #CHI} DataCategory to the changed
 * list. But they wouldn't send info about bending controllers since that is
 * unnecessary.
 *
 * @author CrowsOfWar
 */

public enum DataCategory {

	//@formatter:off
	BENDING_LIST(	BendingData::getAllBendingIds,		BendingData::setAllBendingIds,		DataTransmitters.BENDING_LIST),
	STATUS_CONTROLS(BendingData::getAllStatusControls,	BendingData::setAllStatusControls,	DataTransmitters.STATUS_CONTROLS),
	ABILITY_DATA(	BendingData::getAbilityDataMap,		BendingData::setAbilityDataMap,		DataTransmitters.ABILITY_DATA),
	CHI(			BendingData::chi,					BendingData::setChi,				DataTransmitters.CHI),
	MISC_DATA(		BendingData::getMiscData,			BendingData::setMiscData,			DataTransmitters.MISC_DATA),
	TICK_HANDLERS(	BendingData::getAllTickHandlers,	BendingData::setAllTickHandlers,	DataTransmitters.TICK_HANDLERS),
	ACTIVE_BENDING(	BendingData::getActiveBendingId, 	BendingData::setActiveBendingId,	DataTransmitters.ACTIVE_BENDING),
	VISION(			BendingData::getVision,				BendingData::setVision, 			DataTransmitters.VISION),
	PERFORMANCE(	BendingData::getPerformance,		BendingData::setPerformance,		DataTransmitters.PERFORMANCE);
	//@formatter:on

	private final Function<BendingData, ?> getter;
	private final BiConsumer<BendingData, ?> setter;
	private final DataTransmitter<?> transmitter;

	<T> DataCategory(Function<BendingData, T> getter, BiConsumer<BendingData, T> setter,
					 DataTransmitter<?> transmitter) {
		this.getter = getter;
		this.setter = setter;
		this.transmitter = transmitter;
	}

	/**
	 * Finds the necessary data from the PlayerData and then writes it to the
	 * ByteBuf
	 */
	public void write(ByteBuf buf, BendingData data) {
		((DataTransmitter<Object>) transmitter).write(buf, getter.apply(data));
	}

	/**
	 * Reads from the ByteBuf and saves the result into player data
	 */
	public void read(ByteBuf buf, BendingData data) {
		Object obj = transmitter.read(buf, data);
		((BiConsumer<BendingData, Object>) setter).accept(data, obj);
	}

}
