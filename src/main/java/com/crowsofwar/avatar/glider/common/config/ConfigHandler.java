package com.crowsofwar.avatar.glider.common.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.crowsofwar.avatar.AvatarInfo.MOD_ID;


public class ConfigHandler {

    public static Configuration config;
    public static List<String> categories = new ArrayList<>();

    //Basic Glider
    public static float basicGliderHorizSpeed;
    public static float basicGliderVertSpeed;
    public static float basicGliderShiftHorizSpeed;
    public static float basicGliderShiftVertSpeed;

    public static float basicGliderWindModifier;
    public static float basicGliderAirResistance;
    public static int basicGliderTotalDurability;

    //Advanced Glider
    public static float advancedGliderHorizSpeed;
    public static float advancedGliderVertSpeed;
    public static float advancedGliderShiftHorizSpeed;
    public static float advancedGliderShiftVertSpeed;

    public static float advancedGliderWindModifier;
    public static float advancedGliderAirResistance;
    public static int advancedGliderTotalDurability;

    //Wind
    public static boolean airResistanceEnabled;
    public static boolean windEnabled;
    public static float windOverallPower;
    public static float windGustSize;
    public static float windFrequency;
    public static float windRainingMultiplier;
    public static float windSpeedMultiplier;
    public static float windHeightMultiplier;
    public static float windDurabilityMultiplier;

    //Durability
    public static boolean durabilityEnabled;
    public static int durabilityPerUse;
    public static int durabilityTimeframe;

    //Misc
    public static boolean heatUpdraftEnabled;

    //Client
    public static boolean enableRendering3PP;
    public static boolean enableRenderingFPP;
    public static float gliderVisibilityFPPShiftAmount;
    public static boolean disableOffhandRenderingWhenGliding;
    public static boolean disableHandleBarRenderingWhenGliding;
    public static float shiftSpeedVisualShift;

    @SubscribeEvent
    public void configChanged(ConfigChangedEvent event) {
        if (event.getModID().equals(MOD_ID)) {
            syncConfig();
        }
    }

    public static void init(File file) {
        config = new Configuration(file);
        syncConfig();
    }

