package com.crowsofwar.avatar.bending.bending.earth;

import com.crowsofwar.avatar.bending.bending.Ability;

import java.util.UUID;

//Dopest ability to exist
//Charge up, then explode things
//Dash, shockwave, punch
public class AbilityTectonicBlitz extends Ability {

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     *
     * @param bendingType
     * @param name
     */
    public AbilityTectonicBlitz(UUID bendingType, String name) {
        super(bendingType, name);
    }

    @Override
    public void init() {
        super.init();
        addProperties(JUMP_HEIGHT, EFFECT_RADIUS, FALL_ABSORPTION, DURATION);

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
    public boolean isUtility() {
        return true;
    }

    @Override
    public boolean isOffensive() {
        return true;
    }
}
