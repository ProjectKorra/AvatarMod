package com.crowsofwar.avatar.item;

import com.crowsofwar.avatar.util.GliderInfo;
import com.crowsofwar.avatar.registry.AvatarItem;
import com.crowsofwar.avatar.registry.AvatarItems;
import net.minecraft.item.Item;

import static com.crowsofwar.avatar.config.ConfigGlider.GLIDER_CONFIG;

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
