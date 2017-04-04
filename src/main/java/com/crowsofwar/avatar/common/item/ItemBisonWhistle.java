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
package com.crowsofwar.avatar.common.item;

import java.util.List;
import java.util.Random;

import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public class ItemBisonWhistle extends Item implements AvatarItem {
	
	public ItemBisonWhistle() {
		setCreativeTab(AvatarItems.tabItems);
		setMaxDamage(1);
		setUnlocalizedName("bison_whistle");
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		
		ItemStack stack = player.getHeldItem(hand);
		
		// Search for bison to teleport
		List<EntitySkyBison> entities = world.getEntities(EntitySkyBison.class,
				bison -> bison.getOwner() == player);
		
		if (!entities.isEmpty()) {
			
			EntitySkyBison bison = entities.get(0);
			
			System.out.println("Found a bison");
			
			Random random = new Random();
			
			// Find suitable location near player
			for (int i = 0; i < 5; i++) {
				
				double x = player.posX + (random.nextDouble() * 2 - 1) * 20;
				double y = player.posX + (random.nextDouble() * 2 - 1) * 20;
				double z = player.posX + (random.nextDouble() * 2 - 1) * 20;
				
				if (bison.attemptTeleport(x, y, z)) {
					System.out.println("Successfully teleported to player");
					;
					break;
				} else {
					System.out.println("Try again");
				}
				
			}
			
		}
		
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		
	}
	
	@Override
	public Item item() {
		return this;
	}
	
	@Override
	public String getModelName(int meta) {
		return "bison_whistle";
	}
	
}
