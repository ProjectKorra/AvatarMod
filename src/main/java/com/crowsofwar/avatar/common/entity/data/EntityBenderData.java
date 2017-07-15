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
package com.crowsofwar.avatar.common.entity.data;

import com.crowsofwar.avatar.common.bending.*;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbstractBendingData;
import com.crowsofwar.avatar.common.data.DataCategory;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.nestedCompound;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class EntityBenderData extends AbstractBendingData {
	
	private final EntityLivingBase entity;
	
	public EntityBenderData(EntityLivingBase entity) {
		this.entity = entity;
	}
	
	@Override
	public void save(DataCategory category) {}
	
	public void writeToNbt(NBTTagCompound writeTo) {
		
		AvatarUtils.writeList(getAllBending(),
				(compound, controller) -> compound.setUniqueId("ControllerID", controller.getId()), writeTo,
				"BendingControllers");
		
		AvatarUtils.writeList(getAllStatusControls(),
				(nbtTag, control) -> nbtTag.setInteger("Id", control.id()), writeTo, "StatusControls");
		
		AvatarUtils.writeMap(getAbilityDataMap(), //
				(nbt, abilityId) -> {
					nbt.setUniqueId("Id", abilityId);
					nbt.setString("_AbilityName", Abilities.getName(abilityId));
				}, (nbt, data) -> {
					nbt.setUniqueId("AbilityId", data.getAbility().getId());
					data.writeToNbt(nbt);
				}, writeTo, "AbilityData");
		
		getMiscData().writeToNbt(nestedCompound(writeTo, "Misc"));
		
		chi().writeToNBT(writeTo);
		
		AvatarUtils.writeList(getAllTickHandlers(), //
				(nbt, handler) -> nbt.setInteger("Id", handler.id()), //
				writeTo, "TickHandlers");
		
	}
	
	public void readFromNbt(NBTTagCompound readFrom) {
		
		List<BendingStyle> bendings = new ArrayList<>();
		AvatarUtils.readList(bendings,
				compound -> BendingStyles.get(compound.getUniqueId("ControllerID")), readFrom,
				"BendingControllers");
		clearBending();
		for (BendingStyle bending : bendings) {
			addBending(bending);
		}
		
		List<StatusControl> scs = new ArrayList<>();
		AvatarUtils.readList(scs, nbtTag -> StatusControl.lookup(nbtTag.getInteger("Id")), readFrom,
				"StatusControls");
		clearStatusControls();
		for (StatusControl sc : scs) {
			addStatusControl(sc);
		}
		
		Map<UUID, AbilityData> abilityData = new HashMap<>();
		AvatarUtils.readMap(abilityData, nbt -> nbt.getUniqueId("Id"), nbt -> {
			Ability ability = Abilities.get(nbt.getUniqueId("Id"));
			AbilityData data = new AbilityData(this, ability);
			data.readFromNbt(nbt);
			return data;
		}, readFrom, "AbilityData");
		clearAbilityData();
		for (Map.Entry<UUID, AbilityData> entry : abilityData.entrySet()) {
			setAbilityData(entry.getKey(), entry.getValue());
		}
		
		getMiscData().readFromNbt(nestedCompound(readFrom, "Misc"));
		
		chi().readFromNBT(readFrom);
		
		List<TickHandler> tickHandlers = new ArrayList<>();
		AvatarUtils.readList(tickHandlers, //
				nbt -> TickHandler.fromId(nbt.getInteger("Id")), //
				readFrom, "TickHandlers");
		clearTickHandlers();
		for (TickHandler handler : tickHandlers) {
			addTickHandler(handler);
		}
		
	}
	
}
