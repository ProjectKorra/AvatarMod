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
package com.crowsofwar.avatar.bending.bending.ice.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFlamethrower;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.bending.bending.ice.AbilityIceRaze;
import com.crowsofwar.avatar.bending.bending.ice.Icebending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.AvatarEntity;
import com.crowsofwar.avatar.entity.EntityFlames;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.entity.mob.EntityBender;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarParticleUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.avatar.bending.bending.fire.AbilityFlamethrower.FLAMES_PER_SECOND;
import static com.crowsofwar.avatar.bending.bending.fire.AbilityFlamethrower.RANDOMNESS;
import static com.crowsofwar.avatar.util.data.StatusControlController.STOP_ICE_RAZE;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static java.lang.Math.toRadians;

/**
 * @author CrowsOfWar
 */
public class IceRazeHandler extends TickHandler {

    public static final UUID ICE_RAZE_MOVEMENT_MOD_ID = UUID.randomUUID();

    public IceRazeHandler(int id) {
        super(id);
    }


    @Override
    public boolean tick(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData abilityData = data.getAbilityData("ice_raze");
        AbilityIceRaze iceRaze = (AbilityIceRaze) Abilities.get("ice_raze");

        if (iceRaze == null)
            return false;

        //No dividing by 0 here
        int flamesPerSecond = Math.max(iceRaze.getProperty(FLAMES_PER_SECOND, abilityData).intValue(), 1);

        float requiredChi = iceRaze.getProperty(CHI_COST, abilityData).floatValue() / 20F;
        double powerFactor = 2 - abilityData.getDamageMult();
        //Inverts what happens as you want chi to decrease when you're more powerful
        requiredChi *= powerFactor;

        if (bender.consumeChi(requiredChi) && data.hasStatusControl(STOP_ICE_RAZE)) {

            Vector eye = getEyePos(entity);
//            boolean isRaining = world.isRaining() && world.canSeeSky(entity.getPosition()) && world.getBiome(entity.getPosition()).canRain();
//            boolean inWaterBlock = world.getBlockState(entity.getPosition()) instanceof BlockLiquid || world.getBlockState(entity.getPosition()).getBlock() == Blocks.WATER
//                    || world.getBlockState(entity.getPosition()).getBlock() == Blocks.FLOWING_WATER;
//            boolean headInLiquid = world.getBlockState(entity.getPosition().up()) instanceof BlockLiquid || world.getBlockState(entity.getPosition().up()).getBlock() == Blocks.WATER
//                    || world.getBlockState(entity.getPosition().up()).getBlock() == Blocks.FLOWING_WATER;


            double speedMult = iceRaze.getProperty(SPEED, abilityData).floatValue() * 3;
            double randomness = iceRaze.getProperty(RANDOMNESS, abilityData).doubleValue();
            float size = iceRaze.getProperty(SIZE, abilityData).floatValue();
            int fireTime = iceRaze.getProperty(FIRE_TIME, abilityData).intValue();
            float damage = iceRaze.getProperty(DAMAGE, abilityData).floatValue();
            float performanceAmount = iceRaze.getProperty(PERFORMANCE, abilityData).floatValue();
            float xp = iceRaze.getProperty(XP_HIT, abilityData).floatValue();
            float chiHit = iceRaze.getProperty(CHI_HIT, abilityData).floatValue();
            int lifetime = iceRaze.getProperty(LIFETIME, abilityData).intValue();
            float knockback = iceRaze.getProperty(KNOCKBACK, abilityData).floatValue();

            //RGB values for being kewl
            int r, g, b, fadeR, fadeG, fadeB;
            r = iceRaze.getProperty(FIRE_R, abilityData).intValue();
            g = iceRaze.getProperty(FIRE_G, abilityData).intValue();
            b = iceRaze.getProperty(FIRE_B, abilityData).intValue();
            fadeR = iceRaze.getProperty(FADE_R, abilityData).intValue();
            fadeG = iceRaze.getProperty(FADE_G, abilityData).intValue();
            fadeB = iceRaze.getProperty(FADE_B, abilityData).intValue();


            // Affect stats by power rating
            size *= abilityData.getDamageMult() * abilityData.getXpModifier();
            damage *= abilityData.getDamageMult() * abilityData.getXpModifier();
            fireTime *= abilityData.getDamageMult() * abilityData.getXpModifier();
            speedMult *= abilityData.getDamageMult() * abilityData.getXpModifier();
            randomness -= bender.calcPowerRating(Firebending.ID) / 100;
            randomness *= (0.5 / abilityData.getPowerRatingMult()) * abilityData.getXpModifier();
            randomness = randomness < 0 ? 0 : randomness;
            lifetime *= abilityData.getDamageMult() * abilityData.getXpModifier();
            knockback *= abilityData.getDamageMult() * abilityData.getXpModifier();


            double yawRandom = entity.rotationYaw + (Math.random() * 2 - 1) * randomness;
            double pitchRandom = entity.rotationPitch + (Math.random() * 2 - 1) * randomness;
            double range = Math.min(Math.max(1F - speedMult / 25, 0.1F), 0.25F);
            Vector look = Vector.toRectangular(toRadians(yawRandom), toRadians(pitchRandom));
            Vector start = look.times(range).plus(eye.minusY(0.45));

            //  if (entity.ticksExisted % 2 == 0) {
            EntityFlames ice = new EntityFlames(world);
            ice.setPosition(start);
            ice.setOwner(entity);
            ice.setDynamicSpreadingCollision(true);
            ice.setAbility(new AbilityFlamethrower());
            ice.setDamageSource(abilityData.isDynamicMasterLevel(AbilityTreePath.FIRST) ? AvatarDamageSource.ICE.getDamageType() + "_dragonIce"
                    : AvatarDamageSource.ICE.getDamageType() + "_raze");
            ice.setTier(iceRaze.getCurrentTier(abilityData));
            ice.setXp(iceRaze.getProperty(XP_HIT, abilityData).floatValue());
            ice.setVelocity(look.times(speedMult / 8F).toMinecraft());
            ice.setLifeTime(lifetime + AvatarUtils.getRandomNumberInRange(0, 4));
            ice.setTrailingFires(iceRaze.getBooleanProperty(SETS_FIRES, abilityData));
            ice.setFires(iceRaze.getBooleanProperty(SETS_FIRES, abilityData));
            ice.setFireTime(0);
            ice.setChiHit(chiHit);
            ice.setXp(xp);
            ice.setDamage(damage);
            ice.setPush(knockback);
            ice.setRGB(r, g, b);
            ice.setFade(fadeR, fadeG, fadeB);
            ice.setSmelts(false);
            ice.setFireTime(fireTime);
            ice.setPerformanceAmount((int) performanceAmount);
            ice.setElement(new Icebending());
            ice.setDamageSource("avatar_Ice");
            ice.setRedirectable(true);
            ice.setBehaviour(new FlamethrowerBehaviour());
            ice.setChiHit(iceRaze.getProperty(CHI_HIT, abilityData).floatValue());
            ice.setEntitySize(size / 4);
            if (!world.isRemote)
                world.spawnEntity(ice);


            //Raytrace for the beam; will do collision later
            RayTraceResult res = Raytrace.rayTrace(world, start.toMinecraft(), start.plus(look.times(lifetime)).toMinecraft(), size,
                    true, true, false, Entity.class, Objects::isNull);
            double distance = lifetime;

            if (res != null && res.typeOfHit == RayTraceResult.Type.BLOCK)
                distance = start.toMinecraft().distanceTo(res.hitVec);
            int particles = (int) (distance / lifetime * 90);
            //Particle code.
            if (world.isRemote) {
                //Bruh coloured lighting disables the beam
                    ParticleBuilder.create(ParticleBuilder.Type.BEAM).pos(start.toMinecraft())
                            .target(start.plus(look.times(distance)).toMinecraft()).scale(size * 5F).time(1)
                            .clr(100, 250, 255)
                            .fade(140, 230, 255).collide(true).spawn(world);

                    //Flash and ice particles
                    AvatarUtils.spawnDirectionalHelix(world, entity, look.toMinecraft(), particles, distance, size * 0.75,
                            ParticleBuilder.Type.ICE, start.toMinecraft(), new Vec3d(world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 30, world.rand.nextGaussian() / 40),
                            12, false, 0.7F, 0.9F, 1.0F, 0.5F, size);
                    AvatarUtils.spawnDirectionalHelix(world, entity, look.toMinecraft(), particles, distance, size * 0.75,
                            ParticleBuilder.Type.FLASH, start.toMinecraft(), new Vec3d(world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 30, world.rand.nextGaussian() / 40),
                            8, true, 0.7F, 0.95F, 1.0F, 0.25F, size * 0.5F);
//                speedMult /= 29.5F;
//                for (int i = 0; i < flamesPerSecond; i++) {
//                    int rRandom = fadeR < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeR * 2) : AvatarUtils.getRandomNumberInRange(fadeR / 2,
//                            fadeR * 2);
//                    int gRandom = fadeG < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeG * 2) : AvatarUtils.getRandomNumberInRange(fadeG / 2,
//                            fadeG * 2);
//                    int bRandom = fadeB < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeB * 2) : AvatarUtils.getRandomNumberInRange(fadeB / 2,
//                            fadeB * 2);
//
//
//                    double xRand = world.rand.nextGaussian() / 30;
//                    double yRand = world.rand.nextGaussian() / 30;
//                    double zRand = world.rand.nextGaussian() / 30;
//
//                    ParticleBuilder.create(ParticleBuilder.Type.ICE).pos(start.toMinecraft()).time(lifetime + AvatarUtils.getRandomNumberInRange(4, 8))
//                            .vel(look.times(speedMult).plus(xRand, yRand, zRand).toMinecraft()).
//                            clr(r, g, b, 90).fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(40, 100)).collide(true).collideParticles(true)
//                            .spawnEntity(entity).scale(size * 1.5F).element(new Icebending())
//                            .ability(iceRaze).spawn(world);
//                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start.toMinecraft()).time(lifetime + AvatarUtils.getRandomNumberInRange(4, 8)).vel(look.times(speedMult).toMinecraft()).
//                            clr(r, g, b, 120).fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(40, 100)).collide(true).collideParticles(true)
//                            .spawnEntity(entity).scale(size * 1.5F).element(new Icebending())
//                            .ability(iceRaze).spawn(world);
//                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start.toMinecraft()).time(lifetime + AvatarUtils.getRandomNumberInRange(4, 8)).vel(look.times(speedMult).toMinecraft()).
//                            clr(r, g + 15, b, 90).fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(40, 100)).collide(true).collideParticles(true)
//                            .spawnEntity(entity).scale(size * 1.5F).element(new Icebending())
//                            .ability(iceRaze).spawn(world);
//                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start.toMinecraft()).time(lifetime + AvatarUtils.getRandomNumberInRange(4, 8)).vel(look.times(speedMult).toMinecraft()).
//                            clr(r, g , b * 2, 120).fade(rRandom, gRandom + 60, bRandom * 2, AvatarUtils.getRandomNumberInRange(40, 100)).collide(true).collideParticles(true)
//                            .spawnEntity(entity).scale(size * 1.5F).element(new Icebending())
//                            .ability(iceRaze).glow(true).spawn(world);
//                }
            }

            if (ctx.getData().getTickHandlerDuration(this) % 4 == 0)
                world.playSound(null, entity.getPosition(), SoundEvents.BLOCK_GLASS_STEP,
                        SoundCategory.PLAYERS, 0.2f, 0.8f);

            float movementModifier = 1F - Math.min(requiredChi * 12.5F, 0.7F);
            if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(ICE_RAZE_MOVEMENT_MOD_ID) == null)
                applyMovementModifier(entity, movementModifier);

            entity.world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, entity.getSoundCategory(),
                    1.0F, 0.8F + world.rand.nextFloat() / 10);


        } else {
            // not enough chi
            //makes sure the tick handler is removed
            if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(ICE_RAZE_MOVEMENT_MOD_ID) != null)
                entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ICE_RAZE_MOVEMENT_MOD_ID);
            abilityData.setRegenBurnout(true);
            entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ICE_RAZE_MOVEMENT_MOD_ID);
            return true;
        }
        return !data.hasStatusControl(STOP_ICE_RAZE);
    }

    @Override
    public void onRemoved(BendingContext ctx) {
        super.onRemoved(ctx);
        EntityLivingBase entity = ctx.getBenderEntity();
        AbilityData abilityData = ctx.getData().getAbilityData("ice_raze");
        if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(ICE_RAZE_MOVEMENT_MOD_ID) != null)
            entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ICE_RAZE_MOVEMENT_MOD_ID);
        abilityData.setRegenBurnout(true);
        entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(ICE_RAZE_MOVEMENT_MOD_ID);

        //Keeping this in for next update then removing it
        entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeAllModifiers();
    }

    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        moveSpeed.removeModifier(ICE_RAZE_MOVEMENT_MOD_ID);

        moveSpeed.applyModifier(new AttributeModifier(ICE_RAZE_MOVEMENT_MOD_ID, "Ice Raze Movement Modifier", multiplier - 1, 1));

    }

    public static class FlamethrowerBehaviour extends OffensiveBehaviour {

        @Override
        public OffensiveBehaviour onUpdate(EntityOffensive entity) {
            if (entity.getOwner() != null) {
                entity.motionX *= 0.95;
                entity.motionY *= 0.95;
                entity.motionZ *= 0.95;
                if (entity.world.isRemote && entity.ticksExisted > 1) {
                    int[] fade = entity.getFade();
                    int[] rgb = entity.getRGB();
                    for (int h = 0; h < Math.max(1, entity.getOwner() instanceof EntityPlayer ? 1 : entity.velocity().magnitude() / 10); h++) {
                        for (double i = 0; i < entity.width; i += entity.getOwner() instanceof EntityPlayer ? entity.width / 2 : 0.25 * entity.getAvgSize() * 2) {
                            int rRandom = fade[0] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[0] * 2) : AvatarUtils.getRandomNumberInRange(fade[0] / 2,
                                    fade[0] * 2);
                            int gRandom = fade[1] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[1] * 2) : AvatarUtils.getRandomNumberInRange(fade[1] / 2,
                                    fade[1] * 2);
                            int bRandom = fade[2] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[2] * 2) : AvatarUtils.getRandomNumberInRange(fade[2] / 2,
                                    fade[2] * 2);
                            Random random = new Random();
                            Vec3d box = AvatarEntityUtils.getMiddleOfEntity(entity);
                            AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
                            double spawnX = box.x + random.nextDouble() * 0.125 * (boundingBox.maxX - boundingBox.minX);
                            double spawnY = box.y + random.nextDouble() * 0.125 * (boundingBox.maxY - boundingBox.minY);
                            double spawnZ = box.z + random.nextDouble() * 0.125 * (boundingBox.maxZ - boundingBox.minZ);
                            int time = 10 - (int) (entity.velocity().magnitude() / 20) + AvatarUtils.getRandomNumberInRange(0, 2);
                            float scale = entity.getOwner() instanceof EntityBender ? 2.0F : 1.0F;
                            if (entity.getOwner() instanceof EntityPlayer)
                                time = 4 - (int) (entity.velocity().magnitude() / 20) + AvatarUtils.getRandomNumberInRange(0, 2);
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(entity.world.rand.nextGaussian() / 10 * entity.getAvgSize(),
                                    entity.world.rand.nextGaussian() / 10 * entity.getAvgSize(), entity.world.rand.nextGaussian() / 10 * entity.getAvgSize())
                                    .time(time).clr(rgb[0], rgb[1], rgb[2]).scale(entity.getAvgSize() * scale)
                                    .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175)).element(entity.getElement())
                                    .ability(entity.getAbility()).spawnEntity(entity.getOwner()).collide(true).collideParticles(entity.getOwner() instanceof EntityBender).spawn(entity.world);
                            ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(entity.world.rand.nextGaussian() / 10 * entity.getAvgSize(),
                                    entity.world.rand.nextGaussian() / 10 * entity.getAvgSize(), entity.world.rand.nextGaussian() / 10 * entity.getAvgSize())
                                    .time(time).clr(rgb[0], rgb[1], rgb[2]).scale(entity.getAvgSize() * scale)
                                    .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175)).element(entity.getElement())
                                    .ability(entity.getAbility()).spawnEntity(entity.getOwner()).collide(true).collideParticles(entity.getOwner() instanceof EntityBender).spawn(entity.world);
                            ParticleBuilder.create(ParticleBuilder.Type.FIRE).pos(spawnX, spawnY, spawnZ).vel(entity.world.rand.nextGaussian() / 7.5 * entity.getAvgSize(),
                                    entity.world.rand.nextGaussian() / 7.5 * entity.getAvgSize(), entity.world.rand.nextGaussian() / 7.5 * entity.getAvgSize())
                                    .time(time + 2).clr(rgb[0], rgb[1], rgb[2]).scale(entity.getAvgSize() / 1.25F * scale)
                                    .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175)).element(entity.getElement())
                                    .ability(entity.getAbility()).spawnEntity(entity.getOwner()).collide(true).collideParticles(entity.getOwner() instanceof EntityBender).spawn(entity.world);
                        }
                    }
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
