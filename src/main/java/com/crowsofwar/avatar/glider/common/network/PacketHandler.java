package com.crowsofwar.avatar.glider.common.network;

import com.crowsofwar.avatar.glider.common.lib.ModInfo;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper HANDLER = new SimpleNetworkWrapper(ModInfo.NETWORK_CHANNEL);

    public static void init() {
        int id = 0;
        HANDLER.registerMessage(PacketClientGliding.Handler.class, PacketClientGliding.class, id++, Side.CLIENT);
        HANDLER.registerMessage(PacketUpdateClientTarget.Handler.class, PacketUpdateClientTarget.class, id++, Side.CLIENT);
        HANDLER.registerMessage(PacketUpdateGliderDamage.Handler.class, PacketUpdateGliderDamage.class, id++, Side.CLIENT);
        HANDLER.registerMessage(PacketSyncGliderDataToClient.Handler.class, PacketSyncGliderDataToClient.class, id++, Side.CLIENT);
        HANDLER.registerMessage(PacketServerGliding.Handler.class, PacketServerGliding.class, id, Side.SERVER); //Unused
    }

}

