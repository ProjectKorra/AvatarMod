package com.crowsofwar.avatar.common.item.scroll;

import java.util.List;

import com.crowsofwar.avatar.common.item.AvatarItem;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.gorecore.format.FormattedMessageProcessor;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.crowsofwar.avatar.common.AvatarChatMessages.MSG_SPECIALTY_SCROLL_TOOLTIP;

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

    @Override
    public String getTranslationKey(ItemStack stack) {
        return "scroll_" + type.displayName() + "_" + stack.getMetadata();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltips, ITooltipFlag advanced) {

        String tooltip = I18n.format("avatar." + Scrolls.getTypeForStack(stack).getBendingName());
        tooltips.add(tooltip);

        if (Scrolls.getTypeForStack(stack).isSpecialtyType()) {
            String translated = I18n.format("avatar.specialtyScroll.tooltip");
            String bendingName = Scrolls.getTypeForStack(stack).getBendingName();
            String formatted = FormattedMessageProcessor.formatText(MSG_SPECIALTY_SCROLL_TOOLTIP, translated,
                    bendingName);
            tooltips.add(formatted);
        }

    }

}