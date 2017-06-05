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
package com.crowsofwar.avatar.common;

import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;

import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.ItemScroll;
import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class AvatarScrollDrops {
	
	private AvatarScrollDrops() {}
	
	@SubscribeEvent
	public void onMobDeath(LivingDropsEvent e) {
		
		EntityLivingBase entity = e.getEntityLiving();
		DamageSource source = e.getSource();
		
		if (e.isRecentlyHit()) {
			
			double chance = MOBS_CONFIG.getScrollDropChance(entity);
			ScrollType type = MOBS_CONFIG.getScrollType(entity);
			
			double random = Math.random() * 100;
			if (random < chance) {
				
				ItemStack stack = new ItemStack(AvatarItems.itemScroll);
				ItemScroll.setScrollType(stack, type);
				
				EntityItem entityItem = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ,
						stack);
				entityItem.setDefaultPickupDelay();
				e.getDrops().add(entityItem);
				
			}
			
		}
		
	}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new AvatarScrollDrops());
	}
	
}
