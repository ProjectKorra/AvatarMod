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
package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.entity.data.FireballBehavior;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.data.Bender;
import com.crowsofwar.avatar.util.data.BendingData;
import com.crowsofwar.avatar.util.data.StatusControlController;
import com.crowsofwar.gorecore.util.Vector;
import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;
import java.util.Random;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

/**
 * @author CrowsOfWar
 */
@Optional.Interface(iface = "com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity", modid = "hammercore")
public class EntityFireball extends EntityOffensive implements IGlowingEntity {


    private static final DataParameter<Integer> SYNC_ORBIT_ID = EntityDataManager.createKey(EntityFireball.class,
            DataSerializers.VARINT);

    private static int nextFireballID = 0;

    public EntityFireball(World world) {
        super(world);
        setSize(.8f, .8f);
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            setOrbitID(nextFireballID++);
        }
        this.lightTnt = true;
    }

    @Override
    public BendingStyle getElement() {
        return new Firebending();
    }

    @Override
    public void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_ORBIT_ID, 1);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (getBehaviour() == null) {
            this.setVelocity(world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian());
            this.setBehaviour(new FireballBehavior.Thrown());
        }

        if (ticksExisted % 30 == 0) {
            world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 6, 0.8F);
        }


        //Particles!
        if (world.isRemote && getOwner() != null) {
            int[] fade = getFade();
            int[] rgb = getRGB();
            int rRandom = fade[0] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[0] * 2) : AvatarUtils.getRandomNumberInRange(fade[0] / 2,
                    fade[0] * 2);
            int gRandom = fade[1] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[1] * 2) : AvatarUtils.getRandomNumberInRange(fade[1] / 2,
                    fade[1] * 2);
            int bRandom = fade[2] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[2] * 2) : AvatarUtils.getRandomNumberInRange(fade[2] / 2,
                    fade[2] * 2);

            int rings = (int) (Math.sqrt(getAvgSize()) * 4) + 2;
            float size = 0.75F * getAvgSize() * (1 / getAvgSize());
            int particles = (int) (Math.min((int) (getAvgSize() * Math.PI * 2), 4) + (velocity().magnitude() / 20));
            Vec3d centre = AvatarEntityUtils.getBottomMiddleOfEntity(this);
            centre = centre.add(0, getAvgSize() / 2, 0);
            ParticleBuilder.create(ParticleBuilder.Type.FLASH).clr(rgb[0], rgb[1], rgb[2])
                    .fade(rRandom, gRandom, bRandom, (int) (0.75F * AvatarUtils.getRandomNumberInRange(100, 175)))
                    .scale(size / 1.5F).element(getElement()).spawnEntity(getOwner()).time(16 + AvatarUtils.getRandomNumberInRange(0, 4)).
                            swirl(rings, particles, getAvgSize() * 1.25F,
                                    size / 2.5F, (float) (velocity().magnitude() * 10), (1 / size), this,
                                    world, false, centre, ParticleBuilder.SwirlMotionType.IN,
                                    true, true);

            for (double h = 0; h < width; h += 0.1) {
                Random random = new Random();
                AxisAlignedBB boundingBox = getEntityBoundingBox();
                double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
                double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
                double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
                ParticleBuilder.create(ParticleBuilder.Type.FIRE).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 30, world.rand.nextGaussian() / 30,
                        world.rand.nextGaussian() / 30).time(12 + AvatarUtils.getRandomNumberInRange(0, 4))
                        .scale(getSize() / 100F).element(getElement()).spawnEntity(getOwner())
                        .spawn(world);
                ParticleBuilder.create(ParticleBuilder.Type.FIRE).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 30, world.rand.nextGaussian() / 30,
                        world.rand.nextGaussian() / 30).time(10 + AvatarUtils.getRandomNumberInRange(0, 4))
                        .scale(getSize() / 100F).element(getElement()).spawnEntity(getOwner())
                        .spawn(world);
            }

            for (double h = 0; h < width; h += 0.3) {
                Random random = new Random();
                AxisAlignedBB boundingBox = getEntityBoundingBox();
                double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
                double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
                double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
                        world.rand.nextGaussian() / 60).time(12).clr(rgb[0], rgb[1], rgb[2])
                        .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175))
                        .scale(getSize() * 0.03125F).element(getElement()).spawnEntity(getOwner())
                        .spawn(world);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
                        world.rand.nextGaussian() / 60).time(12).clr(rgb[0], rgb[1], rgb[2])
                        .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175))
                        .scale(getSize() * 0.03125F).element(getElement()).spawnEntity(getOwner())
                        .spawn(world);
            }

            if (getBehaviour() instanceof FireballBehavior.Thrown) {
                for (int i = 0; i < 4; i++) {
                    Vec3d pos = Vector.getOrthogonalVector(getLookVec(), i * 90 + (ticksExisted % 360) * 10, getAvgSize() / 1.25F).toMinecraft();
                    Vec3d velocity;
                    Vec3d entityPos = AvatarEntityUtils.getMiddleOfEntity(this);

                    pos = pos.add(entityPos);
                    velocity = pos.subtract(entityPos).normalize();
                    velocity = velocity.scale(AvatarUtils.getSqrMagnitude(getVelocity()) / 400000);

                    double spawnX = pos.x;
                    double spawnY = pos.y;
                    double spawnZ = pos.z;

                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 100 + velocity.x,
                            world.rand.nextGaussian() / 100 + velocity.y, world.rand.nextGaussian() / 60 + velocity.z)
                            .time(4 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(rgb[0], rgb[1], rgb[2])
                            .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175))
                            .scale(getAvgSize()).element(getElement()).spawnEntity(getOwner())
                            .spawn(world);
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 100 + velocity.x,
                            world.rand.nextGaussian() / 100 + velocity.y, world.rand.nextGaussian() / 60 + velocity.z)
                            .time(4 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(rgb[0], rgb[1], rgb[2])
                            .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175))
                            .scale(getAvgSize()).element(getElement()).spawnEntity(getOwner())
                            .spawn(world);

                }
            }

        }
    }

    @Override
    public boolean onMajorWaterContact() {
        spawnExtinguishIndicators();
        setDead();
        return true;
    }

    @Override
    public boolean onMinorWaterContact() {
        spawnExtinguishIndicators();
        return false;
    }

    @Override
    public EntityLivingBase getController() {
        return this.getBehaviour() instanceof FireballBehavior.PlayerControlled ? getOwner() : null;
    }


    public int getSize() {
        return (int) (getAvgSize() * 16);
    }


    public int getOrbitID() {
        return dataManager.get(SYNC_ORBIT_ID);
    }

    public void setOrbitID(int id) {
        dataManager.set(SYNC_ORBIT_ID, id);
    }


    @Override
    public Vec3d getExplosionKnockbackMult() {
        return super.getExplosionKnockbackMult().scale(STATS_CONFIG.fireballSettings.explosionSize * getSize() / 128F + getPowerRating() * 0.02);
    }

    @Override
    public double getExplosionHitboxGrowth() {
        return STATS_CONFIG.fireballSettings.explosionSize * getSize() / 32F + getPowerRating() * 0.02;
    }

    @Override
    public Vec3d getKnockbackMult() {
        return super.getKnockbackMult().scale(0.125 * STATS_CONFIG.fireballSettings.push);
    }

    @Override
    public void applyElementalContact(AvatarEntity entity) {
        super.applyElementalContact(entity);
        entity.onFireContact();
    }

    @Override
    public boolean shouldExplode() {
        return getBehaviour() instanceof FireballBehavior.Thrown;
    }

    @Override
    public boolean shouldDissipate() {
        return false;
    }

    @Override
    public void spawnExplosionParticles(World world, Vec3d pos) {
        if (world.isRemote && getOwner() != null) {
            int[] fade = getFade();
            int[] rgb = getRGB();
            int rRandom = fade[0] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[0] * 2) : AvatarUtils.getRandomNumberInRange(fade[0] / 2,
                    fade[0] * 2);
            int gRandom = fade[1] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[1] * 2) : AvatarUtils.getRandomNumberInRange(fade[1] / 2,
                    fade[1] * 2);
            int bRandom = fade[2] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[2] * 2) : AvatarUtils.getRandomNumberInRange(fade[2] / 2,
                    fade[2] * 2);

            for (double h = 0; h < width * 2; h += 0.2 / getExplosionHitboxGrowth()) {
                Random random = new Random();
                AxisAlignedBB boundingBox = getEntityBoundingBox();
                double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
                double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
                double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 10 * getExplosionHitboxGrowth(),
                        world.rand.nextGaussian() / 10 * getExplosionHitboxGrowth(), world.rand.nextGaussian() / 10 * getExplosionHitboxGrowth())
                        .time(12 + AvatarUtils.getRandomNumberInRange(0, 4) + (int) getExplosionHitboxGrowth()).clr(rgb[0], rgb[1], rgb[2])
                        .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175))
                        .scale(getSize() * 0.03125F).element(getElement()).spawnEntity(getOwner())
                        .spawn(world);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 10 * getExplosionHitboxGrowth(),
                        world.rand.nextGaussian() / 10 * getExplosionHitboxGrowth(), world.rand.nextGaussian() / 10 * getExplosionHitboxGrowth())
                        .time(12 + AvatarUtils.getRandomNumberInRange(0, 4) + (int) getExplosionHitboxGrowth()).clr(rgb[0], rgb[1], rgb[2])
                        .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175))
                        .scale(getSize() * 0.03125F).element(getElement()).spawnEntity(getOwner())
                        .spawn(world);
            }

        }
    }

    @Override
    public void setDead() {
        super.setDead();
        removeStatCtrl();
    }

    @Override
    public boolean onCollideWithSolid() {
        return super.onCollideWithSolid();
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        //setBehavior((FireballBehavior) Behavior.lookup(nbt.getInteger("Behavior"), this));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        //nbt.setInteger("Behavior", getBehavior().getId());
    }

    @Override
    public int getBrightnessForRender() {
        return 150;
    }

    @Override
    public double getExpandedHitboxWidth() {
        return getWidth() / 4;
    }

    @Override
    public double getExpandedHitboxHeight() {
        return getHeight() / 4;
    }

    @Override
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return super.shouldRenderInPass(pass);
    }
    // Mostly fixes a glitch where the entity turns invisible

    private void removeStatCtrl() {
        if (getOwner() != null) {
            BendingData data = Objects.requireNonNull(Bender.get(getOwner())).getData();
            if (data != null) {
                data.removeStatusControl(StatusControlController.THROW_FIREBALL);
            }
        }
    }

    @Override
    public boolean isProjectile() {
        return true;
    }


    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }


    @Override
    @Optional.Method(modid = "hammercore")
    public ColoredLight produceColoredLight(float partialTicks) {
        return ColoredLight.builder().pos(this).color(1f, 0f, 0f, 1f).radius(getSize() / 4F).build();
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        if (getBehaviour() instanceof FireballBehavior.Thrown)
            return super.canCollideWith(entity);
        else return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        if (getBehaviour() instanceof FireballBehavior.Thrown)
            return super.canBeCollidedWith();
        else return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }
}
