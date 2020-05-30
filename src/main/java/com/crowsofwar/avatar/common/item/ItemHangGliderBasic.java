package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.api.item.ItemHangGliderBase;
import com.crowsofwar.avatar.client.model.ModelGlider;
import com.crowsofwar.avatar.common.GliderInfo;
import com.crowsofwar.avatar.common.config.ConfigHandler;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

public class ItemHangGliderBasic extends ItemHangGliderBase {

    public ItemHangGliderBasic() {
        super(ConfigHandler.basicGliderMinSpeed, ConfigHandler.basicGliderMaxSpeed, ConfigHandler.basicGliderPitchOffset, ConfigHandler.basicGliderYBoost, ConfigHandler.basicGliderFallReduction, ConfigHandler.basicGliderWindModifier, ConfigHandler.basicGliderAirResistance, ConfigHandler.basicGliderTotalDurability, ModelGlider.MODEL_GLIDER_BASIC_TEXTURE_RL);
        setCreativeTab(AvatarMod.tabItems);
        setTranslationKey(GliderInfo.itemGliderBasicName);
    }

    //ToDo: Needed?
    @Override
    public NBTTagCompound serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

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
