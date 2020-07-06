package com.crowsofwar.avatar.common.network.packets;

import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.config.AbilityProperties;
import com.crowsofwar.avatar.common.network.PacketRedirector;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

public class PacketCSyncAbilityProperties extends AvatarPacket<PacketCSyncAbilityProperties> {

    AbilityProperties[] properties;

    public PacketCSyncAbilityProperties() {
    }

    public PacketCSyncAbilityProperties(AbilityProperties... properties) {
        this.properties = properties;
    }

    @Override
    protected void avatarFromBytes(ByteBuf buf) {
        List<AbilityProperties> propertiesList = new ArrayList<>();
        int i = 0;

        while (buf.isReadable()) {
            propertiesList.add(new AbilityProperties(Abilities.all().get(i), buf));
        }

        properties = propertiesList.toArray(new AbilityProperties[0]);
    }

    @Override
    protected void avatarToBytes(ByteBuf buf) {
        for (AbilityProperties properties : properties)
            properties.write(buf);
    }

    @Override
    protected Side getReceivedSide() {
        return Side.CLIENT;
    }

    @Override
    protected Handler<PacketCSyncAbilityProperties> getPacketHandler() {
        return PacketRedirector::redirectMessage;
    }
}
