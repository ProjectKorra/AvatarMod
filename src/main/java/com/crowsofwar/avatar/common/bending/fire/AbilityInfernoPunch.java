package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityLightOrb;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.LightOrbBehavior;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.avatar.common.util.PlayerViewRegistry;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
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
		//TODO: Use a randomiser for the flame particles so they're more dynamic
		//TODO: Fix gitchiness using a status control+raytrace
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		if (data.hasStatusControl(INFERNO_PUNCH_MAIN) || data.hasStatusControl(INFERNO_PUNCH_FIRST) || data.hasStatusControl(INFERNO_PUNCH_SECOND))
			return;

		float chi = STATS_CONFIG.chiInfernoPunch;
		int lightRadius = 4;
		if (ctx.getLevel() == 1) {
			chi = STATS_CONFIG.chiInfernoPunch * 4 / 3;
			//4
			lightRadius += 2;

		}
		if (ctx.getLevel() == 2) {
			chi = STATS_CONFIG.chiInfernoPunch * 5 / 3;
			//5
			lightRadius += 4;

		}
		if (ctx.isMasterLevel(AbilityTreePath.FIRST)) {
			chi = STATS_CONFIG.chiLargeInfernoPunch * 2F;
			//6
			lightRadius += 8;

		}
		if (ctx.isMasterLevel(AbilityTreePath.SECOND)) {
			chi = STATS_CONFIG.chiSmallInfernoPunch * 2F;
			//6
			lightRadius += 3;

		}

		if (bender.consumeChi(chi)) {
			if (ctx.isDynamicMasterLevel(AbilityTreePath.FIRST)) data.addStatusControl(INFERNO_PUNCH_FIRST);
			else if (ctx.isDynamicMasterLevel(AbilityTreePath.SECOND)) data.addStatusControl(INFERNO_PUNCH_SECOND);
			else data.addStatusControl(INFERNO_PUNCH_MAIN);
			data.addTickHandler(INFERNO_PARTICLE_SPAWNER);
			EntityLightOrb orb = new EntityLightOrb(world);
			orb.setOwner(entity);
			orb.setAbility(new AbilityInfernoPunch());
			orb.setPosition(entity.getPositionVector().add(0, entity.height / 2, 0));
			orb.setOrbSize(0.4F);
			//orb.setSpinning(true);
			orb.setColor(1F, 0.3F, 0F, 3F);
			orb.setLightRadius(lightRadius);
			orb.setEmittingEntity(entity);
			orb.setBehavior(new InfernoPunchLightOrb());
			orb.setType(EntityLightOrb.EnumType.COLOR_SPHERE);
			world.spawnEntity(orb);

			//Test for colour shifting. For some reason it isn't spawned?
			EntityLightOrb test = new EntityLightOrb(world);
			test.setOwner(entity);
			test.setPosition(entity.getPositionVector().add(entity.getLookVec()));
			test.setColor(1.0F, 0.3F, 0F, 1F);
			test.setEmittingEntity(entity);
			test.setLightRadius(4);
			test.setAbility(new AbilityInfernoPunch());
			test.setLifeTime(-1);
			test.setOrbSize(0.5F);
			test.setBehavior(new LightOrbBehavior.ShiftColourRandomly());
			test.setType(EntityLightOrb.EnumType.COLOR_SPHERE);
			//world.spawnEntity(test);
		}
	}

	@Override
	public int getTier() {
		return 3;
	}

	@Override
	public BendingAi getAi(EntityLiving entity, Bender bender) {
		return new AiInfernoPunch(this, entity, bender);
	}

	public static class InfernoPunchLightOrb extends LightOrbBehavior {

		@Override
		public Behavior onUpdate(EntityLightOrb entity) {
			Entity emitter = entity.getEmittingEntity();
			if (emitter != null) {
				if (emitter instanceof EntityBender || emitter instanceof EntityPlayer) {
					BendingData be = BendingData.get((EntityLivingBase) emitter);
					boolean hasStatCtrl = be.hasStatusControl(INFERNO_PUNCH_MAIN) || be.hasStatusControl(INFERNO_PUNCH_FIRST)
							|| be.hasStatusControl(INFERNO_PUNCH_SECOND);
					if (hasStatCtrl) {
						//Rn it's lagging tf out. iIdk why. There's no error log.
						Vec3d height;
						Vec3d rightSide;
						if (emitter instanceof EntityPlayer) {
							if (PlayerViewRegistry.getPlayerViewMode(emitter.getUniqueID()) >= 2) {
								//TODO: Use networking, packets, and mc.gameSettings.thirdPersonView to change the positioning of the orb to make it cool.
								height = emitter.getPositionVector().add(0, entity.getOrbSize() * 3.5, 0);
								rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw + 90), 0).times(0.2).withY(0).toMinecraft();
								//Vec3d rightSide = Vector.getRightSide((EntityLivingBase) emitter, 0.725).toMinecraft();
								rightSide = rightSide.add(height);
								//	pos = pos.add(rightSide);
								Vec3d vel = rightSide.subtract(entity.getPositionVector().scale(5));
								entity.motionX = vel.x;
								entity.motionY = vel.y;
								entity.motionZ = vel.z;
								AvatarUtils.afterVelocityAdded(entity);
							} else {
								height = emitter.getPositionVector().add(0, entity.getOrbSize() * 2.1, 0);
								rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw + 90), 0).times(0.4).withY(0).toMinecraft();
								//Vec3d rightSide = Vector.getRightSide((EntityLivingBase) emitter, 0.725).toMinecraft();
								rightSide = rightSide.add(height);
								//	pos = pos.add(rightSide);
								Vec3d vel = rightSide.subtract(entity.getPositionVector().scale(5));
								entity.motionX = vel.x;
								entity.motionY = vel.y;
								entity.motionZ = vel.z;
								AvatarUtils.afterVelocityAdded(entity);
							}

						} else {
							height = emitter.getPositionVector().add(0, entity.getOrbSize() * 2.1, 0);
							rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw + 90), 0).times(0.4).withY(0).toMinecraft();
							//Vec3d rightSide = Vector.getRightSide((EntityLivingBase) emitter, 0.725).toMinecraft();
							rightSide = rightSide.add(height);
							//	pos = pos.add(rightSide);
							Vec3d vel = rightSide.subtract(entity.getPositionVector().scale(5));
							entity.motionX = vel.x;
							entity.motionY = vel.y;
							entity.motionZ = vel.z;
							AvatarUtils.afterVelocityAdded(entity);
						}
						int lightRadius = 4;
						//Stops constant spam and calculations
						if (entity.ticksExisted == 1) {
							AbilityData aD = AbilityData.get((EntityLivingBase) emitter, "inferno_punch");
							int level = aD.getLevel();
							if (level >= 1) {
								lightRadius = 6;
							}
							if (level >= 2) {
								lightRadius = 8;
							}
							if (aD.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
								lightRadius = 12;
							}
							if (aD.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
								lightRadius = 7;
							}
						}
						if (entity.getEntityWorld().isRemote)
							entity.setLightRadius(lightRadius + (int) (Math.random() * 4));
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
