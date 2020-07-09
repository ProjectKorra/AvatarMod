/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/

package com.crowsofwar.avatar.common.bending.fire;

import com.crowsofwar.avatar.AvatarMod;
import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.bending.BendingAi;
import com.crowsofwar.avatar.common.bending.fire.statctrls.StatCtrlFlameStrike;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityLightOrb;
import com.crowsofwar.avatar.common.entity.EntityOffensive;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.LightOrbBehavior;
import com.crowsofwar.avatar.common.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.common.entity.mob.EntityBender;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarEntityUtils;
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

import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.StatusControlController.FLAME_STRIKE_MAIN;
import static com.crowsofwar.avatar.common.data.StatusControlController.FLAME_STRIKE_OFF;
import static com.crowsofwar.avatar.common.data.TickHandlerController.FLAME_STRIKE_HANDLER;

/**
 * @author CrowsOfWar
 */
public class AbilityFlameStrike extends Ability {


    public AbilityFlameStrike() {
        super(Firebending.ID, "flame_strike");
        requireRaytrace(-1, false);
    }

    @Override
    public void execute(AbilityContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        if (data.hasStatusControl(FLAME_STRIKE_MAIN) || data.hasStatusControl(FLAME_STRIKE_OFF))
            return;

        float chi = STATS_CONFIG.chiFlameStrike;
        float orbSize = 0.3F;
        int lightRadius = 4;
        if (ctx.getLevel() == 1) {
            chi = STATS_CONFIG.chiFlameStrike * 1.5F;
            //3
            lightRadius += 2;
            orbSize += 0.1F;

        }
        if (ctx.getLevel() == 2) {
            chi = STATS_CONFIG.chiInfernoPunch * 2;
            //4
            lightRadius += 4;
            orbSize += 0.2F;

        }
        if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
            chi = STATS_CONFIG.chiFlameStrike * 2.5F;
            //5
            lightRadius += 8;
            orbSize += 0.4F;

        }
        if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
            chi = STATS_CONFIG.chiFlameStrike * 3F;
            //6
            lightRadius += 3;
            orbSize += 0.15F;

        }

        if (bender.consumeChi(chi)) {

            //Light orb model translating is currently whack
            Vec3d height = entity.getPositionVector().add(0, 1.8, 0);
            Vec3d rightSide = Vector.toRectangular(Math.toRadians(entity.rotationYaw + 90), 0).times(0.05).withY(0).toMinecraft();
            rightSide = rightSide.add(height);

            EntityLightOrb orb = new EntityLightOrb(world);
            orb.setOwner(entity);
            orb.setAbility(new AbilityFlameStrike());
            orb.setPosition(rightSide);
            orb.setOrbSize(orbSize);
            orb.setInitialSize(orbSize);
            orb.setSpinning(true);
            orb.setColor(1F, 0.3F, 0F, 1F);
            orb.setLightRadius(lightRadius);
            orb.setEmittingEntity(entity);
            orb.setColourShiftRange(0.8F);
            orb.setColourShiftInterval(0.15F);
            orb.setBehavior(new FlameStrikeLightOrb());
            orb.setType(CLIENT_CONFIG.fireRenderSettings.flameStrikeSphere ? EntityLightOrb.EnumType.COLOR_SPHERE : EntityLightOrb.EnumType.COLOR_CUBE);
            world.spawnEntity(orb);

        }
        StatCtrlFlameStrike.setTimesUsed(ctx.getBenderEntity().getPersistentID(), 0);
        data.addTickHandler(FLAME_STRIKE_HANDLER);
        data.addStatusControl(FLAME_STRIKE_MAIN);
    }


    @Override
    public int getBaseTier() {
        return 2;
    }

    @Override
    public BendingAi getAi(EntityLiving entity, Bender bender) {
        return new AiFlameStrike(this, entity, bender);
    }

    @Override
    public boolean isOffensive() {
        return true;
    }

    public static class FireblastBehaviour extends OffensiveBehaviour {

        @Override
        public Behavior onUpdate(EntityOffensive entity) {
            entity.setEntitySize(entity.getAvgSize() * 1.075F);
            entity.setVelocity(entity.getVelocity().scale(0.9375));

            if (entity.velocity().magnitude() < 4)
                entity.setDead();
            if (entity.onGround)
                entity.setDead();

            World world = entity.world;
            if (world.isRemote) {
                if (entity.ticksExisted % 2 == 0) {
                    for (double angle = 0; angle < 360; angle += Math.max((int) (entity.getAvgSize() * 20), 5)) {
                        Vector position = Vector.getOrthogonalVector(entity.getLookVec(), angle, entity.getAvgSize());
                        position = position.plus(world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20, world.rand.nextGaussian() / 20);
                        position = position.plus(AvatarEntityUtils.getMiddleOfEntity(entity).x, AvatarEntityUtils.getMiddleOfEntity(entity).y,
                                AvatarEntityUtils.getMiddleOfEntity(entity).z);
                        double spawnX = position.x();
                        double spawnY = position.y();
                        double spawnZ = position.z();
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(world.rand.nextGaussian() / 45,
                                world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45).pos(spawnX, spawnY, spawnZ).
                                scale(entity.getAvgSize() * 1.25F).time(4 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(255, 10, 5).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(world.rand.nextGaussian() / 45,
                                world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45).pos(spawnX, spawnY, spawnZ).
                                scale(entity.getAvgSize() * 1.25F).time(14 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(255, 10, 5).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(world.rand.nextGaussian() / 45,
                                world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45).pos(spawnX, spawnY, spawnZ).
                                scale(entity.getAvgSize() * 1.25F).time(4 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
                                20 + AvatarUtils.getRandomNumberInRange(0, 30), 10).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(world.rand.nextGaussian() / 45,
                                world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45).pos(spawnX, spawnY, spawnZ).
                                scale(entity.getAvgSize() * 1.25F).time(14 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
                                20 + AvatarUtils.getRandomNumberInRange(0, 30), 10).spawn(world);
                    }
                }
                for (int i = 0; i < 2; i++) {
                    Vec3d mid = AvatarEntityUtils.getMiddleOfEntity(entity);
                    double spawnX = mid.x + world.rand.nextGaussian() / 15;
                    double spawnY = mid.y + world.rand.nextGaussian() / 15;
                    double spawnZ = mid.z + world.rand.nextGaussian() / 15;
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(world.rand.nextGaussian() / 45,
                            world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45).pos(spawnX, spawnY, spawnZ).
                            scale(entity.getAvgSize() * 1.25F).time(4 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(255, 10, 5).spawn(world);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(world.rand.nextGaussian() / 45,
                            world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45).pos(spawnX, spawnY, spawnZ).
                            scale(entity.getAvgSize() * 1.25F).time(14 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(255, 10, 5).spawn(world);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(world.rand.nextGaussian() / 45,
                            world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45).pos(spawnX, spawnY, spawnZ).
                            scale(entity.getAvgSize() * 1.25F).time(4 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
                            20 + AvatarUtils.getRandomNumberInRange(0, 30), 10).spawn(world);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).element(new Firebending()).vel(world.rand.nextGaussian() / 45,
                            world.rand.nextGaussian() / 45, world.rand.nextGaussian() / 45).pos(spawnX, spawnY, spawnZ).
                            scale(entity.getAvgSize() * 1.25F).time(14 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
                            20 + AvatarUtils.getRandomNumberInRange(0, 30), 10).spawn(world);

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

    public static class FlameStrikeLightOrb extends LightOrbBehavior {

        @Override
        public Behavior onUpdate(EntityLightOrb entity) {
            Entity emitter = entity.getEmittingEntity();
            if (emitter != null) {
                if (emitter instanceof EntityBender || emitter instanceof EntityPlayer) {
                    BendingData be = BendingData.get((EntityLivingBase) emitter);
                    boolean hasStatCtrl = be.hasStatusControl(FLAME_STRIKE_MAIN) || be.hasStatusControl(FLAME_STRIKE_OFF);
                    if (hasStatCtrl) {
                        if (CLIENT_CONFIG.fireRenderSettings.showFlameStrikeOrb) {
                            Vec3d height;
                            Vec3d rightSide;
                            if (emitter instanceof EntityPlayer) {
                                if (!AvatarMod.realFirstPersonRender2Compat && (PlayerViewRegistry.getPlayerViewMode(emitter.getUniqueID()) >= 2 || PlayerViewRegistry.getPlayerViewMode(emitter.getUniqueID()) <= -1)) {
                                    entity.setOrbSize(entity.getInitialSize() /*/ 0.2F**/ - 0.05F);
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
                                    entity.setOrbSize(entity.getInitialSize());
                                    height = emitter.getPositionVector().add(0, 0.88, 0);
                                    Vec3d vel;
                                    if (((EntityPlayer) emitter).getPrimaryHand() == EnumHandSide.RIGHT) {
                                        rightSide = Vector.toRectangular(Math.toRadians(emitter.rotationYaw + 90), 0).times(0.55 -
                                                Math.min(0.5F / entity.getOrbSize() * 0.1F, 0.05F)).withY(0).toMinecraft();
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
                                entity.setOrbSize(entity.getInitialSize());
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
                        } else {
                            entity.setOrbSize(0.005F);
                            entity.setPosition(AvatarEntityUtils.getMiddleOfEntity(emitter));
                            //Colour shifting
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
