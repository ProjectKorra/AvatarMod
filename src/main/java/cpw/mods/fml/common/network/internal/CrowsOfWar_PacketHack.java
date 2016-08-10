package cpw.mods.fml.common.network.internal;

import java.util.List;

import cpw.mods.fml.common.network.internal.FMLMessage.EntityMessage;
import cpw.mods.fml.common.network.internal.FMLMessage.EntitySpawnMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

/**
 * Allows access to fields in FMLMessage class which
 * have 'default' access modifiers.
 * 
 * @author CrowsOfWar
 */
public class CrowsOfWar_PacketHack {
	
	public static int getEntityId(EntitySpawnMessage msg) {
		return msg.entityId;
	}
	
	public static Entity getEntity(EntitySpawnMessage msg) {
		return msg.entity;
	}
	
	public static double getScaledX(EntitySpawnMessage msg) {
		return msg.scaledX;
	}
	
	public static double getScaledY(EntitySpawnMessage msg) {
		return msg.scaledY;
	}
	
	public static double getScaledZ(EntitySpawnMessage msg) {
		return msg.scaledZ;
	}
	
	public static float getScaledYaw(EntitySpawnMessage msg) {
		return msg.scaledYaw;
	}
	
	public static float getScaledPitch(EntitySpawnMessage msg) {
		return msg.scaledPitch;
	}
	
	public static float getScaledHeadYaw(EntitySpawnMessage msg) {
		return msg.scaledHeadYaw;
	}
	
	public static int getRawX(EntitySpawnMessage msg) {
		return msg.rawX;
	}
	
	public static int getRawY(EntitySpawnMessage msg) {
		return msg.rawY;
	}
	
	public static int getRawZ(EntitySpawnMessage msg) {
		return msg.rawZ;
	}
	
	public static List<?> getDatawatcherList(EntitySpawnMessage msg) {
		return msg.dataWatcherList;
	}
	
	public static int getThrowerId(EntitySpawnMessage msg) {
		return msg.throwerId;
	}
	
	public static double getSpeedScaledX(EntitySpawnMessage msg) {
		return msg.speedScaledX;
	}
	
	public static double getSpeedScaledY(EntitySpawnMessage msg) {
		return msg.speedScaledY;
	}
	
	public static double getSpeedScaledZ(EntitySpawnMessage msg) {
		return msg.speedScaledZ;
	}
	
	public static ByteBuf getDataStream(EntitySpawnMessage msg) {
		return msg.dataStream;
	}
	
}
