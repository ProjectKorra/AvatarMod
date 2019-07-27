package com.crowsofwar.avatar.common.registry;

import com.crowsofwar.avatar.common.capabilities.GliderCapabilityImplementation;

public class CapabilityRegistry {

    public static void registerAllCapabilities(){
        GliderCapabilityImplementation.init();
    }

}
