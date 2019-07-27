package com.crowsofwar.avatar.glider.common.wind;

import com.crowsofwar.avatar.api.item.IGlider;
import com.crowsofwar.avatar.glider.common.config.ConfigHandler;
import com.crowsofwar.avatar.glider.common.wind.generator.OpenSimplexNoise;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;


/**
 * Applies "wind" effects to the player, moving them around when they fly.
 */
public class WindHelper {

    /** Simplex noise has a better "game feel" than Perlin (the standard for Minecraft), so ti is used here. */
    public static OpenSimplexNoise noiseGenerator;

    /** Initialize noise generator once and store in a static variable for performance. */
    public static void initNoiseGenerator() {
        noiseGenerator = new OpenSimplexNoise();
    }

    /**
     * Apply wind effect, buffeting them around pseudo-randomly.
     * Affected by a variety of things, from player height to glider durability to weather, etc.
     *
     * @param player - the player to move around
     * @param glider - the hang glider item
     */
    public static void applyWind(EntityPlayer player, ItemStack glider){

        if (!ConfigHandler.windEnabled) return; //if no wind, then do nothing

        double windGustSize = ConfigHandler.windGustSize; //18;
        double windFrequency = ConfigHandler.windFrequency; //0.15;
        double windRainingMultiplier = ConfigHandler.windRainingMultiplier; //4;
        double windSpeedMultiplier = ConfigHandler.windSpeedMultiplier; //0.4;
        double windHeightMultiplier = ConfigHandler.windHeightMultiplier; //1.2;
        double windOverallPower = ConfigHandler.windOverallPower; //1;

        //downscale for gust size/occurrence amount
        double noise = WindHelper.noiseGenerator.eval(player.posX / windGustSize, player.posZ / windGustSize); //occurrence amount

        //multiply by intensity factor (alter by multiplier if raining)
        noise *= player.world.isRaining() ? windRainingMultiplier * windFrequency : windFrequency;

        //stabilize somewhat depending on velocity
        double velocity = Math.sqrt(Math.pow(player.motionX, 2) + Math.pow(player.motionZ, 2)); //player's velocity
        double speedStabilized = noise * 1/((velocity * windSpeedMultiplier) + 1); //stabilize somewhat with higher speeds

        //increase wind depending on world height
        double height = player.posY < 256 ? (player.posY / 256) * windHeightMultiplier : windHeightMultiplier; //world height clamp

        //apply stabilized speed with height
        double wind = speedStabilized * height;

        //apply durability modifier
        double additionalDamagePercentage = glider.isItemDamaged() ? ConfigHandler.windDurabilityMultiplier * ((double)glider.getItemDamage() / (glider.getMaxDamage())) : 0; //1.x where x is the percent of durability used
        wind *= 1 + additionalDamagePercentage;

        //apply overall wind power multiplier
        wind *= windOverallPower;

        //apply tier specific wind power multiplier
        wind *= ((IGlider)glider.getItem()).getWindMultiplier();

        //apply final rotation based on all the above
        player.rotationYaw += wind;
    }

}
