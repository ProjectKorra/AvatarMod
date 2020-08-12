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
package com.crowsofwar.avatar.bending.bending.fire.tickhandlers;

import com.crowsofwar.avatar.bending.bending.Abilities;
import com.crowsofwar.avatar.bending.bending.fire.AbilityFlamethrower;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.EntityFlames;
import com.crowsofwar.avatar.entity.EntityOffensive;
import com.crowsofwar.avatar.entity.data.OffensiveBehaviour;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.avatar.util.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.TickHandler;
import com.crowsofwar.avatar.util.data.ctx.BendingContext;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

import static com.crowsofwar.avatar.bending.bending.Ability.*;
import static com.crowsofwar.avatar.bending.bending.fire.AbilityFlamethrower.FLAMES_PER_SECOND;
import static com.crowsofwar.avatar.bending.bending.fire.AbilityFlamethrower.RANDOMNESS;
import static com.crowsofwar.avatar.config.ConfigClient.CLIENT_CONFIG;
import static com.crowsofwar.avatar.util.data.StatusControlController.STOP_FLAMETHROW;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static java.lang.Math.toRadians;

/**
 * @author CrowsOfWar
 */
public class FlamethrowerUpdateTick extends TickHandler {

    public static final UUID FLAMETHROWER_MOVEMENT_MODIFIER_ID = UUID.fromString("34877be6-6cf5-43f4-a8b3-aa12526651cf");

    public FlamethrowerUpdateTick(int id) {
        super(id);
    }


