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
import com.crowsofwar.avatar.bending.bending.fire.AbilityFireShot;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.blocks.BlockTemp;
import com.crowsofwar.avatar.blocks.BlockUtils;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.avatar.util.damageutils.AvatarDamageSource;
import com.crowsofwar.avatar.util.data.AbilityData;
import com.crowsofwar.gorecore.util.Vector;
import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;
import java.util.Random;

/**
 * @author CrowsOfWar
 */

// todo:Colored Flux
@Optional.Interface(iface = "com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity", modid = "hammercore")
public class EntityFlames extends EntityOffensive implements IGlowingEntity, ICustomHitbox {

    //Since it doesn't wanna sync the normal way, we're doing it the hard way.
    private static final DataParameter<Boolean> SYNC_REFLECT = EntityDataManager.createKey(EntityFlames.class,
            DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SYNC_TRAILING_FIRES = EntityDataManager.createKey(EntityFlames.class,
            DataSerializers.BOOLEAN);


    public EntityFlames(World worldIn) {
        super(worldIn);
        setSize(1.0f, 1.0f);
        this.lightTnt = true;
        this.setsFires = true;
    }

    public boolean getTrailingFires() {
        return dataManager.get(SYNC_TRAILING_FIRES);
    }

    public void setTrailingFires(boolean fires) {
        dataManager.set(SYNC_TRAILING_FIRES, fires);
    }

    public boolean getReflect() {
        return dataManager.get(SYNC_REFLECT);
    }

    public void setReflect(boolean reflect) {
        dataManager.set(SYNC_REFLECT, reflect);
    }


    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SYNC_REFLECT, false);
        dataManager.register(SYNC_TRAILING_FIRES, false);
    }

    @Override
    public BendingStyle getElement() {
        return new Firebending();
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        if (entity instanceof EntityItem)
            AvatarEntityUtils.smeltItemEntity((EntityItem) entity, getTier());
        super.onCollideWithEntity(entity);
    }

    @Override
    public boolean onCollideWithSolid() {
        if (collided && setsFires && world.rand.nextBoolean() && !world.isRemote)
            setFires();
        return super.onCollideWithSolid() && !getReflect();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // motionX *= 0.95;
        //  motionY *= 0.95;
        // motionZ *= 0.95;


        if (velocity().sqrMagnitude() <= 0.5) Dissipate();

        //Pretty much

        //Looks for only blocks
        Raytrace.Result raytrace = Raytrace.raytrace(world, position(), velocity().normalize(), 0.125,
                true);
        if (raytrace.hitSomething()) {
            EnumFacing sideHit = raytrace.getSide();
            if (getReflect()) {
                setVelocity(velocity().reflect(new Vector(Objects.requireNonNull(sideHit))).times(0.75));
            }
        }


        if (world.isRemote) {
            int[] fade = getFade();
            int[] rgb = getRGB();
            for (double i = 0; i < width; i += 0.1 * getAvgSize() * 4) {
                int rRandom = fade[0] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[0] * 2) : AvatarUtils.getRandomNumberInRange(fade[0] / 2,
                        fade[0] * 2);
                int gRandom = fade[1] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[1] * 2) : AvatarUtils.getRandomNumberInRange(fade[1] / 2,
                        fade[1] * 2);
                int bRandom = fade[2] < 100 ? AvatarUtils.getRandomNumberInRange(0, fade[2] * 2) : AvatarUtils.getRandomNumberInRange(fade[2] / 2,
                        fade[2] * 2);
                Random random = new Random();
                AxisAlignedBB boundingBox = getEntityBoundingBox();
                double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
                double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
                double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
                        world.rand.nextGaussian() / 60).time(12 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(rgb[0], rgb[1], rgb[2])
                        .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175)).scale(getAvgSize() * 1.75F).element(getElement())
                        .ability(getAbility()).spawnEntity(getOwner()).spawn(world);
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
                        world.rand.nextGaussian() / 60).time(12 + AvatarUtils.getRandomNumberInRange(0, 4)).clr(rgb[0], rgb[1], rgb[2])
                        .fade(rRandom, gRandom, bRandom, AvatarUtils.getRandomNumberInRange(100, 175)).scale(getAvgSize() * 1.75F).element(getElement())
                        .ability(getAbility()).spawnEntity(getOwner()).spawn(world);
                ParticleBuilder.create(ParticleBuilder.Type.FIRE).pos(AvatarEntityUtils.getMiddleOfEntity(this)).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
                        world.rand.nextGaussian() / 60).time(12 + AvatarUtils.getRandomNumberInRange(0, 4)).scale(getAvgSize())
                        .element(getElement()).ability(getAbility()).spawnEntity(getOwner()).spawn(world);

            }
        }

        if (getTrailingFires() && !world.isRemote) {
            if (AvatarUtils.getRandomNumberInRange(1, 10) <= 5) {
                BlockPos pos = getPosition();
                if (BlockUtils.canPlaceFireAt(world, pos)) {
                    BlockTemp.createTempBlock(world, pos, 20, Blocks.FIRE.getDefaultState());
                }
                BlockPos pos2 = getPosition().down();
                if (BlockUtils.canPlaceFireAt(world, pos2)) {
                    BlockTemp.createTempBlock(world, pos2, 20, Blocks.FIRE.getDefaultState());
                }
            }
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        setFade(nbt.getIntArray("Fade"));
        setRGB(nbt.getIntArray("RGB"));
        setReflect(nbt.getBoolean("Reflect"));
        setTrailingFires(nbt.getBoolean("TrailingFires"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setIntArray("Fade", getFade());
        nbt.setIntArray("RGB", getRGB());
        nbt.setBoolean("Reflect", getReflect());
        nbt.setBoolean("TrailingFires", getTrailingFires());
    }

    @Override
    public DamageSource getDamageSource(Entity target, EntityLivingBase owner) {
        return AvatarDamageSource.causeFireShotDamage(target, owner);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return super.canCollideWith(entity) || entity instanceof EntityItem;
    }

    @Override
    public double getExpandedHitboxWidth() {
        return 0.35;
    }

    @Override
    public double getExpandedHitboxHeight() {
        return 0.35;
    }

    @Override
    public void spawnDissipateParticles(World world, Vec3d pos) {

    }

    @Override
    public void spawnPiercingParticles(World world, Vec3d pos) {

    }

    @Override
    public boolean onAirContact() {
        if (getAbility() instanceof AbilityFireShot && getOwner() != null) {
            AbilityData data = AbilityData.get(getOwner(), getAbility().getName());
            if (!data.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
                setDead();
                spawnExtinguishIndicators();
                return true;
            } else return false;
        } else {
            setDead();
            spawnExtinguishIndicators();
            return true;
        }
    }

    @Override
    public boolean shouldDissipate() {
        return !getReflect() || ticksExisted > getLifeTime();
    }

    @Override
    public boolean shouldExplode() {
        return false;
    }

    @Override
    public boolean onMajorWaterContact() {
        setDead();
        spawnExtinguishIndicators();
        return true;
    }

    @Override
    public boolean onMinorWaterContact() {
        if (getAbility() instanceof AbilityFireShot && getOwner() != null) {
            AbilityData data = AbilityData.get(getOwner(), getAbility().getName());
            if (!data.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
                setDead();
                // Spawn less extinguish indicators in the rain to prevent spamming
                if (rand.nextDouble() < 0.4) {
                    spawnExtinguishIndicators();
                }
                return true;

            } else return false;
        } else {
            setDead();
            // Spawn less extinguish indicators in the rain to prevent spamming
            if (rand.nextDouble() < 0.4) {
                spawnExtinguishIndicators();
            }
            return true;
        }
    }

    public void setFires(boolean fires) {
        this.setsFires = fires;
    }


    @Override
    public void applyElementalContact(AvatarEntity entity) {
        entity.onFireContact();
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
    public boolean isPiercing() {
        if (getOwner() != null && getAbility() instanceof AbilityFireShot) {
            AbilityData data = AbilityData.get(getOwner(), getAbility().getName());
            if (data != null)
                return data.isMasterPath(AbilityData.AbilityTreePath.FIRST);
        }
        return false;
    }


    @Override
    @Optional.Method(modid = "hammercore")
    public ColoredLight produceColoredLight(float partialTicks) {
        return ColoredLight.builder().pos(this).color(1f, 0f, 0f, 1f).radius(10f).build();
    }

    @Override
    public Vec3d calculateIntercept(Vec3d origin, Vec3d endpoint, float fuzziness) {
        return null;
    }

    @Override
    public boolean contains(Vec3d point) {
        return false;
    }
}
