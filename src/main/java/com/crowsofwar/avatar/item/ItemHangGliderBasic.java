package com.crowsofwar.avatar.item;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.bending.bending.AbilityModifier;
import com.crowsofwar.avatar.bending.bending.air.AbilityAirGust;
import com.crowsofwar.avatar.bending.bending.air.AbilityAirblade;
import com.crowsofwar.avatar.registry.AvatarItems;
import com.crowsofwar.avatar.util.GliderInfo;
import com.crowsofwar.avatar.util.event.AbilityUseEvent;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

import static com.crowsofwar.avatar.config.ConfigGlider.GLIDER_CONFIG;

@Mod.EventBusSubscriber(modid = AvatarInfo.MOD_ID)
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

    @SubscribeEvent
    public static void onAirUse(AbilityUseEvent event) {
        Ability ability = event.getAbility();
        HashMap<String, Number> properties = new HashMap<>();

        if (ability instanceof AbilityAirGust || ability instanceof AbilityAirblade) {
            //Just edits the cooldown, chi cost, exhaustion, and burnout by a bit for the basic staff.
            properties.put(Ability.COOLDOWN, 0.875);
            properties.put(Ability.EXHAUSTION, 0.875);
            properties.put(Ability.BURNOUT, 0.875);
            properties.put(Ability.CHI_COST, 0.875);

            if (ability.getModifier() != null) {
                ability.getModifier().setID(STAFF_MODIFIER);
                ability.getModifier().addProperties(properties);
            } else {
                ability.setModifier(new AbilityModifier(properties, STAFF_MODIFIER));
            }
        }

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
