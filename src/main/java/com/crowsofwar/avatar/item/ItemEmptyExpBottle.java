package com.crowsofwar.avatar.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemEmptyExpBottle extends Item {

    public ItemEmptyExpBottle() {
        this.setCreativeTab(CreativeTabs.BREWING);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {


        int requiredAmount = 5;
        float storedExp = 3 + worldIn.rand.nextInt(5) + worldIn.rand.nextInt(5);

        if (playerIn.experience >= requiredAmount) {
            if (playerIn.experience < storedExp)
                storedExp = playerIn.experience;
            playerIn.addExperience((int) -storedExp);
            //Spawn particles later
            return new ActionResult<>(EnumActionResult.SUCCESS, turnIntoExpBottle(playerIn.getActiveItemStack(), playerIn));
        } else return new ActionResult<>(EnumActionResult.FAIL, playerIn.getActiveItemStack());
    }

    public ItemStack turnIntoExpBottle(ItemStack bottle, EntityPlayer player) {
        ItemStack expBottle = new ItemStack(Items.EXPERIENCE_BOTTLE, 1);
        bottle.shrink(1);
        player.addItemStackToInventory(expBottle);

        if (!bottle.isEmpty())
            return bottle;
        return expBottle;
    }
}
