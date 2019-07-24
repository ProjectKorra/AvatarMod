package com.crowsofwar.avatar.common.item.scroll;

import com.crowsofwar.avatar.common.item.AvatarItem;
import com.crowsofwar.avatar.common.item.AvatarItems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * Base class for scrolls
 * 
 * @author Aang23
 */
public class ItemScroll extends Item implements AvatarItem {
    private final Scrolls.ScrollType type;

    public ItemScroll(Scrolls.ScrollType type) {
        this.type = type;
        setMaxStackSize(1);
        setMaxDamage(0);
        setCreativeTab(AvatarItems.tabItems);
        setHasSubtypes(true);
        setTranslationKey("scroll_" + type.displayName());
    }

    public Scrolls.ScrollType getScrollType() {
        return type;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int meta = 0; meta < 7; meta++) {
            items.add(new ItemStack(this, 1, meta));
        }
    }

    @Override
    public Item item() {
        return this;
    }

    @Override
    public String getModelName(int meta) {
        return "scroll_" + type.displayName();
    }
}