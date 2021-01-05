package com.crowsofwar.avatar.item;

import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.AbilityModifier;
import com.crowsofwar.avatar.registry.AvatarItem;
import com.crowsofwar.avatar.registry.AvatarItems;
import com.crowsofwar.avatar.util.GliderInfo;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

import static com.crowsofwar.avatar.config.ConfigGlider.GLIDER_CONFIG;

public class ItemHangGliderAdvanced extends ItemHangGliderBase implements AvatarItem {

    private static ItemHangGliderAdvanced instance = null;

    public ItemHangGliderAdvanced() {
        super(GLIDER_CONFIG.advancedGliderMinSpeed, GLIDER_CONFIG.advancedGliderMaxSpeed, GLIDER_CONFIG.advancedGliderPitchOffset, GLIDER_CONFIG.advancedGliderYBoost, GLIDER_CONFIG.advancedGliderFallReduction, GLIDER_CONFIG.advancedGliderWindModifier,
                GLIDER_CONFIG.advancedGliderAirResistance, GLIDER_CONFIG.advancedGliderTotalDurability, ItemHangGliderBase.MODEL_GLIDER_ADVANCED_TEXTURE_RL);
        setCreativeTab(AvatarItems.tabItems);
        setTranslationKey(GliderInfo.itemGliderAdvancedName);
    }

    public static ItemHangGliderAdvanced getInstance() {
        if (instance == null) {
            instance = new ItemHangGliderAdvanced();
            AvatarItems.addItem(instance);
        }

        return instance;
    }


    @Override
    public Item item() {
        return this;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public String getModelName(int meta) {
        switch (meta) {
//            case 1:
//				return "master_glider";
//			case 3:
//				return "master_glider_broken";
            default:
                return "master_airbender_staff";
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public AbilityModifier getAbilityModifier() {
        AbilityModifier modifier = super.getAbilityModifier();
        HashMap<String, Number> properties = new HashMap<>();
        properties.put(Ability.COOLDOWN, 0.75);
        properties.put(Ability.EXHAUSTION, 0.75);
        properties.put(Ability.BURNOUT, 0.75);
        properties.put(Ability.CHI_COST, 0.75);
        properties.put(Ability.DAMAGE, 1.125F);
        properties.put(Ability.KNOCKBACK, 1.125F);
        properties.put(Ability.SPEED, 1.125F);

        modifier.addProperties(properties);
        return modifier;
    }
}
