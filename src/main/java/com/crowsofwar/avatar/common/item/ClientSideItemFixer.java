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

import java.util.ArrayList;
import java.util.List;

import com.crowsofwar.gorecore.GoreCore;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

/**
 * Items can request to use a custom EntityItem instead of the vanilla one.
 * However, this is only used on the server so client will still be using
 * vanilla logic. This class fixes that by replacing vanilla EntityItems with
 * custom EntityItems on the server side if the item requests a custom entity.
 * 
 * @author CrowsOfWar
 */
public class ClientSideItemFixer {
	
	private static final List<Item> customItems = new ArrayList<>();
	
	private ClientSideItemFixer() {}
	
	/**
	 * Register the item so that its wishes of having a custom EntityItem will
	 * be respected on the client side.
	 */
	public static void addCustomItem(Item item) {
		customItems.add(item);
	}
	
	private void inspectItem(EntityItem oldEntity) {
		
		World world = oldEntity.worldObj;
		ItemStack stack = oldEntity.getEntityItem();
		Item item = stack.getItem();
		
		System.out.println(customItems + ", " + item);
		// Thread.dumpStack();
		
		if (item != null && customItems.contains(item)) {
			if (item.hasCustomEntity(stack) && oldEntity.getClass() == EntityItem.class) {
				System.out.println("Replace it");
				
				Entity newEntity = item.createEntity(world, oldEntity, stack);
				if (newEntity != null) {
					// oldEntity.setDead();
					world.spawnEntityInWorld(newEntity);
				}
				
			}
		}
		
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent e) {
		
		// Workaround to get world
		EntityPlayer player = GoreCore.proxy.getClientSidePlayer();
		
		if (e.phase == Phase.START && player != null) {
			World world = player.worldObj;
			
			List<EntityItem> entityItems = world.getEntities(EntityItem.class,
					candidate -> candidate.ticksExisted == 0);
			for (EntityItem entityItem : entityItems) {
				System.out.println("Inspecting " + entityItem);
				inspectItem(entityItem);
			}
			
		}
		
	}
	
	public static void register() {
		MinecraftForge.EVENT_BUS.register(new ClientSideItemFixer());
	}
	
}
