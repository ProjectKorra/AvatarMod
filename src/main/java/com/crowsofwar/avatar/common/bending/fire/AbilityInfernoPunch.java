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

			Vec3d height = entity.getPositionVector().add(0, 1.8, 0);
			Vec3d rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).times(0.05).withY(0).toMinecraft();
			rightSide = rightSide.add(height);
			EntityLightOrb orb = new EntityLightOrb(world);
			orb.setOwner(entity);
			orb.setAbility(new AbilityInfernoPunch());
			orb.setPosition(rightSide);
			orb.setOrbSize(0.4F);
			orb.setSpinning(true);
			orb.setColor(1F, 0.3F, 0F, 3F);
			orb.setLightRadius(lightRadius);
			orb.setEmittingEntity(entity);
			orb.setColourShiftRange(0.075F);
			orb.setColourShiftInterval(0.0005F);
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
								entity.setOrbSize(0.15F);
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
								entity.setOrbSize(0.4F);
								height = emitter.getPositionVector().add(0, 0.88, 0);
								Vec3d vel;
								if (((EntityPlayer) emitter).getPrimaryHand() == EnumHandSide.RIGHT) {
									rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw + 90), 0).times(0.4).withY(0).toMinecraft();
									rightSide = rightSide.add(height);
									vel = rightSide.subtract(entity.getPositionVector());
								} else {
									rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw - 90), 0).times(0.4).withY(0).toMinecraft();
									rightSide = rightSide.add(height);
									vel = rightSide.subtract(entity.getPositionVector());
								}
								entity.setVelocity(vel.scale(0.5));
								AvatarUtils.afterVelocityAdded(entity);
							}

						} else {
							entity.setOrbSize(0.4F);
							height = emitter.getPositionVector().add(0, 0.88, 0);
							Vec3d vel;
							if (((EntityBender) emitter).getPrimaryHand() == EnumHandSide.RIGHT) {
								rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw + 90), 0).times(0.385).withY(0).toMinecraft();
								rightSide = rightSide.add(height);
								vel = rightSide.subtract(entity.getPositionVector());
							} else {
								rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw - 90), 0).times(0.385).withY(0).toMinecraft();
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
							entity.setLightRadius(lightRadius + (int) (Math.random() * 4));
						//Shift colour. Copied from the randomly shift colour class.
						if (entity.ticksExisted % 8 == 0) {
							if (entity.getColourShiftRange() != 0) {
								float range = entity.getColourShiftRange() / 2;
								float r = entity.getInitialColourR();
								float g = entity.getInitialColourG();
								float b = entity.getInitialColourB();
								float a = entity.getInitialColourA();
								for (int i = 0; i < 4; i++) {
									//Even though it looks redundant, this allows for decimal values, making the colour shifting more believable/accurate
									float amount = AvatarUtils.getRandomNumberInRange((int) (-1 / entity.getColourShiftInterval()),
											(int) (1 / entity.getColourShiftInterval())) * entity.getColourShiftInterval();
									float red = r, green = g, blue = b, alpha = a;
									switch (i) {
										case 0:
											red = r + amount > r + range ? r - amount : r + amount;
											/*boolean r1 = r + amount < r + range;
											boolean r2 = r + amount > r - range;
											red = r1 && r2 ? red : r + range;**/
											entity.setColorR(red);
											break;

										case 1:
											green = g + amount > g + range ? g - amount : g + amount;
											/*boolean g1 = g + amount < g + range;
											boolean g2 = g + amount > g - range;
											green = g1 && g2 ? green : g + range;**/
											entity.setColorG(green);
											break;

										case 2:
											blue = b + amount > b + range ? b - amount : r + amount;
											/*boolean b1 = r + amount < b + range;
											boolean b2 = r + amount > b - range;
											blue = b1 && b2 ? blue : b + range;**/
											entity.setColorB(blue);
											break;

										case 3:
											alpha = a + amount > a + range ? a - amount : a + amount;
											/*boolean a1 = a + amount < a + range;
											boolean a2 = a + amount > a - range;
											alpha = a1 && a2 ? alpha : a + range;
											entity.setColorA(alpha);**/
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
