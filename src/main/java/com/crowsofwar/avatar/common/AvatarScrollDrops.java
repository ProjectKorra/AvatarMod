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

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.analytics.AnalyticEvents;
import com.crowsofwar.avatar.common.analytics.AvatarAnalytics;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.common.item.ItemScroll;
import com.crowsofwar.avatar.common.item.ItemScroll.ScrollType;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigMobs.MOBS_CONFIG;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarScrollDrops {
	
	@SubscribeEvent
	public static void onMobDeath(LivingDropsEvent e) {
		
		EntityLivingBase entity = e.getEntityLiving();
		DamageSource source = e.getSource();
		
		if (e.isRecentlyHit()) {
			
			double chance = MOBS_CONFIG.getScrollDropChance(entity);
			ScrollType type = MOBS_CONFIG.getScrollType(entity);
			
			double random = Math.random() * 100;
			if (random < chance) {
				
				ItemStack stack = new ItemStack(AvatarItems.itemScroll);
				ItemScroll.setScrollType(stack, type);
				
				EntityItem entityItem = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ,
						stack);
				entityItem.setDefaultPickupDelay();
				e.getDrops().add(entityItem);

			}
			
		}

		// Send analytics for any entities that dropped scrolls

		List<EntityItem> drops = e.getDrops();

		for (EntityItem drop : drops) {
			ItemStack stack = drop.getItem();
			if (stack.getItem() == AvatarItems.itemScroll) {

				ScrollType type = ScrollType.values()[stack.getMetadata()];
				String entityName = EntityList.getEntityString(entity);
				AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onMobScrollDrop(entityName,
						type.name().toLowerCase()));

			}
		}

	}
	
}
