package com.crowsofwar.avatar.bending.bending.lightning;

import com.crowsofwar.avatar.client.particle.AvatarParticles;
import com.crowsofwar.avatar.client.render.lightning.main.*;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.avatar.entity.EntityLightningArc;
import com.crowsofwar.avatar.client.particle.NetworkParticleSpawner;
import com.crowsofwar.avatar.client.particle.ParticleSpawner;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Lightning benders or redirectors need to "charge" the lightning for a short time before
 * releasing it. This is a generic handler for lightning charging, which can either be used for
 * {@link LightningCreateHandler regular lightningbending} or
 * {@link LightningRedirectHandler lightning redirection}.
 *
 * @author CrowsOfWar
 */
public abstract class LightningChargeHandler extends TickHandler {
	private static final UUID MOVEMENT_MODIFIER_ID = UUID.fromString("dfb6235c-82b6-407e-beaf-a48045735a82");
	private ParticleSpawner particleSpawner;

	LightningChargeHandler(int id) {
		super(id);
		this.particleSpawner = new NetworkParticleSpawner();
	}

	/**
	 * Gets AbilityData to be used for determining lightning strength. This is normally the
	 * bender's AbilityData, but in the case of redirection, it is the original bender's
	 * AbilityData.
	 */
	@Nullable
	protected abstract AbilityData getLightningData(BendingContext ctx);

	@Override
	public boolean tick(BendingContext ctx) {

		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		double powerRating = ctx.getBender().calcPowerRating(Lightningbending.ID);

		if (world.isRemote) {
			return false;
		}

		int duration = data.getTickHandlerDuration(this);

		EntityPlayer player = (EntityPlayer) entity;

		if(!player.world.isRemote) {
			NBTTagCompound perDat = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			int lightning = perDat.getInteger("lightningCharge");
			if(lightning > 0){
				lightning ++;
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
					lightning = 0;
				}
			}
			perDat.setInteger("lightningCharge", lightning);
			return true;
		}

//		float movementMultiplier = 0.6f - 0.7f * MathHelper.sqrt(duration / 40f);
//		applyMovementModifier(entity, MathHelper.clamp(movementMultiplier, 0.1f, 1));
//		double inverseRadius = (40F - duration) / 10;

//		if (duration % 3 == 0) {
//			for (int i = 0; i < 8; i++) {
//				Vector lookpos = Vector.toRectangular(Math.toRadians(entity.rotationYaw +
//						i * 45), 0).times(inverseRadius).withY(entity.getEyeHeight() / 2);
//				particleSpawner.spawnParticles(world, AvatarParticles.getParticleElectricity(), 1, 2, lookpos.x() + entity.posX,
//						lookpos.y() + entity.getEntityBoundingBox().minY, lookpos.z() + entity.posZ, 2, 1.2, 2, true);
//			}
//		}

//		if (duration >= 40) {
//
//			AbilityData abilityData = getLightningData(ctx);
//			if (abilityData == null) {
//				return true;
//			}
//
//			double speed = abilityData.getLevel() >= 1 ? 30 : 40;
//			float damage = abilityData.getLevel() >= 2 ? 8 : 6;
//			float size = 1;
//			float[] turbulenceValues = { 0.6f, 1.2f };
//
//			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
//				damage = 12;
//				size = 0.75f;
//				turbulenceValues = new float[] { 0.6f, 1.2f, 0.8f };
//			}
//			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
//				size = 1.5f;
//			}
//
//			speed += powerRating / 15;
//			damage *= ctx.getBender().getDamageMult(Lightningbending.ID);
//
//			fireLightning(world, entity, damage, speed, size, turbulenceValues);
//
//			entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_MODIFIER_ID);
//
//			world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1, 2);
//
//			return true;
//
//		}

		return false;

	}

	private void fireLightning(World world, EntityLivingBase entity, float damage, double speed, float size, float[] turbulenceValues) {

		for (float turbulence : turbulenceValues) {

			EntityLightningArc lightning = new EntityLightningArc(world);
			lightning.setOwner(entity);
			lightning.setTurbulence(turbulence);
			lightning.setDamage(damage);
			lightning.setSizeMultiplier(size);
			lightning.setAbility(new AbilityLightningArc());
			lightning.setMainArc(turbulence == turbulenceValues[0]);

			lightning.setPosition(Vector.getEyePos(entity));
			lightning.setEndPos(Vector.getEyePos(entity));

			Vector velocity = Vector.getLookRectangular(entity);
			velocity = velocity.normalize().times(speed);
			lightning.setVelocity(velocity);

			world.spawnEntity(lightning);

		}

	}

	private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

		IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

		moveSpeed.removeModifier(MOVEMENT_MODIFIER_ID);

		moveSpeed.applyModifier(new AttributeModifier(MOVEMENT_MODIFIER_ID, "Lightning charge modifier", multiplier - 1, 1));

	}

}
