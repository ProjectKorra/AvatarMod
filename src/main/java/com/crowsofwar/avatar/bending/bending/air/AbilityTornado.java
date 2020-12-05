package com.crowsofwar.avatar.bending.bending.air;

import com.crowsofwar.avatar.bending.bending.Ability;

import java.util.UUID;

public class AbilityTornado extends Ability {

    //Ok functionality time, nerds.
    //3 uses: Shoot, Summon, Shield. You can charge it before shooting it. Tornado go voom.

    /**
     * NOTE: DO NOT CREATE A NEW INSTANCE OF AN ABILITY FOR GETTING PROPERTIES, IT'LL JUST RETURN NULL.
     * INSTEAD, call {@code Abilities.get(String name)} and use that.
     *
     * @param bendingType
     * @param name
     */
    public AbilityTornado(UUID bendingType, String name) {
        super(bendingType, name);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public boolean isChargeable() {
        return true;
    }

    @Override
    public boolean isUtility() {
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
