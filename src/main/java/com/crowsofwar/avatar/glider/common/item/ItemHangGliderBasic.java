package com.crowsofwar.avatar.glider.common.item;

import com.crowsofwar.avatar.common.item.AvatarItems;
import com.crowsofwar.avatar.glider.api.item.ItemHangGliderBase;
import com.crowsofwar.avatar.glider.client.model.ModelGlider;
import com.crowsofwar.avatar.glider.common.config.ConfigHandler;
import com.crowsofwar.avatar.glider.common.lib.ModInfo;
import net.minecraft.nbt.NBTTagCompound;

import static com.crowsofwar.avatar.AvatarInfo.MOD_ID;

public class ItemHangGliderBasic extends ItemHangGliderBase {

    public ItemHangGliderBasic() {
        super(ConfigHandler.basicGliderHorizSpeed, ConfigHandler.basicGliderVertSpeed, ConfigHandler.basicGliderShiftHorizSpeed, ConfigHandler.basicGliderShiftVertSpeed, ConfigHandler.basicGliderWindModifier, ConfigHandler.basicGliderAirResistance, ConfigHandler.basicGliderTotalDurability, ModelGlider.MODEL_GLIDER_BASIC_TEXTURE_RL);
        setCreativeTab(AvatarItems.tabItems);
        setTranslationKey(MOD_ID +":" + ModInfo.ITEM_GLIDER_BASIC_NAME);
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
