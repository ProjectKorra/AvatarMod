package com.crowsofwar.avatar.common.config;

import com.crowsofwar.avatar.AvatarInfo;
import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AbilityProperties {

    /**
     * Big code block time! This class will mirror Electroblob Wizardry's SpellProperties class, except for ability usage.
     * Spell Context (as in the enum Context) is completely unnecssary here, and can be ignore/deleted.
     * The only constant variable that won't change based on an ability's level is their xp on a hit, as AbilityData handles decreasing xp calculations.
     * Everything else should vary based on level. Therefore, you only need to store one base value.
     * Additionally, the SpellProperties class needs an equivalent AbilityProperties class, and abilities also need a getPropertyKeys() method.
     * Make sure to check out Spell, SpellProperties, and SpellContext for what to put down!
     * Theoretically, you should be able ot delete all the variables except xp cost. (You'll have to add that). Everything else would be a custom value.
     */


    private static final Gson gson = new Gson();
    /**
     * A map storing the base values for this ability. These values are defined by the ability class and cannot be
     * changed.
     */
    // We're using Number here because it makes implementors think about what they convert it to.
    // If we did what attributes do and just use doubles, people (myself included!) might plug them into calculations
    // without thinking. However, with Number you can't just do that, you have to convert and therefore you have to
    // decide how to do the conversion. Internally they're handled as floats though.
    private final Multimap<String, Number> baseValues;

    private final Multimap<String, Boolean> baseBooleans;

    /**
     * Parses the given JSON object and constructs a new {@code AbilityProperties} from it, setting all the relevant
     * fields and references.
     *
     * @param json    A JSON object representing the spell properties to be constructed.
     * @param ability The spell that this {@code AbilityConfig} object is for.
     * @throws JsonSyntaxException if at any point the JSON object is found to be invalid.
     */
    private AbilityProperties(JsonObject json, Ability ability) {

        List<String> baseValueNames = Arrays.asList(ability.getPropertyKeys());
        List<String> baseBooleanNames = Arrays.asList(ability.getBooleanPropertyKeys());

        baseValues = ArrayListMultimap.create();
        baseBooleans = ArrayListMultimap.create();
        JsonObject customProperties = JsonUtils.getJsonObject(json, "custom_properties");

        Collections.sort(baseValueNames);
        Collections.sort(baseBooleanNames);
        for (String name : baseValueNames) {
            if (name.equalsIgnoreCase("xpOnHit") || name.equalsIgnoreCase("xpOnUse")) {
                baseValues.put(name, JsonUtils.getFloat(customProperties, name));
            }
        }

//        for (String name : baseBooleanNames) {
//            baseBooleans.put(name, JsonUtils.getBoolean(customProperties, name));
//        }

        // There's not much point specifying the classes of the numbers here because the json getter methods just
        // perform conversion to the requested type anyway. It therefore makes very little difference whether the
        // conversion is done during JSON parsing or when we actually use the value - and at least in the latter case,
        // individual subclasses have control over how it is converted.

        // My case in point: summoning 2.5 spiders is obviously nonsense, but what happens when we cast that with a
        // modifier of 2? Should we round the base value down to 2 and then apply the x2 modifier to get 4 spiders?
        // Should we round it up instead? Or should we apply the modifier first and then do the rounding, so with no
        // modifier we still get 2 spiders but with the x2 modifier we get 5?
        // The most pragmatic solution is to let the spell class decide for itself.
        // (Of course, we can only hope that the users aren't jerks and don't try to summon 2 and a half spiders...)

        /*
         * Big note! FavouriteDragon here (most of the other comments are EB). Rather than just using "base_properties",
         * you'll have to iterate through each level property. level1, level2, level3, level4_1, and level4_2, as each of them contain their own
         * unique values for each variable. Additionally, some may have special booleans or extra numbers. E.g:
         * Fireball level4_1 has an int numberOfFireballs.
         */

        for (int i = 0; i < 5; i++) {
            String jsonName = "level" + (i + 1);
            if (i >= 3)
                jsonName = "level4_" + (i - 2);
            JsonObject baseValueObject = JsonUtils.getJsonObject(customProperties, jsonName);

            // If the code requests more values than the JSON file contains, that will cause a JsonSyntaxException here anyway.
            // If there are redundant values in the JSON file, chances are that a user has misunderstood the system and tried
            // to add properties that aren't implemented. However, redundant values will also be found if a programmer has
            // forgotten to call addProperties in their ability constructor (I know I have!), potentially causing a crash at
            // some random point in the future. Since redundant values aren't a problem by themselves, we shouldn't throw an
            // exception, but a warning is appropriate.

            int redundantKeys = baseValueObject.size() - baseValueNames.size();
            if (redundantKeys > 0) AvatarLog.warn("Ability " + ability.getName() + " has " + redundantKeys +
                    " redundant ability property key(s) defined in its JSON file. Extra values will have no effect! (Av2 devs:" +
                    " make sure you have called addProperties(...) during ability construction)");

            if (baseValueNames.size() > 0) {

                for (String baseValueName : baseValueNames) {
                    if (!baseValueName.equalsIgnoreCase("xpOnHit") && !baseValueName.equalsIgnoreCase("xpOnUse"))
                        try {
                            baseValues.put(baseValueName, JsonUtils.getFloat(baseValueObject, baseValueName));
                        } catch (JsonSyntaxException e) {
                            AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Either someone's been lazy and left out a value, or your properties file is screwed.");
                        }
                }
            }
            if (baseBooleanNames.size() > 0) {
                for (String baseBooleanName : baseBooleanNames) {
                    try {
                        baseBooleans.put(baseBooleanName, JsonUtils.getBoolean(baseValueObject, baseBooleanName));
                    } catch (JsonSyntaxException e) {
                        AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Either someone's been lazy and left out a value, or your properties file is screwed.");
                    }
                }
            }
        }

    }

    // Sometimes it just makes more sense to do the JSON parsing in the constructor
    // It's the only way we're gonna keep the fields final!

    /**
     * Constructs a new AbilityProperties object for the given ability, reading its values from the given ByteBuf.
     */
    public AbilityProperties(Ability ability, ByteBuf buf) {

        baseValues = ArrayListMultimap.create();
        baseBooleans = ArrayListMultimap.create();

        List<String> keys = Arrays.asList(ability.getPropertyKeys());
        List<String> bKeys = Arrays.asList(ability.getBooleanPropertyKeys());
        Collections.sort(keys); // Should be the same list of keys in the same order they were written to the ByteBuf
        Collections.sort(bKeys);

        for (String key : keys) {
            baseValues.put(key, buf.readFloat());
        }
        for (String key : bKeys) {
            baseBooleans.put(key, buf.readBoolean());
        }
    }

    /**
     * Called from preInit() in the main mod class to initialise the ability property system.
     */
    // For some reason I had this called from a method in CommonProxy which was overridden to do nothing in
    // ClientProxy, but that method was never called and instead this one was called directly from the main mod class.
    // I *think* I decided against the proxy thing and just forgot to delete the methods (they're gone now), but if
    // things don't work as expected then that may be why - pretty sure it's fine though since the properties get
    // wiped client-side on each login anyway.
    public static void init() {
        boolean flag = loadConfigAbilityProperties();

        flag &= loadBuiltInAbilityProperties(); // Don't short-circuit, or mods later on won't get loaded!

        if (!flag)
            AvatarLog.warn("Some ability property files did not load correctly; this will likely cause problems later!");
    }

    public static void loadWorldSpecificAbilityProperties(World world) {

        AvatarLog.info("Loading custom ability properties for world {" + world.getWorldInfo().getWorldName() + "}");

        File abilityJSONDir = new File(new File(world.getSaveHandler().getWorldDirectory(), "data"), "abilities");

        if (abilityJSONDir.mkdirs()) return; // If it just got created it can't possibly have anything inside

        if (!loadAbilityPropertiesFromDir(abilityJSONDir))
            AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Some ability property files did not load correctly; this will likely cause problems later!");
    }

    private static boolean loadConfigAbilityProperties() {

        AvatarLog.info("Loading ability properties from config folder");

        File abilityJsonDir = new File("avatar/abilities");

        if (!abilityJsonDir.exists() || !abilityJsonDir.mkdir())
            return true; // If there's no global spell properties folder, do nothing

        return loadAbilityPropertiesFromDir(abilityJsonDir);
    }

    private static boolean loadBuiltInAbilityProperties() {

        ModContainer mod = Loader.instance().getModList().stream().filter(modContainer -> modContainer.getModId().equals(AvatarInfo.MOD_ID)).findFirst()
                .orElse(null);
        // Spells will be removed from this list as their properties are set
        // If everything works properly, it should be empty by the end
        List<Ability> abilities = new ArrayList<>(Abilities.all());

        AvatarLog.info("Loading built-in ability properties for " + abilities.size() + " abilities in " + AvatarInfo.MOD_ID);

        // This method is used by Forge to load mod recipes and advancements, so it's a fair bet it's the right one
        // In the absence of Javadoc, here's what the non-obvious parameters do:
        // - preprocessor is called once with just the root directory, allowing any global index files to be processed
        // - processor is called once for each file in the directory so processing can be done
        // - defaultUnfoundRoot is the default value to return if the root specified isn't found
        // - visitAllFiles determines whether the method short-circuits; in other words, if the processor returns false
        // at any point and visitAllFiles is false, the method returns immediately.
        assert mod != null;
        //If `mod` is null then av2 isn't loaded and you have other issues
        boolean success = CraftingHelper.findFiles(mod, "assets/" + AvatarInfo.MOD_ID + "/abilities", null,

                (root, file) -> {

                    String relative = root.relativize(file).toString();
                    if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
                        return true; // True or it'll look like it failed just because it found a non-JSON file

                    String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
                    ResourceLocation key = new ResourceLocation(AvatarInfo.MOD_ID, name);

                    Ability ability = Abilities.get(name);

                    // If no ability matches a particular file, log it and just ignore the file
                    if (ability == null) {
                        AvatarLog.error("Ability config file is for an ability that doesn't exist.");
                        return true;
                    }

                    // We want to do this regardless of whether the JSON file got read properly, because that prints its
                    // own separate warning
                    if (!abilities.remove(ability)) AvatarLog.error("What's going on?!");

                    // Ignore spells overridden in the config folder
                    // This needs to be done AFTER the above line or it'll think there are missing ability properties files
                    if (ability.arePropertiesInitialised()) return true;

                    BufferedReader reader = null;

                    try {

                        reader = Files.newBufferedReader(file);

                        JsonObject json = JsonUtils.fromJson(gson, reader, JsonObject.class);
                        AbilityProperties properties = new AbilityProperties(json, ability);
                        ability.setProperties(properties);
                        AvatarLog.info("Property load successful for " + key + " !");

                    } catch (JsonParseException jsonparseexception) {
                        AvatarLog.error("Parsing error loading ability property file for " + key, jsonparseexception);
                        return false;
                    } catch (IOException ioexception) {
                        AvatarLog.error("Couldn't read ability property file for " + key, ioexception);
                        return false;
                    } finally {
                        IOUtils.closeQuietly(reader);
                    }

                    return true;

                },
                true, true);

        // If a spell is missing its file, log an error
        if (!abilities.isEmpty()) {
            if (abilities.size() <= 15) {
                abilities.forEach(a -> AvatarLog.error("Ability " + a.getName() + " is missing a properties file!"));
            } else {
                // If there are more than 15 don't bother logging them all, chances are they're all missing
                AvatarLog.error("Mod " + AvatarInfo.MOD_ID + " has " + abilities.size() + " abilities that are missing properties files!");
            }
        }

        return success;
    }

    private static boolean loadAbilityPropertiesFromDir(File dir) {

        boolean success = true;

        for (File file : FileUtils.listFiles(dir, new String[]{"json"}, true)) {

            // The structure in world and config folders is subtly different in that the "spells" and mod id directories
            // are in the opposite order, i.e. it's spells/modid/whatever.json instead of modid/spells/whatever.json
            String relative = dir.toPath().relativize(file.toPath()).toString(); // modid\whatever.json
            String nameAndModID = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/"); // modid/whatever
            String modID = nameAndModID.split("/")[0]; // modid
            String name = nameAndModID.substring(nameAndModID.indexOf('/') + 1); // whatever

            ResourceLocation key = new ResourceLocation(modID, name);

            Ability ability = Abilities.get(name);

            // If no ability matches a particular file, log it and just ignore the file
            if (ability == null) {
                AvatarLog.error("Attempted to get an ability that doesn't exist from file location " + key);
                continue;
            }

            BufferedReader reader = null;

            try {

                reader = Files.newBufferedReader(file.toPath());

                JsonObject json = JsonUtils.fromJson(gson, reader, JsonObject.class);
                AbilityProperties properties = new AbilityProperties(json, ability);
                ability.setProperties(properties);

            } catch (JsonParseException jsonparseexception) {
                AvatarLog.error("Parsing error loading ability property file for " + key, jsonparseexception);
                success = false;
            } catch (IOException ioexception) {
                AvatarLog.error("Couldn't read ability property file for " + key, ioexception);
                success = false;
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }

        return success;
    }

    // There are now three 'layers' of ability properties - in order of priority, these are:
    // 1. World-specific properties, stored in saves/[world]/data/abilities
    // 2. Global overrides, stored in config/avatar/abilities
    // 3. Built-in properties, stored in mods/[mod jar]/assets/abilities
    // There's a method for loading each of these below, because that makes sense to me!

    /**
     * Writes this AbilityProperties object to the given ByteBuf so it can be sent via packets.
     */
    public void write(ByteBuf buf) {

        List<String> keys = new ArrayList<>(baseValues.keys());
        Collections.sort(keys); // Sort alphabetically (as long as the order is consistent it doesn't matter)

        if (!keys.isEmpty())
            for (String key : keys) {
                for (Number num : baseValues.get(key))
                    buf.writeFloat(num.floatValue());
            }

        List<String> bKeys = new ArrayList<>(baseBooleans.keys());
        Collections.sort(bKeys);
        if (!bKeys.isEmpty())
            for (String key : bKeys) {
                for (boolean b : baseBooleans.get(key))
                    buf.writeBoolean(b);
            }
    }

    // For crafting recipes, Forge does some stuff behind the scenes to load recipe JSON files from mods' namespaces.
    // This leverages the same methods.

    /**
     * Returns the base value for this spell that corresponds to the given identifier.
     *
     * @param identifier The string identifier to fetch the base value for.
     * @return The base value, as a {@code Number}.
     * @throws IllegalArgumentException if no base value was defined with the given identifier.
     */
    public Number getBaseValue(String identifier, int abilityLevel, AbilityData.AbilityTreePath path) {
        if (!baseValues.containsKey(identifier)) {
            throw new IllegalArgumentException("Base value with identifier '" + identifier + "' is not defined.");
        }
        ArrayList<Number> list = new ArrayList<>(baseValues.get(identifier));
        return list.get(Math.min(list.size() - 1, Math.max(path == AbilityData.AbilityTreePath.SECOND ? abilityLevel + 1 : abilityLevel, 0)));
    }

    /**
     * Returns the base value for this spell that corresponds to the given identifier.
     *
     * @param identifier The string identifier to fetch the base value for.
     * @return The base value, as a {@code Number}.
     * @throws IllegalArgumentException if no base value was defined with the given identifier.
     */
    public boolean getBaseBooleanValue(String identifier, int abilityLevel, AbilityData.AbilityTreePath path) {
        if (!baseBooleans.containsKey(identifier)) {
            throw new IllegalArgumentException("Base value with identifier '" + identifier + "' is not defined.");
        }
        ArrayList<Boolean> list = new ArrayList<>(baseBooleans.get(identifier));
        return list.get(Math.min(list.size() - 1, Math.max(path == AbilityData.AbilityTreePath.SECOND ? abilityLevel + 1 : abilityLevel, 0)));
    }

}

