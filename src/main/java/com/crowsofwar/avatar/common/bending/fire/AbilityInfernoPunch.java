package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityLightOrb;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.LightOrbBehavior;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.bending.StatusControl.*;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.TickHandlerController.INFERNO_PARTICLE_SPAWNER;

public class AbilityInfernoPunch extends Ability {
	public AbilityInfernoPunch() {
		super(Firebending.ID, "inferno_punch");
	}

	@Override
	public void execute(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		if (data.hasStatusControl(INFERNO_PUNCH_MAIN) || data.hasStatusControl(INFERNO_PUNCH_FIRST) || data.hasStatusControl(INFERNO_PUNCH_SECOND))
			return;

		float chi = STATS_CONFIG.chiInfernoPunch;
		float orbSize = 0.2F;
		int lightRadius = 4;
		if (ctx.getLevel() == 1) {
			chi = STATS_CONFIG.chiInfernoPunch * 4 / 3;
			//4
			orbSize += 0.1F;
			lightRadius += 2;

		}
		if (ctx.getLevel() == 2) {
			chi = STATS_CONFIG.chiInfernoPunch * 5 / 3;
			//5
			orbSize += 0.2F;
			lightRadius += 4;

		}
		if (ctx.isMasterLevel(AbilityTreePath.FIRST)) {
			chi = STATS_CONFIG.chiLargeInfernoPunch * 2F;
			//6
			orbSize += 0.4F;
			lightRadius += 8;

		}
		if (ctx.isMasterLevel(AbilityTreePath.SECOND)) {
			chi = STATS_CONFIG.chiSmallInfernoPunch * 2F;
			//6
			orbSize += 0.2F;
			lightRadius += 3;

		}

		Vector pos = Vector.getRightSide(entity, 0.55).plus(0, 0.8, 0);
		Vector direction = Vector.getLookRectangular(entity);
		if (entity instanceof EntityPlayer && entity.getPrimaryHand() == EnumHandSide.LEFT) {
			pos = Vector.getLeftSide(entity, 0.55).plus(0, 1.8, 0);
		}
		Vector hand = pos.plus(direction.times(0.6));

		if (bender.consumeChi(chi)) {
			if (ctx.isDynamicMasterLevel(AbilityTreePath.FIRST)) data.addStatusControl(INFERNO_PUNCH_FIRST);
			else if (ctx.isDynamicMasterLevel(AbilityTreePath.SECOND)) data.addStatusControl(INFERNO_PUNCH_SECOND);
			else data.addStatusControl(INFERNO_PUNCH_MAIN);
			data.addTickHandler(INFERNO_PARTICLE_SPAWNER);
			EntityLightOrb orb = new EntityLightOrb(world);
			orb.setOwner(entity);
			orb.setAbility(new AbilityInfernoPunch());
			orb.setPosition(hand);
			orb.setOrbSize(orbSize);
			orb.setColor(1F, 0.5F, 0F, 1F);
			orb.setLightRadius(lightRadius);
			orb.setEmittingEntity(entity.getUniqueID().toString());
			orb.setBehavior(new InfernoPunchLightOrb());
			orb.setType(EntityLightOrb.EnumType.TEXTURE_SPHERE);
			orb.setTexture("avatarmod:textures/entity/fireball/frame_%number%.png");
			orb.setTextureFrameCount(24);
			world.spawnEntity(orb);
		}
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiInfernoPunch(this, entity, bender);
	}

	public static class InfernoPunchLightOrb extends LightOrbBehavior {

		@Override
		public Behavior onUpdate(EntityLightOrb entity) {
			Entity emitter = AvatarUtils.getEntityFromStringID(entity.getEmittingEntity());
			if (emitter != null) {
				if (emitter instanceof EntityBender || emitter instanceof EntityPlayer) {
					BendingData b = BendingData.get((EntityLivingBase) emitter);
					boolean hasStatCtrl = b.hasStatusControl(INFERNO_PUNCH_MAIN) || b.hasStatusControl(INFERNO_PUNCH_FIRST)
							|| b.hasStatusControl(INFERNO_PUNCH_SECOND);
					if (hasStatCtrl) {
						Vector pos = Vector.getRightSide((EntityLivingBase) emitter, 0.55).plus(0, 1, 0);
						Vector direction = Vector.getLookRectangular(emitter);

						if (emitter instanceof EntityPlayer && ((EntityLivingBase) emitter).getPrimaryHand() == EnumHandSide.LEFT) {
							pos = Vector.getLeftSide((EntityLivingBase) emitter, 0.55).plus(0, 1.8, 0);
						}
						Vector hand = pos.plus(direction.times(0.6));
						entity.setPosition(hand);

					} else entity.setDead();
				}
			}
			return this;
		}

		@Override
		public void fromBytes(PacketBuffer buf) {

		}

		@Override
		public void toBytes(PacketBuffer buf) {

		}

		@Override
		public void load(NBTTagCompound nbt) {

		}

		@Override
		public void save(NBTTagCompound nbt) {

		}
	}
}
