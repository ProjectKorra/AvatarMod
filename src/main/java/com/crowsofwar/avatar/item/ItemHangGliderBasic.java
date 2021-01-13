package com.crowsofwar.avatar.item;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.AbilityModifier;
import com.crowsofwar.avatar.registry.AvatarItems;
import com.crowsofwar.avatar.util.GliderInfo;
import net.minecraft.item.Item;

import java.util.HashMap;

import static com.crowsofwar.avatar.config.ConfigGlider.GLIDER_CONFIG;

public class ItemHangGliderBasic extends ItemHangGliderBase {

    private static ItemHangGliderBasic instance = null;

    public ItemHangGliderBasic() {
        super(GLIDER_CONFIG.basicGliderMinSpeed, GLIDER_CONFIG.basicGliderMaxSpeed, GLIDER_CONFIG.basicGliderPitchOffset, GLIDER_CONFIG.basicGliderYBoost, GLIDER_CONFIG.basicGliderFallReduction, GLIDER_CONFIG.basicGliderWindModifier,
                GLIDER_CONFIG.basicGliderAirResistance, GLIDER_CONFIG.basicGliderTotalDurability, ItemHangGliderBase.MODEL_GLIDER_BASIC_TEXTURE_RL);
        setCreativeTab(AvatarItems.tabItems);
        setTranslationKey(GliderInfo.itemGliderBasicName);
    }

    public static ItemHangGliderBasic getInstance() {
        if (instance == null) {
            instance = new ItemHangGliderBasic();
            AvatarItems.addItem(instance);
        }

        return instance;
    }

    @Override
    public Item item() {
        return this;
    }

    @Override
    public AbilityModifier getAbilityModifier() {
        AbilityModifier mods = super.getAbilityModifier();
        HashMap<String, Number> properties = new HashMap<>();

        properties.put(Ability.COOLDOWN, 0.875);
        properties.put(Ability.EXHAUSTION, 0.875);
        properties.put(Ability.BURNOUT, 0.875);
        properties.put(Ability.CHI_COST, 0.875);

        mods.addProperties(properties);
        return mods;
    }
}
