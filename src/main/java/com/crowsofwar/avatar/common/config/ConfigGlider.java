package com.crowsofwar.avatar.common.config;

import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

public class ConfigGlider {

    public static final ConfigGlider GLIDER_CONFIG = new ConfigGlider();

    //Basic Glider
    @Load
    public static final float basicGliderMinSpeed = 0.03F;
    @Load
    public static final float basicGliderMaxSpeed = 0.0515F;
    @Load
    public static final float basicGliderPitchOffset = 20F;
    @Load
    public static final float basicGliderYBoost = 0.0015F;
    @Load
    public static final float basicGliderFallReduction = 0.7F;
    @Load
    public static final float basicGliderWindModifier = 1.4F;
    @Load
    public static final float basicGliderAirResistance = 0.985F;
    @Load
    public static final int basicGliderTotalDurability = 512;

    //Advanced Glider
    @Load
    public static final float advancedGliderMinSpeed = 0.03f;
    @Load
    public static final float advancedGliderMaxSpeed = 0.0715f;
    @Load
    public static final float advancedGliderPitchOffset = 20f;
    @Load
    public static final float advancedGliderYBoost = 0.025f;
    @Load
    public static final float advancedGliderFallReduction = 0.9f;
    @Load
    public static final float advancedGliderWindModifier = 0.75f;
    @Load
    public static final float advancedGliderAirResistance = 0.99f;
    @Load
    public static final int advancedGliderTotalDurability = 2048;

    //Wind
    @Load
    public static boolean airResistanceEnabled = true;
    @Load
    public static boolean windEnabled = true;
    @Load
    public static float windOverallPower = 1.0f;
    @Load
    public static float windGustSize = 19;
    @Load
    public static float windFrequency = 0.15f;
    @Load
    public static float windRainingMultiplier = 3;
    @Load
    public static float windSpeedMultiplier = 0.4f;
    @Load
    public static float windHeightMultiplier = 1.5f;
    @Load
    public static float windDurabilityMultiplier = 0.7f;

    //Durability
    @Load
    public static boolean durabilityEnabled = true; // durability is disabled in code right now but this will be changed.
    @Load
    public static int durabilityPerUse = 1;
    @Load
    public static int durabilityTimeframe = 200;

    //Misc
    @Load
    public static boolean heatUpdraftEnabled = false;

    //Client
    @Load
    public static final boolean enableRendering3PP = true;
    @Load
    public static final boolean enableRenderingFPP = true;
    @Load
    public static final float gliderVisibilityFPPShiftAmount = 1.9f;
    @Load
    public static final boolean disableOffhandRenderingWhenGliding = true;
    @Load
    public static final boolean disableHandleBarRenderingWhenGliding = true;
    @Load
    public static final float shiftSpeedVisualShift = 0.05f;
    @Load
    public static final float airbenderHeightGain = 0.5f;

    private ConfigGlider() {
    }

    public static void load() {
        ConfigLoader.load(GLIDER_CONFIG, "avatar/glider.yml");
    }
}
