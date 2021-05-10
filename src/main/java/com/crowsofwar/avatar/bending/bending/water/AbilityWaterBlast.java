package com.crowsofwar.avatar.bending.bending.water;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.avatar.util.data.ctx.AbilityContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.Objects;

/**
 * Are you proud of me, dad? I'm outlining how this should work in the documentation.
 * I'm actually following conventions. Praise be!
 * <p>
 * Water Blast -- Waterbending
 * This ability is designed for offense. It allows for some defensive manoeuvres and complete annihilation.
 * Charge up to create a bigger blast! As with most waterbending abilities,
 * it can be frozen and electrocuted.
 * <p>
 * Level 1 - Simple Water Blast. Animated swirl charging.
 * Level 2 - Faster, Stronger, Bigger, Cooler. Ya know. Pierces.
 * Level 3 - Charging now creates a shield, and you can left click to burst! 3D Animated Swirl. Draw from plants!
 *           Big source radius.
 * Level 4 Path 1: Crushing Cyclone - When charging, your shield has an AOE slow and damaging effect. Additionally, your burst pulls nearby enemies in
 * before annihilating them.
 * Level 4 Path 2: Piercing Maelstrom - Your blasts are significantly empowered, creating massive explosions upon hitting a surface.
 * They carry hit objects to where they explode.
 *
 * @author FavouriteDragon
 */
public class AbilityWaterBlast extends Ability {

    public static String
            SHIELD = "shield",
            BURST = "burst",
            PULL = "pull";

    public AbilityWaterBlast() {
        super(Waterbending.ID, "water_blast");
        requireRaytrace(-1, false);
    }

    @Override
    public void init() {
        super.init();
        addProperties(WATER_AMOUNT, SOURCE_ANGLES, SOURCE_RANGE);
        addBooleanProperties(PLANT_BEND, SHIELD, BURST, PULL);
    }

    @Override
    public void execute(AbilityContext ctx) {

        //TODO: Swirl!
        Bender bender = ctx.getBender();
        EntityLivingBase entity = ctx.getBenderEntity();
        BendingData data = ctx.getData();
        World world = ctx.getWorld();
        AbilityData abilityData = ctx.getAbilityData();

        Vector targetPos = Waterbending.getClosestWaterbendableBlock(entity,
                Objects.requireNonNull(Abilities.get("water_blast")), ctx);
        boolean hasWaterCharge = data.hasStatusControl(StatusControlController.CHARGE_WATER) ||
                data.hasStatusControl(StatusControlController.RELEASE_WATER);
        int waterAmount = getProperty(WATER_AMOUNT).intValue();

        if (ctx.getAbilityData().getAbilityCooldown(entity) == 0 || getCooldown(ctx) == 0) {
            if (bender.consumeChi(getChiCost(ctx)) && !hasWaterCharge) {
                if (ctx.consumeWater(waterAmount)) {
                    data.addStatusControl(StatusControlController.CHARGE_WATER);
                } else if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
                    data.addStatusControl(StatusControlController.CHARGE_WATER);
                } else if (targetPos != null) {
                    world.setBlockToAir(targetPos.toBlockPos());
                    data.addStatusControl(StatusControlController.CHARGE_WATER);
                } else {
                    bender.sendMessage("avatar.waterSourceFail");
                }
            }
        }
    }

    @Override
    public int getBaseTier() {
        return 3;
    }

    @Override
    public boolean isChargeable() {
        return true;
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    @Override
    public boolean isOffensive() {
        return true;
    }
}

