package com.crowsofwar.avatar.registry;

import com.crowsofwar.avatar.capabilities.GliderCapabilityImplementation;

public class CapabilityRegistry {

    public static void registerAllCapabilities(){
        GliderCapabilityImplementation.init();
    }

}
