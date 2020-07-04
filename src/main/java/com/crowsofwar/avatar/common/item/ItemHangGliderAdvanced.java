package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.api.item.ItemHangGliderBase;
import com.crowsofwar.avatar.client.model.ModelGlider;
import com.crowsofwar.avatar.common.GliderInfo;
import com.crowsofwar.avatar.common.config.ConfigGlider;
import net.minecraft.item.Item;


public class ItemHangGliderAdvanced extends ItemHangGliderBase implements AvatarItem {

	public ItemHangGliderAdvanced() {
		super(ConfigGlider.advancedGliderMinSpeed, ConfigGlider.advancedGliderMaxSpeed, ConfigGlider.advancedGliderPitchOffset, ConfigGlider.advancedGliderYBoost, ConfigGlider.advancedGliderFallReduction, ConfigGlider.advancedGliderWindModifier, ConfigGlider.advancedGliderAirResistance, ConfigGlider.advancedGliderTotalDurability, ModelGlider.MODEL_GLIDER_ADVANCED_TEXTURE_RL);
		setCreativeTab(AvatarItems.tabItems);
		setTranslationKey(GliderInfo.itemGliderAdvancedName);
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
