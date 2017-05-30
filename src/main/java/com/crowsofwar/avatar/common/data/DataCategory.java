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

import java.util.function.Function;

import com.crowsofwar.avatar.common.network.DataTransmitter;
import com.crowsofwar.avatar.common.network.DataTransmitters;

import io.netty.buffer.ByteBuf;

/**
 * 
 * 
 * @author CrowsOfWar
 */

public enum DataCategory {
	
	// @formatter:off
	BENDING_LIST(	data -> data.getAllBending(),			DataTransmitters.BENDING_LIST),
	STATUS_CONTROLS(data -> data.getAllStatusControls(),	DataTransmitters.STATUS_CONTROLS),
	ABILITY_DATA(	data -> data.getAbilityDataMap(),		DataTransmitters.ABILITY_DATA),
	CHI(			data -> data.chi(),						DataTransmitters.CHI),
	MISC_DATA(		data -> data.getMiscData(),				DataTransmitters.MISC_DATA),
	TICK_HANDLERS(	data -> data.getAllTickHandlers(),		DataTransmitters.TICK_HANDLERS);
	// @formatter:on
	
	private final Function<BendingData, Object> getter;
	private final DataTransmitter<?> transmitter;
	
	private DataCategory(Function<BendingData, Object> getter, DataTransmitter<?> transmitter) {
		this.getter = getter;
		this.transmitter = transmitter;
	}
	
	public Object get(BendingData data) {
		return getter.apply(data);
	}
	
	public void write(ByteBuf buf, Object obj) {
		((DataTransmitter<Object>) transmitter).write(buf, obj);
	}
	
	public Object read(ByteBuf buf, BendingData data) {
		return transmitter.read(buf, data);
	}
	
}
