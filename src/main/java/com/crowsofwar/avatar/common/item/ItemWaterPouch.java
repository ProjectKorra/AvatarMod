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

import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @author CrowsOfWar
 */
public class ItemWaterPouch extends Item implements AvatarItem {

	public ItemWaterPouch() {
		setCreativeTab(AvatarItems.tabItems);
		setUnlocalizedName("water_pouch");
		setMaxStackSize(1);
		setMaxDamage(0);
		setHasSubtypes(false);
	}

	@Override
	public Item item() {
		return this;
	}

	@Override
	public String getModelName(int meta) {
		return "water_pouch";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltips,
							   ITooltipFlag advanced) {

		int meta = stack.getMetadata();
		tooltips.add(I18n.format("avatar.tooltip.water_pouch" + (meta == 0 ? ".empty" : ""), meta));

	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {

		if (isInCreativeTab(tab)) {
			for (int meta = 0; meta <= 5; meta++) {
				subItems.add(new ItemStack(this, 1, meta));
			}
		}

	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		ItemStack stack = player.getHeldItem(hand);

		Vector eye = Vector.getEntityPos(player);
		Vector look = Vector.getLookRectangular(player);

		Raytrace.Result raytrace = Raytrace.predicateRaytrace(world, eye, look, 4,
				(pos, state) -> state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER);

		if (raytrace.hitSomething()) {
			BlockPos pos = raytrace.getPos().toBlockPos();
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER) {
				stack.setItemDamage(5);
			}
		}

		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}

}