    public static void syncConfig() {
        categories.clear();

        String category;

        category = "1) Basic Hang Glider Stats";
        categories.add(category);
        //ToDo: Angle and speed eventually
        basicGliderHorizSpeed = config.getFloat("1) Normal Forward Movement", category, 0.025F, 0, 100,"The amount of blocks to move forwards (per-tick) while gliding normally.");
        basicGliderVertSpeed = config.getFloat("2) Normal Fall Distance", category, 0.55F, 0, 100,"The amount of blocks a player falls (per-tick) while gliding normally.");
        basicGliderShiftHorizSpeed = config.getFloat("3) Fast Forward Movement", category, 0.05F, 0, 100,"The amount of blocks to move forwards (per-tick) while gliding fast (pressing 'Shift').");
        basicGliderShiftVertSpeed = config.getFloat("4) Fast Fall Distance", category, 0.675F, 0, 100,"The amount of blocks to fall (per-tick) while gliding fast (pressing 'Shift').");
        basicGliderWindModifier = config.getFloat("5) Overall Wind Power", category, 1.4F, 0.001F, 10, "A quality-of-life option to quickly change the overall power of the wind effect for this glider. Default is an overall relatively weak wind, with moderate gusts that occur semi-commonly. Note that this value can be a decimal (i.e. 0.5 would be half as strong). More fine-grained options are available in the 'wind' section of this config.");
        basicGliderAirResistance = config.getFloat("6) Air Resistance", category, 0.985F, 0, 1, "The rate at which air resistance hinders your movement. 1 is no resistance, 0.5 is 1/2 as fast each tick.");
        basicGliderTotalDurability = config.getInt("7) Total Durability", category, 818, 1, 10000, "The maximum durability of an unused hang glider.");

        category = "2) Advanced Hang Glider Stats";
        categories.add(category);
        advancedGliderHorizSpeed = config.getFloat("1) Normal Forward Movement", category, 0.04F, 0, 100,"The amount of blocks to move forwards (per-tick) while gliding normally.");
        advancedGliderVertSpeed = config.getFloat("2) Normal Fall Distance", category, 0.55F, 0, 100,"The amount of blocks a player falls (per-tick) while gliding normally.");
        advancedGliderShiftHorizSpeed = config.getFloat("3) Fast Forward Movement", category, 0.08F, 0, 100,"The amount of blocks to move forwards (per-tick) while gliding fast (pressing 'Shift').");
        advancedGliderShiftVertSpeed = config.getFloat("4) Fast Fall Distance", category, 0.675F, 0, 100,"The amount of blocks to fall (per-tick) while gliding fast (pressing 'Shift').");
        advancedGliderWindModifier = config.getFloat("5) Overall Wind Power", category, 0.75F, 0.001F, 10, "A quality-of-life option to quickly change the overall power of the wind effect for this glider. Default is an overall quite weak wind, with mild gusts that occur semi-commonly. Note that this value can be a decimal (i.e. 0.5 would be half as strong). More fine-grained options are available in the 'wind' section of this config."); //ToDo: playtest
        advancedGliderAirResistance = config.getFloat("6) Air Resistance", category, 0.99F, 0, 1, "The rate at which air resistance hinders your movement. 1 is no resistance, 0.5 is 1/2 as fast each tick.");
        advancedGliderTotalDurability = config.getInt("7) Total Durability", category, 2202, 1, 100000, "The maximum durability of an unused advanced hang glider.");

        category = "3) Wind";
        categories.add(category);
        airResistanceEnabled = config.getBoolean("1) Enable Air Resistance", category, true, "Enables air resistance, making the player slow down over time when flying. Values conditional on tier of glider.");
        windEnabled = config.getBoolean("1) Enable Wind", category, true, "Enables wind, making the player move unpredictably around when gliding.");
        windOverallPower = config.getFloat("2) Overall Power", category, 1.0F, 0.001F, 10, "A quality-of-life option to quickly change the overall power of the wind effect for all gliders. Default is an overall relatively weak wind, with moderate gusts that occur semi-commonly. Note that this value can be a decimal (i.e. 0.5 would be half as strong). More fine-grained options are available below.");
        windGustSize = config.getFloat("3) Gust Size", category, 19, 1, 100, "The size of the wind gusts, larger values mean the gusts push the player around in greater angles from their intended direction. Default is moderately sized. Observable gameplay effects are highly tied with wind frequency.");
        windFrequency = config.getFloat("4) Wind Frequency", category, 0.15F, 0, 5, "The frequency of the wind gusts, larger values mean the wind effects occur more often. 0 removes wind. Default is semi-common. Observable gameplay effects are highly tied with gust size.");
        windRainingMultiplier = config.getFloat("5) Rain Multiplier", category, 3, 1, 10, "How much stronger the wind should be while it is raining. 1 means the wind is the same if raining or not, 10 means the wind is 10x stronger while it is raining.");
        windSpeedMultiplier = config.getFloat("6) Speed Multiplier", category, 0.4F, -10, 10, "When going fast, the overall wind effect is changed by this multiplier. Default is that going fast reduces the wind effect by a moderate amount. 0 means the player's speed has no effect on the wind.");
        windHeightMultiplier = config.getFloat("7) Height Multiplier", category, 1.5F, -10, 10, "The player's y-level/height changes the overall wind effect by this multiplier. Default is that the higher you are up in the world the stronger the wind is, but only by a moderate amount. 0 means the player's height has no effect on the wind.");
        windDurabilityMultiplier = config.getFloat("8) Durability Multiplier", category, 0.7F, 0, 5, "The glider's durability remaining changes the overall wind effect by this additional amount. 0 means the glider's durability won't effect the wind power, whereas 1 will mean a nearly broken glider is affected by wind about twice as much as a new one.");

        category = "4) Durability";
        categories.add(category);
        durabilityEnabled = config.getBoolean("Enable Durability", category, true, "Enables durability usage of the hang glider when gliding.");
        durabilityPerUse = config.getInt("Durability Per-Use", category, 1, 0, 10000, "The durability used up each time.");
        durabilityTimeframe = config.getInt("Durability Timeframe", category, 200, 1, 10000, "The timeframe for durability usage, in ticks. Recall that there are 20 ticks in a second, so a value of 20 would damage the item about once a second. Default is 1 damage about every 10 seconds of flight, so with the default durability (618) means about 15 minutes of flight time with an undamaged glider.");

        category = "5) Misc";
        categories.add(category);
        heatUpdraftEnabled = config.getBoolean("Enable Heat Updraft", category, false, "Allows gliders to rise when gliding over hot blocks (e.g. lava). EXPERIMENTAL so disabled by default (for now).");

        category = "6) Visuals";
        categories.add(category);
        enableRendering3PP = config.getBoolean("1) Enable Rendering 3PP", category, true, "Enables rendering of the hang glider on the player in third-person perspective (or to others).");
        enableRenderingFPP = config.getBoolean("2) Enable Rendering FPP", category, true, "Enables rendering of the hang glider above the player's head in first person perspective.");
        disableOffhandRenderingWhenGliding = config.getBoolean("4) Disable Offhand Rendering While Gliding", category, true, "Disables rendering of the offhand while the player is gliding.");
        disableHandleBarRenderingWhenGliding = config.getBoolean("4) Disable Handlebar Rendering While Gliding", category, true, "Disables rendering of the handlebar (and also therefore any items held) while the player is gliding.");
        gliderVisibilityFPPShiftAmount = config.getFloat("3) First-Person Glider Visibility", category, 1.9F, 1, 4, "How high above the player's head the glider appears as in first person perspective while flying. Lower values will make it more visible/intrusive.");
        shiftSpeedVisualShift = config.getFloat("5) First-Person Glider Speed Shift", category, 0.05F, 0, 1, "How much the glider should shift visually when in fast/shift mode. 0 is none.");

        if (config.hasChanged())
            config.save();
    }

}
