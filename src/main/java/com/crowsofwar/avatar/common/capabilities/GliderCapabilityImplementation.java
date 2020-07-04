package com.crowsofwar.avatar.common.capabilities;

import com.crowsofwar.avatar.AvatarLog;
import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.api.capabilities.IAdvancedGliderCapabilityHandler;
import com.crowsofwar.avatar.common.network.packets.glider.PacketCSyncGliderDataToClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import static com.crowsofwar.avatar.AvatarInfo.MOD_ID;
import static com.crowsofwar.avatar.api.capabilities.CapabilityHelper.GLIDER_CAPABILITY;

public final class GliderCapabilityImplementation {

    //make it un-constructable, as it is just a container for the other nested classes and static methods
    private GliderCapabilityImplementation() {
    }

    public static void init() {
        CapabilityManager.INSTANCE.register(IAdvancedGliderCapabilityHandler.class, new Capability.IStorage<IAdvancedGliderCapabilityHandler>() {

            @Override
            public NBTBase writeNBT(Capability<IAdvancedGliderCapabilityHandler> capability, IAdvancedGliderCapabilityHandler instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IAdvancedGliderCapabilityHandler> capability, IAdvancedGliderCapabilityHandler instance, EnumFacing side, NBTBase nbt) {
                if (nbt instanceof NBTTagCompound)
                    instance.deserializeNBT(((NBTTagCompound) nbt));
            }
        }, DefaultGliderCapImplementation.class);
    }

    public static class DefaultGliderCapImplementation implements IAdvancedGliderCapabilityHandler {

        private static final String CAP_PLAYER_GLIDING = MOD_ID + ".isPlayerGliding";
        private static final String CAP_GLIDER_DEPLOYED = MOD_ID + ".isGliderDeployed";
        private static final String CAP_GLIDER_USED = MOD_ID + ".gliderUsed";

        //Glider data
        private boolean isPlayerGliding;
        private boolean isGliderDeployed;

        public DefaultGliderCapImplementation() {
            this.isPlayerGliding = false;
            this.isGliderDeployed = false;
        }

        @Override
        public boolean getIsPlayerGliding() {
            return isGliderDeployed && isPlayerGliding;
        }

        //Serializing and Deserializing NBT

        @Override
        public void setIsPlayerGliding(boolean isGliding) {
            if (!isGliderDeployed && isGliding)
                AvatarLog.error("Can't set a player to be gliding if they don't have a deployed glider!");
            else
                isPlayerGliding = isGliding;
        }

        @Override
        public boolean getIsGliderDeployed() {
            return isGliderDeployed;
        }

        @Override
        public void setIsGliderDeployed(boolean isDeployed) {
            if (isPlayerGliding && isDeployed)
                AvatarLog.error("Player is already flying, deploying now is not needed.");
            else
                isGliderDeployed = isDeployed;
            if (!isDeployed) isPlayerGliding = false; //if not deployed, cannot be flying either
        }

        @Override
        public NBTTagCompound serializeNBT() {

            //save the boolean
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean(CAP_PLAYER_GLIDING, isPlayerGliding);
            compound.setBoolean(CAP_GLIDER_DEPLOYED, isGliderDeployed);

            //return compound
            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound compound) {

            //load and set
            setIsPlayerGliding(compound.getBoolean(CAP_PLAYER_GLIDING));
            setIsGliderDeployed(compound.getBoolean(CAP_GLIDER_DEPLOYED));

        }

        //Sync

        @Override
        public void sync(EntityPlayerMP player) {
            AvatarMod.network.sendTo(new PacketCSyncGliderDataToClient(serializeNBT()), player);
        }

    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

        public static final ResourceLocation NAME = new ResourceLocation(MOD_ID, "cap");

        private final IAdvancedGliderCapabilityHandler capabilityImplementation = new DefaultGliderCapImplementation();

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == GLIDER_CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == GLIDER_CAPABILITY) {
                return GLIDER_CAPABILITY.cast(capabilityImplementation);
            }
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return capabilityImplementation.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            capabilityImplementation.deserializeNBT(nbt);
        }
    }

}
