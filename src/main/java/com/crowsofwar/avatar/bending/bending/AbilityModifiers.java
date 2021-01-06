package com.crowsofwar.avatar.bending.bending;

import java.util.UUID;

//Class that contains independent static modifiers and modifier ids
public class AbilityModifiers {

    //IDs are for modifiers that change based on the entity, but are still unique to that entity.
    //For example, bison have a unique modifier that scales as they grow.
    public static final UUID
            STAFF_ID = UUID.randomUUID(),
            BISON_ID = UUID.randomUUID(),
            AIRBENDER_ID = UUID.randomUUID(),
            FIREBENDER_ID = UUID.randomUUID(),
            CONFIG_ID = UUID.randomUUID();

    public static AbilityModifier
            STAFF_BASE_MODIFIER = new AbilityModifier(STAFF_ID),
            STAFF_MASTER_MODIFIER = new AbilityModifier(STAFF_ID),
            BISON_MODIFIER = new AbilityModifier(BISON_ID),
            AIRBENDER_MODIFIER = new AbilityModifier(AIRBENDER_ID),
            FIREBENDER_MODIFIER = new AbilityModifier(FIREBENDER_ID),
            CONFIG_MODIFIER = new AbilityModifier(CONFIG_ID);

    //Just reference the class to set stuf fup
    public static void init() {
    }


}
