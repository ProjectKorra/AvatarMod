package com.crowsofwar.avatar.common.capabilities;

import com.crowsofwar.avatar.api.capabilities.IPlayerShoulders;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityPlayerShoulders {
	
	 @CapabilityInject(IPlayerShoulders.class)
	    public static Capability<IPlayerShoulders> TEST_HANDLER = null;

	    public static void register()
	    {
	        CapabilityManager.INSTANCE.register(IPlayerShoulders.class, new Capability.IStorage<IPlayerShoulders>()
	        {

	        	@Override
				public NBTBase writeNBT(Capability<IPlayerShoulders> capability, IPlayerShoulders instance, EnumFacing side) {
					return null;
				}
	        	
	            @Override
	            public void readNBT(Capability<IPlayerShoulders> capability, IPlayerShoulders instance, EnumFacing side, NBTBase base)
	            {

	            }

	        }, PlayerShouldersHandler::new);
	    }
}
