package com.crowsofwar.avatar.client.render.lightning.main;

import com.crowsofwar.avatar.client.render.lightning.math.Vec3;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.lwjgl.input.Keyboard;

import java.util.Random;

public class ModEventHandler {

    public static Random rand = new Random();
	int c = 0; // Cali's brute force for lightning

//    @SubscribeEvent
//    public void soundRegistering(RegistryEvent.Register<SoundEvent> evt) {
//
//        for(SoundEvent e : HBMSoundHandler.ALL_SOUNDS) {
//            evt.getRegistry().register(e);
//        }
//    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;

        if(!player.world.isRemote && event.phase == TickEvent.Phase.START) {
			NBTTagCompound perDat = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
//			int lightning = perDat.getInteger("lightningCharge");
			if(Keyboard.isKeyDown(Keyboard.KEY_I))
				c = 1;
			int lightning = c;
			if(lightning > 0){
				lightning++; c++;
				if(lightning == 60){
					RayTraceResult r = Library.rayTraceIncludeEntities(player, 100, 1);
					if(r != null && r.typeOfHit != RayTraceResult.Type.MISS){
						NBTTagCompound tag = new NBTTagCompound();
						tag.setString("type", "lightning");
						tag.setString("mode", "beam");
						tag.setDouble("hitX", r.hitVec.x);
						tag.setDouble("hitY", r.hitVec.y);
						tag.setDouble("hitZ", r.hitVec.z);
						Vec3d normal = new Vec3d(r.sideHit.getXOffset(), r.sideHit.getYOffset(), r.sideHit.getZOffset());
						tag.setDouble("normX", normal.x);
						tag.setDouble("normY", normal.y);
						tag.setDouble("normZ", normal.z);
						if(r.typeOfHit == RayTraceResult.Type.ENTITY){
							r.entityHit.attackEntityFrom(ModDamageSource.electricity, 20);
							if(r.entityHit instanceof EntityLiving && ((EntityLiving)r.entityHit).getHealth() <= 0){
								r.entityHit.setDead();
								PacketDispatcher.wrapper.sendToAllTracking(new PacketSpecialDeath(r.entityHit, 2, (float)player.getLookVec().x, (float)player.getLookVec().y, (float)player.getLookVec().z), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), r.entityHit.posX, r.entityHit.posY, r.entityHit.posZ, 0));
							}
							tag.setInteger("hitType", 1);
						} else if(r.typeOfHit == RayTraceResult.Type.BLOCK){
							tag.setInteger("hitType", 0);
						}

						Vec3d direction = player.getLookVec().scale(0.75);
						switch(r.sideHit.getAxis()){
						case X:
							direction = new Vec3d(-direction.x, direction.y, direction.z);
							break;
						case Y:
							direction = new Vec3d(direction.x, -direction.y, direction.z);
							break;
						case Z:
							direction = new Vec3d(direction.x, direction.y, -direction.z);
							break;
						}

						NBTTagCompound tag2 = new NBTTagCompound();
						tag2.setString("type", "spark");
						tag2.setString("mode", "coneBurst");
						tag2.setDouble("posX", r.hitVec.x);
						tag2.setDouble("posY", r.hitVec.y);
						tag2.setDouble("posZ", r.hitVec.z);
						tag2.setDouble("dirX", direction.x);
						tag2.setDouble("dirY", direction.y);
						tag2.setDouble("dirZ", direction.z);
						tag2.setFloat("r", 0.4F);
						tag2.setFloat("g", 0.8F);
						tag2.setFloat("b", 0.9F);
						tag2.setFloat("a", 2F);
						tag2.setInteger("lifetime", 5);
						tag2.setInteger("randLifetime", 20);
						tag2.setFloat("width", 0.04F);
						tag2.setFloat("length", 0.7F);
						tag2.setFloat("randLength", 1.5F);
						tag2.setFloat("gravity", 0.1F);
						tag2.setFloat("angle", 80F);
						tag2.setInteger("count", 60+player.world.rand.nextInt(20));
						tag2.setFloat("randomVelocity", 0.4F);
						PacketDispatcher.wrapper.sendToAllTracking(new AuxParticlePacketNT(tag2, r.hitVec.x, r.hitVec.y, r.hitVec.z), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 0));
						Vec3d ssgChainPos = new Vec3d(-0.18, -0.1, 0.35);
						ssgChainPos = ssgChainPos.rotatePitch((float) Math.toRadians(-player.rotationPitch));
						ssgChainPos = ssgChainPos.rotateYaw((float) Math.toRadians(-player.rotationYaw));
						ssgChainPos = ssgChainPos.add(player.posX, player.posY + player.getEyeHeight(), player.posZ);
						PacketDispatcher.wrapper.sendToAllTracking(new AuxParticlePacketNT(tag, ssgChainPos.x, ssgChainPos.y, ssgChainPos.z), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 0));
					} else {
						NBTTagCompound tag = new NBTTagCompound();
						tag.setString("type", "lightning");
						tag.setString("mode", "beam");
						Vec3d hit = player.getPositionEyes(1).add(player.getLookVec().scale(100));
						tag.setDouble("hitX", hit.x);
						tag.setDouble("hitY", hit.y);
						tag.setDouble("hitZ", hit.z);
						tag.setInteger("hitType", -1);

						Vec3d ssgChainPos = new Vec3d(-0.18, -0.1, 0.35);
						ssgChainPos = ssgChainPos.rotatePitch((float) Math.toRadians(-player.rotationPitch));
						ssgChainPos = ssgChainPos.rotateYaw((float) Math.toRadians(-player.rotationYaw));
						ssgChainPos = ssgChainPos.add(player.posX, player.posY + player.getEyeHeight(), player.posZ);

						PacketDispatcher.wrapper.sendToAllTracking(new AuxParticlePacketNT(tag, ssgChainPos.x, ssgChainPos.y, ssgChainPos.z), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 0));
					}
				}
				if(lightning == 84){
					lightning = 0; c = 0;
				}
			}
			perDat.setInteger("lightningCharge", lightning);
        }


        if(player.world.isRemote && event.phase == Phase.START && !player.isInvisible() && !player.isSneaking()) {

            if(player.getUniqueID().toString().equals(Library.HbMinecraft)) {

                int i = player.ticksExisted * 3;

                Vec3 vec = Vec3.createVectorHelper(3, 0, 0);

                vec.rotateAroundY((float) (i * Math.PI / 180D));
                for(int k = 0; k < 5; k++) {

                    vec.rotateAroundY((float) (1F * Math.PI / 180D));
                    player.world.spawnParticle(EnumParticleTypes.TOWN_AURA, player.posX + vec.xCoord, player.posY + 1 + player.world.rand.nextDouble() * 0.05, player.posZ + vec.zCoord, 0.0, 0.0, 0.0);
                }
            }
        }
    }
}