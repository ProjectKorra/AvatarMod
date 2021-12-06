package com.crowsofwar.avatar.client.render.lightning.main;

import com.crowsofwar.avatar.client.render.lightning.math.Vec3;
import com.crowsofwar.avatar.client.render.lightning.particle.DisintegrationParticleHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Method;

public class PacketSpecialDeath implements IMessage {

	public static Method rGetHurtSound;
	
	Entity serverEntity;
	int entId;
	int effectId;
	float[] auxData;
	Object auxObj;
	
	public PacketSpecialDeath() {
	}
	
	public PacketSpecialDeath(Entity ent, int effectId, float... auxData) {
		serverEntity = ent;
		this.effectId = effectId;
		this.entId = ent.getEntityId();
		this.auxData = auxData;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		entId = buf.readInt();
		effectId = buf.readInt();
		int len = buf.readByte();
		auxData = new float[len];
		for(int i = 0; i < len; i++){
			auxData[i] = buf.readFloat();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entId);
		buf.writeInt(effectId);
		buf.writeByte(auxData.length);
		for(float f : auxData){
			buf.writeFloat(f);
		}
	}

	public static class Handler implements IMessageHandler<PacketSpecialDeath, IMessage> {

		@SuppressWarnings("deprecation")
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketSpecialDeath m, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				Entity ent = Minecraft.getMinecraft().world.getEntityByID(m.entId);
				if(ent instanceof EntityLivingBase){
					ent.setDead();
					ModEventHandlerClient.specialDeathEffectEntities.add((EntityLivingBase) ent);
					DisintegrationParticleHandler.spawnLightningDisintegrateParticles(ent, new Vec3(m.auxData[0], m.auxData[1], m.auxData[2]));
				}
			});
			return null;
		}

	}
	
	//Epic games lighting model falloff
	public static float pointLightFalloff(float radius, float dist){
		float distOverRad = dist/radius;
		float distOverRad2 = distOverRad*distOverRad;
		float distOverRad4 = distOverRad2*distOverRad2;
		
		float falloff = MathHelper.clamp(1-distOverRad4, 0, 1);
		return (falloff * falloff)/(dist*dist + 1);
	}
	
}