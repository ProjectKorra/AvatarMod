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
import com.crowsofwar.avatar.item.scroll.Scrolls;
import com.crowsofwar.avatar.item.scroll.Scrolls.ScrollType;
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

			double chance = MOBS_CONFIG.getScrollDropChance(entity);
			//We're doing this dynamically rather than making a crap ton of maps in the mob config file. Gets how many tiers the entity can drop.
			int tier = Math.max((int) (chance / MOBS_CONFIG.scrollSettings.percentPerTier), 1);
			int amount = Math.max((int) (chance / MOBS_CONFIG.scrollSettings.percentPerNumber), 1);
			ScrollType type = MOBS_CONFIG.getScrollType(entity);

			if (!MOBS_CONFIG.scrollSettings.chaos && !MOBS_CONFIG.scrollSettings.absoluteChaos) {
				for (int i = 0; i < tier; i++) {
					for (int j = 0; j < amount; j++) {
						double random = Math.random() * 100;
						//Each tier has by default 2 / 3 of the original chance to drop.
						chance = MOBS_CONFIG.getScrollDropChance(entity) * Math.pow(MOBS_CONFIG.scrollSettings.tierChanceDecreaseMult, i);
						//There's a 5% less chance for each scroll to drop. Ex: 10% for 1, 5% for 2, e.t.c, which then stacks based on the tier.
						chance *= Math.pow(MOBS_CONFIG.scrollSettings.numberChanceDecreaseMult, j);
						if (random < chance) {
							assert Scrolls.getItemForType(type) != null;
							//We don't want there to be too many high grade scrolls.
							int totalAmount = Math.min(j + 1, tier - j);
							ItemStack stack = new ItemStack(Objects.requireNonNull(Scrolls.getItemForType(type)), totalAmount, i);

							EntityItem entityItem = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ,
									stack);
							entityItem.setDefaultPickupDelay();
							e.getDrops().add(entityItem);

						}
					}
				}
			}
			else if (MOBS_CONFIG.scrollSettings.chaos) {
				tier = AvatarUtils.getRandomNumberInRange(1, 7);
				amount = AvatarUtils.getRandomNumberInRange(1, 7);
				for (int i = 0; i < tier; i++) {
					for (int j = 0; j < amount; j++) {
						double random = Math.random() * 100;
						//Each tier has by default 2 / 3 of the original chance to drop.
						chance = MOBS_CONFIG.getScrollDropChance(entity) * Math.pow(MOBS_CONFIG.scrollSettings.tierChanceDecreaseMult, i);
						//There's a 5% less chance for each scroll to drop. Ex: 10% for 1, 5% for 2, e.t.c, which then stacks based on the tier.
						chance *= Math.pow(MOBS_CONFIG.scrollSettings.numberChanceDecreaseMult, j);
						if (random < chance) {
							assert Scrolls.getItemForType(type) != null;
							//We don't want there to be too many high grade scrolls.
							int totalAmount = Math.min(j + 1, tier - j);
							ItemStack stack = new ItemStack(Objects.requireNonNull(Scrolls.getItemForType(type)), totalAmount, i);

							EntityItem entityItem = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ,
									stack);
							entityItem.setDefaultPickupDelay();
							e.getDrops().add(entityItem);

						}
					}
				}
			}
			else if (MOBS_CONFIG.scrollSettings.absoluteChaos) {
				tier = AvatarUtils.getRandomNumberInRange(1, 7);
				amount = AvatarUtils.getRandomNumberInRange(1, 7);
				for (int i = 0; i < tier; i++) {
					for (int j = 0; j < amount; j++) {
						double random = Math.random() * 100;
						//Each tier has by default 2 / 3 of the original chance to drop.
						chance = AvatarUtils.getRandomNumberInRange(1, 100) * Math.pow(MOBS_CONFIG.scrollSettings.tierChanceDecreaseMult, i);
						//There's a 5% less chance for each scroll to drop. Ex: 10% for 1, 5% for 2, e.t.c, which then stacks based on the tier.
						chance *= Math.pow(MOBS_CONFIG.scrollSettings.numberChanceDecreaseMult, j);
						if (random < chance) {
							assert Scrolls.getItemForType(type) != null;
							//We don't want there to be too many high grade scrolls.
							int totalAmount = Math.min(j + 1, tier - j);
							ItemStack stack = new ItemStack(Objects.requireNonNull(Scrolls.getItemForType(type)), totalAmount, i);

							EntityItem entityItem = new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ,
									stack);
							entityItem.setDefaultPickupDelay();
							e.getDrops().add(entityItem);

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
