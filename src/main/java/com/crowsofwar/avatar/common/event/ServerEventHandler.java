package com.crowsofwar.avatar.common.event;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.api.capabilities.CapabilityHelper;
import com.crowsofwar.avatar.api.capabilities.IPlayerShoulders;
import com.crowsofwar.avatar.api.helper.GliderHelper;
import com.crowsofwar.avatar.common.capabilities.CapabilityPlayerShoulders;
import com.crowsofwar.avatar.common.capabilities.GliderCapabilityImplementation;
import com.crowsofwar.avatar.common.entity.EntityAscendedFlyingLemur;
import com.crowsofwar.avatar.common.entity.EntityFlyingLemur;
import com.crowsofwar.avatar.common.helper.GliderPlayerHelper;
import com.crowsofwar.avatar.common.network.packets.glider.PacketCUpdateClientTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ServerEventHandler {

    /**
     * Initialize the cap to the player.
     *
     * @param event - attach cap event
     */
    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            if (!CapabilityHelper.hasGliderCapability((EntityPlayer) event.getObject())) {
                event.addCapability(GliderCapabilityImplementation.Provider.NAME, new GliderCapabilityImplementation.Provider());
            }
        }
    }

    /**
     * Deal with end movement and copying capability data over.
     *
     * @param event - the player being cloned (teleported in vanilla code)
     */
    @SubscribeEvent
    public void onPlayerCloning(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
//        if (!event.isWasDeath()) { //return from end (deal with dumb returning from the end code) //ToDo: Test without
        if (CapabilityHelper.hasGliderCapability(event.getOriginal())) {
            NBTTagCompound gliderData = CapabilityHelper.getGliderCapability(event.getOriginal()).serializeNBT();
            CapabilityHelper.getGliderCapability(event.getEntityPlayer()).deserializeNBT(gliderData);
        }
//        }
    }

    /**
     * Update the position of the player when flying.
     *
     * @param event - tick event
     */
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (GliderHelper.getIsGliderDeployed(event.player)) {
            GliderPlayerHelper.updatePosition(event.player);
        }
    }

    /**
     * Sync capability of a tracked player for visual person flying updates.
     *
     * @param event - the tracking event
     */
    @SubscribeEvent
    public void onTrack(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        EntityPlayer tracker = event.getEntityPlayer(); //the tracker
        Entity targetEntity = event.getTarget(); //the target that is being tracked
        if (targetEntity instanceof EntityPlayerMP) { //only entityPlayerMP ( MP part is very important!)
            EntityPlayer targetPlayer = (EntityPlayer) targetEntity; //typecast to entityPlayer
            if (CapabilityHelper.hasGliderCapability(targetPlayer)) { //if have the capability
                if (GliderHelper.getIsGliderDeployed(targetPlayer)) { //if the target has capability need to update
                    AvatarMod.network.sendTo(new PacketCUpdateClientTarget(targetPlayer, true), (EntityPlayerMP) targetPlayer);
                } else {
                    AvatarMod.network.sendTo(new PacketCUpdateClientTarget(targetPlayer, false), (EntityPlayerMP) targetPlayer);
                }
            }
        }
    }

    //===========================================================Simple Sync Capability EVents==============================================

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        syncGlidingCapability(event.player);
    }

    @SubscribeEvent
    public void onPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncGlidingCapability(event.player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        syncGlidingCapability(event.player);
    }

    /**
     * Sends a message to the client to update the status of the glider to whatever it is on the server.
     *
     * @param player - the player to sync the data for
     */
    private void syncGlidingCapability(EntityPlayer player) {
        CapabilityHelper.getGliderCapability(player).sync((EntityPlayerMP) player);
    }


    //===========================================================Lemur Events==============================================

    @SubscribeEvent
    void onOwnerTeleport(PlayerEvent.PlayerChangedDimensionEvent event)
    {

        IPlayerShoulders playerShoulders = event.player.getCapability(CapabilityPlayerShoulders.TEST_HANDLER, null);

        if(playerShoulders.getRiders().size() > 0) {

            for(Entity passeger : playerShoulders.getRiders())
            {
                if(passeger instanceof EntityFlyingLemur) {
                    EntityFlyingLemur lemur = (EntityFlyingLemur) passeger;
                    playerShoulders.removeRiders(lemur);
                    lemur.setRightShoulder(false);
                    lemur.setLeftShoulder(false);
                    lemur.setSitting(true);

                }
                else if(passeger instanceof EntityAscendedFlyingLemur) {
                    EntityAscendedFlyingLemur lemur = (EntityAscendedFlyingLemur) passeger;
                    playerShoulders.removeRiders(lemur);
                    lemur.setRightShoulder(false);
                    lemur.setLeftShoulder(false);
                    lemur.setSitting(true);
                }
            }
        }

    }

    @SubscribeEvent
    void onLemurRideExit(PlayerInteractEvent.RightClickBlock event)
    {

        IPlayerShoulders playerShoulders = event.getEntityPlayer().getCapability(CapabilityPlayerShoulders.TEST_HANDLER, null);

        if(playerShoulders.getRiders().size() > 0 && event.getEntityPlayer().isSneaking())
        {
            BlockPos pos = new BlockPos(event.getPos().getX(), event.getPos().getY()+1f,event.getPos().getZ());

            if(event.getWorld().getBlockState(pos).getBlock() == Blocks.AIR)
            {
                for(Entity passeger : playerShoulders.getRiders())
                {


                    if(passeger instanceof EntityFlyingLemur) {
                        EntityFlyingLemur lemur = (EntityFlyingLemur) passeger;
                        playerShoulders.removeRiders(lemur);
                        lemur.setRightShoulder(false);
                        lemur.setLeftShoulder(false);
                        lemur.setSitting(true);
                        lemur.setPosition(event.getPos().getX() +0.5f, event.getPos().getY()+1.0F, event.getPos().getZ() +0.5f);
                    }
                    else if(passeger instanceof EntityAscendedFlyingLemur) {
                        EntityAscendedFlyingLemur lemur = (EntityAscendedFlyingLemur) passeger;
                        playerShoulders.removeRiders(lemur);
                        lemur.setRightShoulder(false);
                        lemur.setLeftShoulder(false);
                        lemur.setSitting(true);
                        lemur.setPosition(event.getPos().getX() +0.5f, event.getPos().getY()+1.0F, event.getPos().getZ() +0.5f);
                    }



                }

            }

        }


    }


    @SubscribeEvent
    void onLogOutLemurExit(PlayerEvent.PlayerLoggedOutEvent event) {
        IPlayerShoulders playerShoulders = event.player.getCapability(CapabilityPlayerShoulders.TEST_HANDLER, null);

        if(playerShoulders.getRiders().size() > 0)
        {
            for(Entity passeger : playerShoulders.getRiders())
            {
                if(passeger instanceof EntityFlyingLemur) {
                    EntityFlyingLemur lemur = (EntityFlyingLemur) passeger;
                    lemur.height = 1f;
                    lemur.width = 0.3f;
                    lemur.setRightShoulder(false);
                    lemur.setLeftShoulder(false);
                }
                else if(passeger instanceof EntityAscendedFlyingLemur) {
                    EntityAscendedFlyingLemur lemur = (EntityAscendedFlyingLemur) passeger;;
                    lemur.height = 1f;
                    lemur.width = 0.3f;
                    lemur.setRightShoulder(false);
                    lemur.setLeftShoulder(false);
                }
            }

        }
    }
}
