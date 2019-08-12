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
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.bending.StatusControl.*;
import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.TickHandlerController.INFERNO_PARTICLE_SPAWNER;

public class AbilityInfernoPunch extends Ability {
	public AbilityInfernoPunch() {
		super(Firebending.ID, "inferno_punch");
	}

	@Override
	public void execute(AbilityContext ctx) {
		//Todo: Figure out why the player is slowed with inferno punch
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData data = ctx.getData();
		Bender bender = ctx.getBender();
		World world = ctx.getWorld();
		if (data.hasStatusControl(INFERNO_PUNCH_MAIN) || data.hasStatusControl(INFERNO_PUNCH_FIRST) || data.hasStatusControl(INFERNO_PUNCH_SECOND))
			return;

		float chi = STATS_CONFIG.chiInfernoPunch;
		float orbSize = 0.3F;
		int lightRadius = 4;
		if (ctx.getLevel() == 1) {
			chi = STATS_CONFIG.chiInfernoPunch * 4 / 3;
			//4
			lightRadius += 2;
			orbSize += 0.1F;

		}
		if (ctx.getLevel() == 2) {
			chi = STATS_CONFIG.chiInfernoPunch * 5 / 3;
			//5
			lightRadius += 4;
			orbSize += 0.2F;

		}
		if (ctx.isMasterLevel(AbilityTreePath.FIRST)) {
			chi = STATS_CONFIG.chiLargeInfernoPunch * 2F;
			//6
			lightRadius += 8;
			orbSize += 0.4F;

		}
		if (ctx.isMasterLevel(AbilityTreePath.SECOND)) {
			chi = STATS_CONFIG.chiSmallInfernoPunch * 2F;
			//6
			lightRadius += 3;
			orbSize += 0.15F;

		}

		if (bender.consumeChi(chi)) {
			if (ctx.isDynamicMasterLevel(AbilityTreePath.FIRST)) data.addStatusControl(INFERNO_PUNCH_FIRST);
			else if (ctx.isDynamicMasterLevel(AbilityTreePath.SECOND)) data.addStatusControl(INFERNO_PUNCH_SECOND);
			else data.addStatusControl(INFERNO_PUNCH_MAIN);
			data.addTickHandler(INFERNO_PARTICLE_SPAWNER);

			Vec3d height = entity.getPositionVector().add(0, 1.8, 0);
			Vec3d rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).times(0.05).withY(0).toMinecraft();
			rightSide = rightSide.add(height);
			EntityLightOrb orb = new EntityLightOrb(world);
			orb.setOwner(entity);
			orb.setAbility(new AbilityInfernoPunch());
			orb.setPosition(rightSide);
			orb.setOrbSize(orbSize);
			orb.setSpinning(true);
			orb.setColor(1F, 0.3F, 0F, 1F);
			orb.setLightRadius(lightRadius);
			orb.setEmittingEntity(entity);
			orb.setColourShiftRange(0.8F);
			orb.setColourShiftInterval(0.15F);
			orb.setBehavior(new InfernoPunchLightOrb());
			orb.setType(CLIENT_CONFIG.fireRenderSettings.infernoPunchSphere ? EntityLightOrb.EnumType.COLOR_SPHERE : EntityLightOrb.EnumType.COLOR_CUBE);
			world.spawnEntity(orb);

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
				float size = 0;
				if (entity.ticksExisted == 1)
					size = entity.getOrbSize();
				if (emitter instanceof EntityBender || emitter instanceof EntityPlayer) {
					BendingData be = BendingData.get((EntityLivingBase) emitter);
					boolean hasStatCtrl = be.hasStatusControl(INFERNO_PUNCH_MAIN) || be.hasStatusControl(INFERNO_PUNCH_FIRST)
							|| be.hasStatusControl(INFERNO_PUNCH_SECOND);
					if (hasStatCtrl) {
						Vec3d height;
						Vec3d rightSide;
						if (emitter instanceof EntityPlayer) {
							if (PlayerViewRegistry.getPlayerViewMode(emitter.getUniqueID()) >= 2 || PlayerViewRegistry.getPlayerViewMode(emitter.getUniqueID()) <= -1) {
								entity.setOrbSize(size / 2F - 0.05F);
								height = emitter.getPositionVector().add(0, 1.65, 0);
								height = height.add(emitter.getLookVec().scale(0.8));
								Vec3d vel;
								//Right
								if (((EntityPlayer) emitter).getPrimaryHand() == EnumHandSide.RIGHT) {
									rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw + 90), 0).times(0.5).withY(0).toMinecraft();
									rightSide = rightSide.add(height);
									vel = rightSide.subtract(entity.getPositionVector());
								}
								//Left
								else {
									rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw - 90), 0).times(0.5).withY(0).toMinecraft();
									rightSide = rightSide.add(height);
									vel = rightSide.subtract(entity.getPositionVector());
								}
								entity.setVelocity(vel.scale(0.5));
								AvatarUtils.afterVelocityAdded(entity);
							} else {
								entity.setOrbSize(size);
								height = emitter.getPositionVector().add(0, 0.88, 0);
								Vec3d vel;
								if (((EntityPlayer) emitter).getPrimaryHand() == EnumHandSide.RIGHT) {
									rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw + 90), 0).times(0.55).withY(0).toMinecraft();
									rightSide = rightSide.add(height);
									vel = rightSide.subtract(entity.getPositionVector());
								} else {
									rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw - 90), 0).times(0.55).withY(0).toMinecraft();
									rightSide = rightSide.add(height);
									vel = rightSide.subtract(entity.getPositionVector());
								}
								entity.setVelocity(vel.scale(0.5));
								AvatarUtils.afterVelocityAdded(entity);
							}

						} else {
							entity.setOrbSize(size);
							height = emitter.getPositionVector().add(0, 0.88, 0);
							Vec3d vel;
							if (((EntityBender) emitter).getPrimaryHand() == EnumHandSide.RIGHT) {
								rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw + 90), 0).times(0.55).withY(0).toMinecraft();
								rightSide = rightSide.add(height);
								vel = rightSide.subtract(entity.getPositionVector());
							} else {
								rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw - 90), 0).times(0.55).withY(0).toMinecraft();
								rightSide = rightSide.add(height);
								vel = rightSide.subtract(entity.getPositionVector());
							}
							entity.setVelocity(vel.scale(0.5));
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
							entity.setLightRadius(lightRadius + (int) (java.lang.Math.random() * 4));
						//Shift colour. Copied from the randomly shift colour class.
						if (entity.ticksExisted % 6 == 0) {
							if (entity.getColourShiftRange() != 0) {
								float range = entity.getColourShiftRange() / 2;
								float r = entity.getInitialColourR();
								float g = entity.getInitialColourG();
								float b = entity.getInitialColourB();
								float a = entity.getInitialColourA();
								for (int i = 0; i < 4; i++) {
									float red, green, blue, alpha;
									float rMin = r < range ? 0 : r - range;
									float gMin = g < range ? 0 : r - range;
									float bMin = b < range ? 0 : r - range;
									float aMin = a < range ? 0 : a - range;
									float rMax = r + range;
									float gMax = b + range;
									float bMax = g + range;
									float aMax = a + range;
									switch (i) {
										case 0:
											float amountR = AvatarUtils.getRandomNumberInRange(0,
													(int) (100 / rMax)) / 100F * entity.getColourShiftInterval();
											red = entity.world.rand.nextBoolean() ? r + amountR : r - amountR;
											red = MathHelper.clamp(red, rMin, rMax);
											entity.setColorR(red);
											break;

										case 1:
											float amountG = AvatarUtils.getRandomNumberInRange(0,
													(int) (100 / gMax)) / 100F * entity.getColourShiftInterval();
											green = entity.world.rand.nextBoolean() ? g + amountG : g - amountG;
											green = MathHelper.clamp(green, gMin, gMax);
											entity.setColorG(green);
											break;

										case 2:
											float amountB = AvatarUtils.getRandomNumberInRange(0,
													(int) (100 / bMax)) / 100F * entity.getColourShiftInterval();
											blue = entity.world.rand.nextBoolean() ? b + amountB : b - amountB;
											blue = MathHelper.clamp(blue, bMin, bMax);
											entity.setColorB(blue);
											break;

										case 3:
											float amountA = AvatarUtils.getRandomNumberInRange(0,
													(int) (100 / aMax)) / 100F * entity.getColourShiftInterval();
											alpha = entity.world.rand.nextBoolean() ? a + amountA : a - amountA;
											alpha = MathHelper.clamp(alpha, aMin, aMax);
											entity.setColorA(alpha);
											break;
									}
								}
							}
						}
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
