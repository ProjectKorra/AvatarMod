package com.crowsofwar.avatar.common.capabilities;

import com.crowsofwar.avatar.api.capabilities.IPlayerShoulders;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PlayerShoulderProvider implements ICapabilityProvider {

	private IPlayerShoulders instance = CapabilityPlayerShoulders.TEST_HANDLER.getDefaultInstance();
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityPlayerShoulders.TEST_HANDLER;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		 return capability == CapabilityPlayerShoulders.TEST_HANDLER ? CapabilityPlayerShoulders.TEST_HANDLER.<T> cast(this.instance) : null;
	}

}