    @Override
    public boolean tick(BendingContext ctx) {
        BendingData data = ctx.getData();
        EntityLivingBase entity = ctx.getBenderEntity();
        Bender bender = ctx.getBender();
        World world = ctx.getWorld();
        AbilityData abilityData = data.getAbilityData("flamethrower");
        AbilityFlamethrower flamethrower = (AbilityFlamethrower) Abilities.get(new AbilityFlamethrower().getName());

        if (flamethrower == null)
            return false;

        //No dividing by 0 here
        int flamesPerSecond = Math.max(flamethrower.getProperty(FLAMES_PER_SECOND).intValue(), 1);

        float requiredChi = flamethrower.getProperty(CHI_COST, abilityData).floatValue() / 20F;
        double powerFactor = 2 - abilityData.getDamageMult();
        //Inverts what happens as you want chi to decrease when you're more powerful
        requiredChi *= powerFactor;

        if (bender.consumeChi(requiredChi)) {

            Vector eye = getEyePos(entity);
            boolean isRaining = world.isRaining() && world.canSeeSky(entity.getPosition()) && world.getBiome(entity.getPosition()).canRain();
            boolean inWaterBlock = world.getBlockState(entity.getPosition()) instanceof BlockLiquid || world.getBlockState(entity.getPosition()).getBlock() == Blocks.WATER
                    || world.getBlockState(entity.getPosition()).getBlock() == Blocks.FLOWING_WATER;
            boolean headInLiquid = world.getBlockState(entity.getPosition().up()) instanceof BlockLiquid || world.getBlockState(entity.getPosition().up()).getBlock() == Blocks.WATER
                    || world.getBlockState(entity.getPosition().up()).getBlock() == Blocks.FLOWING_WATER;

            if ((!isRaining || flamethrower.getCurrentTier(abilityData) > 3) && !(headInLiquid || inWaterBlock)) {

                double speedMult = flamethrower.getProperty(SPEED, abilityData).floatValue() * 3;
                double randomness = flamethrower.getProperty(RANDOMNESS, abilityData).doubleValue();
                float size = flamethrower.getProperty(SIZE, abilityData).floatValue();
                int fireTime = flamethrower.getProperty(FIRE_TIME, abilityData).intValue();
                float damage = flamethrower.getProperty(DAMAGE, abilityData).floatValue();
                float performanceAmount = flamethrower.getProperty(PERFORMANCE, abilityData).floatValue();
                float xp = flamethrower.getProperty(XP_HIT, abilityData).floatValue();
                float chiHit = flamethrower.getProperty(CHI_HIT, abilityData).floatValue();
                int lifetime = flamethrower.getProperty(LIFETIME, abilityData).intValue();
                float knockback = flamethrower.getProperty(KNOCKBACK, abilityData).floatValue();

                //RGB values for being kewl
                int r, g, b, fadeR, fadeG, fadeB;
                r = flamethrower.getProperty(FIRE_R, abilityData).intValue();
                g = flamethrower.getProperty(FIRE_G, abilityData).intValue();
                b = flamethrower.getProperty(FIRE_B, abilityData).intValue();
                fadeR = flamethrower.getProperty(FADE_R, abilityData).intValue();
                fadeG = flamethrower.getProperty(FADE_G, abilityData).intValue();
                fadeB = flamethrower.getProperty(FADE_B, abilityData).intValue();


                // Affect stats by power rating
                size *= abilityData.getPowerRatingMult() * abilityData.getXpModifier();
                damage *= abilityData.getDamageMult() * abilityData.getXpModifier();
                fireTime *= abilityData.getDamageMult() * abilityData.getXpModifier();
                speedMult *= abilityData.getPowerRatingMult() * abilityData.getXpModifier();
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


                EntityFlames flames = new EntityFlames(world);
                flames.setPosition(start);
                flames.setOwner(entity);
                flames.setDynamicSpreadingCollision(true);
                flames.setEntitySize(0.1F, 0.1F);
                flames.setAbility(new AbilityFlamethrower());
                flames.setDamageSource(abilityData.isDynamicMasterLevel(AbilityTreePath.FIRST) ? AvatarDamageSource.FIRE.getDamageType() + "_dragonFire"
                        : AvatarDamageSource.FIRE.getDamageType() + "_flamethrower");
                flames.setTier(flamethrower.getCurrentTier(abilityData));
                flames.setXp(flamethrower.getProperty(XP_HIT, abilityData).floatValue());
                flames.setVelocity(look.times(speedMult / 29.5F).toMinecraft());
                flames.setLifeTime(lifetime + AvatarUtils.getRandomNumberInRange(0, 4));
                flames.setTrailingFires(flamethrower.getBooleanProperty(SETS_FIRES, abilityData));
                flames.setFires(flamethrower.getBooleanProperty(SETS_FIRES, abilityData));
                flames.setFireTime(fireTime);
                flames.setChiHit(chiHit);
                flames.setXp(xp);
                flames.setDamage(damage);
                flames.setPush(knockback);
                flames.setRGB(r, g, b);
                flames.setFade(fadeR, fadeG, fadeB);
                flames.setSmelts(flamethrower.getBooleanProperty(SMELTS, abilityData));
                flames.setFireTime(fireTime);
                flames.setPerformanceAmount((int) performanceAmount);
                flames.setElement(new Firebending());
                flames.setBehaviour(new FlamethrowerBehaviour());
                flames.setChiHit(flamethrower.getProperty(CHI_HIT, abilityData).floatValue());

                if (size >= 1.5) {
                    for (int i = 0; i < 4; i++) {
                        yawRandom = entity.rotationYaw + (Math.random() * 2 - 1) * randomness * 2;
                        pitchRandom = entity.rotationPitch + (Math.random() * 2 - 1) * randomness * 2;
                        look = Vector.toRectangular(toRadians(yawRandom), toRadians(pitchRandom));
                        start = look.plus(eye.minusY(0.5));
                        flames.setPosition(start);
                        flames.setVelocity(look.times(speedMult / 1.5F));
                        flames.setEntitySize(size / 6);
                        if (!world.isRemote)
                            world.spawnEntity(flames);
                    }
                } else {
                    flames.setEntitySize(size / 4);
                    if (!world.isRemote)
                        world.spawnEntity(flames);
                }

                //Particle code.
                if (world.isRemote && (flamethrower.getCurrentTier(abilityData) < 4 || !isRaining)) {
                    speedMult /= 29.5F;
                    for (int i = 0; i < flamesPerSecond; i++) {
                        int rRandom = fadeR < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeR * 2) : AvatarUtils.getRandomNumberInRange(fadeR / 2,
                                fadeR * 2);
                        int gRandom = fadeG < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeG * 2) : AvatarUtils.getRandomNumberInRange(fadeG / 2,
                                fadeG * 2);
                        int bRandom = fadeB < 100 ? AvatarUtils.getRandomNumberInRange(1, fadeB * 2) : AvatarUtils.getRandomNumberInRange(fadeB / 2,
                                fadeB * 2);

                        if (CLIENT_CONFIG.fireRenderSettings.solidFlamethrowerParticles) {
                            ParticleBuilder.create(ParticleBuilder.Type.FIRE).pos(start.toMinecraft()).scale(size * 0.75F).time(lifetime - AvatarUtils.getRandomNumberInRange(0, 2)).collide(true).vel(look.times(speedMult / 1.25).toMinecraft()).
                                    ability(new AbilityFlamethrower()).collide(true).collideParticles(true).spawnEntity(entity).spawn(world);
                        }

                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start.toMinecraft()).time(lifetime + AvatarUtils.getRandomNumberInRange(4, 8)).vel(look.times(speedMult).toMinecraft()).
                                clr(r, g, b, 180).fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(40, 100)).collide(true).collideParticles(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                .ability(flamethrower).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start.toMinecraft()).time(lifetime + AvatarUtils.getRandomNumberInRange(4, 8)).vel(look.times(speedMult).toMinecraft()).
                                clr(r, g + 15, b, 180).fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(40, 100)).collide(true).collideParticles(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                .ability(flamethrower).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start.toMinecraft()).time(lifetime + AvatarUtils.getRandomNumberInRange(4, 8)).vel(look.times(speedMult).toMinecraft()).
                                clr(r, g + 60, b * 2, 180).fade(rRandom, gRandom + 60, bRandom * 2, AvatarUtils.getRandomNumberInRange(40, 100)).collide(true).collideParticles(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                .ability(flamethrower).spawn(world);
                    }
                }

                if (ctx.getData().getTickHandlerDuration(this) % 4 == 0)
                    world.playSound(null, entity.getPosition(), SoundEvents.ITEM_FIRECHARGE_USE,
                            SoundCategory.PLAYERS, 0.2f, 0.8f);

                float movementModifier = 1F - Math.min(requiredChi * 12.5F, 0.7F);
                if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(FLAMETHROWER_MOVEMENT_MODIFIER_ID) == null)
                    applyMovementModifier(entity, movementModifier);


            } else {
                if (world.isRemote) {
                    //Fix lag
                    for (int i = 0; i < 30; i++)
                        ParticleBuilder.create(ParticleBuilder.Type.SNOW).collide(true).time(12 + AvatarUtils.getRandomNumberInRange(0, 3)).vel(world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40, world.rand.nextGaussian() / 40)
                                .scale(0.125F + flamethrower.getProperty(SIZE, abilityData).floatValue() / 2).pos(getEyePos(entity).plus(Vector.getLookRectangular(entity)).toMinecraft()).clr(1F, 1F, 1F, 0.85F).spawn(world);

                }
                entity.world.playSound(null, new BlockPos(entity), SoundEvents.BLOCK_FIRE_EXTINGUISH, entity.getSoundCategory(),
                        1.0F, 0.8F + world.rand.nextFloat() / 10);
                //makes sure the tick handler is removed
                if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(FLAMETHROWER_MOVEMENT_MODIFIER_ID) != null)
                    entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(FLAMETHROWER_MOVEMENT_MODIFIER_ID);
                return true;
            }


        } else {
            // not enough chi
            abilityData.setRegenBurnout(true);
            entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(FLAMETHROWER_MOVEMENT_MODIFIER_ID);
            return true;
        }
        if (data.hasStatusControl(STOP_FLAMETHROW))
            abilityData.setRegenBurnout(true);
        return !data.hasStatusControl(STOP_FLAMETHROW);
    }

    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        moveSpeed.removeModifier(FLAMETHROWER_MOVEMENT_MODIFIER_ID);

        moveSpeed.applyModifier(new AttributeModifier(FLAMETHROWER_MOVEMENT_MODIFIER_ID, "Flamethrower Movement Modifier", multiplier - 1, 1));

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
                    for (double i = 0; i < entity.width; i += 0.1 * entity.getAvgSize() * 4) {
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
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(entity.world.rand.nextGaussian() / 20 * entity.getAvgSize() * 3,
                                entity.world.rand.nextGaussian() / 20 * entity.getAvgSize() * 3, entity.world.rand.nextGaussian() / 20 * entity.getAvgSize() * 3).time(6 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(rgb[0], rgb[1], rgb[2])
                                .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175)).scale(entity.getAvgSize() * 1.5F).element(entity.getElement())
                                .ability(entity.getAbility()).spawnEntity(entity.getOwner()).spawn(entity.world);
                        ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(entity.world.rand.nextGaussian() / 20 * entity.getAvgSize() * 3,
                                entity.world.rand.nextGaussian() / 20 * entity.getAvgSize() * 3, entity.world.rand.nextGaussian() / 20 * entity.getAvgSize() * 3).time(6 + AvatarUtils.getRandomNumberInRange(0, 2)).clr(rgb[0], rgb[1], rgb[2])
                                .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175)).scale(entity.getAvgSize() * 1.5F).element(entity.getElement())
                                .ability(entity.getAbility()).spawnEntity(entity.getOwner()).spawn(entity.world);
                        ParticleBuilder.create(ParticleBuilder.Type.FIRE).pos(spawnX, spawnY, spawnZ).vel(entity.world.rand.nextGaussian() / 15 * entity.getAvgSize() * 2,
                                entity.world.rand.nextGaussian() / 15 * entity.getAvgSize() * 2, entity.world.rand.nextGaussian() / 15 * entity.getAvgSize() * 2).time(6 + AvatarUtils.getRandomNumberInRange(0, 2)).scale(entity.getAvgSize() / 2)
                                .element(entity.getElement()).ability(entity.getAbility()).spawnEntity(entity.getOwner()).spawn(entity.world);
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
