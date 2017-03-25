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
package com.crowsofwar.avatar.common.bending.air;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.AbilityContext;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.entity.EntityAirBubble;
import com.crowsofwar.avatar.common.network.packets.PacketCErrorMessage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AbilityAirBubble extends AirAbility {
	
	public AbilityAirBubble() {
		super("air_bubble");
	}
	
	@Override
	public void execute(AbilityContext ctx) {
		EntityPlayer player = ctx.getPlayerEntity();
		World world = ctx.getWorld();
		AvatarPlayerData data = ctx.getData();
		
		ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		boolean elytraOk = (STATS_CONFIG.allowAirBubbleElytra || chest.getItem() != Items.ELYTRA);
		
		if (!elytraOk) {
			AvatarMod.network.sendTo(new PacketCErrorMessage("avatar.airBubbleElytra"),
					(EntityPlayerMP) player);
		}
		
		if (!data.hasStatusControl(StatusControl.BUBBLE_CONTRACT) && elytraOk) {
			
			if (!ctx.consumeChi(STATS_CONFIG.chiAirBubble)) return;
			
			float xp = data.getAbilityData(this).getXp();
			
			EntityAirBubble bubble = new EntityAirBubble(world);
			bubble.setOwner(player);
			bubble.setPosition(player.posX, player.posY, player.posZ);
			bubble.setHealth(15 + xp / 10f);
			world.spawnEntityInWorld(bubble);
			
			data.addStatusControl(StatusControl.BUBBLE_EXPAND);
			data.addStatusControl(StatusControl.BUBBLE_CONTRACT);
			data.sync();
		}
		
	}
	
}
