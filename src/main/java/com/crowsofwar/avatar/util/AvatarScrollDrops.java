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
package com.crowsofwar.avatar.util;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.config.DropInfo;
import com.crowsofwar.avatar.config.MobDrops;
import com.crowsofwar.avatar.item.scroll.Scrolls;
import com.crowsofwar.avatar.item.scroll.Scrolls.ScrollType;
import com.crowsofwar.avatar.util.analytics.AnalyticEvents;
import com.crowsofwar.avatar.util.analytics.AvatarAnalytics;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

import static com.crowsofwar.avatar.config.ConfigMobs.MOBS_CONFIG;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
public class AvatarScrollDrops {

    @SubscribeEvent
    public static void onMobDeath(LivingDropsEvent e) {

        EntityLivingBase entity = e.getEntityLiving();

        if (e.isRecentlyHit()) {
            MobDrops mobInfo = MOBS_CONFIG.getMobDropInfo(entity);
            if (mobInfo != null) {
                DropInfo[] dropInfo = mobInfo.getDropInformation().clone();

                for (DropInfo info : dropInfo) {
                    int amount = info.getAmount();
                    int tier = info.getTier();
                    double chance = info.getDropChance();
                    ScrollType type = info.getType();

                    if (MOBS_CONFIG.scrollSettings.chaos || MOBS_CONFIG.scrollSettings.absoluteChaos) {
                        amount = AvatarUtils.getRandomNumberInRange(1, Ability.MAX_TIER);
                        tier = AvatarUtils.getRandomNumberInRange(1, Ability.MAX_TIER);
                    }

                    if (MOBS_CONFIG.scrollSettings.absoluteChaos) {
                        chance = AvatarUtils.getRandomNumberInRange(1, 100);
                        type = ScrollType.get(AvatarUtils.getRandomNumberInRange(0, ScrollType.amount()));
                    }

                    for (int i = 0; i < amount; i++) {
                        if (Math.random() <= chance / 100) {
                            assert type != null;
                            if (Scrolls.getItemForType(type) != null) {
                                ItemStack stack = new ItemStack(Objects.requireNonNull(Scrolls.getItemForType(type)),
                                        1, tier - 1);
                                EntityItem item = new EntityItem(entity.world, entity.posX, entity.posY,
                                        entity.posZ, stack);
                                item.setDefaultPickupDelay();
                                e.getDrops().add(item);
                                String entityName = EntityList.getEntityString(entity);
                                AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onMobScrollDrop(entityName,
                                        type.name().toLowerCase()));
                            }
                        }
                    }
                }
            }
        }

        // Send analytics for any entities that dropped scrolls

        //List<EntityItem> drops = e.getDrops();

		/*for (EntityItem drop : drops) {
			ItemStack stack = drop.getItem();
			if (stack.getItem() instanceof ItemScroll) {

				ScrollType type = ScrollType.values()[stack.getMetadata()];
				String entityName = EntityList.getEntityString(entity);
				AvatarAnalytics.INSTANCE.pushEvent(AnalyticEvents.onMobScrollDrop(entityName,
						type.name().toLowerCase()));

			}
		}**/

    }

}
