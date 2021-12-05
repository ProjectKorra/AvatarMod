package com.crowsofwar.avatar.client.render.lightning.configs;

import com.crowsofwar.avatar.client.render.lightning.main.MainRegistry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CommonConfig {

    public static boolean createConfigBool(Configuration config, String category, String name, String comment, boolean def) {

        Property prop = config.get(category, name, def);
        prop.setComment(comment);
        return prop.getBoolean();
    }

    public static String createConfigString(Configuration config, String category, String name, String comment, String def) {

        Property prop = config.get(category, name, def);
        prop.setComment(comment);
        return prop.getString();
    }

    public static String[] createConfigStringList(Configuration config, String category, String name, String comment) {

        Property prop = config.get(category, name, new String[] { "PLACEHOLDER" });
        prop.setComment(comment);
        return prop.getStringList();
    }

    public static int createConfigInt(Configuration config, String category, String name, String comment, int def) {

        Property prop = config.get(category, name, def);
        prop.setComment(comment);
        return prop.getInt();
    }

    public static int setDefZero(int value, int def) {

        if(value < 0) {
            MainRegistry.logger.error("Fatal error config: Randomizer value has been below zero, despite bound having to be positive integer!");
            MainRegistry.logger.error(String.format("Errored value will default back to %d, PLEASE REVIEW CONFIGURATION DESCRIPTION BEFORE MEDDLING WITH VALUES!", def));
            return def;
        }

        return value;
    }

    public static int setDef(int value, int def) {

        if(value <= 0) {
            MainRegistry.logger.error("Fatal error config: Randomizer value has been set to zero, despite bound having to be positive integer!");
            MainRegistry.logger.error(String.format("Errored value will default back to %d, PLEASE REVIEW CONFIGURATION DESCRIPTION BEFORE MEDDLING WITH VALUES!", def));
            return def;
        }

        return value;
    }

}
