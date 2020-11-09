package com.crowsofwar.avatar.item;

import com.crowsofwar.avatar.registry.AvatarItem;
import com.crowsofwar.avatar.registry.AvatarItems;
import com.crowsofwar.avatar.util.XPUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemEmptyExpBottle extends Item implements AvatarItem {

    public static ItemEmptyExpBottle instance = null;

    public ItemEmptyExpBottle() {
        this.setCreativeTab(CreativeTabs.BREWING);
        setTranslationKey("empty_experience_bottle");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {



        int orbAmount = 3 + worldIn.rand.nextInt(5) + worldIn.rand.nextInt(5);

        float requiredAmount = 11, storedExp = 0;

        while (orbAmount > 0) {
            storedExp += EntityXPOrb.getXPSplit(orbAmount);
            orbAmount -= storedExp;
        }

        if (playerIn.experienceTotal >= requiredAmount) {
            if (playerIn.experienceTotal < storedExp)
                storedExp = playerIn.experienceTotal;
            XPUtils.takeXP(playerIn, (int) (storedExp * 0.75F));
            //Spawn particles later
            return new ActionResult<>(EnumActionResult.SUCCESS, turnIntoExpBottle(playerIn.getHeldItem(handIn), playerIn));
        } else return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }

    public ItemStack turnIntoExpBottle(ItemStack bottle, EntityPlayer player) {
        ItemStack expBottle = new ItemStack(Items.EXPERIENCE_BOTTLE, 1);
        bottle.shrink(1);
        player.addItemStackToInventory(expBottle);

        if (!bottle.isEmpty())
            return bottle;
        return expBottle;
    }

    public static ItemEmptyExpBottle getInstance() {
        if (instance == null) {
            instance = new ItemEmptyExpBottle();
            AvatarItems.addItem(instance);
        }
        return instance;
    }

    @Override
    public Item item() {
        return this;
    }

    @Override
    public String getModelName(int meta) {
        return "empty_experience_bottle";
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
