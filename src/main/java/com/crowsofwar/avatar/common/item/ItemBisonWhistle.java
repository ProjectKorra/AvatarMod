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

import static com.crowsofwar.avatar.common.AvatarChatMessages.*;
import static com.crowsofwar.gorecore.util.GoreCoreNBTUtil.stackCompound;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.crowsofwar.avatar.common.data.AvatarPlayerData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.entity.mob.EntitySkyBison;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
	
	// Logic for assigning bison whistle is in the bison class
	// itemInteractionForEntity didn't work while sneaking
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		
		ItemStack stack = player.getHeldItem(hand);
		
		if (world.isRemote) {
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
		
		if (isBound(stack)) {
			
			// Make sure there's a bison to teleport first
			UUID boundTo = getBoundTo(stack);
			List<EntitySkyBison> entities = world.getEntities(EntitySkyBison.class,
					bison -> bison.getUniqueID() == boundTo);
			
			if (!entities.isEmpty()) {
				
				EntitySkyBison bison = entities.get(0);
				double dist = player.getDistanceToEntity(bison);
				
				double seconds = dist / 20;
				
				AvatarPlayerData data = AvatarPlayerData.fetcher().fetch(player);
				data.setPetSummonCooldown((int) (seconds * 20));
				data.addTickHandler(TickHandler.BISON_SUMMONER);
				
				MSG_BISON_WHISTLE_SUMMON.send(player, (int) seconds);
				
			} else {
				MSG_BISON_WHISTLE_NOT_FOUND.send(player, getBisonName(stack));
			}
			
		} else {
			MSG_BISON_WHISTLE_NOSUMMON.send(player);
		}
		
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		
		if (isBound(stack)) {
			tooltip.add(I18n.format("avatar.bisonWhistle.tooltipBound", getBisonName(stack)));
		} else {
			tooltip.add(I18n.format("avatar.bisonWhistle.tooltipUnbound"));
		}
		
	}
	
	@Override
	public Item item() {
		return this;
	}
	
	@Override
	public String getModelName(int meta) {
		return "bison_whistle";
	}
	
	@Nullable
	public static UUID getBoundTo(ItemStack stack) {
		NBTTagCompound nbt = stackCompound(stack);
		return nbt.hasKey("SkyBisonMost") ? nbt.getUniqueId("SkyBison") : null;
	}
	
	public static void setBoundTo(ItemStack stack, @Nullable UUID id) {
		NBTTagCompound nbt = stackCompound(stack);
		if (id != null) {
			nbt.setUniqueId("SkyBison", id);
		} else {
			nbt.removeTag("SkyBison");
		}
	}
	
	@Nullable
	public static String getBisonName(ItemStack stack) {
		String name = stackCompound(stack).getString("BisonName");
		return name.isEmpty() ? null : name;
	}
	
	public static void setBisonName(ItemStack stack, @Nullable String name) {
		if (name == null) {
			stackCompound(stack).removeTag("BisonName");
		} else {
			stackCompound(stack).setString("BisonName", name);
		}
	}
	
	public static boolean isBound(ItemStack stack) {
		return getBisonName(stack) != null && getBoundTo(stack) != null;
	}
	
}
