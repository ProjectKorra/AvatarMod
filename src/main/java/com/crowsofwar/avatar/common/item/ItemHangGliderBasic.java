package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.api.item.ItemHangGliderBase;
import com.crowsofwar.avatar.client.model.ModelGlider;
import com.crowsofwar.avatar.common.GliderInfo;
import com.crowsofwar.avatar.common.config.ConfigHandler;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import static com.crowsofwar.avatar.AvatarInfo.MOD_ID;

public class ItemHangGliderBasic extends ItemHangGliderBase {

    public ItemHangGliderBasic() {
        super(ConfigHandler.basicGliderHorizSpeed, ConfigHandler.basicGliderVertSpeed, ConfigHandler.basicGliderShiftHorizSpeed, ConfigHandler.basicGliderShiftVertSpeed, ConfigHandler.advancedGliderSpaceHorizSpeed, ConfigHandler.advancedGliderSpaceVertSpeed, ConfigHandler.basicGliderWindModifier, ConfigHandler.basicGliderAirResistance, ConfigHandler.basicGliderTotalDurability, ModelGlider.MODEL_GLIDER_BASIC_TEXTURE_RL);
        setCreativeTab(AvatarItems.tabItems);
        setTranslationKey(GliderInfo.ITEM_GLIDER_BASIC_NAME);
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
