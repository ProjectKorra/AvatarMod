package com.crowsofwar.avatar.glider.common.registry;

import com.crowsofwar.avatar.glider.common.capabilities.GliderCapabilityImplementation;

public class CapabilityRegistry {

    public static void registerAllCapabilities(){
        GliderCapabilityImplementation.init();
    }

}
