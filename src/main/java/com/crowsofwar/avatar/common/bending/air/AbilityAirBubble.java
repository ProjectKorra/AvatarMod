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
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.FIRST;
import static com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath.SECOND;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.data.ctx.Bender;
import com.crowsofwar.avatar.common.entity.EntityAirBubble;
import com.crowsofwar.avatar.common.network.packets.PacketCErrorMessage;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
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
		EntityLivingBase bender = ctx.getBenderEntity();
		World world = ctx.getWorld();
		BendingData data = ctx.getData();
		
		ItemStack chest = bender.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		boolean elytraOk = (STATS_CONFIG.allowAirBubbleElytra || chest.getItem() != Items.ELYTRA);
		
		if (!elytraOk && bender instanceof EntityPlayerMP) {
			AvatarMod.network.sendTo(new PacketCErrorMessage("avatar.airBubbleElytra"),
					(EntityPlayerMP) bender);
		}
		
		if (!data.hasStatusControl(StatusControl.BUBBLE_CONTRACT) && elytraOk) {
			
			if (!ctx.consumeChi(STATS_CONFIG.chiAirBubble)) return;
			
			float xp = data.getAbilityData(this).getTotalXp();
			
			float size = 1.5f;
			float health = 10 + ctx.getLevel() * 6;
			if (ctx.getLevel() > 0) size = 2.5f;
			if (ctx.isMasterLevel(FIRST)) size = 4f;
			if (ctx.isMasterLevel(SECOND)) health = 10f;
			
			EntityAirBubble bubble = new EntityAirBubble(world);
			bubble.setOwner(bender);
			bubble.setPosition(bender.posX, bender.posY, bender.posZ);
			bubble.setHealth(health);
			bubble.setMaxHealth(health);
			bubble.setSize(size);
			bubble.setAllowHovering(ctx.isMasterLevel(SECOND));
			world.spawnEntityInWorld(bubble);
			
			data.addStatusControl(StatusControl.BUBBLE_EXPAND);
			data.addStatusControl(StatusControl.BUBBLE_CONTRACT);
		}
		
	}
	
	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiAirBubble(this, entity, bender);
	}
	
}
