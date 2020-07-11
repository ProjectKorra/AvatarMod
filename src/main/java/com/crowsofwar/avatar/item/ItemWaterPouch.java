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
package com.crowsofwar.avatar.item;

import com.crowsofwar.avatar.registry.AvatarItem;
import com.crowsofwar.avatar.registry.AvatarItems;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @author CrowsOfWar
 * @author Mahtaran
 */
public class ItemWaterPouch extends Item implements AvatarItem {
	private static ItemWaterPouch instance = null;

	public static ItemWaterPouch getInstance() {
		if(instance == null) {
			instance = new ItemWaterPouch();
			AvatarItems.addItem(instance);
		}

		return instance;
	}


	public ItemWaterPouch() {
		setCreativeTab(AvatarItems.tabItems);
		setTranslationKey("water_pouch");
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
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player,
													EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		boolean isFull = itemstack.getMetadata() == 5;
		if (isFull) {
			// We're already completely filled
			return new ActionResult(EnumActionResult.PASS, itemstack);
		}
		// Last boolean is useLiquids, which obviously should be true
		RayTraceResult raytraceresult = rayTrace(world, player, true);
		if (raytraceresult == null || raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
			// We're not looking at a block
			return new ActionResult(EnumActionResult.PASS, itemstack);
		} else {
			BlockPos blockpos = raytraceresult.getBlockPos();
			// We're not allowed to edit the block
			if (!world.isBlockModifiable(player, blockpos) || !player
					.canPlayerEdit(blockpos.offset(raytraceresult.sideHit),
							raytraceresult.sideHit, itemstack)) {
				return new ActionResult(EnumActionResult.PASS, itemstack);
			}
			IBlockState state = world.getBlockState(blockpos);
			Material material = state.getMaterial();
			if (state.getBlock() instanceof BlockCauldron) {
				// Get how full the block is
				int canBeFilled = state.getValue(BlockCauldron.LEVEL).intValue();
				int toBeFilled = 5 - itemstack.getItemDamage();
				int willBeFilled = Math.min(canBeFilled, toBeFilled);
				((BlockCauldron) state.getBlock())
						.setWaterLevel(world, blockpos, state, canBeFilled - willBeFilled);
				player.addStat(StatList.CAULDRON_USED);
				player.addStat(StatList.getObjectUseStats(this));
				// TODO: Custom sound?
				player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
				return new ActionResult<>(EnumActionResult.SUCCESS,
						fillPouch(itemstack, player, willBeFilled));
			} else if (material == Material.WATER) {
				// Get how full the block is
				int level = state.getValue(BlockLiquid.LEVEL).intValue();
				// Level will be 0 when completely filled, and 7 when nearly empty, so we have to invert it
				int canBeFilled = 8 - level;
				int toBeFilled = 5 - itemstack.getItemDamage();
				int willBeFilled = Math.min(canBeFilled, toBeFilled);
				IBlockState newState;
				if (willBeFilled > 0) {
					newState = state.getBlock().getStateFromMeta(level + willBeFilled);
				} else {
					newState = Blocks.AIR.getDefaultState();
				}
				/* 11 are the flags. Flags are binary. 11 in binary:
				 *  0 1 0 1 1
				 * 16 8 4 2 1
				 * 1: cause block update
				 * 2: send change to clients
				 * 4: prevent re-render
				 * 8: run re-renders on main thread
				 * 16: prevent observers update
				 * For more information see {@link net.minecraft.world.World#setBlockState World#setBlockState}
				 */
				world.setBlockState(blockpos, newState, 11);
				player.addStat(StatList.getObjectUseStats(this));
				// TODO: Custom sound?
				player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
				return new ActionResult<>(EnumActionResult.SUCCESS,
						fillPouch(itemstack, player, willBeFilled));
			} else {
				return new ActionResult(EnumActionResult.PASS, itemstack);
			}
		}
	}

	private ItemStack fillPouch(ItemStack emptyPouches, EntityPlayer player, int levels) {
		if (player.capabilities.isCreativeMode) {
			return emptyPouches;
		} else {
			int newLevel = Math.max(5, emptyPouches.getItemDamage() + levels);
			ItemStack filledPouch = new ItemStack(emptyPouches.getItem(), 1, newLevel);
			emptyPouches.shrink(1);
			if (emptyPouches.isEmpty()) {
				return filledPouch;
			} else {
				if (!player.inventory.addItemStackToInventory(filledPouch)) {
					player.dropItem(filledPouch, false);
				} else if (player instanceof EntityPlayerMP) {
					((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
				}
				return emptyPouches;
			}
		}
	}
}
