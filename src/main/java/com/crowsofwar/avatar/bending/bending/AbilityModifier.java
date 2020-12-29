package com.crowsofwar.avatar.bending.bending;

import jdk.nashorn.internal.objects.NativeUint8Array;

import java.util.HashMap;
import java.util.UUID;

/**
 * Serves as a wrapper class for modifying abilities. Allows you to alter the cooldown,
 * burnout, exhaustion, e.t.c of an ability through its use event. Also useful for
 * mobs.
 */
public class AbilityModifier {

    //I should probably support modifying booleans in the future

    private HashMap<String, Number> properties;
    //UUID's to distinguish modifies; e.g the staff modifier
    private UUID id = UUID.fromString("55c88686-6fc1-4cf5-8a31-887702fb2d5e");

    public AbilityModifier() {
        properties = new HashMap<>();
    }

    public AbilityModifier(HashMap<String, Number> properties) {
        this.properties = properties;
    }

    public AbilityModifier(UUID id) {
        this.id = id;
    }

    public AbilityModifier(HashMap<String, Number> properties, UUID id) {
        this.properties = properties;
        this.id = id;
    }


    public void addProperties(HashMap<String, Number> properties) {
        this.properties.putAll(properties);
    }

    public void removeProperty(String property) {
        this.properties.remove(property);
    }

    public boolean hasProperty(String property) {
        return this.properties.containsKey(property);
    }

    public Number getProperty(String property) {
        return this.properties.getOrDefault(property, 1);
    }

    public UUID getID() {
        return this.id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

}
