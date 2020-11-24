package com.crowsofwar.avatar.config;

import com.crowsofwar.gorecore.config.ConfigLoader;
import com.crowsofwar.gorecore.config.Load;

public class ConfigGlider {

    public static final ConfigGlider GLIDER_CONFIG = new ConfigGlider();
    //Misc
    @Load
    public static boolean heatUpdraftEnabled = false;
    //Basic Glider
    @Load
    public float basicGliderMinSpeed = 0.03F;
    @Load
    public float basicGliderMaxSpeed = 0.0615F;
    @Load
    public float basicGliderPitchOffset = 20F;
    @Load
    public float basicGliderYBoost = 0.0025F;
    @Load
    public float basicGliderFallReduction = 0.7F;
    @Load
    public float basicGliderWindModifier = 1.4F;
    @Load
    public float basicGliderAirResistance = 0.985F;
    @Load
    public int basicGliderTotalDurability = 512;
    //Advanced Glider
    @Load
    public float advancedGliderMinSpeed = 0.03f;
    @Load
    public float advancedGliderMaxSpeed = 0.0715f;
    @Load
    public float advancedGliderPitchOffset = 20f;
    @Load
    public float advancedGliderYBoost = 0.025f;
    @Load
    public float advancedGliderFallReduction = 0.9f;
    @Load
    public float advancedGliderWindModifier = 0.75f;
    @Load
    public float advancedGliderAirResistance = 0.99f;
    @Load
    public int advancedGliderTotalDurability = 2048;
    //Wind
    @Load
    public boolean airResistanceEnabled = true;
    @Load
    public boolean windEnabled = true;
    @Load
    public float windOverallPower = 3.0f;
    @Load
    public float windGustSize = 19;
    @Load
    public float windFrequency = 0.15f;
    @Load
    public float windRainingMultiplier = 3;
    @Load
    public float windSpeedMultiplier = 0.4f;
    @Load
    public float windHeightMultiplier = 1.5f;
    @Load
    public float windDurabilityMultiplier = 0.7f;
    //Durability
    @Load
    public boolean durabilityEnabled = true; // durability is disabled in code right now but this will be changed.
    @Load
    public int durabilityPerUse = 1;
    @Load
    public int durabilityTimeframe = 200;
    //Client
    @Load
    public boolean enableRendering3PP = true;
    @Load
    public boolean enableRenderingFPP = true;
    @Load
    public float gliderVisibilityFPPShiftAmount = 1.9f;
    @Load
    public boolean disableOffhandRenderingWhenGliding = true;
    @Load
    public boolean disableHandleBarRenderingWhenGliding = true;
    @Load
    public float shiftSpeedVisualShift = 0.05f;
    @Load
    public float airbenderHeightGain = 0.5f;

    private ConfigGlider() {
    }

    public static void load() {
        ConfigLoader.load(GLIDER_CONFIG, "avatar/glider.yml");
    }
}
