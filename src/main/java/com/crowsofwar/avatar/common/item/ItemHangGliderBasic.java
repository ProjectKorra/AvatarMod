package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.api.item.ItemHangGliderBase;
import com.crowsofwar.avatar.client.model.ModelGlider;
import com.crowsofwar.avatar.common.GliderInfo;
import com.crowsofwar.avatar.common.config.ConfigGlider;
import net.minecraft.item.Item;

public class ItemHangGliderBasic extends ItemHangGliderBase {

    public ItemHangGliderBasic() {
        super(ConfigGlider.basicGliderMinSpeed, ConfigGlider.basicGliderMaxSpeed, ConfigGlider.basicGliderPitchOffset, ConfigGlider.basicGliderYBoost, ConfigGlider.basicGliderFallReduction, ConfigGlider.basicGliderWindModifier, ConfigGlider.basicGliderAirResistance, ConfigGlider.basicGliderTotalDurability, ModelGlider.MODEL_GLIDER_BASIC_TEXTURE_RL);
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
