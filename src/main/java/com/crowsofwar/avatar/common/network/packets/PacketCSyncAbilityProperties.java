package com.crowsofwar.avatar.common.network.packets;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.common.bending.Abilities;
import com.crowsofwar.avatar.common.config.AbilityProperties;
import com.crowsofwar.avatar.common.network.PacketRedirector;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PacketCSyncAbilityProperties extends AvatarPacket<PacketCSyncAbilityProperties> {

    public AbilityProperties[] properties;

    public PacketCSyncAbilityProperties() {
    }

    public PacketCSyncAbilityProperties(AbilityProperties... properties) {
        this.properties = properties;
    }

    @Override
    protected void avatarFromBytes(ByteBuf buf) {
        List<AbilityProperties> propertiesList = new ArrayList<>();
        int i = 0;

        while (buf.isReadable() && i < Abilities.all().size()) {
            propertiesList.add(Abilities.all().stream().map(a -> a.properties).collect(Collectors.toList()).get(i++));
        }

        properties = propertiesList.toArray(new AbilityProperties[0]);
    }

    @Override
    protected void avatarToBytes(ByteBuf buf) {
        for (AbilityProperties properties : properties)
            if (properties != null)
                properties.write(buf);
            else AvatarLog.warn(AvatarLog.WarningType.CONFIGURATION, "Properties file is null! Whack!");
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
