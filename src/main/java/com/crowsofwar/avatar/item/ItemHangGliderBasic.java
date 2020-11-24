package com.crowsofwar.avatar.item;

import com.crowsofwar.avatar.util.GliderInfo;
import com.crowsofwar.avatar.registry.AvatarItems;
import net.minecraft.item.Item;

import static com.crowsofwar.avatar.config.ConfigGlider.GLIDER_CONFIG;

public class ItemHangGliderBasic extends ItemHangGliderBase {

    private static ItemHangGliderBasic instance = null;

    public static ItemHangGliderBasic getInstance() {
        if(instance == null) {
            instance = new ItemHangGliderBasic();
            AvatarItems.addItem(instance);
        }

        return instance;
    }

    public ItemHangGliderBasic() {
        super(GLIDER_CONFIG.basicGliderMinSpeed, GLIDER_CONFIG.basicGliderMaxSpeed, GLIDER_CONFIG.basicGliderPitchOffset, GLIDER_CONFIG.basicGliderYBoost, GLIDER_CONFIG.basicGliderFallReduction, GLIDER_CONFIG.basicGliderWindModifier,
                GLIDER_CONFIG.basicGliderAirResistance, GLIDER_CONFIG.basicGliderTotalDurability, ItemHangGliderBase.MODEL_GLIDER_BASIC_TEXTURE_RL);
        setCreativeTab(AvatarItems.tabItems);
        setTranslationKey(GliderInfo.itemGliderBasicName);
    }

    @Override
    public Item item() {
        return this;
    }

    @Override
    public String getModelName(int meta) {
        return super.getModelName(meta);
    }
}
