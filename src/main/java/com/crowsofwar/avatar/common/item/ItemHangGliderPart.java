package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.GliderInfo;
import com.crowsofwar.avatar.common.item.scroll.ItemScroll;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import static com.crowsofwar.avatar.AvatarInfo.MOD_ID;

public class ItemHangGliderPart extends Item implements AvatarItem {

    private static ItemHangGliderPart instance = null;

    public static String[] names = {"wing_left", "wing_right", "scaffolding"};

    public ItemHangGliderPart() {
        super();
        setCreativeTab(AvatarItems.tabItems);
        setHasSubtypes(true);
        setTranslationKey(GliderInfo.itemGliderPartName + ".");
    }

    public static ItemHangGliderPart getInstance() {
        if(instance == null) {
            instance = new ItemHangGliderPart();
            AvatarItems.addItem(instance);
        }
        return instance;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(tab)) {
            for (int i = 0; i < names.length; i++)
                subItems.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey(stack) + names[stack.getMetadata()];
    }

    @Override
    public Item item() {
        return this;
    }

    @Override
    public String getModelName(int meta) {
        return names[meta];
    }
}
