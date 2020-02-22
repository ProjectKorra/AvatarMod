package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.api.item.ItemHangGliderBase;
import com.crowsofwar.avatar.client.model.ModelGlider;
import com.crowsofwar.avatar.common.GliderInfo;
import com.crowsofwar.avatar.common.config.ConfigHandler;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;


public class ItemHangGliderAdvanced extends ItemHangGliderBase implements AvatarItem {

	public ItemHangGliderAdvanced() {
		super(ConfigHandler.advancedGliderMinSpeed, ConfigHandler.advancedGliderMaxSpeed, ConfigHandler.advancedGliderPitchOffset, ConfigHandler.advancedGliderYBoost, ConfigHandler.advancedGliderFallReduction, ConfigHandler.advancedGliderWindModifier, ConfigHandler.advancedGliderAirResistance, ConfigHandler.advancedGliderTotalDurability, ModelGlider.MODEL_GLIDER_ADVANCED_TEXTURE_RL);
		setCreativeTab(AvatarItems.tabItems);
		setTranslationKey(GliderInfo.ITEM_GLIDER_ADVANCED_NAME);
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
		switch (meta) {
            case 1:
				return "hang_glider_advanced_deployed";
			case 3:
				return "hang_glider_advanced_broken";
			default:
				return "hang_glider_advanced";
		}
	}
}
