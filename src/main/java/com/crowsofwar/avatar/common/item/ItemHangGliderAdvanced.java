package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.api.item.ItemHangGliderBase;
import com.crowsofwar.avatar.client.model.ModelGlider;
import com.crowsofwar.avatar.common.GliderInfo;
import net.minecraft.item.Item;

import static com.crowsofwar.avatar.common.config.ConfigGlider.GLIDER_CONFIG;

public class ItemHangGliderAdvanced extends ItemHangGliderBase implements AvatarItem {

	private static ItemHangGliderAdvanced instance = null;

	public static ItemHangGliderAdvanced getInstance() {
		if(instance == null) {
			instance = new ItemHangGliderAdvanced();
			AvatarItems.addItem(instance);
		}

		return instance;
	}

	public ItemHangGliderAdvanced() {
		super(GLIDER_CONFIG.advancedGliderMinSpeed, GLIDER_CONFIG.advancedGliderMaxSpeed, GLIDER_CONFIG.advancedGliderPitchOffset, GLIDER_CONFIG.advancedGliderYBoost, GLIDER_CONFIG.advancedGliderFallReduction, GLIDER_CONFIG.advancedGliderWindModifier,
				GLIDER_CONFIG.advancedGliderAirResistance, GLIDER_CONFIG.advancedGliderTotalDurability, ItemHangGliderBase.MODEL_GLIDER_ADVANCED_TEXTURE_RL);
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
