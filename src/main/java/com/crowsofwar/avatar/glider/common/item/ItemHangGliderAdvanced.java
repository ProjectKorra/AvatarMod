package com.crowsofwar.avatar.glider.common.item;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.glider.api.item.ItemHangGliderBase;
import com.crowsofwar.avatar.glider.client.model.ModelGlider;
import com.crowsofwar.avatar.glider.common.config.ConfigHandler;
import net.minecraft.nbt.NBTTagCompound;

import static com.crowsofwar.avatar.AvatarInfo.DOMAIN;


public class ItemHangGliderAdvanced extends ItemHangGliderBase {

    public ItemHangGliderAdvanced() {
        super(ConfigHandler.advancedGliderHorizSpeed, ConfigHandler.advancedGliderVertSpeed, ConfigHandler.advancedGliderShiftHorizSpeed, ConfigHandler.advancedGliderShiftVertSpeed, ConfigHandler.advancedGliderWindModifier, ConfigHandler.advancedGliderAirResistance, ConfigHandler.advancedGliderTotalDurability, ModelGlider.MODEL_GLIDER_ADVANCED_TEXTURE_RL);
        setCreativeTab(AvatarItems.tabItems);
        setTranslationKey(DOMAIN + AvatarInfo.ITEM_GLIDER_ADVANCED_NAME);
    }

    //ToDo: Needed?
    @Override
    public NBTTagCompound serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

    }
}
