package com.crowsofwar.avatar.network.packets;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.Ability;
import com.crowsofwar.avatar.network.PacketRedirector;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.gorecore.util.GoreCoreByteBufUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

//Client equivalent of the network packet. Ensures all players witness abilities executed twice.
public class PacketCUseAbility extends AvatarPacket<PacketCUseAbility> {

    private Ability ability;
    private Raytrace.Result raytrace;
    private boolean switchPath;
    //The original source of the ability.
    private UUID bender;

    public PacketCUseAbility() {
    }

    public PacketCUseAbility(Ability ability, Raytrace.Result raytrace, boolean switchPath, UUID bender) {
        this.ability = ability;
        this.raytrace = raytrace;
        this.switchPath = switchPath;
        this.bender = bender;
    }

    @Override
    public void avatarFromBytes(ByteBuf buf) {
        ability = Abilities.get(GoreCoreByteBufUtil.readString(buf));
        if (ability == null) {
            throw new NullPointerException("Server sent invalid ability over client: ID " + ability);
        }
        raytrace = Raytrace.Result.fromBytes(buf);
        switchPath = buf.readBoolean();
        bender = GoreCoreByteBufUtil.readUUID(buf);
    }

    @Override
    public void avatarToBytes(ByteBuf buf) {
        GoreCoreByteBufUtil.writeString(buf, ability.getName());
        raytrace.toBytes(buf);
        buf.writeBoolean(switchPath);
        GoreCoreByteBufUtil.writeUUID(buf, bender);
    }

    @Override
    public Side getReceivedSide() {
        return Side.CLIENT;
    }

    public Ability getAbility() {
        return ability;
    }

    public boolean getSwitchpath(){
        return switchPath;
    }

    public Raytrace.Result getRaytrace() {
        return raytrace;
    }

    public UUID getBender() {
        return bender;
    }

    @Override
    protected AvatarPacket.Handler<PacketCUseAbility> getPacketHandler() {
        return PacketRedirector::redirectMessage;
    }

}
