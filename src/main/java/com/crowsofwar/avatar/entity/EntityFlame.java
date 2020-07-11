package com.crowsofwar.avatar.entity;

import com.crowsofwar.avatar.bending.bending.BendingStyle;
import com.crowsofwar.avatar.bending.bending.fire.Firebending;
import com.crowsofwar.avatar.blocks.BlockTemp;
import com.crowsofwar.avatar.blocks.BlockUtils;
import com.crowsofwar.avatar.client.particle.ParticleBuilder;
import com.crowsofwar.avatar.util.AvatarEntityUtils;
import com.crowsofwar.avatar.util.AvatarUtils;
import com.crowsofwar.avatar.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import com.zeitheron.hammercore.api.lighting.ColoredLight;
import com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Objects;

import static com.crowsofwar.avatar.config.ConfigStats.STATS_CONFIG;

@Optional.Interface(iface = "com.zeitheron.hammercore.api.lighting.impl.IGlowingEntity", modid = "hammercore")
public class EntityFlame extends EntityOffensive implements IGlowingEntity, ICustomHitbox {

    private boolean reflect;
    private boolean lightTrailingFire;
    private boolean smelt;
    private float sizeMult;

    public EntityFlame(World worldIn) {
        super(worldIn);
        setSize(0.1f, 0.1f);
        this.setsFires = false;
        this.lightTrailingFire = false;
        this.reflect = false;
        this.ignoreFrustumCheck = true;
        this.lightTnt = true;
        this.sizeMult = 1.1F;
    }

    public void setSmelt(boolean smelt) {
        this.smelt = smelt;
    }
    
    public float getSizeMult() {
        return this.sizeMult;
    }

    public void setSizeMult(float mult) {
        this.sizeMult = mult;
    }

    @Override
    public BendingStyle getElement() {
        return new Firebending();
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        if (entity instanceof EntityItem && smelt)
            AvatarEntityUtils.smeltItemEntity((EntityItem) entity, getTier());
        super.onCollideWithEntity(entity);
    }

    @Override
    public boolean onCollideWithSolid() {
        if (collided && setsFires)
            setFires();
        return super.onCollideWithSolid();
    }

    @Nullable
    @Override
    public SoundEvent[] getSounds() {
        return new SoundEvent[0];
    }

    @Override
    public void spawnDissipateParticles(World world, Vec3d pos) {

    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        motionX *= 0.965;
        motionY *= 0.965;
        motionZ *= 0.965;

        resetPositionToBB();

        if (velocity().sqrMagnitude() <= 0.5 * 0.5) Dissipate();

        Raytrace.Result raytrace = Raytrace.raytrace(world, position(), velocity().normalize(), 0.5,
                true);
        if (raytrace.hitSomething()) {
            EnumFacing sideHit = raytrace.getSide();
            if (reflect) {
                setVelocity(velocity().reflect(new Vector(Objects.requireNonNull(sideHit))).times(0.5));

                // Try to light fires
                if (sideHit != EnumFacing.DOWN && !world.isRemote) {

                    BlockPos bouncingOff = getPosition().add(-sideHit.getXOffset(),
                            -sideHit.getYOffset(),
                            -sideHit.getZOffset());

                    if (sideHit == EnumFacing.UP || world.getBlockState(bouncingOff).getBlock()
                            .isFlammable(world, bouncingOff, sideHit)) {

                        world.setBlockState(getPosition(), Blocks.FIRE.getDefaultState());

                    }

                }

            }
        }

        if (world.isRemote && getOwner() != null) {
            for (double i = 0; i < this.width; i += 0.15) {
                ParticleBuilder.create(ParticleBuilder.Type.FIRE).time(6 + AvatarUtils.getRandomNumberInRange(0, 6))
                        .scale(getAvgSize() / 4.5F).vel(world.rand.nextGaussian() / 10 * getAvgSize(), world.rand.nextGaussian() / 10 * getAvgSize(),
                        world.rand.nextGaussian() / 10 * getAvgSize()).pos(AvatarEntityUtils.getMiddleOfEntity(this)).spawnEntity(getOwner()).spawn(world);
            }
        }

        setEntitySize(getAvgSize() * sizeMult);

        if (lightTrailingFire && !world.isRemote) {
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
    public void spawnPiercingParticles(World world, Vec3d pos) {

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
    public boolean onAirContact() {
        if (getTier() >= 4) {
            spawnExtinguishIndicators();
            return false;
        } else {
            setDead();
            spawnExtinguishIndicators();
            return true;
        }
    }

    @Override
    public boolean shouldDissipate() {
        return true;
    }

    @Override
    public boolean shouldExplode() {
        return false;
    }

    @Override
    public boolean onMajorWaterContact() {
        Dissipate();
        spawnExtinguishIndicators();
        return true;
    }

    @Override
    public boolean onMinorWaterContact() {
        if (getTier() >= 4) {
            return false;
        } else {
            Dissipate();
            // Spawn less extinguish indicators in the rain to prevent spamming
            if (rand.nextDouble() < 0.4) {
                spawnExtinguishIndicators();
            }
            return true;
        }
    }

    public void setReflect(boolean reflect) {
        this.reflect = reflect;
    }

    public void setTrailingFire(boolean fire) {
        this.lightTrailingFire = fire;
        //Laziness 101
        this.setsFires = fire;
    }

    @Override
    public void applyElementalContact(AvatarEntity entity) {
        entity.onFireContact();
    }

    @Override
    public boolean isProjectile() {
        return true;
    }

    @Override
    public double getParticleSpeed() {
        return 0.04;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }


    @Override
    public boolean multiHit() {
        return true;
    }

    @Override
    public boolean setVelocity() {
        return false;
    }

    @Override
    public Vec3d getKnockbackMult() {
        return new Vec3d(STATS_CONFIG.fireShotSetttings.push * 0.125, STATS_CONFIG.fireShotSetttings.push / 2, STATS_CONFIG.fireShotSetttings.push * 0.125);
    }

    @Override
    public boolean isPiercing() {
       return true;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    //Lets velocity control arrows
    @Override
    public boolean canBeCollidedWith() {
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
