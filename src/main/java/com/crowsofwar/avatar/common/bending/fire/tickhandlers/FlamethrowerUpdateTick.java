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
package com.crowsofwar.avatar.common.bending.fire.tickhandlers;

import com.crowsofwar.avatar.common.bending.fire.AbilityFlamethrower;
import com.crowsofwar.avatar.common.bending.fire.Firebending;
import com.crowsofwar.avatar.common.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.AbilityData.AbilityTreePath;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.TickHandler;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.EntityFlame;
import com.crowsofwar.avatar.common.entity.EntityLightOrb;
import com.crowsofwar.avatar.common.entity.data.Behavior;
import com.crowsofwar.avatar.common.entity.data.LightOrbBehavior;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
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
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.UUID;

import static com.crowsofwar.avatar.common.config.ConfigClient.CLIENT_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.avatar.common.data.StatusControlController.STOP_FLAMETHROW;
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
        int duration = data.getTickHandlerDuration(this);

        //Don't remove this, it makes sure the ability data works properly.
        if (!world.isRemote)
            abilityData = data.getAbilityData(new AbilityFlamethrower().getName());

        AbilityTreePath path = abilityData.getPath();

        int level = abilityData.getLevel();
        int flamesPerSecond;

        flamesPerSecond = level <= 0 ? 1 : 2;
        if (level == 3 && path == AbilityTreePath.FIRST)
            flamesPerSecond = 3;
        else if (level == 3 && path == AbilityTreePath.SECOND)
            flamesPerSecond = 1;


        double powerRating = bender.calcPowerRating(Firebending.ID);

        float requiredChi = STATS_CONFIG.chiFlamethrowerSecond / 20;
        if (level == 3 && path == AbilityTreePath.FIRST) {
            requiredChi = STATS_CONFIG.chiFlamethrowerSecondLvl4_1 / 20;
        }
        if (level == 3 && path == AbilityTreePath.SECOND) {
            requiredChi = STATS_CONFIG.chiFlamethrowerSecondLvl4_2 / 20;
        }
        if (level < 3)
            requiredChi = requiredChi * (1F + (float) (Math.max(level, 0)) / 10F);

        // Adjust chi to power rating
        // Multiply chi by a number (from 0..2) based on the power rating - powerFactor
        //  Numbers 0..1 would reduce the chi, while numbers 1..2 would increase the chi
        // maxPowerFactor: maximum amount that the chi can be multiplied by
        // e.g. 0.1 -> chi can be changed by 10%; powerFactor in between 0.9..1.1
        double maxPowerFactor = 0.4;
        double powerFactor = (powerRating + 100) / 100 * maxPowerFactor + 1 - maxPowerFactor;
        requiredChi *= powerFactor;

        if (bender.consumeChi(requiredChi)) {

            Vector eye = getEyePos(entity);
            boolean isRaining = world.isRaining() && world.canSeeSky(entity.getPosition()) && world.getBiome(entity.getPosition()).canRain();
            boolean inWaterBlock = world.getBlockState(entity.getPosition()) instanceof BlockLiquid || world.getBlockState(entity.getPosition()).getBlock() == Blocks.WATER
                    || world.getBlockState(entity.getPosition()).getBlock() == Blocks.FLOWING_WATER;
            boolean headInLiquid = world.getBlockState(entity.getPosition().up()) instanceof BlockLiquid || world.getBlockState(entity.getPosition().up()).getBlock() == Blocks.WATER
                    || world.getBlockState(entity.getPosition().up()).getBlock() == Blocks.FLOWING_WATER;

            if (!isRaining && !(headInLiquid || inWaterBlock)) {

                double speedMult = 15 + 5 * abilityData.getXpModifier();
                double randomness = 3.0 - 0.5 * (abilityData.getXpModifier() + Math.max(abilityData.getLevel(), 0));
                float size = 0.75F;
                int fireTime = 2;
                float damage = STATS_CONFIG.flamethrowerSettings.damage;
                float performanceAmount = 3;
                float xp = SKILLS_CONFIG.flamethrowerHit;

                switch (abilityData.getLevel()) {
                    case 1:
                        size = 1.125F;
                        damage = 1.75F;
                        fireTime = 3;
                        speedMult += 5;
                        break;
                    case 2:
                        size = 1.5F;
                        fireTime = 4;
                        damage = 2.5F;
                        performanceAmount = 4;
                        speedMult += 10;
                        break;
                }
                if (abilityData.isDynamicMasterLevel(AbilityTreePath.FIRST)) {
                    speedMult = 38;
                    randomness = 0;
                    speedMult += 20;
                    randomness = 0;
                    fireTime = 5;
                    size = 1.25F;
                    damage = 4.5F;
                    performanceAmount = 6;


                }
                if (abilityData.isDynamicMasterLevel(AbilityTreePath.SECOND)) {
                    speedMult = 12;
                    randomness = 9;
                    fireTime = 10;
                    size = 2.25F;
                    damage = 1.5F;
                    performanceAmount = 3;

                }
                // Affect stats by power rating
                size += powerRating / 200F;
                damage += powerRating / 100F;
                fireTime += (int) (powerRating / 50F);
                speedMult += powerRating / 100f * 2.5f;
                speedMult *= abilityData.getXpModifier();
                randomness = randomness >= powerRating / 100f * 2.5f ? randomness - powerRating / 100F * 2.5 : 0;
                randomness = randomness < 0 ? 0 : randomness;

                // Affect stats by power rating
                size += powerRating / 200F;
                speedMult += powerRating / 100f * 2.5f;
                randomness = randomness >= powerRating / 100f * 2.5f ? randomness - powerRating / 100F * 2.5 : 0;
                randomness = randomness < 0 ? 0 : randomness;

                double yawRandom = entity.rotationYaw + (Math.random() * 2 - 1) * randomness;
                double pitchRandom = entity.rotationPitch + (Math.random() * 2 - 1) * randomness;
                Vector look = Vector.toRectangular(toRadians(yawRandom), toRadians(pitchRandom));
                Vector start = look.plus(eye.minusY(0.5));


                //Particle code.
                if (world.isRemote) {
                    speedMult /= 28.75;
                    if (CLIENT_CONFIG.fireRenderSettings.solidFlamethrowerParticles) {
                        for (double i = 0; i < flamesPerSecond; i += 3) {
                            Vector start1 = look.times((i / (double) flamesPerSecond) / 10000).plus(eye.minusY(0.5));
                            ParticleBuilder.create(ParticleBuilder.Type.FIRE).pos(start1.toMinecraft()).scale(size * 1.125F).time(10 + AvatarUtils.getRandomNumberInRange(0, 4)).collide(true).spawnEntity(entity).vel(look.times(speedMult / 1.25).toMinecraft())
                                    .ability(new AbilityFlamethrower()).spawn(world);
                        }
                    }
                    for (int i = 0; i < flamesPerSecond; i++) {
                        Vector start1 = look.times((i / (double) flamesPerSecond) / 10000).plus(eye.minusY(0.5));
                        if (CLIENT_CONFIG.fireRenderSettings.solidFlamethrowerParticles) {
                            ParticleBuilder.create(ParticleBuilder.Type.FIRE).pos(start.toMinecraft()).scale(size * 1.125F).time(10 + AvatarUtils.getRandomNumberInRange(0, 4)).collide(true).vel(look.times(speedMult / 1.25).toMinecraft()).
                                    ability(new AbilityFlamethrower()).spawnEntity(entity).spawn(world);
                            if (abilityData.isDynamicMasterLevel(AbilityTreePath.FIRST)) {
                                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start1.toMinecraft()).time(14 + AvatarUtils.getRandomNumberInRange(0, 5)).vel(look.times(speedMult).toMinecraft()).
                                        clr(235 + AvatarUtils.getRandomNumberInRange(0, 20), 10, 5, 225).collide(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                        .ability(new AbilityFlamethrower()).fade(AvatarUtils.getRandomNumberInRange(100, 255), AvatarUtils.getRandomNumberInRange(1, 180),
                                        AvatarUtils.getRandomNumberInRange(1, 180), AvatarUtils.getRandomNumberInRange(100, 175)).spawn(world);
                                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start1.toMinecraft()).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).vel(look.times(speedMult).toMinecraft()).
                                        clr(255, 60 + AvatarUtils.getRandomNumberInRange(1, 40), 10, 200).collide(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                        .ability(new AbilityFlamethrower()).fade(AvatarUtils.getRandomNumberInRange(100, 255), AvatarUtils.getRandomNumberInRange(1, 180),
                                        AvatarUtils.getRandomNumberInRange(1, 180), AvatarUtils.getRandomNumberInRange(100, 175)).spawn(world);
                            } else {
                                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start1.toMinecraft()).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).vel(look.times(speedMult).toMinecraft()).
                                        clr(235 + AvatarUtils.getRandomNumberInRange(0, 20), 10, 5, 225).collide(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                        .ability(new AbilityFlamethrower()).spawn(world);
                                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start1.toMinecraft()).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).vel(look.times(speedMult).toMinecraft()).
                                        clr(255, 60 + AvatarUtils.getRandomNumberInRange(1, 40), 10, 200).collide(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                        .ability(new AbilityFlamethrower()).spawn(world);
                            }
                        } else if (!CLIENT_CONFIG.fireRenderSettings.solidFlamethrowerParticles) {
                            if (abilityData.isDynamicMasterLevel(AbilityTreePath.FIRST)) {
                                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start1.toMinecraft()).time(14 + AvatarUtils.getRandomNumberInRange(0, 5)).vel(look.times(speedMult).toMinecraft()).
                                        clr(235 + AvatarUtils.getRandomNumberInRange(0, 20), 10, 5, 225).collide(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                        .ability(new AbilityFlamethrower()).fade(AvatarUtils.getRandomNumberInRange(100, 255), AvatarUtils.getRandomNumberInRange(1, 180),
                                        AvatarUtils.getRandomNumberInRange(1, 180), AvatarUtils.getRandomNumberInRange(100, 175)).spawn(world);
                                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start1.toMinecraft()).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).vel(look.times(speedMult).toMinecraft()).
                                        clr(255, 60 + AvatarUtils.getRandomNumberInRange(1, 40), 10, 200).collide(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                        .ability(new AbilityFlamethrower()).fade(AvatarUtils.getRandomNumberInRange(100, 255), AvatarUtils.getRandomNumberInRange(1, 180),
                                        AvatarUtils.getRandomNumberInRange(1, 180), AvatarUtils.getRandomNumberInRange(100, 175)).spawn(world);
                            } else {
                                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start1.toMinecraft()).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).vel(look.times(speedMult).toMinecraft()).
                                        clr(235 + AvatarUtils.getRandomNumberInRange(0, 20), 10, 5, 225).collide(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                        .ability(new AbilityFlamethrower()).spawn(world);
                                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start1.toMinecraft()).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).vel(look.times(speedMult).toMinecraft()).
                                        clr(255, 60 + AvatarUtils.getRandomNumberInRange(1, 40), 10, 200).collide(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                        .ability(new AbilityFlamethrower()).spawn(world);
                            }
                        }
                    }
                }
                //}

                //  if (duration % 2 == 0) {
                EntityFlame flames = new EntityFlame(world);
                flames.setPosition(start);
                flames.setOwner(entity);
                flames.setDynamicSpreadingCollision(true);
                flames.setEntitySize(0.1F, 0.1F);
                flames.setAbility(new AbilityFlamethrower());
                flames.setDamageSource(abilityData.isDynamicMasterLevel(AbilityTreePath.FIRST) ? AvatarDamageSource.FIRE.getDamageType() + "dragonFire"
                        : AvatarDamageSource.FIRE.getDamageType());
                flames.setTier(new AbilityFlamethrower().getCurrentTier(abilityData));
                //Will need to be changed later as I go through and add in the new ability config
                flames.setXp(SKILLS_CONFIG.flamethrowerHit);
                flames.setVelocity(look.times(speedMult / 1.625F));
                flames.setLifeTime(8 + AvatarUtils.getRandomNumberInRange(0, 4));
                flames.setTrailingFire(abilityData.isDynamicMasterLevel(AbilityTreePath.SECOND));
                flames.setFireTime(fireTime);
                flames.setDamage(damage);
                flames.setSmelt(true);
                flames.setFireTime(fireTime);
                flames.setPerformanceAmount((int) performanceAmount);
                flames.setElement(new Firebending());
                if (abilityData.isDynamicMasterLevel(AbilityTreePath.SECOND)) {
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
                //  }

                if (ctx.getData().getTickHandlerDuration(this) % 4 == 0)
                    world.playSound(null, entity.getPosition(), SoundEvents.ITEM_FIRECHARGE_USE,
                            SoundCategory.PLAYERS, 0.2f, 0.8f);

                float movementModifier = 1F - (float) speedMult / 90F;
                if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getModifier(FLAMETHROWER_MOVEMENT_MODIFIER_ID) == null)
                    applyMovementModifier(entity, movementModifier);


            } else {
                if (world.isRemote) {
                    for (int i = 0; i < 5; i++)
                        ParticleBuilder.create(ParticleBuilder.Type.SNOW).collide(true).time(15).vel(world.rand.nextGaussian() / 50, world.rand.nextGaussian() / 50, world.rand.nextGaussian() / 50)
                                .scale(1.5F + abilityData.getLevel() / 2F).pos(getEyePos(entity).plus(Vector.getLookRectangular(entity)).toMinecraft()).clr(0.75F, 0.75F, 0.75f).spawn(world);

                }
                Vector pos = getEyePos(entity).plus(Vector.getLookRectangular(entity));
                if (!world.isRemote && world instanceof WorldServer) {
                    WorldServer World = (WorldServer) world;
                    World.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, pos.x(), pos.y(), pos.z(), 3 + Math.max(abilityData.getLevel(), 0),
                            0, 0, 0, 0.0015);
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
            entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(FLAMETHROWER_MOVEMENT_MODIFIER_ID);
            return true;
        }
        return !data.hasStatusControl(STOP_FLAMETHROW);
    }

    private void applyMovementModifier(EntityLivingBase entity, float multiplier) {

        IAttributeInstance moveSpeed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        moveSpeed.removeModifier(FLAMETHROWER_MOVEMENT_MODIFIER_ID);

        moveSpeed.applyModifier(new AttributeModifier(FLAMETHROWER_MOVEMENT_MODIFIER_ID, "Flamethrower Movement Modifier", multiplier - 1, 1));

    }
    //TODO: Rather than having a server-side laggy event, make a client side event and create a damage packet based on that

    public static class FlamethrowerBehaviour extends LightOrbBehavior {

        @Override
        public Behavior onUpdate(EntityLightOrb entity) {
            if (entity.getOwner() != null) {
                AbilityData abilityData = AbilityData.get(entity.getOwner(), new AbilityFlamethrower().getName());
                int flamesPerSecond;
                World world = entity.world;
                entity.setVelocity(entity.getPositionVector().subtract(entity.getOwner().getPositionVector()
                        .add(0, entity.getOwner().getEyeHeight() - 0.5, 0)));
                if (abilityData != null) {
                    int level = abilityData.getLevel();
                    AbilityTreePath path = abilityData.getPath();
                    Bender bender = Bender.get(entity.getOwner());
                    System.out.println(level);
                    if (bender != null && level > -1) {

                        flamesPerSecond = level <= 0 ? 1 : 2;
                        if (level == 3 && path == AbilityTreePath.FIRST)
                            flamesPerSecond = 3;
                        else if (level == 3 && path == AbilityTreePath.SECOND)
                            flamesPerSecond = 1;


                        double powerRating = bender.calcPowerRating(Firebending.ID);
                        double speedMult = 15 + 5 * abilityData.getXpModifier();
                        double randomness = 3.0 - 0.5 * (abilityData.getXpModifier() + Math.max(abilityData.getLevel(), 0));
                        float size = 0.75F;


                        switch (abilityData.getLevel()) {
                            case 1:
                                size = 1.125F;
                                break;
                            case 2:
                                size = 1.5F;
                                break;
                        }
                        if (level == 3 && path == AbilityTreePath.FIRST) {
                            speedMult = 38;
                            randomness = 0;
                            size = 1.25F;

                        }
                        if (level == 3 && path == AbilityTreePath.SECOND) {
                            speedMult = 12;
                            randomness = 9;
                            size = 2.5F;

                        }

                        // Affect stats by power rating
                        size += powerRating / 200F;
                        speedMult += powerRating / 100f * 2.5f;

                        double yawRandom = entity.rotationYaw + (Math.random() * 2 - 1) * randomness;
                        double pitchRandom = entity.rotationPitch + (Math.random() * 2 - 1) * randomness;
                        Vector eye = getEyePos(entity.getOwner());
                        Vector look = randomness == 0 ? Vector.getLookRectangular(entity) : Vector.toRectangular(toRadians(yawRandom), toRadians(pitchRandom));
                        Vector start = look.plus(eye.minusY(0.5));


                        if (entity.world.isRemote) {
                            speedMult /= 28.75;
                            if (CLIENT_CONFIG.fireRenderSettings.solidFlamethrowerParticles) {
                                for (double i = 0; i < flamesPerSecond; i += 3) {
                                    Vector start1 = look.times((i / (double) flamesPerSecond) / 10000).plus(eye.minusY(0.5));
                                    ParticleBuilder.create(ParticleBuilder.Type.FIRE).pos(start1.toMinecraft()).scale(size * 1.5F).time(22).collide(true).spawnEntity(entity).vel(look.times(speedMult).toMinecraft())
                                            .ability(new AbilityFlamethrower()).spawn(world);
                                }
                            }
                            for (int i = 0; i < flamesPerSecond; i++) {
                                Vector start1 = look.times((i / (double) flamesPerSecond) / 10000).plus(eye.minusY(0.5));
                                if (CLIENT_CONFIG.fireRenderSettings.solidFlamethrowerParticles) {
                                    ParticleBuilder.create(ParticleBuilder.Type.FIRE).pos(start.toMinecraft()).scale(size * 1.5F).time(22).collide(true).vel(look.times(speedMult / 1.25).toMinecraft()).
                                            ability(new AbilityFlamethrower()).spawnEntity(entity).spawn(world);
                                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start1.toMinecraft()).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).vel(look.times(speedMult).toMinecraft()).
                                            clr(235 + AvatarUtils.getRandomNumberInRange(0, 20), 10, 5, 255).collide(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                            .ability(new AbilityFlamethrower()).spawn(world);
                                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start1.toMinecraft()).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).vel(look.times(speedMult).toMinecraft()).
                                            clr(255, 60 + AvatarUtils.getRandomNumberInRange(1, 40), 10, 200).collide(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                            .ability(new AbilityFlamethrower()).spawn(world);
                                } else if (!CLIENT_CONFIG.fireRenderSettings.solidFlamethrowerParticles) {
                                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start1.toMinecraft()).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).vel(look.times(speedMult).toMinecraft()).
                                            clr(235 + AvatarUtils.getRandomNumberInRange(0, 20), 10, 5, 255).collide(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                            .ability(new AbilityFlamethrower()).spawn(world);
                                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(start1.toMinecraft()).time(12 + AvatarUtils.getRandomNumberInRange(0, 5)).vel(look.times(speedMult).toMinecraft()).
                                            clr(255, 60 + AvatarUtils.getRandomNumberInRange(1, 40), 10, 200).collide(true).spawnEntity(entity).scale(size * 1.75F).element(new Firebending())
                                            .ability(new AbilityFlamethrower()).spawn(world);
                                }
                            }
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
