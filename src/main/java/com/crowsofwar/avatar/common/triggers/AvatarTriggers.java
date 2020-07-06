package com.crowsofwar.avatar.common.triggers;

public class AvatarTriggers {
    public static final CustomTrigger UNLOCK_AN_ELEMENT = new CustomTrigger("unlock_an_element");

    /*
     * This array just makes it convenient to register all the criteria.
     */
    public static final CustomTrigger[] TRIGGER_ARRAY = new CustomTrigger[] {
            UNLOCK_AN_ELEMENT
    };
}
